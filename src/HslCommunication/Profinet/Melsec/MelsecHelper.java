package HslCommunication.Profinet.Melsec;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Address.McAddressData;
import HslCommunication.Core.Types.FunctionOperateExOne;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.StringResources;
import HslCommunication.Utilities;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * 所有三菱通讯类的通用辅助工具类，包含了一些通用的静态方法，可以使用本类来获取一些原始的报文信息。详细的操作参见例子<br />
 * All general auxiliary tool classes of Mitsubishi communication class include some general static methods.
 * You can use this class to get some primitive message information. See the example for detailed operation
 */
public class MelsecHelper {

    /**
     * 解析A1E协议数据地址<br />
     * Parse A1E protocol data address
     *
     * @param address 数据地址
     * @return 解析值
     */
    public static OperateResultExTwo<MelsecA1EDataType, Integer> McA1EAnalysisAddress(String address) {
        OperateResultExTwo<MelsecA1EDataType, Integer> result = new OperateResultExTwo<MelsecA1EDataType, Integer>();
        try {
            switch (address.charAt(0)) {
                case 'T':
                case 't': {
                    if (address.charAt(1) == 'S' || address.charAt(1) == 's') {
                        result.Content1 = MelsecA1EDataType.TS;
                        result.Content2 = Integer.parseInt(address.substring(2), MelsecA1EDataType.TS.getFromBase());
                    } else if (address.charAt(1) == 'C' || address.charAt(1) == 'c') {
                        result.Content1 = MelsecA1EDataType.TC;
                        result.Content2 = Integer.parseInt(address.substring(2), MelsecA1EDataType.TC.getFromBase());
                    } else if (address.charAt(1) == 'N' || address.charAt(1) == 'n') {
                        result.Content1 = MelsecA1EDataType.TN;
                        result.Content2 = Integer.parseInt(address.substring(2), MelsecA1EDataType.TN.getFromBase());
                    } else {
                        throw new Exception(StringResources.Language.NotSupportedDataType());
                    }
                    break;
                }
                case 'C':
                case 'c': {
                    if (address.charAt(1) == 'S' || address.charAt(1) == 's') {
                        result.Content1 = MelsecA1EDataType.CS;
                        result.Content2 = Integer.parseInt(address.substring(2), MelsecA1EDataType.CS.getFromBase());
                    } else if (address.charAt(1) == 'C' || address.charAt(1) == 'c') {
                        result.Content1 = MelsecA1EDataType.CC;
                        result.Content2 = Integer.parseInt(address.substring(2), MelsecA1EDataType.CC.getFromBase());
                    } else if (address.charAt(1) == 'N' || address.charAt(1) == 'n') {
                        result.Content1 = MelsecA1EDataType.CN;
                        result.Content2 = Integer.parseInt(address.substring(2), MelsecA1EDataType.CN.getFromBase());
                    } else {
                        throw new Exception(StringResources.Language.NotSupportedDataType());
                    }
                    break;
                }
                case 'X':
                case 'x': {
                    result.Content1 = MelsecA1EDataType.X;
                    address = address.substring(1);
                    if (address.startsWith("0"))
                        result.Content2 = Integer.parseInt(address, 8);
                    else
                        result.Content2 = Integer.parseInt(address, MelsecA1EDataType.X.getFromBase());
                    break;
                }
                case 'Y':
                case 'y': {
                    result.Content1 = MelsecA1EDataType.Y;
                    address = address.substring(1);
                    if (address.startsWith("0"))
                        result.Content2 = Integer.parseInt(address, 8);
                    else
                        result.Content2 = Integer.parseInt(address, MelsecA1EDataType.Y.getFromBase());
                    break;
                }
                case 'M':
                case 'm': {
                    result.Content1 = MelsecA1EDataType.M;
                    result.Content2 = Integer.parseInt(address.substring(1), MelsecA1EDataType.M.getFromBase());
                    break;
                }
                case 'S':
                case 's': {
                    result.Content1 = MelsecA1EDataType.S;
                    result.Content2 = Integer.parseInt(address.substring(1), MelsecA1EDataType.S.getFromBase());
                    break;
                }
                case 'F':
                case 'f': {
                    result.Content1 = MelsecA1EDataType.F;
                    result.Content2 = Integer.parseInt(address.substring(1), MelsecA1EDataType.F.getFromBase());
                    break;
                }
                case 'B':
                case 'b': {
                    result.Content1 = MelsecA1EDataType.B;
                    result.Content2 = Integer.parseInt(address.substring(1), MelsecA1EDataType.B.getFromBase());
                    break;
                }
                case 'D':
                case 'd': {
                    result.Content1 = MelsecA1EDataType.D;
                    result.Content2 = Integer.parseInt(address.substring(1), MelsecA1EDataType.D.getFromBase());
                    break;
                }
                case 'R':
                case 'r': {
                    result.Content1 = MelsecA1EDataType.R;
                    result.Content2 = Integer.parseInt(address.substring(1), MelsecA1EDataType.R.getFromBase());
                    break;
                }
                case 'W':
                case 'w': {
                    result.Content1 = MelsecA1EDataType.W;
                    result.Content2 = Integer.parseInt(address.substring(1), MelsecA1EDataType.W.getFromBase());
                    break;
                }
                default:
                    throw new Exception(StringResources.Language.NotSupportedDataType());
            }
        } catch (Exception ex) {
            result.Message = ex.getMessage();
            return result;
        }

        result.IsSuccess = true;
        return result;
    }

