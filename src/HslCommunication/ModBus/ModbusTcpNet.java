package HslCommunication.ModBus;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.BasicFramework.SoftIncrementCount;
import HslCommunication.Core.Address.ModbusAddress;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.ModbusTcpMessage;
import HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import HslCommunication.Core.Transfer.ByteTransformHelper;
import HslCommunication.Core.Transfer.DataFormat;
import HslCommunication.Core.Transfer.ReverseWordTransform;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.StringResources;
import HslCommunication.Utilities;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


/**
 * Modbus-Tcp协议的客户端通讯类，方便的和服务器进行数据交互，支持标准的功能码，也支持扩展的功能码实现，地址采用富文本的形式，详细见备注说明<br />
 * The client communication class of Modbus-Tcp protocol is convenient for data interaction with the server. It supports standard function codes and also supports extended function codes.
 * The address is in rich text. For details, see the remarks.
 */
public class ModbusTcpNet extends NetworkDeviceBase {
    /**
     * 实例化一个Modbus-Tcp协议的客户端对象<br />
     * Instantiate a client object of the Modbus-Tcp protocol
     */
    public ModbusTcpNet() {
        this.softIncrementCount = new SoftIncrementCount(65535, 0);
        this.WordLength = 1;
        this.station = 1;
        setByteTransform(new ReverseWordTransform());
    }


    /**
     * 指定服务器地址，端口号，客户端自己的站号来初始化<br />
     * Specify the server address, port number, and client's own station number to initialize
     * @param ipAddress 服务器的Ip地址
     * @param port      服务器的端口号
     * @param station   客户端自身的站号，可以在读取的时候动态配置
     */
    public ModbusTcpNet(String ipAddress, int port, byte station) {
        this.softIncrementCount = new SoftIncrementCount(65535, 0);
        setIpAddress(ipAddress);
        setPort(port);
        this.WordLength = 1;
        this.station = station;
        setByteTransform(new ReverseWordTransform());
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new ModbusTcpMessage();
    }

    private byte station = 0x01;                                // 本客户端的站号
    private SoftIncrementCount softIncrementCount;              // 自增消息的对象
    private boolean isAddressStartWithZero = true;                 // 线圈值的地址值是否从零开始


    /**
     * 获取起始地址是否从0开始
     *
     * @return bool值
     */
    public boolean getAddressStartWithZero() {
        return isAddressStartWithZero;
    }

    /**
     * 设置起始地址是否从0开始
     *
     * @param addressStartWithZero true代表从0开始，false代表从1开始
     */
    public void setAddressStartWithZero(boolean addressStartWithZero) {
        this.isAddressStartWithZero = addressStartWithZero;
    }

    /**
     * 获取站号
     *
     * @return 站号
     */
    public byte getStation() {
        return station;
    }

    /**
     * 设置站号
     *
     * @param station 站号
     */
    public void setStation(byte station) {
        this.station = station;
    }

    /**
     * 设置多字节数据的反转类型，适用于int,float,double,long类型的数据
     *
     * @param dataFormat 数据类型
     */
    public void setDataFormat(DataFormat dataFormat) {
        getByteTransform().setDataFormat(dataFormat);
    }

    /**
     * 获取多字节数据的反转类型，适用于int,float,double,long类型的数据
     *
     * @return
     */
    public DataFormat getDataFormat() {
        return getByteTransform().getDataFormat();
    }


    /**
     * 字符串数据是否发生反转
     *
     * @return bool值
     */
    public boolean isStringReverse() {
        return getByteTransform().getIsStringReverse();
    }

    /**
     * 设置字符串数据是否反转
     *
     * @param stringReverse bool值
     */
    public void setStringReverse(boolean stringReverse) {
        getByteTransform().setIsStringReverse(stringReverse);
    }


    // region Read Write Support

    /**
     * 读取线圈，需要指定起始地址，如果富文本地址不指定，默认使用的功能码是 0x01<br />
     * To read the coil, you need to specify the start address. If the rich text address is not specified, the default function code is 0x01.
     * @param address 起始地址，格式为"1234"
     * @return 带有成功标志的bool对象
     */
    public OperateResultExOne<Boolean> ReadCoil( String address ) {
        return ReadBool(address);
    }

