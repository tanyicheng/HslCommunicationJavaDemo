package HslCommunication.ModBus;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Address.ModbusAddress;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Serial.SoftCRC16;
import HslCommunication.Serial.SoftLRC;
import HslCommunication.StringResources;
import HslCommunication.Utilities;

/**
 * Modbus协议相关的一些信息，包括功能码定义，报文的生成的定义等等信息<br />
 * Some information related to Modbus protocol, including function code definition, definition of message generation, etc.
 */
public class ModbusInfo {
    /**
     * 读取线圈
     */
    public static final byte ReadCoil = 0x01;

    /**
     * 读取离散量
     */
    public static final byte ReadDiscrete = 0x02;


    /**
     * 读取寄存器
     */
    public static final byte ReadRegister = 0x03;

    /**
     * 读取输入寄存器
     */
    public static final byte ReadInputRegister = 0x04;


    /**
     * 写单个线圈
     */
    public static final byte WriteOneCoil = 0x05;

    /**
     * 写单个寄存器
     */
    public static final byte WriteOneRegister = 0x06;


    /**
     * 写多个线圈
     */
    public static final byte WriteCoil = 0x0F;

    /**
     * 写多个寄存器
     */
    public static final byte WriteRegister = 0x10;

    /**
     * 写多个寄存器
     */
    public static final byte WriteMaskRegister = 0x16;

    /**
     * 没有意思
     */
    public static final byte NoMean = 0x17;

    /*****************************************************************************************
     *
     *    本服务器和客户端支持的异常返回
     *
     *******************************************************************************************/


    /**
     * 不支持该功能码
     */
    public static final byte FunctionCodeNotSupport = 0x01;

    /**
     * 该地址越界
     */
    public static final byte FunctionCodeOverBound = 0x02;

    /**
     * 读取长度超过最大值
     */
    public static final byte FunctionCodeQuantityOver = 0x03;

    /**
     * 读写异常
     */
    public static final byte FunctionCodeReadWriteException = 0x04;


    // region Static Helper Method

    private static void CheckModbusAddressStart(ModbusAddress mAddress, boolean isStartWithZero) throws Exception {
        if (!isStartWithZero) {
            if (mAddress.getAddress() < 1) throw new Exception(StringResources.Language.ModbusAddressMustMoreThanOne());
            mAddress.setAddress(mAddress.getAddress() - 1);
        }
    }