    /**
     * 从字节构建一个ASCII格式的地址字节
     *
     * @param value 字节信息
     * @return ASCII格式的地址
     */
    public static byte[] BuildBytesFromData(byte value) {
        return Utilities.getBytes(String.format("%02x", value), "ASCII");
    }


    /**
     * 从short数据构建一个ASCII格式地址字节
     *
     * @param value short值
     * @return ASCII格式的地址
     */
    public static byte[] BuildBytesFromData(short value) {
        return Utilities.getBytes(String.format("%04x", value), "ASCII");
    }

    /**
     * 从int数据构建一个ASCII格式地址字节
     *
     * @param value int值
     * @return ASCII格式的地址
     */
    public static byte[] BuildBytesFromData(int value) {
        return Utilities.getBytes(String.format("%04x", value), "ASCII");
    }


    /**
     * 从三菱的地址中构建MC协议的6字节的ASCII格式的地址
     *
     * @param address 三菱地址
     * @param type    三菱的数据类型
     * @return 6字节的ASCII格式的地址
     */
    public static byte[] BuildBytesFromAddress(int address, MelsecMcDataType type) {
        return Utilities.getBytes(String.format(type.getFromBase() == 10 ? "%06d" : "%06x", address), "ASCII");
    }


    /**
     * 从字节数组构建一个ASCII格式的地址字节
     *
     * @param value 字节信息
     * @return ASCII格式的地址
     */
    public static byte[] BuildBytesFromData(byte[] value) {
        byte[] buffer = new byte[value.length * 2];
        for (int i = 0; i < value.length; i++) {
            byte[] data = BuildBytesFromData(value[i]);
            buffer[2 * i + 0] = data[0];
            buffer[2 * i + 1] = data[1];
        }
        return buffer;
    }


    /**
     * 将0，1，0，1的字节数组压缩成三菱格式的字节数组来表示开关量的
     *
     * @param value 原始的数据字节
     * @return 压缩过后的数据字节
     */
    public static byte[] TransBoolArrayToByteData(byte[] value) {
        int length = value.length % 2 == 0 ? value.length / 2 : (value.length / 2) + 1;
        byte[] buffer = new byte[length];

        for (int i = 0; i < length; i++) {
            if (value[i * 2 + 0] != 0x00) buffer[i] += 0x10;
            if ((i * 2 + 1) < value.length) {
                if (value[i * 2 + 1] != 0x00) buffer[i] += 0x01;
            }
        }

        return buffer;
    }

    /**
     * 将bool的组压缩成三菱格式的字节数组来表示开关量的
     *
     * @param value 原始的数据字节
     * @return 压缩过后的数据字节
     */
    public static byte[] TransBoolArrayToByteData(boolean[] value) {
        int length = (value.length + 1) / 2;
        byte[] buffer = new byte[length];

        for (int i = 0; i < length; i++) {
            if (value[i * 2 + 0]) buffer[i] += 0x10;
            if ((i * 2 + 1) < value.length) {
                if (value[i * 2 + 1]) buffer[i] += 0x01;
            }
        }

        return buffer;
    }


    /**
     * 计算Fx协议指令的和校验信息
     *
     * @param data 字节数据
     * @return 校验之后的数据
     */
    public static byte[] FxCalculateCRC(byte[] data) {
        int sum = 0;
        for (int i = 1; i < data.length - 2; i++) {
            sum += data[i];
        }
        return BuildBytesFromData((byte) sum);
    }


