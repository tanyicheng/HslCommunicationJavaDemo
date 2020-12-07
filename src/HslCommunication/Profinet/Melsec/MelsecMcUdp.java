package HslCommunication.Profinet.Melsec;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Address.McAddressData;
import HslCommunication.Core.Net.NetworkBase.NetworkUdpDeviceBase;
import HslCommunication.Core.Transfer.RegularByteTransform;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.StringResources;
import HslCommunication.Utilities;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * 三菱PLC通讯类，采用UDP的协议实现，采用Qna兼容3E帧协议实现，需要在PLC侧先的以太网模块先进行配置，必须为二进制通讯<br />
 * Mitsubishi PLC communication class is implemented using UDP protocol and Qna compatible 3E frame protocol.
 * The Ethernet module needs to be configured first on the PLC side, and it must be binary communication.
 */
public class MelsecMcUdp extends NetworkUdpDeviceBase {

    public MelsecMcUdp() {
        this.WordLength = 1;
        setByteTransform(new RegularByteTransform());
    }

    public MelsecMcUdp(String ipAddress, int port) {
        this.WordLength = 1;
        this.setIpAddress(ipAddress);
        this.setPort(port);
        setByteTransform(new RegularByteTransform());
    }


    private byte NetworkNumber = 0x00;                       // 网络号
    private byte NetworkStationNumber = 0x00;                // 网络站号

    /**
     * 获取网络号
     *
     * @return 网络号
     */
    public byte getNetworkNumber() {
        return NetworkNumber;
    }

    /**
     * 设置网络号
     *
     * @param networkNumber 网络号
     */
    public void setNetworkNumber(byte networkNumber) {
        NetworkNumber = networkNumber;
    }

    /**
     * 获取网络站号
     *
     * @return 网络站号
     */
    public byte getNetworkStationNumber() {
        return NetworkStationNumber;
    }

    /**
     * 设置网络站号
     *
     * @param networkStationNumber 网络站号
     */
    public void setNetworkStationNumber(byte networkStationNumber) {
        NetworkStationNumber = networkStationNumber;
    }

    protected OperateResultExOne<McAddressData> McAnalysisAddress(String address, short length) {
        return McAddressData.ParseMelsecFrom(address, length);
    }


    public OperateResultExOne<byte[]> Read(String address, short length) {
        // 分析地址
        OperateResultExOne<McAddressData> addressResult = McAnalysisAddress(address, length);
        if (!addressResult.IsSuccess) return OperateResultExOne.CreateFailedResult(addressResult);

        ArrayList<Byte> bytesContent = new ArrayList<Byte>();
        short alreadyFinished = 0;
        while (alreadyFinished < length) {
            short readLength = (short) Math.min(length - alreadyFinished, 900);
            addressResult.Content.setLength(readLength);
            OperateResultExOne<byte[]> read = ReadAddressData(addressResult.Content);
            if (!read.IsSuccess) return read;

            Utilities.ArrayListAddArray(bytesContent, read.Content);
            alreadyFinished += readLength;

            // 字的话就是正常的偏移位置，如果是位的话，就转到位的数据
            if (addressResult.Content.getMcDataType().getDataType() == 0)
                addressResult.Content.setAddressStart(addressResult.Content.getAddressStart() + readLength);
            else
                addressResult.Content.setAddressStart(addressResult.Content.getAddressStart() + readLength * 16);
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.getBytes(bytesContent));
    }

    private OperateResultExOne<byte[]> ReadAddressData(McAddressData addressData) {
        byte[] coreResult = MelsecHelper.BuildReadMcCoreCommand(addressData, false);

        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(MelsecMcNet.PackMcCommand(coreResult, this.NetworkNumber, this.NetworkStationNumber));
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        // 错误代码验证
        OperateResult check = MelsecMcNet.CheckResponseContent(read.Content);
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

        // 数据解析，需要传入是否使用位的参数
        return MelsecMcNet.ExtractActualData(SoftBasic.BytesArrayRemoveBegin(read.Content, 11), false);
    }

    public OperateResult Write(String address, byte[] value) {
        // 分析地址
        OperateResultExOne<McAddressData> addressResult = McAnalysisAddress(address, (short) 0);
        if (!addressResult.IsSuccess) return OperateResultExOne.CreateFailedResult(addressResult);

        return WriteAddressData(addressResult.Content, value);
    }

