package HslCommunication.Profinet.Melsec;

import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.MelsecA1EBinaryMessage;
import HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import HslCommunication.Core.Transfer.RegularByteTransform;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.StringResources;
import HslCommunication.Utilities;

/**
 * 三菱PLC通讯协议，采用A兼容1E帧协议实现，使用二进制码通讯，请根据实际型号来进行选取<br />
 * Mitsubishi PLC communication protocol, implemented using A compatible 1E frame protocol, using binary code communication, please choose according to the actual model
 */
public class MelsecA1ENet extends NetworkDeviceBase {
    /**
     * 实例化三菱的A兼容1E帧协议的通讯对象
     */
    public MelsecA1ENet() {
        WordLength = 1;
        setByteTransform(new RegularByteTransform());
    }

    /**
     * 实例化一个三菱的A兼容1E帧协议的通讯对象
     *
     * @param ipAddress PLCd的Ip地址
     * @param port      PLC的端口
     */
    public MelsecA1ENet(String ipAddress, int port) {
        WordLength = 1;
        super.setIpAddress(ipAddress);
        super.setPort(port);
        setByteTransform(new RegularByteTransform());
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new MelsecA1EBinaryMessage( );
    }

    private byte PLCNumber = (byte) (0xFF);                       // PLC编号

    /**
     * 获取PLC编号
     *
     * @return PLC编号
     */
    public byte getPLCNumber() {
        return PLCNumber;
    }

    /**
     * 设置PLC编号
     *
     * @param plcNumber PLC编号
     */
    public void setPLCNumber(byte plcNumber) {
        PLCNumber = plcNumber;
    }
    
    public OperateResultExOne<byte[]> Read(String address, short length) {
        // 获取指令
        OperateResultExOne<byte[]> command = BuildReadCommand(address, length, false, PLCNumber);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        // 错误代码验证
        OperateResult check = CheckResponseLegal(read.Content);
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

        // 数据解析，需要传入是否使用位的参数
        return ExtractActualData(read.Content, false);
    }


    /**
     * 批量读取 {@link Boolean} 数组信息，需要指定地址和长度，地址示例M100，S100，B1A，如果是X,Y, X017就是8进制地址，Y10就是16进制地址。<br />
     * Batch read {@link Boolean} array information, need to specify the address and length, return <see cref="bool"/> array.
     * Examples of addresses M100, S100, B1A, if it is X, Y, X017 is an octal address, Y10 is a hexadecimal address.
     * @param address 起始地址
     * @param length 读取的长度
     * @return 带成功标志的结果数据对象
     */
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        // 获取指令
        OperateResultExOne<byte[]> command = BuildReadCommand(address, length, true, PLCNumber);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        // 错误代码验证
        OperateResult check = CheckResponseLegal(read.Content);
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

        // 数据解析，需要传入是否使用位的参数
        OperateResultExOne<byte[]> extract = ExtractActualData(read.Content, true);
        if (!extract.IsSuccess) return OperateResultExOne.CreateFailedResult(extract);