    /**
     * 检查指定的和校验是否是正确的
     *
     * @param data 字节数据
     * @return 是否成功
     */
    public static boolean CheckCRC(byte[] data) {
        byte[] crc = FxCalculateCRC(data);
        if (crc[0] != data[data.length - 2]) return false;
        if (crc[1] != data[data.length - 1]) return false;
        return true;
    }

    /**
     * 从三菱地址，是否位读取进行创建读取的MC的核心报文<br />
     * From the Mitsubishi address, whether to read the core message of the MC for creating and reading
     *
     * @param addressData 三菱Mc协议的数据地址
     * @param isBit       是否进行了位读取操作
     * @return 带有成功标识的报文对象
     */
    public static byte[] BuildReadMcCoreCommand(McAddressData addressData, boolean isBit) {
        byte[] command = new byte[10];
        command[0] = 0x01;                                                        // 批量读取数据命令
        command[1] = 0x04;
        command[2] = isBit ? (byte) 0x01 : (byte) 0x00;                             // 以点为单位还是字为单位成批读取
        command[3] = 0x00;
        command[4] = Utilities.getBytes(addressData.getAddressStart())[0];      // 起始地址的地位
        command[5] = Utilities.getBytes(addressData.getAddressStart())[1];
        command[6] = Utilities.getBytes(addressData.getAddressStart())[2];
        command[7] = addressData.getMcDataType().getDataCode();                   // 指明读取的数据
        command[8] = (byte) (addressData.getLength() % 256);                       // 软元件的长度
        command[9] = (byte) (addressData.getLength() / 256);

        return command;
    }

    /**
     * 从三菱地址，是否位读取进行创建读取Ascii格式的MC的核心报文
     *
     * @param addressData 三菱Mc协议的数据地址
     * @param isBit       是否进行了位读取操作
     * @return 带有成功标识的报文对象
     */
    public static byte[] BuildAsciiReadMcCoreCommand(McAddressData addressData, boolean isBit) {
        try {
            byte[] command = new byte[20];
            command[0] = 0x30;                                                               // 批量读取数据命令
            command[1] = 0x34;
            command[2] = 0x30;
            command[3] = 0x31;
            command[4] = 0x30;                                                               // 以点为单位还是字为单位成批读取
            command[5] = 0x30;
            command[6] = 0x30;
            command[7] = isBit ? (byte) 0x31 : (byte) 0x30;
            command[8] = (addressData.getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII))[0];          // 软元件类型
            command[9] = (addressData.getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII))[1];
            command[10] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[0];            // 起始地址的地位
            command[11] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[1];
            command[12] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[2];
            command[13] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[3];
            command[14] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[4];
            command[15] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[5];
            command[16] = SoftBasic.BuildAsciiBytesFrom((short) addressData.getLength())[0];                                             // 软元件点数
            command[17] = SoftBasic.BuildAsciiBytesFrom((short) addressData.getLength())[1];
            command[18] = SoftBasic.BuildAsciiBytesFrom((short) addressData.getLength())[2];
            command[19] = SoftBasic.BuildAsciiBytesFrom((short) addressData.getLength())[3];

            return command;

        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 以字为单位，创建数据写入的核心报文
     *
     * @param addressData 三菱Mc协议的数据地址
     * @param value       实际的原始数据信息
     * @return 带有成功标识的报文对象
     */
    public static byte[] BuildWriteWordCoreCommand(McAddressData addressData, byte[] value) {
        if (value == null) value = new byte[0];
        byte[] command = new byte[10 + value.length];
        command[0] = 0x01;                                                        // 批量写入数据命令
        command[1] = 0x14;
        command[2] = 0x00;                                                        // 以字为单位成批读取
        command[3] = 0x00;
        command[4] = Utilities.getBytes(addressData.getAddressStart())[0];        // 起始地址的地位
        command[5] = Utilities.getBytes(addressData.getAddressStart())[1];
        command[6] = Utilities.getBytes(addressData.getAddressStart())[2];
        command[7] = addressData.getMcDataType().getDataCode();                             // 指明写入的数据
        command[8] = (byte) (value.length / 2 % 256);                              // 软元件长度的地位
        command[9] = (byte) (value.length / 2 / 256);
        System.arraycopy(value, 0, command, 10, value.length);
        return command;
    }

