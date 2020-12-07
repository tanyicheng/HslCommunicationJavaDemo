package HslCommunication.Profinet.Melsec;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Address.McAddressData;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.MelsecQnA3EAsciiMessage;
import HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import HslCommunication.Core.Transfer.RegularByteTransform;
import HslCommunication.Core.Types.FunctionOperateExOne;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.StringResources;
import HslCommunication.Utilities;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


/**
 * 三菱PLC通讯类，采用Qna兼容3E帧协议实现，需要在PLC侧先的以太网模块先进行配置，必须为ASCII通讯格式
 */
public class MelsecMcAsciiNet extends NetworkDeviceBase {

    /**
     * 实例化三菱的Qna兼容3E帧协议的通讯对象
     */
    public MelsecMcAsciiNet()
    {
        WordLength = 1;
        setByteTransform(new RegularByteTransform());
    }


    /**
     * 实例化一个三菱的Qna兼容3E帧协议的通讯对象
     * @param ipAddress PLC的Ip地址
     * @param port PLC的端口
     */
    public MelsecMcAsciiNet(String ipAddress, int port) {
        WordLength = 1;
        setIpAddress(ipAddress);
        setPort(port);
        setByteTransform(new RegularByteTransform());
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new MelsecQnA3EAsciiMessage();
    }

    private byte NetworkNumber = 0x00;                       // 网络号
    private byte NetworkStationNumber = 0x00;                // 网络站号

    /**
     * 获取网络号
     *
     * @return
     */
    public byte getNetworkNumber() {
        return NetworkNumber;
    }

    /**
     * 设置网络号
     *
     * @param networkNumber
     */
    public void setNetworkNumber(byte networkNumber) {
        NetworkNumber = networkNumber;
    }

    /**
     * 获取网络站号
     *
     * @return
     */
    public byte getNetworkStationNumber() {
        return NetworkStationNumber;
    }

    /**
     * 设置网络站号
     *
     * @param networkStationNumber
     */
    public void setNetworkStationNumber(byte networkStationNumber) {
        NetworkStationNumber = networkStationNumber;
    }

    /**
     * 分析地址的方法，允许派生类里进行重写操作
     * @param address 地址信息
     * @param length 读取的长度信息
     * @return 解析后的数据信息
     */
    protected OperateResultExOne<McAddressData> McAnalysisAddress( String address, short length ) {
        return McAddressData.ParseMelsecFrom(address, length);
    }

    public OperateResultExOne<byte[]> Read(String address, short length) {
        // 分析地址
        OperateResultExOne<McAddressData> addressResult = McAnalysisAddress( address, length );
        if (!addressResult.IsSuccess) return OperateResultExOne.CreateFailedResult( addressResult );

        ArrayList<Byte> bytesContent = new ArrayList<Byte>( );
        short alreadyFinished = 0;
        while (alreadyFinished < length)
        {
            short readLength = (short)Math.min( length - alreadyFinished, 450 );
            addressResult.Content.setLength(readLength);
            OperateResultExOne<byte[]> read = ReadAddressData( addressResult.Content );
            if (!read.IsSuccess) return read;

            Utilities.ArrayListAddArray(bytesContent,read.Content);
            alreadyFinished += readLength;

            // 字的话就是正常的偏移位置，如果是位的话，就转到位的数据
            if (addressResult.Content.getMcDataType().getDataType() == 0)
                addressResult.Content.setAddressStart(addressResult.Content.getAddressStart() + readLength);
            else
                addressResult.Content.setAddressStart(addressResult.Content.getAddressStart() + readLength * 16);
        }
        return OperateResultExOne.CreateSuccessResult( Utilities.getBytes(bytesContent) );
    }


    private OperateResultExOne<byte[]> ReadAddressData( McAddressData addressData ) {
        // 地址分析
        byte[] coreResult = MelsecHelper.BuildAsciiReadMcCoreCommand(addressData, false);

        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(PackMcCommand(coreResult, NetworkNumber, NetworkStationNumber));
        if (!read.IsSuccess) return OperateResultExOne.<byte[]>CreateFailedResult(read);

        // 错误代码验证
        OperateResult check = CheckResponseContent(read.Content);
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

        // 数据解析，需要传入是否使用位的参数
        return ExtractActualData(read.Content, false);
    }

    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        // 分析地址
        OperateResultExOne<McAddressData> addressResult = McAnalysisAddress( address, length );
        if (!addressResult.IsSuccess) return OperateResultExOne.CreateFailedResult( addressResult );

        // 地址分析
        byte[] coreResult = MelsecHelper.BuildAsciiReadMcCoreCommand( addressResult.Content, true );

        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer( PackMcCommand( coreResult, NetworkNumber, NetworkStationNumber ) );
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult( read );

        // 错误代码验证
        OperateResult check = CheckResponseContent( read.Content );
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult( check );

        // 数据解析，需要传入是否使用位的参数
        OperateResultExOne<byte[]> extract = ExtractActualData( read.Content, true );
        if(!extract.IsSuccess) return OperateResultExOne.CreateFailedResult( extract );