    /**
     * 构建Modbus读取数据的核心报文，需要指定地址，长度，站号，是否起始地址0，默认的功能码应该根据bool或是字来区分
     *
     * @param address         Modbus的富文本地址
     * @param length          读取的数据长度
     * @param station         默认的站号信息
     * @param isStartWithZero 起始地址是否从0开始
     * @param defaultFunction 默认的功能码
     * @return 包含最终命令的结果对象
     */
    public static OperateResultExOne<byte[]> BuildReadModbusCommand(String address, short length, byte station, boolean isStartWithZero, byte defaultFunction) {
        try {
            ModbusAddress mAddress = new ModbusAddress(address, station, defaultFunction);
            CheckModbusAddressStart(mAddress, isStartWithZero);

            return BuildReadModbusCommand(mAddress, length);
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    /**
     * 构建Modbus读取数据的核心报文，需要指定地址，长度，站号，是否起始地址0，默认的功能码应该根据bool或是字来区分
     *
     * @param mAddress Modbus的富文本地址
     * @param length   读取的数据长度
     * @return 包含最终命令的结果对象
     */
    public static OperateResultExOne<byte[]> BuildReadModbusCommand(ModbusAddress mAddress, short length) {
        byte[] content = new byte[6];
        content[0] = (byte) mAddress.getStation();
        content[1] = (byte) mAddress.getFunction();
        content[2] = Utilities.getBytes(mAddress.getAddress())[1];
        content[3] = Utilities.getBytes(mAddress.getAddress())[0];
        content[4] = Utilities.getBytes(length)[1];
        content[5] = Utilities.getBytes(length)[0];
        return OperateResultExOne.CreateSuccessResult(content);
    }

    /**
     * 构建Modbus写入bool数据的核心报文，需要指定地址，长度，站号，是否起始地址0，默认的功能码
     *
     * @param address         Modbus的富文本地址
     * @param values          bool数组的信息
     * @param station         默认的站号信息
     * @param isStartWithZero 起始地址是否从0开始
     * @param defaultFunction 默认的功能码
     * @return 包含最终命令的结果对象
     */
    public static OperateResultExOne<byte[]> BuildWriteBoolModbusCommand(String address, boolean[] values, byte station, boolean isStartWithZero, byte defaultFunction) {
        try {
            ModbusAddress mAddress = new ModbusAddress(address, station, defaultFunction);
            CheckModbusAddressStart(mAddress, isStartWithZero);

            return BuildWriteBoolModbusCommand(mAddress, values);
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    /**
     * 构建Modbus写入bool数据的核心报文，需要指定地址，长度，站号，是否起始地址0，默认的功能码
     *
     * @param address         Modbus的富文本地址
     * @param value           bool的信息
     * @param station         默认的站号信息
     * @param isStartWithZero 起始地址是否从0开始
     * @param defaultFunction 默认的功能码
     * @return 包含最终命令的结果对象
     */
    public static OperateResultExOne<byte[]> BuildWriteBoolModbusCommand(String address, boolean value, byte station, boolean isStartWithZero, byte defaultFunction) {
        try {
            if (address.indexOf('.') <= 0) {
                ModbusAddress mAddress = new ModbusAddress(address, station, defaultFunction);
                CheckModbusAddressStart(mAddress, isStartWithZero);

                return BuildWriteBoolModbusCommand(mAddress, value);
            } else {
                int bitIndex = Integer.parseInt(address.substring(address.indexOf('.') + 1));
                if (bitIndex < 0 || bitIndex > 15)
                    return new OperateResultExOne<byte[]>(StringResources.Language.ModbusBitIndexOverstep());

                int orMask = 1 << bitIndex;
                int andMask = ~orMask;
                if (!value) orMask = 0;

                return BuildWriteMaskModbusCommand(address.substring(0, address.indexOf('.')), (short) andMask, (short) orMask, station, isStartWithZero, ModbusInfo.WriteMaskRegister);
            }
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    /**
     * 构建Modbus写入bool数据的核心报文，需要指定地址，长度，站号，是否起始地址0，默认的功能码
     *
     * @param mAddress Modbus的富文本地址
     * @param values   bool数组的信息
     * @return 包含最终命令的结果对象
     */
    public static OperateResultExOne<byte[]> BuildWriteBoolModbusCommand(ModbusAddress mAddress, boolean[] values) {
        try {
            byte[] data = SoftBasic.BoolArrayToByte(values);
            byte[] content = new byte[7 + data.length];
            content[0] = (byte) mAddress.getStation();
            content[1] = (byte) mAddress.getFunction();
            content[2] = Utilities.getBytes(mAddress.getAddress())[1];
            content[3] = Utilities.getBytes(mAddress.getAddress())[0];
            content[4] = (byte) (values.length / 256);
            content[5] = (byte) (values.length % 256);
            content[6] = (byte) (data.length);
            System.arraycopy(data, 0, content, 7, data.length);
            return OperateResultExOne.CreateSuccessResult(content);
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    /**
     * 构建Modbus写入bool数据的核心报文，需要指定地址，长度，站号，是否起始地址0，默认的功能码
     *
     * @param mAddress Modbus的富文本地址
     * @param value    bool数值的信息
     * @return 包含最终命令的结果对象
     */
    public static OperateResultExOne<byte[]> BuildWriteBoolModbusCommand(ModbusAddress mAddress, boolean value) {
        byte[] content = new byte[6];
        content[0] = (byte) mAddress.getStation();
        content[1] = (byte) mAddress.getFunction();
        content[2] = Utilities.getBytes(mAddress.getAddress())[1];
        content[3] = Utilities.getBytes(mAddress.getAddress())[0];
        if (value) {
            content[4] = (byte) 0xFF;
            content[5] = 0x00;
        } else {
            content[4] = 0x00;
            content[5] = 0x00;
        }
        return OperateResultExOne.CreateSuccessResult(content);
    }

    /**
     * 构建Modbus写入字数据的核心报文，需要指定地址，长度，站号，是否起始地址0，默认的功能码
     *
     * @param address         Modbus的富文本地址
     * @param values          bool数组的信息
     * @param station         默认的站号信息
     * @param isStartWithZero 起始地址是否从0开始
     * @param defaultFunction 默认的功能码
     * @return 包含最终命令的结果对象
     */
    public static OperateResultExOne<byte[]> BuildWriteWordModbusCommand(String address, byte[] values, byte station, boolean isStartWithZero, byte defaultFunction) {
        try {
            ModbusAddress mAddress = new ModbusAddress(address, station, defaultFunction);
            if (mAddress.getFunction() == ModbusInfo.ReadRegister) mAddress.setFunction(defaultFunction);
            CheckModbusAddressStart(mAddress, isStartWithZero);

            return BuildWriteWordModbusCommand(mAddress, values);
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    /**
     * 构建Modbus写入字数据的核心报文，需要指定地址，长度，站号，是否起始地址0，默认的功能码
     *
     * @param address         Modbus的富文本地址
     * @param value           short数据信息
     * @param station         默认的站号信息
     * @param isStartWithZero 起始地址是否从0开始
     * @param defaultFunction 默认的功能码
     * @return 包含最终命令的结果对象
     */
    public static OperateResultExOne<byte[]> BuildWriteWordModbusCommand(String address, short value, byte station, boolean isStartWithZero, byte defaultFunction) {
        try {
            ModbusAddress mAddress = new ModbusAddress(address, station, defaultFunction);
            if (mAddress.getFunction() == ModbusInfo.ReadRegister) mAddress.setFunction(defaultFunction);
            CheckModbusAddressStart(mAddress, isStartWithZero);

            return BuildWriteOneRegisterModbusCommand(mAddress, value);
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    /**
     * 构建Modbus写入掩码的核心报文，需要指定地址，长度，站号，是否起始地址0，默认的功能码
     *
     * @param address         Modbus的富文本地址
     * @param andMask         进行与操作的掩码信息
     * @param orMask          进行或操作的掩码信息
     * @param station         默认的站号信息
     * @param isStartWithZero 起始地址是否从0开始
     * @param defaultFunction 默认的功能码
     * @return 包含最终命令的结果对象
     */
    public static OperateResultExOne<byte[]> BuildWriteMaskModbusCommand(String address, short andMask, short orMask, byte station, boolean isStartWithZero, byte defaultFunction) {
        try {
            ModbusAddress mAddress = new ModbusAddress(address, station, defaultFunction);
            if (mAddress.getFunction() == ModbusInfo.ReadRegister) mAddress.setFunction(defaultFunction);
            CheckModbusAddressStart(mAddress, isStartWithZero);

            return BuildWriteMaskModbusCommand(mAddress, andMask, orMask);
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    /**
     * 构建Modbus写入字数据的核心报文，需要指定地址，长度，站号，是否起始地址0，默认的功能码
     *
     * @param mAddress Modbus的富文本地址
     * @param values   bool数组的信息
     * @return 包含最终命令的结果对象
     */
    public static OperateResultExOne<byte[]> BuildWriteWordModbusCommand(ModbusAddress mAddress, byte[] values) {
        byte[] content = new byte[7 + values.length];
        content[0] = (byte) mAddress.getStation();
        content[1] = (byte) mAddress.getFunction();
        content[2] = Utilities.getBytes(mAddress.getAddress())[1];
        content[3] = Utilities.getBytes(mAddress.getAddress())[0];
        content[4] = (byte) (values.length / 2 / 256);
        content[5] = (byte) (values.length / 2 % 256);
        content[6] = (byte) (values.length);
        System.arraycopy(values, 0, content, 7, values.length);
        return OperateResultExOne.CreateSuccessResult(content);
    }

    /**
     * 构建Modbus写入掩码数据的核心报文，需要指定地址，长度，站号，是否起始地址0，默认的功能码
     *
     * @param mAddress Modbus的富文本地址
     * @param andMask  等待进行与操作的掩码
     * @param orMask   等待进行或操作的掩码
     * @return 包含最终命令的结果对象
     */
    public static OperateResultExOne<byte[]> BuildWriteMaskModbusCommand(ModbusAddress mAddress, short andMask, short orMask) {
        byte[] content = new byte[8];
        content[0] = (byte) mAddress.getStation();
        content[1] = (byte) mAddress.getFunction();
        content[2] = Utilities.getBytes(mAddress.getAddress())[1];
        content[3] = Utilities.getBytes(mAddress.getAddress())[0];
        content[4] = Utilities.getBytes(andMask)[1];
        content[5] = Utilities.getBytes(andMask)[0];
        content[6] = Utilities.getBytes(orMask)[1];
        content[7] = Utilities.getBytes(orMask)[0];
        return OperateResultExOne.CreateSuccessResult(content);
    }

    /**
     * 构建Modbus写入字数据的核心报文，需要指定地址，长度，站号，是否起始地址0，默认的功能码
     *
     * @param mAddress Modbus的富文本地址
     * @param value    sho 包含最终命令的结果对象rt的值
     * @return
     */
    public static OperateResultExOne<byte[]> BuildWriteOneRegisterModbusCommand(ModbusAddress mAddress, short value) {
        byte[] content = new byte[6];
        content[0] = (byte) mAddress.getStation();
        content[1] = (byte) mAddress.getFunction();
        content[2] = Utilities.getBytes(mAddress.getAddress())[1];
        content[3] = Utilities.getBytes(mAddress.getAddress())[0];
        content[4] = Utilities.getBytes(value)[1];
        content[5] = Utilities.getBytes(value)[0];
        return OperateResultExOne.CreateSuccessResult(content);
    }

    /**
     * 从返回的modbus的书内容中，提取出真实的数据，适用于写入和读取操作
     *
     * @param response 返回的核心modbus报文信息
     * @return 结果数据内容
     */
    public static OperateResultExOne<byte[]> ExtractActualData(byte[] response) {
        try {
            if ((response[1] & 0xff) >= 0x80)
                return new OperateResultExOne<byte[]>(ModbusInfo.GetDescriptionByErrorCode(response[2]));
            else if (response.length > 3)
                return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(response, 3));
            else
                return OperateResultExOne.CreateSuccessResult(new byte[0]);
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    /**
     * 将modbus指令打包成Modbus-Tcp指令
     *
     * @param value Modbus指令
     * @param id    消息的序号
     * @return Modbus-Tcp指令
     */
    public static byte[] PackCommandToTcp(byte[] value, int id) {
        byte[] buffer = new byte[value.length + 6];
        buffer[0] = Utilities.getBytes(id)[1];
        buffer[1] = Utilities.getBytes(id)[0];
        buffer[4] = Utilities.getBytes(value.length)[1];
        buffer[5] = Utilities.getBytes(value.length)[0];

        System.arraycopy(value, 0, buffer, 6, value.length);
        return buffer;
    }

    /**
     * 将modbus-tcp的数据重新还原成modbus数据
     *
     * @param value modbus-tcp的报文
     * @return modbus数据报文
     */
    public static byte[] ExplodeTcpCommandToCore(byte[] value) {
        return SoftBasic.BytesArrayRemoveBegin(value, 6);
    }

    /**
     * 将modbus-rtu的数据重新还原成modbus数据
     *
     * @param value modbus-rtu的报文
     * @return modbus数据报文
     */
    public static byte[] ExplodeRtuCommandToCore(byte[] value) {
        return SoftBasic.BytesArrayRemoveLast(value, 2);
    }

    /**
     * 将modbus指令打包成Modbus-Rtu指令
     *
     * @param value Modbus指令
     * @return Modbus-Rtu指令
     */
    public static byte[] PackCommandToRtu(byte[] value) {
        return SoftCRC16.CRC16(value);
    }

    /**
     * 将一个modbus-rtu的数据报文，转换成modbus-ascii的数据报文
     *
     * @param value modbus-rtu的完整报文，携带相关的校验码
     * @return 可以用于直接发送的modbus-ascii的报文
     */
    public static byte[] TransRtuToAsciiPackCommand(byte[] value) {
        // remove add LRC check
        byte[] modbus_lrc = SoftLRC.LRC(value);

        // Translate to ascii information
        byte[] modbus_ascii = SoftBasic.BytesToAsciiBytes(modbus_lrc);

        // add head and end informarion
        return SoftBasic.SpliceTwoByteArray(new byte[]{0x3A}, modbus_ascii, new byte[]{0x0D, 0x0A});
    }

    /**
     * 将一个modbus-ascii的数据报文，转换成的modbus核心数据报文
     *
     * @param value modbus-ascii的完整报文，携带相关的校验码
     * @return 可以用于直接发送的modbus的报文
     */
    public static OperateResultExOne<byte[]> TransAsciiPackCommandToRtu(byte[] value) {
        try {
            // response check
            if (value[0] != 0x3A || value[value.length - 2] != 0x0D || value[value.length - 1] != 0x0A)
                return new OperateResultExOne<byte[]>(StringResources.Language.ModbusAsciiFormatCheckFailed() + SoftBasic.ByteToHexString(value, ' '));

            // get modbus core
            byte[] modbus_core = SoftBasic.AsciiBytesToBytes(SoftBasic.BytesArrayRemoveDouble(value, 1, 2));

            if (!SoftLRC.CheckLRC(modbus_core))
                return new OperateResultExOne<byte[]>(StringResources.Language.ModbusLRCCheckFailed() + SoftBasic.ByteToHexString(modbus_core, ' '));

            // remove the last info
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveLast(modbus_core, 1));
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage() + SoftBasic.ByteToHexString(value, ' '));
        }
    }

    /**
     * 分析Modbus协议的地址信息，该地址适应于tcp及rtu模式
     *
     * @param address         带格式的地址，比如"100"，"x=4;100"，"s=1;100","s=1;x=4;100"
     * @param defaultStation  默认的站号信息
     * @param isStartWithZero 起始地址是否从0开始
     * @param defaultFunction 默认的功能码信息
     * @return 转换后的地址信息
     */
    public static OperateResultExOne<ModbusAddress> AnalysisAddress(String address, byte defaultStation, boolean isStartWithZero, byte defaultFunction) {
        try {
            ModbusAddress mAddress = new ModbusAddress(address, defaultStation, defaultFunction);
            if (!isStartWithZero) {
                if (mAddress.getAddress() < 1)
                    throw new Exception(StringResources.Language.ModbusAddressMustMoreThanOne());
                mAddress.setAddress((short) (mAddress.getAddress() - 1));
            }
            return OperateResultExOne.CreateSuccessResult(mAddress);
        } catch (Exception ex) {
            return new OperateResultExOne<ModbusAddress>(ex.getMessage());
        }
    }

    /**
     * 通过错误码来获取到对应的文本消息
     *
     * @param code 错误码
     * @return 错误的文本描述
     */
    public static String GetDescriptionByErrorCode(byte code) {
        switch (code) {
            case ModbusInfo.FunctionCodeNotSupport:
                return StringResources.Language.ModbusTcpFunctionCodeNotSupport();
            case ModbusInfo.FunctionCodeOverBound:
                return StringResources.Language.ModbusTcpFunctionCodeOverBound();
            case ModbusInfo.FunctionCodeQuantityOver:
                return StringResources.Language.ModbusTcpFunctionCodeQuantityOver();
            case ModbusInfo.FunctionCodeReadWriteException:
                return StringResources.Language.ModbusTcpFunctionCodeReadWriteException();
            default:
                return StringResources.Language.UnknownError();
        }
    }

    // endregion

}