    /**
     * 以字为单位，创建ASCII数据写入的核心报文
     *
     * @param addressData 三菱Mc协议的数据地址
     * @param value       实际的原始数据信息
     * @return 带有成功标识的报文对象
     */
    public static byte[] BuildAsciiWriteWordCoreCommand(McAddressData addressData, byte[] value) {
        value = TransByteArrayToAsciiByteArray(value);

        byte[] command = new byte[20 + value.length];
        command[0] = 0x31;                                                                                         // 批量写入的命令
        command[1] = 0x34;
        command[2] = 0x30;
        command[3] = 0x31;
        command[4] = 0x30;                                                                                         // 子命令
        command[5] = 0x30;
        command[6] = 0x30;
        command[7] = 0x30;
        command[8] = (addressData.getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII))[0];               // 软元件类型
        command[9] = (addressData.getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII))[1];
        command[10] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[0];    // 起始地址的地位
        command[11] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[1];
        command[12] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[2];
        command[13] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[3];
        command[14] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[4];
        command[15] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[5];
        command[16] = SoftBasic.BuildAsciiBytesFrom((short) (value.length / 4))[0];                               // 软元件点数
        command[17] = SoftBasic.BuildAsciiBytesFrom((short) (value.length / 4))[1];
        command[18] = SoftBasic.BuildAsciiBytesFrom((short) (value.length / 4))[2];
        command[19] = SoftBasic.BuildAsciiBytesFrom((short) (value.length / 4))[3];
        System.arraycopy(value, 0, command, 20, value.length);

        return command;
    }

    /**
     * 以位为单位，创建数据写入的核心报文
     *
     * @param addressData 三菱Mc协议的数据地址
     * @param value       原始的bool数组数据
     * @return 带有成功标识的报文对象
     */
    public static byte[] BuildWriteBitCoreCommand(McAddressData addressData, boolean[] value) {
        if (value == null) value = new boolean[0];
        byte[] buffer = MelsecHelper.TransBoolArrayToByteData(value);
        byte[] command = new byte[10 + buffer.length];
        command[0] = 0x01;                                                        // 批量写入数据命令
        command[1] = 0x14;
        command[2] = 0x01;                                                        // 以位为单位成批写入
        command[3] = 0x00;
        command[4] = Utilities.getBytes(addressData.getAddressStart())[0];        // 起始地址的地位
        command[5] = Utilities.getBytes(addressData.getAddressStart())[1];
        command[6] = Utilities.getBytes(addressData.getAddressStart())[2];
        command[7] = addressData.getMcDataType().getDataCode();                             // 指明写入的数据
        command[8] = (byte) (value.length % 256);                                  // 软元件长度的地位
        command[9] = (byte) (value.length / 256);
        System.arraycopy(buffer, 0, command, 10, buffer.length);

        return command;
    }

    /**
     * 以位为单位，创建ASCII数据写入的核心报文
     *
     * @param addressData 三菱Mc协议的数据地址
     * @param value       原始的bool数组数据
     * @return 带有成功标识的报文对象
     */
    public static byte[] BuildAsciiWriteBitCoreCommand(McAddressData addressData, boolean[] value) {
        if (value == null) value = new boolean[0];
        byte[] buffer = new byte[value.length];
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = value[i] ? (byte) 0x31 : (byte) 0x30;
        }

        byte[] command = new byte[20 + buffer.length];
        command[0] = 0x31;                                                                              // 批量写入的命令
        command[1] = 0x34;
        command[2] = 0x30;
        command[3] = 0x31;
        command[4] = 0x30;                                                                              // 子命令
        command[5] = 0x30;
        command[6] = 0x30;
        command[7] = 0x31;
        command[8] = (addressData.getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII))[0];            // 软元件类型
        command[9] = (addressData.getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII))[1];
        command[10] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[0];     // 起始地址的地位
        command[11] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[1];
        command[12] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[2];
        command[13] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[3];
        command[14] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[4];
        command[15] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[5];
        command[16] = SoftBasic.BuildAsciiBytesFrom((short) (value.length))[0];              // 软元件点数
        command[17] = SoftBasic.BuildAsciiBytesFrom((short) (value.length))[1];
        command[18] = SoftBasic.BuildAsciiBytesFrom((short) (value.length))[2];
        command[19] = SoftBasic.BuildAsciiBytesFrom((short) (value.length))[3];
        System.arraycopy(buffer, 0, command, 20, buffer.length);

        return command;
    }


    /**
     * 从三菱扩展地址，是否位读取进行创建读取的MC的核心报文
     *
     * @param addressData 是否进行了位读取操作
     * @param extend      扩展指定
     * @param isBit       三菱Mc协议的数据地址
     * @return 带有成功标识的报文对象
     */
    public static byte[] BuildReadMcCoreExtendCommand(McAddressData addressData, short extend, boolean isBit) {
        byte[] command = new byte[17];
        command[0] = 0x01;                                                      // 批量读取数据命令
        command[1] = 0x04;
        command[2] = isBit ? (byte) 0x81 : (byte) 0x80;                           // 以点为单位还是字为单位成批读取
        command[3] = 0x00;
        command[4] = 0x00;
        command[5] = 0x00;
        command[6] = Utilities.getBytes(addressData.getAddressStart())[0];      // 起始地址的地位
        command[7] = Utilities.getBytes(addressData.getAddressStart())[1];
        command[8] = Utilities.getBytes(addressData.getAddressStart())[2];
        command[9] = addressData.getMcDataType().getDataCode();                           // 指明读取的数据
        command[10] = 0x00;
        command[11] = 0x00;
        command[12] = Utilities.getBytes(extend)[0];
        command[13] = Utilities.getBytes(extend)[1];
        command[14] = (byte) 0xF9;
        command[15] = (byte) (addressData.getLength() % 256);                          // 软元件的长度
        command[16] = (byte) (addressData.getLength() / 256);

        return command;
    }

    /**
     * 按字为单位随机读取的指令创建
     *
     * @param address 地址数组
     * @return 指令
     */
    public static byte[] BuildReadRandomWordCommand(McAddressData[] address) {
        byte[] command = new byte[6 + address.length * 4];
        command[0] = 0x03;                                                                  // 批量读取数据命令
        command[1] = 0x04;
        command[2] = 0x00;
        command[3] = 0x00;
        command[4] = (byte) address.length;                                                  // 访问的字点数
        command[5] = 0x00;                                                                  // 双字访问点数
        for (int i = 0; i < address.length; i++) {
            command[i * 4 + 6] = Utilities.getBytes(address[i].getAddressStart())[0];       // 软元件起始地址
            command[i * 4 + 7] = Utilities.getBytes(address[i].getAddressStart())[1];
            command[i * 4 + 8] = Utilities.getBytes(address[i].getAddressStart())[2];
            command[i * 4 + 9] = address[i].getMcDataType().getDataCode();                            // 软元件代号
        }
        return command;
    }

    /**
     * 随机读取的指令创建
     *
     * @param address 地址数组
     * @return 指令
     */
    public static byte[] BuildReadRandomCommand(McAddressData[] address) {
        byte[] command = new byte[6 + address.length * 6];
        command[0] = 0x06;                                                                  // 批量读取数据命令
        command[1] = 0x04;
        command[2] = 0x00;                                                                  // 子命令
        command[3] = 0x00;
        command[4] = (byte) address.length;                                                  // 字软元件的块数
        command[5] = 0x00;                                                                  // 位软元件的块数
        for (int i = 0; i < address.length; i++) {
            command[i * 6 + 6] = Utilities.getBytes(address[i].getAddressStart())[0];      // 字软元件的编号
            command[i * 6 + 7] = Utilities.getBytes(address[i].getAddressStart())[1];
            command[i * 6 + 8] = Utilities.getBytes(address[i].getAddressStart())[2];
            command[i * 6 + 9] = address[i].getMcDataType().getDataCode();                           // 字软元件的代码
            command[i * 6 + 10] = (byte) (address[i].getLength() % 256);                          // 软元件的长度
            command[i * 6 + 11] = (byte) (address[i].getLength() / 256);
        }
        return command;
    }

    /**
     * 按字为单位随机读取的指令创建
     *
     * @param address 地址数组
     * @return 指令
     */
    public static byte[] BuildAsciiReadRandomWordCommand(McAddressData[] address) {
        byte[] command = new byte[12 + address.length * 8];
        command[0] = 0x30;                                                               // 批量读取数据命令
        command[1] = 0x34;
        command[2] = 0x30;
        command[3] = 0x33;
        command[4] = 0x30;                                                               // 以点为单位还是字为单位成批读取
        command[5] = 0x30;
        command[6] = 0x30;
        command[7] = 0x30;
        command[8] = SoftBasic.BuildAsciiBytesFrom((byte) address.length)[0];
        command[9] = SoftBasic.BuildAsciiBytesFrom((byte) address.length)[1];
        command[10] = 0x30;
        command[11] = 0x30;
        for (int i = 0; i < address.length; i++) {
            command[i * 8 + 12] = (address[i].getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII))[0];            // 软元件类型
            command[i * 8 + 13] = (address[i].getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII))[1];
            command[i * 8 + 14] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[0];            // 起始地址的地位
            command[i * 8 + 15] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[1];
            command[i * 8 + 16] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[2];
            command[i * 8 + 17] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[3];
            command[i * 8 + 18] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[4];
            command[i * 8 + 19] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[5];
        }
        return command;
    }

    /// <summary>
    /// 随机读取的指令创建
    /// </summary>
    /// <param name="address">地址数组</param>
    /// <returns>指令</returns>
    public static byte[] BuildAsciiReadRandomCommand(McAddressData[] address) {
        byte[] command = new byte[12 + address.length * 12];
        command[0] = 0x30;                                                               // 批量读取数据命令
        command[1] = 0x34;
        command[2] = 0x30;
        command[3] = 0x36;
        command[4] = 0x30;                                                               // 以点为单位还是字为单位成批读取
        command[5] = 0x30;
        command[6] = 0x30;
        command[7] = 0x30;
        command[8] = SoftBasic.BuildAsciiBytesFrom((byte) address.length)[0];
        command[9] = SoftBasic.BuildAsciiBytesFrom((byte) address.length)[1];
        command[10] = 0x30;
        command[11] = 0x30;
        for (int i = 0; i < address.length; i++) {
            command[i * 12 + 12] = (address[i].getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII))[0];            // 软元件类型
            command[i * 12 + 13] = (address[i].getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII))[1];
            command[i * 12 + 14] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[0];            // 起始地址的地位
            command[i * 12 + 15] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[1];
            command[i * 12 + 16] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[2];
            command[i * 12 + 17] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[3];
            command[i * 12 + 18] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[4];
            command[i * 12 + 19] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[5];
            command[i * 12 + 20] = SoftBasic.BuildAsciiBytesFrom((short) address[i].getLength())[0];
            command[i * 12 + 21] = SoftBasic.BuildAsciiBytesFrom((short) address[i].getLength())[1];
            command[i * 12 + 22] = SoftBasic.BuildAsciiBytesFrom((short) address[i].getLength())[2];
            command[i * 12 + 23] = SoftBasic.BuildAsciiBytesFrom((short) address[i].getLength())[3];
        }
        return command;
    }


    /// <summary>
    /// 创建批量读取标签的报文数据信息
    /// </summary>
    /// <param name="tags">标签名</param>
    /// <param name="lengths">长度信息</param>
    /// <returns>报文名称</returns>
    public static byte[] BuildReadTag(String[] tags, short[] lengths) throws Exception {
        if (tags.length != lengths.length) throw new Exception(StringResources.Language.TwoParametersLengthIsNotSame());

        ByteArrayOutputStream command = new ByteArrayOutputStream();
        command.write(0x1A);                                                          // 批量读取标签的指令
        command.write(0x04);
        command.write(0x00);                                                          // 子命令
        command.write(0x00);
        command.write(Utilities.getBytes(tags.length)[0]);                       // 排列点数
        command.write(Utilities.getBytes(tags.length)[1]);
        command.write(0x00);                                                          // 省略指定
        command.write(0x00);
        for (int i = 0; i < tags.length; i++) {
            byte[] tagBuffer = tags[i].getBytes(StandardCharsets.UTF_16);
            command.write(Utilities.getBytes(tagBuffer.length / 2)[0]);          // 标签长度
            command.write(Utilities.getBytes(tagBuffer.length / 2)[1]);
            command.write(tagBuffer, 0, tagBuffer.length);                          // 标签名称
            command.write(0x01);                                                      // 单位指定
            command.write(0x00);                                                      // 固定值
            command.write(Utilities.getBytes(lengths[i] * 2)[0]);                 // 排列数据长
            command.write(Utilities.getBytes(lengths[i] * 2)[1]);
        }
        byte[] buffer = command.toByteArray();
        command.close();
        return buffer;
    }

    /**
     * 解析出标签读取的数据内容
     *
     * @param content 返回的数据信息
     * @return 解析结果
     */
    public static OperateResultExOne<byte[]> ExtraTagData(byte[] content) {
        try {
            int count = Utilities.getUShort(content, 0);
            int index = 2;
            ArrayList<Byte> array = new ArrayList<Byte>(20);
            for (int i = 0; i < count; i++) {
                int length = Utilities.getUShort(content, index + 2);
                Utilities.ArrayListAddArray(array, SoftBasic.BytesArraySelectMiddle(content, index + 4, length));
                index += 4 + length;
            }
            return OperateResultExOne.CreateSuccessResult(Utilities.getBytes(array));
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage() + " Source:" + SoftBasic.ByteToHexString(content, ' '));
        }
    }

    /**
     * 读取本站缓冲寄存器的数据信息，需要指定寄存器的地址，和读取的长度
     *
     * @param address 寄存器的地址
     * @param length  数据长度
     * @return 结果内容
     */
    public static OperateResultExOne<byte[]> BuildReadMemoryCommand(String address, short length) {
        try {
            int add = Integer.parseInt(address);
            byte[] command = new byte[8];
            command[0] = 0x13;                                                      // 读取缓冲数据命令
            command[1] = 0x06;
            command[2] = Utilities.getBytes(add)[0];                           // 起始地址的地位
            command[3] = Utilities.getBytes(add)[1];
            command[4] = Utilities.getBytes(add)[2];
            command[5] = Utilities.getBytes(add)[3];
            command[6] = (byte) (length % 256);                                      // 软元件的长度
            command[7] = (byte) (length / 256);

            return OperateResultExOne.CreateSuccessResult(command);
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }


    public static byte[] TransByteArrayToAsciiByteArray(byte[] value) {
        if (value == null) return new byte[0];

        byte[] buffer = new byte[value.length * 2];
        for (int i = 0; i < value.length / 2; i++) {
            byte[] data = SoftBasic.BuildAsciiBytesFrom(Utilities.getShort(value, i * 2));
            System.arraycopy(data, 0, buffer, 4 * i, data.length);
        }
        return buffer;
    }

    /**
     * 根据三菱的错误码去查找对象描述信息
     *
     * @param code 错误码
     * @return 描述信息
     */
    public static String GetErrorDescription(int code) {
        switch (code) {
            case 0x0002:
                return StringResources.Language.MelsecError02();
            case 0x0051:
                return StringResources.Language.MelsecError51();
            case 0x0052:
                return StringResources.Language.MelsecError52();
            case 0x0054:
                return StringResources.Language.MelsecError54();
            case 0x0055:
                return StringResources.Language.MelsecError55();
            case 0x0056:
                return StringResources.Language.MelsecError56();
            case 0x0058:
                return StringResources.Language.MelsecError58();
            case 0x0059:
                return StringResources.Language.MelsecError59();
            case 0xC04D:
                return StringResources.Language.MelsecErrorC04D();
            case 0xC050:
                return StringResources.Language.MelsecErrorC050();
            case 0xC051:
            case 0xC052:
            case 0xC053:
            case 0xC054:
                return StringResources.Language.MelsecErrorC051_54();
            case 0xC055:
                return StringResources.Language.MelsecErrorC055();
            case 0xC056:
                return StringResources.Language.MelsecErrorC056();
            case 0xC057:
                return StringResources.Language.MelsecErrorC057();
            case 0xC058:
                return StringResources.Language.MelsecErrorC058();
            case 0xC059:
                return StringResources.Language.MelsecErrorC059();
            case 0xC05A:
            case 0xC05B:
                return StringResources.Language.MelsecErrorC05A_B();
            case 0xC05C:
                return StringResources.Language.MelsecErrorC05C();
            case 0xC05D:
                return StringResources.Language.MelsecErrorC05D();
            case 0xC05E:
                return StringResources.Language.MelsecErrorC05E();
            case 0xC05F:
                return StringResources.Language.MelsecErrorC05F();
            case 0xC060:
                return StringResources.Language.MelsecErrorC060();
            case 0xC061:
                return StringResources.Language.MelsecErrorC061();
            case 0xC062:
                return StringResources.Language.MelsecErrorC062();
            case 0xC070:
                return StringResources.Language.MelsecErrorC070();
            case 0xC072:
                return StringResources.Language.MelsecErrorC072();
            case 0xC074:
                return StringResources.Language.MelsecErrorC074();
            default:
                return StringResources.Language.MelsecPleaseReferToManualDocument();
        }
    }
}