    /**
     * 批量的读取线圈，需要指定起始地址，读取长度，如果富文本地址不指定，默认使用的功能码是 0x01<br />
     * For batch reading coils, you need to specify the start address and read length. If the rich text address is not specified, the default function code is 0x01.
     * @param address 起始地址，格式为"1234"
     * @param length 读取长度
     * @return 带有成功标志的bool数组对象
     */
    public OperateResultExOne<boolean[]> ReadCoil( String address, short length ) {
        return ReadBool(address, length);
    }

    /**
     * 读取输入线圈，需要指定起始地址，如果富文本地址不指定，默认使用的功能码是 0x02<br />
     * To read the input coil, you need to specify the start address. If the rich text address is not specified, the default function code is 0x02.
     * @param address 起始地址，格式为"1234"
     * @return 带有成功标志的bool对象
     */
    public OperateResultExOne<Boolean> ReadDiscrete( String address ) {
        OperateResultExOne<boolean[]> read = ReadDiscrete(address, (short) 1);
        if(!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(read.Content[0]);
    }

    /**
     * 批量的读取输入点，需要指定起始地址，读取长度，如果富文本地址不指定，默认使用的功能码是 0x02<br />
     * To read input points in batches, you need to specify the start address and read length. If the rich text address is not specified, the default function code is 0x02
     * @param address 起始地址，格式为"1234"
     * @param length 读取长度
     * @return 带有成功标志的bool数组对象
     */
    public OperateResultExOne<boolean[]> ReadDiscrete( String address, short length ) {
        OperateResultExOne<byte[]> command = ModbusInfo.BuildReadModbusCommand(address, length, getStation(), getAddressStartWithZero(), ModbusInfo.ReadDiscrete);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        OperateResultExOne<byte[]> read = ReadFromCoreServer(ModbusInfo.PackCommandToTcp(command.Content, (int) softIncrementCount.GetCurrentValue()));
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        OperateResultExOne<byte[]> extract = ModbusInfo.ExtractActualData(ModbusInfo.ExplodeTcpCommandToCore(read.Content));
        if (!extract.IsSuccess) return OperateResultExOne.CreateFailedResult(extract);

        return OperateResultExOne.CreateSuccessResult(SoftBasic.ByteToBoolArray(extract.Content, length));
    }

    /**
     * 从Modbus服务器批量读取寄存器的信息，需要指定起始地址，读取长度，如果富文本地址不指定，默认使用的功能码是 0x03，如果需要使用04功能码，那么地址就写成 x=4;100<br />
     * To read the register information from the Modbus server in batches, you need to specify the start address and read length. If the rich text address is not specified,
     * the default function code is 0x03. If you need to use the 04 function code, the address is written as x = 4; 100
     * @param address 起始地址，比如"100"，"x=4;100"，"s=1;100","s=1;x=4;100"
     * @param length 读取的数量
     * @return 带有成功标志的字节信息
     */
    public OperateResultExOne<byte[]> Read( String address, short length ) {
        OperateResultExOne<ModbusAddress> analysis = ModbusInfo.AnalysisAddress(address, getStation(), getAddressStartWithZero(), ModbusInfo.ReadRegister);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        ArrayList<Byte> lists = new ArrayList<Byte>();
        short alreadyFinished = 0;
        while (alreadyFinished < length) {
            short lengthTmp = (short) Math.min((length - alreadyFinished), 120);
            OperateResultExOne<byte[]> read = ReadModBus(analysis.Content.AddressAdd(alreadyFinished), lengthTmp);
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            Utilities.ArrayListAddArray(lists, read.Content);
            alreadyFinished += lengthTmp;
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.getBytes(lists));
    }

    /**
     * 从Modbus服务器批量读取寄存器的信息，需要指定Modbus地址，该地址可以携带站号信息，功能码<br />
     * To read register information in batches from the Modbus server, you need to specify the Modbus address, which can carry station number information and function code
     * @param address 地址
     * @param length 长度
     * @return 带是否成功的结果数据
     */
    private OperateResultExOne<byte[]> ReadModBus(ModbusAddress address, short length ) {
        OperateResultExOne<byte[]> command = ModbusInfo.BuildReadModbusCommand(address, length);
        if (!command.IsSuccess) return command;

        OperateResultExOne<byte[]> read = ReadFromCoreServer(ModbusInfo.PackCommandToTcp(command.Content, (short) softIncrementCount.GetCurrentValue()));
        if (!read.IsSuccess) return read;

        return ModbusInfo.ExtractActualData(ModbusInfo.ExplodeTcpCommandToCore(read.Content));
    }

    /**
     * 将数据写入到Modbus的寄存器上去，需要指定起始地址和数据内容，如果富文本地址不指定，默认使用的功能码是 0x10<br />
     * To write data to Modbus registers, you need to specify the start address and data content. If the rich text address is not specified, the default function code is 0x10
     * @param address 起始地址，比如"100"，"x=4;100"，"s=1;100","s=1;x=4;100"
     * @param value 写入的数据，长度根据data的长度来指示
     * @return 返回写入结果
     */
    public OperateResult Write( String address, byte[] value ) {
        OperateResultExOne<byte[]> command = ModbusInfo.BuildWriteWordModbusCommand(address, value, getStation(), getAddressStartWithZero(), ModbusInfo.WriteRegister);
        if (!command.IsSuccess) return command;

        OperateResultExOne<byte[]> write = ReadFromCoreServer(ModbusInfo.PackCommandToTcp(command.Content, (short) softIncrementCount.GetCurrentValue()));
        if (!write.IsSuccess) return write;

        return ModbusInfo.ExtractActualData(ModbusInfo.ExplodeTcpCommandToCore(write.Content));
    }

    /**
     * 将数据写入到Modbus的单个寄存器上去，需要指定起始地址和数据值，如果富文本地址不指定，默认使用的功能码是 0x06<br />
     * To write data to a single register of Modbus, you need to specify the start address and data value. If the rich text address is not specified, the default function code is 0x06.
     * @param address 起始地址，比如"100"，"x=4;100"，"s=1;100","s=1;x=4;100"
     * @param value 写入的short数据
     * @return 是否写入成功
     */
    public OperateResult Write( String address, short value ) {
        OperateResultExOne<byte[]> command = ModbusInfo.BuildWriteWordModbusCommand(address, value, getStation(), getAddressStartWithZero(), ModbusInfo.WriteOneRegister);
        if (!command.IsSuccess) return command;

        OperateResultExOne<byte[]> write = ReadFromCoreServer(ModbusInfo.PackCommandToTcp(command.Content, (short) softIncrementCount.GetCurrentValue()));
        if (!write.IsSuccess) return write;

        return ModbusInfo.ExtractActualData(ModbusInfo.ExplodeTcpCommandToCore(write.Content));
    }

    /**
     * 向设备写入掩码数据，使用0x16功能码，需要确认对方是否支持相关的操作，掩码数据的操作主要针对寄存器。<br />
     * To write mask data to the server, using the 0x16 function code, you need to confirm whether the other party supports related operations.
     * The operation of mask data is mainly directed to the register.
     * @param address 起始地址，比如"100"，"x=4;100"，"s=1;100","s=1;x=4;100"
     * @param andMask 等待与操作的掩码数据
     * @param orMask 等待或操作的掩码数据
     * @return 是否写入成功
     */
    public OperateResult WriteMask(String address, short andMask, short orMask ) {
        OperateResultExOne<byte[]> command = ModbusInfo.BuildWriteMaskModbusCommand(address, andMask, orMask, getStation(), getAddressStartWithZero(), ModbusInfo.WriteMaskRegister);
        if (!command.IsSuccess) return command;

        OperateResultExOne<byte[]> write = ReadFromCoreServer(ModbusInfo.PackCommandToTcp(command.Content, (short) softIncrementCount.GetCurrentValue()));
        if (!write.IsSuccess) return write;

        return ModbusInfo.ExtractActualData(ModbusInfo.ExplodeTcpCommandToCore(write.Content));
    }

    // endregion

    // Region Write One Register

    /**
     * 将数据写入到Modbus的单个寄存器上去，需要指定起始地址和数据值，如果富文本地址不指定，默认使用的功能码是 0x06<br />
     * To write data to a single register of Modbus, you need to specify the start address and data value. If the rich text address is not specified, the default function code is 0x06.
     * @param address 起始地址，比如"100"，"x=4;100"，"s=1;100","s=1;x=4;100"
     * @param value 写入的short数据
     * @return 是否写入成功
     */
    public OperateResult WriteOneRegister( String address, short value ) {
        return Write(address, value);
    }

    // endregion

    // region Read Write Bool

    /**
     * 批量读取线圈或是离散的数据信息，需要指定地址和长度，具体的结果取决于实现，如果富文本地址不指定，默认使用的功能码是 0x01<br />
     * To read coils or discrete data in batches, you need to specify the address and length. The specific result depends on the implementation. If the rich text address is not specified, the default function code is 0x01.
     * @param address 数据地址
     * @param length 数据长度
     * @return 带有成功标识的bool[]数组
     */
    public OperateResultExOne<boolean[]> ReadBool( String address, short length ) {
        OperateResultExOne<byte[]> command = ModbusInfo.BuildReadModbusCommand(address, length, getStation(), getAddressStartWithZero(), ModbusInfo.ReadCoil);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        OperateResultExOne<byte[]> read = ReadFromCoreServer(ModbusInfo.PackCommandToTcp(command.Content, (short) softIncrementCount.GetCurrentValue()));
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        OperateResultExOne<byte[]> extract = ModbusInfo.ExtractActualData(ModbusInfo.ExplodeTcpCommandToCore(read.Content));
        if (!extract.IsSuccess) return OperateResultExOne.CreateFailedResult(extract);

        return OperateResultExOne.CreateSuccessResult(SoftBasic.ByteToBoolArray(extract.Content, length));
    }

    /**
     * 向线圈中写入bool数组，返回是否写入成功，如果富文本地址不指定，默认使用的功能码是 0x0F<br />
     * Write the bool array to the coil, and return whether the writing is successful. If the rich text address is not specified, the default function code is 0x0F.
     * @param address 要写入的数据地址
     * @param values 要写入的实际数组
     * @return 返回写入结果
     */
    public OperateResult Write( String address, boolean[] values ) {
        OperateResultExOne<byte[]> command = ModbusInfo.BuildWriteBoolModbusCommand(address, values, getStation(), getAddressStartWithZero(), ModbusInfo.WriteCoil);
        if (!command.IsSuccess) return command;

        OperateResultExOne<byte[]> write = ReadFromCoreServer(ModbusInfo.PackCommandToTcp(command.Content, (short) softIncrementCount.GetCurrentValue()));
        if (!write.IsSuccess) return write;

        return ModbusInfo.ExtractActualData(ModbusInfo.ExplodeTcpCommandToCore(write.Content));
    }

    /**
     * 向线圈中写入bool数值，返回是否写入成功，如果富文本地址不指定，默认使用的功能码是 0x05<br />
     * Write bool value to the coil and return whether the writing is successful. If the rich text address is not specified, the default function code is 0x05.
     * @param address 要写入的数据地址
     * @param value 要写入的实际数据
     * @return 返回写入结果
     */
    public OperateResult Write( String address, boolean value ) {
        OperateResultExOne<byte[]> command = ModbusInfo.BuildWriteBoolModbusCommand(address, value, getStation(), getAddressStartWithZero(), ModbusInfo.WriteOneCoil);
        if (!command.IsSuccess) return command;

        OperateResultExOne<byte[]> write = ReadFromCoreServer(ModbusInfo.PackCommandToTcp(command.Content, (short) softIncrementCount.GetCurrentValue()));
        if (!write.IsSuccess) return write;

        return ModbusInfo.ExtractActualData(ModbusInfo.ExplodeTcpCommandToCore(write.Content));
    }

    // endregion


    /**
     * 返回表示当前对象的字符串
     * @return 字符串信息
     */
    @Override
    public String toString() {
        return "ModbusTcpNet[" + getIpAddress() + ":"+getPort()+"]";
    }

}
