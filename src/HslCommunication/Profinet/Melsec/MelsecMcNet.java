package HslCommunication.Profinet.Melsec;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Address.McAddressData;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.MelsecQnA3EBinaryMessage;
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
import java.util.regex.Pattern;

/**
 * 三菱PLC通讯类，采用Qna兼容3E帧协议实现，需要在PLC侧先的以太网模块先进行配置，必须为二进制通讯<br />
 * Mitsubishi PLC communication class is implemented using Qna compatible 3E frame protocol.
 * The Ethernet module on the PLC side needs to be configured first. It must be binary communication.
 */
public class MelsecMcNet extends NetworkDeviceBase {


    /**
     * 实例化三菱的Qna兼容3E帧协议的通讯对象<br />
     * Instantiate the communication object of Mitsubishi's Qna compatible 3E frame protocol
     */
    public MelsecMcNet() {
        WordLength = 1;
        setByteTransform(new RegularByteTransform());
    }


    /**
     * 指定ip地址和端口号来实例化一个默认的对象<br />
     * Specify the IP address and port number to instantiate a default object
     * @param ipAddress PLCd的Ip地址
     * @param port      PLC的端口
     */
    public MelsecMcNet(String ipAddress, int port) {
        WordLength = 1;
        super.setIpAddress(ipAddress);
        super.setPort(port);
        setByteTransform(new RegularByteTransform());
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new MelsecQnA3EBinaryMessage();
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

    /**
     * 分析地址的方法，允许派生类里进行重写操作
     * @param address 地址信息
     * @return 解析后的数据信息
     */
    protected OperateResultExOne<McAddressData> McAnalysisAddress( String address, short length) {
        return McAddressData.ParseMelsecFrom(address, length);
    }

    public OperateResultExOne<byte[]> Read(String address, short length) {
        if (address.startsWith("s=") || address.startsWith("S=")) {
            return ReadTags(address.substring(2), length);
        } else if (Pattern.matches("ext=[0-9]+;", address)) {
            String extStr = Pattern.compile("ext=[0-9]+;").matcher(address).group();
            short ext = Short.parseShort(Pattern.compile("[0-9]+").matcher(extStr).group());
            return ReadExtend(ext, address.substring(extStr.length()), length);
        } else if (Pattern.matches("mem=", address)) {
            return ReadMemory(address.substring(4), length);
        } else {
            // 分析地址
            OperateResultExOne<McAddressData> addressResult = McAnalysisAddress(address, length);
            if (!addressResult.IsSuccess) return OperateResultExOne.<byte[]>CreateFailedResult(addressResult);

            ArrayList<Byte> bytesContent = new ArrayList<Byte>();
            int alreadyFinished = 0;
            while (alreadyFinished < length) {
                int readLength = Math.min(length - alreadyFinished, 900);
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
    }

    private OperateResultExOne<byte[]> ReadAddressData( McAddressData addressData ) {
        byte[] coreResult = MelsecHelper.BuildReadMcCoreCommand(addressData, false);

        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(PackMcCommand(coreResult, this.NetworkNumber, this.NetworkStationNumber));
        if (!read.IsSuccess) return OperateResultExOne.<byte[]>CreateFailedResult(read);

        // 错误代码验证
        OperateResult check = CheckResponseContent(read.Content);
        if (!check.IsSuccess) return OperateResultExOne.<byte[]>CreateFailedResult(check);

        // 数据解析，需要传入是否使用位的参数
        return ExtractActualData(SoftBasic.BytesArrayRemoveBegin(read.Content, 11), false);
    }

    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        // 获取指令
        OperateResultExOne<McAddressData> addressResult = McAnalysisAddress(address, length);
        if (!addressResult.IsSuccess) return OperateResultExOne.CreateFailedResult(addressResult);

        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(PackMcCommand(MelsecHelper.BuildReadMcCoreCommand(addressResult.Content, true), this.NetworkNumber, this.NetworkStationNumber));
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        // 错误代码验证
        OperateResult check = CheckResponseContent(read.Content);
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

        // 数据解析，需要传入是否使用位的参数
        OperateResultExOne<byte[]> extract = ExtractActualData(SoftBasic.BytesArrayRemoveBegin(read.Content, 11), true);
        if (!extract.IsSuccess) return OperateResultExOne.CreateFailedResult(extract);

        // 转化bool数组
        boolean[] result = new boolean[length];
        for (int i = 0; i < result.length; i++) {
            if (extract.Content[i] == 0x01) result[i] = true;
        }
        return OperateResultExOne.CreateSuccessResult(result);
    }

    public OperateResult Write(String address, byte[] value) {
        OperateResultExOne<McAddressData> addressResult = McAnalysisAddress( address, (short) 0 );
        if (!addressResult.IsSuccess) return OperateResultExOne.<byte[]>CreateFailedResult( addressResult );

        return WriteAddressData( addressResult.Content, value );
    }

    private OperateResult WriteAddressData( McAddressData addressData, byte[] value ) {
        // 创建核心报文
        byte[] coreResult = MelsecHelper.BuildWriteWordCoreCommand(addressData, value);

        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(PackMcCommand(coreResult, NetworkNumber, NetworkStationNumber));
        if (!read.IsSuccess) return read;

        // 错误码校验
        OperateResult check = CheckResponseContent(read.Content);
        if (!check.IsSuccess) return OperateResultExOne.<byte[]>CreateFailedResult(check);

        // 成功
        return OperateResult.CreateSuccessResult();
    }

    public OperateResult Write(String address, boolean[] values) {
        // 分析地址
        OperateResultExOne<McAddressData> addressResult = McAnalysisAddress(address, (short) 0);
        if (!addressResult.IsSuccess) return addressResult;

        byte[] coreResult = MelsecHelper.BuildWriteBitCoreCommand(addressResult.Content, values);

        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(PackMcCommand(coreResult, NetworkNumber, NetworkStationNumber));
        if (!read.IsSuccess) return read;

        // 错误码校验
        OperateResult check = CheckResponseContent(read.Content);
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
    public OperateResultExOne<byte[]> ReadRandom( String[] address ) {
        McAddressData[] mcAddressDatas = new McAddressData[address.length];
        for (int i = 0; i < address.length; i++) {
            OperateResultExOne<McAddressData> addressResult = McAddressData.ParseMelsecFrom(address[i], 1);
            if (!addressResult.IsSuccess) return OperateResultExOne.<byte[]>CreateFailedResult(addressResult);

            mcAddressDatas[i] = addressResult.Content;
        }

        byte[] coreResult = MelsecHelper.BuildReadRandomWordCommand(mcAddressDatas);

        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(PackMcCommand(coreResult, this.NetworkNumber, this.NetworkStationNumber));
        if (!read.IsSuccess) return OperateResultExOne.<byte[]>CreateFailedResult(read);

        // 错误代码验证
        OperateResult check = CheckResponseContent(read.Content);
        if (!check.IsSuccess) return OperateResultExOne.<byte[]>CreateFailedResult(check);

        // 数据解析，需要传入是否使用位的参数
        return ExtractActualData(SoftBasic.BytesArrayRemoveBegin(read.Content, 11), false);
    }

    /**
     * 随机读取PLC的数据信息，可以跨地址，跨类型组合，每个地址是任意的长度。收到结果后，需要自行解析数据，目前只支持字地址，比如D区，W区，R区，不支持X，Y，M，B，L等等<br />
     * Read the data information of the PLC randomly. It can be combined across addresses and types. Each address is of any length. After receiving the results,
     * you need to parse the data yourself. Currently, only word addresses are supported, such as D area, W area, R area. X, Y, M, B, L, etc
     * @param address 所有的地址的集合
     * @param length 每个地址的长度信息
     * @return 结果
     */
    public OperateResultExOne<byte[]> ReadRandom( String[] address, short[] length ) {
        if (length.length != address.length)
            return new OperateResultExOne<byte[]>(StringResources.Language.TwoParametersLengthIsNotSame());

        McAddressData[] mcAddressDatas = new McAddressData[address.length];
        for (int i = 0; i < address.length; i++) {
            OperateResultExOne<McAddressData> addressResult = McAddressData.ParseMelsecFrom(address[i], length[i]);
            if (!addressResult.IsSuccess) return OperateResultExOne.<byte[]>CreateFailedResult(addressResult);

            mcAddressDatas[i] = addressResult.Content;
        }

        byte[] coreResult = MelsecHelper.BuildReadRandomCommand(mcAddressDatas);

        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(PackMcCommand(coreResult, this.NetworkNumber, this.NetworkStationNumber));
        if (!read.IsSuccess) return OperateResultExOne.<byte[]>CreateFailedResult(read);

        // 错误代码验证
        OperateResult check = CheckResponseContent(read.Content);
        if (!check.IsSuccess) return OperateResultExOne.<byte[]>CreateFailedResult(check);

        // 数据解析，需要传入是否使用位的参数
        return ExtractActualData(SoftBasic.BytesArrayRemoveBegin(read.Content, 11), false);
    }

    /**
     * 随机读取PLC的数据信息，可以跨地址，跨类型组合，但是每个地址只能读取一个word，也就是2个字节的内容。收到结果后，自动转换为了short类型的数组<br />
     * Randomly read PLC data information, which can be combined across addresses and types, but each address can only read one word,
     * which is the content of 2 bytes. After receiving the result, it is automatically converted to an array of type short.
     * @param address 所有的地址的集合
     * @return 结果
     */
    public OperateResultExOne<short[]> ReadRandomInt16( String[] address ) {
        OperateResultExOne<byte[]> read = ReadRandom(address);
        if (!read.IsSuccess) return OperateResultExOne.<short[]>CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(getByteTransform().TransInt16(read.Content, 0, address.length));
    }

    /**
     * 读取PLC的标签信息，需要传入标签的名称，读取的字长度，标签举例：A; label[1]; bbb[10,10,10]<br />
     * To read the label information of the PLC, you need to pass in the name of the label,
     * the length of the word read, and an example of the label: A; label [1]; bbb [10,10,10]
     * @param tag 标签名
     * @param length 读取长度
     * @return 是否成功
     */
    public OperateResultExOne<byte[]> ReadTags( String tag, short length ) {
        return ReadTags( new String[] { tag }, new short[] { length } );
    }

    /**
     * 读取PLC的标签信息，需要传入标签的名称，读取的字长度，标签举例：A; label[1]; bbb[10,10,10]<br />
     * To read the label information of the PLC, you need to pass in the name of the label,
     * the length of the word read, and an example of the label: A; label [1]; bbb [10,10,10]
     * @param tags 标签名
     * @param length 读取长度
     * @return 是否成功
     */
    public OperateResultExOne<byte[]> ReadTags(String[] tags, short[] length ) {
        byte[] coreResult = new byte[0];
        try {
            coreResult = MelsecHelper.BuildReadTag(tags, length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(PackMcCommand(coreResult, this.NetworkNumber, this.NetworkStationNumber));
        if (!read.IsSuccess) return OperateResultExOne.<byte[]>CreateFailedResult(read);

        // 错误代码验证
        OperateResult check = CheckResponseContent(read.Content);
        if (!check.IsSuccess) return OperateResultExOne.<byte[]>CreateFailedResult(check);

        // 数据初级解析，需要传入是否使用位的参数
        OperateResultExOne<byte[]> extract = ExtractActualData(SoftBasic.BytesArrayRemoveBegin(read.Content, 11), false);
        if (!extract.IsSuccess) return extract;

        return MelsecHelper.ExtraTagData(extract.Content);
    }

    /**
     * 读取扩展的数据信息，需要在原有的地址，长度信息之外，输入扩展值信息<br />
     * To read the extended data information, you need to enter the extended value information in addition to the original address and length information
     * @param extend 扩展信息
     * @param address 地址
     * @param length 数据长度
     * @return 返回结果
     */
    public OperateResultExOne<byte[]> ReadExtend( short extend, String address, short length ) {
        OperateResultExOne<McAddressData> addressResult = McAnalysisAddress(address, length);
        if (!addressResult.IsSuccess) return OperateResultExOne.<byte[]>CreateFailedResult(addressResult);

        byte[] coreResult = MelsecHelper.BuildReadMcCoreExtendCommand(addressResult.Content, extend, false);

        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(PackMcCommand(coreResult, this.NetworkNumber, this.NetworkStationNumber));
        if (!read.IsSuccess) return OperateResultExOne.<byte[]>CreateFailedResult(read);

        // 错误代码验证
        OperateResult check = CheckResponseContent(read.Content);
        if (!check.IsSuccess) return OperateResultExOne.<byte[]>CreateFailedResult(check);

        // 数据初级解析，需要传入是否使用位的参数
        OperateResultExOne<byte[]> extract = ExtractActualData(SoftBasic.BytesArrayRemoveBegin(read.Content, 11), false);
        if (!extract.IsSuccess) return extract;

        return MelsecHelper.ExtraTagData(extract.Content);
    }

    /**
     * 读取缓冲寄存器的数据信息，地址直接为偏移地址<br />
     * Read the data information of the buffer register, the address is directly the offset address
     * @param address 偏移地址
     * @param length 读取长度
     * @return 读取的内容
     */
    public OperateResultExOne<byte[]> ReadMemory(String address, short length ) {
        OperateResultExOne<byte[]> coreResult = MelsecHelper.BuildReadMemoryCommand(address, length);
        if (!coreResult.IsSuccess) return coreResult;

        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(PackMcCommand(coreResult.Content, this.NetworkNumber, this.NetworkStationNumber));
        if (!read.IsSuccess) return OperateResultExOne.<byte[]>CreateFailedResult(read);

        // 错误代码验证
        OperateResult check = CheckResponseContent(read.Content);
        if (!check.IsSuccess) return OperateResultExOne.<byte[]>CreateFailedResult(check);

        // 数据初级解析，需要传入是否使用位的参数
        return ExtractActualData(SoftBasic.BytesArrayRemoveBegin(read.Content, 11), false);
    }

    /**
     * 远程Run操作<br />
     * Remote Run Operation
     * @return 是否成功
     */
    public OperateResult RemoteRun( ) {
        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(PackMcCommand(new byte[]{0x01, 0x10, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00}, NetworkNumber, NetworkStationNumber));
        if (!read.IsSuccess) return read;

        // 错误码校验
        OperateResult check = CheckResponseContent(read.Content);
        if (!check.IsSuccess) return check;

        // 成功
        return OperateResult.CreateSuccessResult();
    }

    /**
     * 远程Stop操作<br />
     * Remote Stop operation
     * @return 是否成功
     */
    public OperateResult RemoteStop( ) {
        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(PackMcCommand(new byte[]{0x02, 0x10, 0x00, 0x00, 0x01, 0x00}, NetworkNumber, NetworkStationNumber));
        if (!read.IsSuccess) return read;

        // 错误码校验
        OperateResult check = CheckResponseContent(read.Content);
        if (!check.IsSuccess) return check;

        // 成功
        return OperateResult.CreateSuccessResult();
    }

    /**
     * 远程Reset操作<br />
     * Remote Reset Operation
     * @return 是否成功
     */
    public OperateResult RemoteReset() {
        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(PackMcCommand(new byte[]{0x06, 0x10, 0x00, 0x00, 0x01, 0x00}, NetworkNumber, NetworkStationNumber));
        if (!read.IsSuccess) return read;

        // 错误码校验
        OperateResult check = CheckResponseContent(read.Content);
        if (!check.IsSuccess) return check;

        // 成功
        return OperateResult.CreateSuccessResult();
    }


    /**
     * 读取PLC的型号信息，例如 Q02HCPU<br />
     * Read PLC model information, such as Q02HCPU
     * @return 返回型号的结果对象
     */
    public OperateResultExOne<String> ReadPlcType( ) {
        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(PackMcCommand(new byte[]{0x01, 0x01, 0x00, 0x00}, NetworkNumber, NetworkStationNumber));
        if (!read.IsSuccess) return OperateResultExOne.<String>CreateFailedResult(read);

        // 错误码校验
        OperateResult check = CheckResponseContent(read.Content);
        if (!check.IsSuccess) return OperateResultExOne.<String>CreateFailedResult(check);

        // 成功
        return OperateResultExOne.CreateSuccessResult(new String(SoftBasic.BytesArraySelectMiddle(read.Content, 11, 16), StandardCharsets.US_ASCII).trim());
    }

    /**
     * LED 熄灭 出错代码初始化<br />
     * LED off Error code initialization
     * @return 是否成功
     */
    public OperateResult ErrorStateReset( ) {
        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(PackMcCommand(new byte[]{0x17, 0x16, 0x00, 0x00}, NetworkNumber, NetworkStationNumber));
        if (!read.IsSuccess) return read;

        // 错误码校验
        OperateResult check = CheckResponseContent(read.Content);
        if (!check.IsSuccess) return check;

        // 成功
        return OperateResult.CreateSuccessResult();
    }

    /**
     * 获取当前对象的字符串标识形式
     * @return 字符串信息
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
    public static byte[] PackMcCommand(byte[] mcCore, byte networkNumber, byte networkStationNumber)
    {
        byte[] _PLCCommand = new byte[11 + mcCore.length];
        _PLCCommand[0] = 0x50;                                               // 副标题
        _PLCCommand[1] = 0x00;
        _PLCCommand[2] = networkNumber;                                      // 网络号
        _PLCCommand[3] = (byte) 0xFF;                                               // PLC编号
        _PLCCommand[4] = (byte) 0xFF;                                               // 目标模块IO编号
        _PLCCommand[5] = 0x03;
        _PLCCommand[6] = networkStationNumber;                               // 目标模块站号
        _PLCCommand[7] = (byte)((_PLCCommand.length - 9) % 256);             // 请求数据长度
        _PLCCommand[8] = (byte)((_PLCCommand.length - 9) / 256);
        _PLCCommand[9] = 0x0A;                                               // CPU监视定时器
        _PLCCommand[10] = 0x00;
        System.arraycopy(mcCore, 0, _PLCCommand, 11, mcCore.length);

        return _PLCCommand;
    }

    /**
     * 从PLC反馈的数据中提取出实际的数据内容，需要传入反馈数据，是否位读取
     * @param response 反馈的数据内容
     * @param isBit 是否位读取
     * @return 解析后的结果对象
     */
    public static OperateResultExOne<byte[]> ExtractActualData( byte[] response, boolean isBit ) {
        if (isBit) {
            // 位读取
            byte[] Content = new byte[response.length * 2];
            for (int i = 0; i < response.length; i++) {
                if ((response[i] & 0x10) == 0x10) {
                    Content[i * 2 + 0] = 0x01;
                }

                if ((response[i] & 0x01) == 0x01) {
                    Content[i * 2 + 1] = 0x01;
                }
            }

            return OperateResultExOne.CreateSuccessResult(Content);
        } else {
            // 字读取
            byte[] Content = new byte[response.length];
            System.arraycopy(response, 0, Content, 0, Content.length);

            return OperateResultExOne.CreateSuccessResult(Content);
        }
    }

    /**
     * 检查从MC返回的数据是否是合法的。
     * @param content 数据内容
     * @return 是否合法
     */
    public static OperateResult CheckResponseContent( byte[] content )
    {
        int errorCode = Utilities.getUShort( content, 9 );
        if (errorCode != 0) return new OperateResult( errorCode, MelsecHelper.GetErrorDescription( errorCode ) );

        return OperateResult.CreateSuccessResult( );
    }
}