        // 转化bool数组
        boolean[] result = new boolean[length];
        for (int i = 0; i < result.length; i++) {
            if (read.Content[i] == 0x01) result[i] = true;
        }
        return OperateResultExOne.CreateSuccessResult(result);
    }

    public OperateResult Write(String address, byte[] value) {
        // 解析指令
        OperateResultExOne<byte[]> command = BuildWriteWordCommand( address, value, PLCNumber );
        if (!command.IsSuccess) return command;

        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer( command.Content );
        if (!read.IsSuccess) return read;

        // 错误码校验 (在A兼容1E协议中，结束代码后面紧跟的是异常信息的代码)
        OperateResult check = CheckResponseLegal( read.Content );
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult( check );

        // 成功
        return OperateResult.CreateSuccessResult( );
    }

    /**
     * 批量写入 {@link Boolean} 数组数据，返回是否成功，地址示例M100，S100，B1A，如果是X,Y, X017就是8进制地址，Y10就是16进制地址。<br />
     * Batch write {@link Boolean} array data, return whether the write was successful.
     * Examples of addresses M100, S100, B1A, if it is X, Y, X017 is an octal address, Y10 is a hexadecimal address.
     * @param address 起始地址
     * @param values 写入值
     * @return 带有成功标识的结果类对象
     */
    public OperateResult Write(String address, boolean[] values) {
        // 解析指令
        OperateResultExOne<byte[]> command = BuildWriteBoolCommand( address, values, PLCNumber );
        if (!command.IsSuccess) return command;

        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer( command.Content );
        if (!read.IsSuccess) return read;

        // 错误码校验 (在A兼容1E协议中，结束代码后面紧跟的是异常信息的代码)
        return CheckResponseLegal( read.Content );
    }

    public String toString() {
        return "MelsecA1ENet";
    }

    /**
     * 根据类型地址长度确认需要读取的指令头
     * @param address 起始地址
     * @param length 长度
     * @param isBit 指示是否按照位成批的读出
     * @param plcNumber PLC编号
     * @return 带有成功标志的指令数据
     */
    public static OperateResultExOne<byte[]> BuildReadCommand(String address, short length, boolean isBit, byte plcNumber )
    {
        OperateResultExTwo<MelsecA1EDataType, Integer> analysis = MelsecHelper.McA1EAnalysisAddress(address);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        // 默认信息----注意：高低字节交错
        // byte subtitle = analysis.Content1.DataType == 0x01 ? (byte)0x00 : (byte)0x01;
        byte subtitle = isBit ? (byte)0x00 : (byte)0x01;

        byte[] _PLCCommand = new byte[12];
        _PLCCommand[ 0] = subtitle;                                           // 副标题
        _PLCCommand[ 1] = plcNumber;                                          // PLC号
        _PLCCommand[ 2] = 0x0A;                                               // CPU监视定时器（L）这里设置为0x00,0x0A，等待CPU返回的时间为10*250ms=2.5秒
        _PLCCommand[ 3] = 0x00;                                               // CPU监视定时器（H）
        _PLCCommand[ 4] = Utilities.getBytes( analysis.Content2 )[0];         // 起始软元件（开始读取的地址）
        _PLCCommand[ 5] = Utilities.getBytes( analysis.Content2 )[1];
        _PLCCommand[ 6] = Utilities.getBytes( analysis.Content2 )[2];
        _PLCCommand[ 7] = Utilities.getBytes( analysis.Content2 )[3];
        _PLCCommand[ 8] = analysis.Content1.getDataCode()[1];                  // 软元件代码（L）
        _PLCCommand[ 9] = analysis.Content1.getDataCode()[0];                  // 软元件代码（H）
        _PLCCommand[10] = (byte)(length % 256);                                // 软元件点数
        _PLCCommand[11] = 0x00;

        return OperateResultExOne.CreateSuccessResult(_PLCCommand);
    }

    /**
     * 根据类型地址以及需要写入的数据来生成指令头
     * @param address 起始地址
     * @param value 数据值
     * @param plcNumber PLC编号
     * @return 带有成功标志的指令数据
     */
    public static OperateResultExOne<byte[]> BuildWriteWordCommand( String address, byte[] value, byte plcNumber )
    {
        OperateResultExTwo<MelsecA1EDataType, Integer> analysis = MelsecHelper.McA1EAnalysisAddress( address );
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult( analysis );

        byte[] _PLCCommand = new byte[12 + value.length];
        _PLCCommand[ 0] = 03;                                                 // 副标题，字单位成批写入
        _PLCCommand[ 1] = plcNumber;                                          // PLC号
        _PLCCommand[ 2] = 0x0A;                                               // CPU监视定时器（L）这里设置为0x00,0x0A，等待CPU返回的时间为10*250ms=2.5秒
        _PLCCommand[ 3] = 0x00;                                               // CPU监视定时器（H）
        _PLCCommand[ 4] = Utilities.getBytes( analysis.Content2 )[0];         // 起始软元件（开始读取的地址）
        _PLCCommand[ 5] = Utilities.getBytes( analysis.Content2 )[1];
        _PLCCommand[ 6] = Utilities.getBytes( analysis.Content2 )[2];
        _PLCCommand[ 7] = Utilities.getBytes( analysis.Content2 )[3];
        _PLCCommand[ 8] = analysis.Content1.getDataCode()[1];                   // 软元件代码（L）
        _PLCCommand[ 9] = analysis.Content1.getDataCode()[0];                   // 软元件代码（H）
        _PLCCommand[10] = Utilities.getBytes( value.length / 2 )[0];       // 软元件点数
        _PLCCommand[11] = Utilities.getBytes( value.length / 2 )[1];
        System.arraycopy( value, 0, _PLCCommand, 12, value.length );                   // 将具体的要写入的数据附加到写入命令后面
        return OperateResultExOne.CreateSuccessResult( _PLCCommand );
    }


    /**
     * 根据类型地址以及需要写入的数据来生成指令头
     * @param address 起始地址
     * @param value 数据值
     * @param plcNumber PLC编号
     * @return 带有成功标志的指令数据
     */
    public static OperateResultExOne<byte[]> BuildWriteBoolCommand( String address, boolean[] value, byte plcNumber )
    {
        OperateResultExTwo<MelsecA1EDataType, Integer>  analysis = MelsecHelper.McA1EAnalysisAddress( address );
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult( analysis );

        byte[] buffer = MelsecHelper.TransBoolArrayToByteData( value );
        byte[] _PLCCommand = new byte[12 + buffer.length];
        _PLCCommand[ 0] = 02;                                                 // 副标题，位单位成批写入
        _PLCCommand[ 1] = plcNumber;                                          // PLC号
        _PLCCommand[ 2] = 0x0A;                                               // CPU监视定时器（L）这里设置为0x00,0x0A，等待CPU返回的时间为10*250ms=2.5秒
        _PLCCommand[ 3] = 0x00;                                               // CPU监视定时器（H）
        _PLCCommand[ 4] = Utilities.getBytes( analysis.Content2 )[0];      // 起始软元件（开始读取的地址）
        _PLCCommand[ 5] = Utilities.getBytes( analysis.Content2 )[1];
        _PLCCommand[ 6] = Utilities.getBytes( analysis.Content2 )[2];
        _PLCCommand[ 7] = Utilities.getBytes( analysis.Content2 )[3];
        _PLCCommand[ 8] = analysis.Content1.getDataCode()[1];                      // 软元件代码（L）
        _PLCCommand[ 9] = analysis.Content1.getDataCode()[0];                      // 软元件代码（H）
        _PLCCommand[10] = Utilities.getBytes( value.length )[0];           // 软元件点数
        _PLCCommand[11] = Utilities.getBytes( value.length )[1];
        System.arraycopy( buffer, 0, _PLCCommand, 12, buffer.length );              // 将具体的要写入的数据附加到写入命令后面
        return OperateResultExOne.CreateSuccessResult( _PLCCommand );
    }

    /**
     * 检测反馈的消息是否合法
     * @param response 接收的报文
     * @return 是否成功
     */
    public static OperateResult CheckResponseLegal( byte[] response ) {
        if (response.length < 2) return new OperateResult(StringResources.Language.ReceiveDataLengthTooShort());
        if (response[1] == 0) return OperateResult.CreateSuccessResult();
        if (response[1] == 0x5B)
            return new OperateResult(response[2], StringResources.Language.MelsecPleaseReferToManualDocument());
        return new OperateResult(response[1], StringResources.Language.MelsecPleaseReferToManualDocument());
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
            byte[] Content = new byte[(response.length - 2) * 2];
            for (int i = 2; i < response.length; i++)
            {
                if ((response[i] & 0x10) == 0x10)
                {
                    Content[(i - 2) * 2 + 0] = 0x01;
                }

                if ((response[i] & 0x01) == 0x01)
                {
                    Content[(i - 2) * 2 + 1] = 0x01;
                }
            }

            return OperateResultExOne.CreateSuccessResult( Content );
        }
        else
        {
            // 字读取
            byte[] Content = new byte[response.length - 2];
            System.arraycopy(response,2,Content,0,Content.length);

            return OperateResultExOne.CreateSuccessResult( Content );
        }
    }
}