    private OperateResult WriteAddressData(McAddressData addressData, byte[] value) {
        // 创建核心报文
        byte[] coreResult = MelsecHelper.BuildWriteWordCoreCommand(addressData, value);

        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(MelsecMcNet.PackMcCommand(coreResult, NetworkNumber, NetworkStationNumber));
        if (!read.IsSuccess) return read;

        // 错误码校验
        OperateResult check = MelsecMcNet.CheckResponseContent(read.Content);
        if (!check.IsSuccess) return check;

        // 成功
        return OperateResult.CreateSuccessResult();
    }

    /**
     * 随机读取PLC的数据信息，可以跨地址，跨类型组合，但是每个地址只能读取一个word，也就是2个字节的内容。收到结果后，需要自行解析数据<br />
     * Randomly read PLC data information, which can be combined across addresses and types, but each address can only read one word,
     * which is the content of 2 bytes. After receiving the results, you need to parse the data yourself
     * @param address 所有的地址的集合
     * @return 结果
     */
    public OperateResultExOne<byte[]> ReadRandom( String[] address )
    {
        McAddressData[] mcAddressDatas = new McAddressData[address.length];
        for (int i = 0; i < address.length; i++)
        {
            OperateResultExOne<McAddressData> addressResult = McAddressData.ParseMelsecFrom( address[i], 1 );
            if (!addressResult.IsSuccess) return OperateResultExOne.CreateFailedResult( addressResult );

            mcAddressDatas[i] = addressResult.Content;
        }

        byte[] coreResult = MelsecHelper.BuildReadRandomWordCommand( mcAddressDatas );

        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer( MelsecMcNet.PackMcCommand( coreResult, this.NetworkNumber, this.NetworkStationNumber ) );
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult( read );

        // 错误代码验证
        OperateResult check = MelsecMcNet.CheckResponseContent( read.Content );
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult( check );

        // 数据解析，需要传入是否使用位的参数
        return MelsecMcNet.ExtractActualData( SoftBasic.BytesArrayRemoveBegin( read.Content, 11 ), false );
    }

    /**
     * 随机读取PLC的数据信息，可以跨地址，跨类型组合，但是每个地址只能读取一个word，也就是2个字节的内容。收到结果后，自动转换为了short类型的数组<br />
     * Randomly read PLC data information, which can be combined across addresses and types, but each address can only read one word,
     * which is the content of 2 bytes. After receiving the result, it is automatically converted to an array of type short.
     * @param address 所有的地址的集合
     * @return 结果
     */
    public OperateResultExOne<byte[]> ReadRandom( String[] address, short[] length )
    {
        if (length.length != address.length) return new OperateResultExOne<byte[]>( StringResources.Language.TwoParametersLengthIsNotSame() );

        McAddressData[] mcAddressDatas = new McAddressData[address.length];
        for (int i = 0; i < address.length; i++)
        {
            OperateResultExOne<McAddressData> addressResult = McAddressData.ParseMelsecFrom( address[i], length[i] );
            if (!addressResult.IsSuccess) return OperateResultExOne.CreateFailedResult( addressResult );

            mcAddressDatas[i] = addressResult.Content;
        }

        byte[] coreResult = MelsecHelper.BuildReadRandomCommand( mcAddressDatas );

        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer( MelsecMcNet.PackMcCommand( coreResult, this.NetworkNumber, this.NetworkStationNumber ) );
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult( read );

        // 错误代码验证
        OperateResult check = MelsecMcNet.CheckResponseContent( read.Content );
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult( check );

        // 数据解析，需要传入是否使用位的参数
        return MelsecMcNet.ExtractActualData( SoftBasic.BytesArrayRemoveBegin( read.Content, 11 ), false );
    }

    public OperateResultExOne<short[]> ReadRandomInt16( String[] address )
    {
        OperateResultExOne<byte[]> read = ReadRandom( address );
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult( read );

        return OperateResultExOne.CreateSuccessResult( getByteTransform().TransInt16( read.Content, 0, address.length ) );
    }