        // 转化bool数组
        boolean[] buffer = new boolean[length];
        for (int i = 0; i < length; i++){
            buffer[i] = extract.Content[i] == 0x01;
        }
        return OperateResultExOne.CreateSuccessResult( buffer );
    }

    public OperateResult Write(String address, byte[] value) {
        // 分析地址
        OperateResultExOne<McAddressData> addressResult = McAnalysisAddress(address, (short) 0);
        if (!addressResult.IsSuccess) return OperateResultExOne.CreateFailedResult(addressResult);

        // 地址分析
        byte[] coreResult = MelsecHelper.BuildAsciiWriteWordCoreCommand(addressResult.Content, value);

        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(PackMcCommand(coreResult, NetworkNumber, NetworkStationNumber));
        if (!read.IsSuccess) return read;

        // 错误码验证
        OperateResult check = CheckResponseContent(read.Content);
        if (!check.IsSuccess) return check;

        // 写入成功
        return OperateResult.CreateSuccessResult();
    }

    public OperateResult Write(String address, boolean[] values) {
        // 分析地址
        OperateResultExOne<McAddressData> addressResult = McAnalysisAddress(address, (short) 0);
        if (!addressResult.IsSuccess) return addressResult;

        // 解析指令
        byte[] coreResult = MelsecHelper.BuildAsciiWriteBitCoreCommand(addressResult.Content, values);

        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(PackMcCommand(coreResult, NetworkNumber, NetworkStationNumber));
        if (!read.IsSuccess) return read;

        // 错误码验证
        OperateResult check = CheckResponseContent(read.Content);
        if (!check.IsSuccess) return check;

        // 写入成功
        return OperateResult.CreateSuccessResult();
    }

    /**
     * 返回表示当前对象的字符串
     * @return 字符串
     */
    @Override
    public String toString() {
        return "MelsecMcNet";
    }

    /**
     * 将MC协议的核心报文打包成一个可以直接对PLC进行发送的原始报文
     * @param mcCore MC协议的核心报文
     * @param networkNumber 网络号
     * @param networkStationNumber 网络站号
     * @return 原始报文信息
     */
    public static byte[] PackMcCommand( byte[] mcCore, byte networkNumber, byte networkStationNumber )
    {
        byte[] plcCommand = new byte[22 + mcCore.length];
        plcCommand[ 0] = 0x35;                                                                        // 副标题
        plcCommand[ 1] = 0x30;
        plcCommand[ 2] = 0x30;
        plcCommand[ 3] = 0x30;
        plcCommand[ 4] = MelsecHelper.BuildBytesFromData( networkNumber )[0];                         // 网络号
        plcCommand[ 5] = MelsecHelper.BuildBytesFromData( networkNumber )[1];
        plcCommand[ 6] = 0x46;                                                                        // PLC编号
        plcCommand[ 7] = 0x46;
        plcCommand[ 8] = 0x30;                                                                        // 目标模块IO编号
        plcCommand[ 9] = 0x33;
        plcCommand[10] = 0x46;
        plcCommand[11] = 0x46;
        plcCommand[12] = MelsecHelper.BuildBytesFromData( networkStationNumber )[0];                  // 目标模块站号
        plcCommand[13] = MelsecHelper.BuildBytesFromData( networkStationNumber )[1];
        plcCommand[14] = MelsecHelper.BuildBytesFromData( (short)(plcCommand.length - 18) )[0];     // 请求数据长度
        plcCommand[15] = MelsecHelper.BuildBytesFromData( (short)(plcCommand.length - 18) )[1];
        plcCommand[16] = MelsecHelper.BuildBytesFromData( (short)(plcCommand.length - 18) )[2];
        plcCommand[17] = MelsecHelper.BuildBytesFromData( (short)(plcCommand.length - 18) )[3];
        plcCommand[18] = 0x30;                                                                        // CPU监视定时器
        plcCommand[19] = 0x30;
        plcCommand[20] = 0x31;
        plcCommand[21] = 0x30;
        System.arraycopy(mcCore, 0, plcCommand, 22, mcCore.length);

        return plcCommand;
    }

    /**
     * 从PLC反馈的数据中提取出实际的数据内容，需要传入反馈数据，是否位读取
     * @param response 反馈的数据内容
     * @param isBit 是否位读取
     * @return 解析后的结果对象
     */
    public static OperateResultExOne<byte[]> ExtractActualData( byte[] response, boolean isBit )
    {
        if (isBit)
        {
            // 位读取
            byte[] Content = new byte[response.length - 22];
            for (int i = 22; i < response.length; i++)
            {
                if (response[i] == 0x30)
                {
                    Content[i - 22] = 0x00;
                }
                else
                {
                    Content[i - 22] = 0x01;
                }
            }

            return OperateResultExOne.CreateSuccessResult( Content );
        }
        else
        {
            // 字读取
            byte[] Content = new byte[(response.length - 22) / 2];
            for (int i = 0; i < Content.length / 2; i++)
            {
                int tmp = Integer.parseInt( Utilities.getString( response, i * 4 + 22, 4 ,"ASCII"), 16 );
                byte[] buffer = Utilities.getBytes(tmp);

                Content[i*2+0] = buffer[0];
                Content[i*2+1] = buffer[1];
            }

            return OperateResultExOne.CreateSuccessResult( Content );
        }
    }


    /**
     * 检查反馈的内容是否正确的
     * @param content MC的反馈的内容
     * @return 是否正确
     */
    public static OperateResult CheckResponseContent( byte[] content ) {
        int errorCode = Integer.parseInt(new String(SoftBasic.BytesArraySelectMiddle(content, 18, 4), StandardCharsets.US_ASCII), 16);
        if (errorCode != 0) return new OperateResult(errorCode, MelsecHelper.GetErrorDescription(errorCode));

        return OperateResult.CreateSuccessResult();
    }

}