    public OperateResultExOne<boolean[]> ReadBool( String address, short length )
    {
        // 分析地址
        OperateResultExOne<McAddressData> addressResult = McAnalysisAddress( address, length );
        if (!addressResult.IsSuccess) return OperateResultExOne.CreateFailedResult( addressResult );

        // 获取指令
        byte[] coreResult = MelsecHelper.BuildReadMcCoreCommand( addressResult.Content, true );

        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer( MelsecMcNet.PackMcCommand( coreResult, NetworkNumber, NetworkStationNumber ) );
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult( read );

        // 错误代码验证
        OperateResult check = MelsecMcNet.CheckResponseContent( read.Content );
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult( check );

        // 数据解析，需要传入是否使用位的参数
        OperateResultExOne<byte[]> extract = MelsecMcNet.ExtractActualData( SoftBasic.BytesArrayRemoveBegin( read.Content, 11 ), true );
        if(!extract.IsSuccess) return OperateResultExOne.CreateFailedResult( extract );

        // 转化bool数组
        boolean[] result = new boolean[length];
        for(int i = 0; i < length; i++){
            if(extract.Content[i] == 0x01) result[i] = true;
        }
        return OperateResultExOne.CreateSuccessResult( result );
    }

    public OperateResult Write( String address, boolean[] values )
    {
        // 分析地址
        OperateResultExOne<McAddressData> addressResult = McAnalysisAddress( address, (short) 0 );
        if (!addressResult.IsSuccess) return addressResult;

        byte[] coreResult = MelsecHelper.BuildWriteBitCoreCommand( addressResult.Content, values );

        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer( MelsecMcNet.PackMcCommand( coreResult, NetworkNumber, NetworkStationNumber ) );
        if (!read.IsSuccess) return read;

        // 错误码校验
        OperateResult check = MelsecMcNet.CheckResponseContent( read.Content );
        if (!check.IsSuccess) return check;

        // 成功
        return OperateResult.CreateSuccessResult( );
    }

    public OperateResult RemoteRun( )
    {
        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer( MelsecMcNet.PackMcCommand( new byte[] { 0x01, 0x10, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00 }, NetworkNumber, NetworkStationNumber ) );
        if (!read.IsSuccess) return read;

        // 错误码校验
        OperateResult check = MelsecMcNet.CheckResponseContent( read.Content );
        if (!check.IsSuccess) return check;

        // 成功
        return OperateResult.CreateSuccessResult( );
    }

    public OperateResult RemoteStop( )
    {
        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer( MelsecMcNet.PackMcCommand( new byte[] { 0x02, 0x10, 0x00, 0x00, 0x01, 0x00 }, NetworkNumber, NetworkStationNumber ) );
        if (!read.IsSuccess) return read;

        // 错误码校验
        OperateResult check = MelsecMcNet.CheckResponseContent( read.Content );
        if (!check.IsSuccess) return check;

        // 成功
        return OperateResult.CreateSuccessResult( );
    }

    public OperateResult RemoteReset()
    {
        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer( MelsecMcNet.PackMcCommand( new byte[] { 0x06, 0x10, 0x00, 0x00, 0x01, 0x00 }, NetworkNumber, NetworkStationNumber ) );
        if (!read.IsSuccess) return read;

        // 错误码校验
        OperateResult check = MelsecMcNet.CheckResponseContent( read.Content );
        if (!check.IsSuccess) return check;

        // 成功
        return OperateResult.CreateSuccessResult( );
    }

    public OperateResultExOne<String> ReadPlcType( )
    {
        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer( MelsecMcNet.PackMcCommand( new byte[] { 0x01, 0x01, 0x00, 0x00 }, NetworkNumber, NetworkStationNumber ) );
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult( read );

        // 错误码校验
        OperateResult check = MelsecMcNet.CheckResponseContent( read.Content );
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult( check );

        // 成功
        return OperateResultExOne.CreateSuccessResult( new String(SoftBasic.BytesArraySelectMiddle(read.Content, 11, 16), StandardCharsets.US_ASCII).trim() );
    }

    public OperateResult ErrorStateReset( )
    {
        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer( MelsecMcNet.PackMcCommand( new byte[] { 0x17, 0x16, 0x00, 0x00 }, NetworkNumber, NetworkStationNumber ) );
        if (!read.IsSuccess) return read;

        // 错误码校验
        OperateResult check = MelsecMcNet.CheckResponseContent( read.Content );
        if (!check.IsSuccess) return check;

        // 成功
        return OperateResult.CreateSuccessResult( );
    }

    public String toString(){
        return "MelsecMcUdp[" + getIpAddress() + ":" + getPort() + "]";
    }
}
