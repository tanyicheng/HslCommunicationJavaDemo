package HslCommunication.Core.Net;

import HslCommunication.Core.Types.IDataTransfer;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.LogNet.Core.ILogNet;

/**
 * 所有的和设备或是交互类统一读写标准，公开了如何读写对方的一些api接口，并支持基于特性的读写操作<br />
 * All unified read and write standards for devices and interaction classes,
 * expose how to read and write some API interfaces of each other, and support feature-based read and write operations
 */
public interface IReadWriteNet {

    // region ILogNet

    /**
     * 当前读写交互类的日志信息，实例化之后，就可以看到系统运行的详细信息。<br />
     * Currently read and write interactive log information. After instantiation, you can see detailed information about system operation.
     */
    ILogNet LogNet = null;

    /**
     * 当前连接的唯一ID号，默认为长度20的guid码加随机数组成，方便列表管理，也可以自己指定<br />
     * The unique ID number of the current connection. The default is a 20-digit guid code plus a random number.
     * @return 字符串信息
     */
    public String getConnectionId();

    /**
     * 设置当前的连接ID
     * @param connectionId 设置的数据信息
     */
    public void setConnectionId(String connectionId);

    // endregion

    // region Read Write Bytes

    /**
     * 批量读取字节数组信息，需要指定地址和长度，返回原始的字节数组<br />
     * Batch read byte array information, need to specify the address and length, return the original byte array
     * @param address 数据地址
     * @param length 数据长度
     * @return 带有成功标识的byte[]数组
     */
    OperateResultExOne<byte[]> Read( String address, short length );

    /**
     * 写入原始的byte数组数据到指定的地址，返回是否写入成功<br />
     * Write the original byte array data to the specified address, and return whether the write was successful
     * @param address 起始地址
     * @param value 写入值
     * @return 带有成功标识的结果类对象
     */
    OperateResult Write( String address, byte[] value );

    // endregion

    // region Read Write Bool

    // Bool类型的读写，不一定所有的设备都实现，比如西门子，就没有实现bool[]的读写，Siemens的fetch/write没有实现bool操作

    /**
     * 批量读取 {@link Boolean} 数组信息，需要指定地址和长度，返回 {@link Boolean} 数组<br />
     * Batch read {@link Boolean} array information, need to specify the address and length, return {@link Boolean} array
     * @param address 数据地址
     * @param length 数据长度
     * @return 带有成功标识的boolean[]数组
     */
    OperateResultExOne<boolean[]> ReadBool( String address, short length );

    /**
     * 读取单个的 {@link Boolean} 数据信息<br />
     * Read a single {@link Boolean} data message
     * @param address 数据地址
     * @return 带有成功标识的boolean值
     */
    OperateResultExOne<Boolean> ReadBool( String address );

    /**
     * 批量写入 {@link Boolean} 数组数据，返回是否成功<br />
     * Batch write {@link Boolean}  array data, return whether the write was successful
     * @param address 起始地址
     * @param value 写入值
     * @return 是否写入成功
     */
    OperateResult Write( String address, boolean[] value );

    /**
     * 写入单个的 {@link Boolean} 数据，返回是否成功<br />
     * Write a single {@link Boolean} data, and return whether the write was successful
     * @param address 起始地址
     * @param value 写入值
     * @return 是否写入成功
     */
    OperateResult Write( String address, boolean value );

    // endregion

    /**
     * 读取16位的有符号的整型数据<br />
     * Read 16-bit signed integer data
     * @param address 起始地址
     * @return 带有成功标识的short数据
     */
    OperateResultExOne<Short> ReadInt16(String address);

    /**
     * 读取16位的有符号整型数组<br />
     * Read 16-bit signed integer array
     * @param address 起始地址
     * @param length 读取的数组长度
     * @return 带有成功标识的short数组
     */
    OperateResultExOne<short[]> ReadInt16( String address, short length );

    /**
     * 读取16位的无符号整型<br />
     * Read 16-bit unsigned integer
     * @param address 起始地址
     * @return 带有成功标识的 ushort 数据
     */
    OperateResultExOne<Integer> ReadUInt16( String address );

    /**
     * 读取16位的无符号整型数组<br />
     * Read 16-bit unsigned integer array
     * @param address 起始地址
     * @param length 读取的数组长度
     * @return 带有成功标识的 ushort 数组
     */
    OperateResultExOne<int[]> ReadUInt16( String address, short length );

    /**
     * 读取32位的有符号整型<br />
     * Read 32-bit signed integer
     * @param address 起始地址
     * @return 带有成功标识的int数据
     */
    OperateResultExOne<Integer> ReadInt32(String address);

    /**
     * 读取32位有符号整型数组<br />
     * Read 32-bit signed integer array
     * @param address 起始地址
     * @param length 数组长度
     * @return 带有成功标识的int数组
     */
    OperateResultExOne<int[]> ReadInt32( String address, short length );

    /**
     * 读取32位的无符号整型<br />
     * Read 32-bit unsigned integer
     * @param address 起始地址
     * @return 带有成功标识的uint数据
     */
    OperateResultExOne<Long> ReadUInt32( String address );

    /**
     * 读取32位的无符号整型数组<br />
     * Read 32-bit unsigned integer array
     * @param address 起始地址
     * @param length 数组长度
     * @return 带有成功标识的uint数组
     */
    OperateResultExOne<long[]> ReadUInt32( String address, short length );

    /**
     * 读取64位的有符号整型<br />
     * Read 64-bit signed integer
     * @param address 起始地址
     * @return 带有成功标识的long数据
     */
    OperateResultExOne<Long> ReadInt64(String address);

    /**
     * 读取64位的有符号整型数组<br />
     * Read 64-bit signed integer array
     * @param address 起始地址
     * @param length 数组长度
     * @return 带有成功标识的long数组
     */
    OperateResultExOne<long[]> ReadInt64( String address, short length );

    /**
     * 读取单浮点数据<br />
     * Read single floating point data
     * @param address 起始地址
     * @return 带有成功标识的float数据
     */
    OperateResultExOne<Float> ReadFloat(String address);

    /**
     * 读取单浮点精度的数组<br />
     * Read single floating point array
     * @param address 起始地址
     * @param length 数组长度
     * @return 带有成功标识的float数组
     */
    OperateResultExOne<float[]> ReadFloat( String address, short length );

    /**
     * 读取双浮点的数据<br />
     * Read double floating point data
     * @param address 起始地址
     * @return 带有成功标识的double数据
     */
    OperateResultExOne<Double> ReadDouble(String address);


    /**
     * 读取双浮点数据的数组<br />
     * Read double floating point data array
     * @param address 起始地址
     * @param length 数组长度
     * @return 带有成功标识的double数组
     */
    OperateResultExOne<double[]> ReadDouble( String address, short length );

    /**
     * 读取字符串数据，默认为最常见的ASCII编码<br />
     * Read string data, default is the most common ASCII encoding
     * @param address 起始地址
     * @param length 数据长度
     * @return 带有成功标识的string数据
     */
    OperateResultExOne<String> ReadString(String address, short length);

    /**
     * 使用指定的编码，读取字符串数据<br />
     * Reads string data using the specified encoding
     * @param address 起始地址
     * @param length 数据长度
     * @param encoding 指定的自定义的编码
     * @return 带有成功标识的string数据
     */
    OperateResultExOne<String> ReadString( String address, short length, String encoding );

    /**
     * 读取自定义的数据类型，需要继承自IDataTransfer接口
     * @param address 起始地址
     * @param <T> 自定义的类型
     * @return 带有成功标识的自定义类型数据
     */
    <T extends IDataTransfer> OperateResultExOne<T> ReadCustomer(String address,Class<T> tClass);






    /**
     * 写入short数据，返回是否成功<br />
     * Write short data, returns whether success
     * @param address 起始地址
     * @param value 写入值
     * @return 带有成功标识的结果类对象
     */
    OperateResult Write(String address, short value);

    /**
     * 写入short数组，返回是否成功<br />
     * Write short array, return whether the write was successful
     * @param address 起始地址
     * @param values 写入值
     * @return 带有成功标识的结果类对象
     */
    OperateResult Write(String address, short[] values);

    /**
     * 写入int数据，返回是否成功<br />
     * Write int data, return whether the write was successful
     * @param address 起始地址
     * @param value 写入值
     * @return 带有成功标识的结果类对象
     */
    OperateResult Write(String address, int value);

    /**
     * 写入int[]数组，返回是否成功<br />
     * Write int array, return whether the write was successful
     * @param address 起始地址
     * @param values 写入值
     * @return 带有成功标识的结果类对象
     */
    OperateResult Write(String address, int[] values);

    /**
     * 写入long数据，返回是否成功<br />
     * Write long data, return whether the write was successful
     * @param address 起始地址
     * @param value 写入值
     * @return 带有成功标识的结果类对象
     */
    OperateResult Write(String address, long value);

    /**
     * 写入long数组，返回是否成功<br />
     * Write long array, return whether the write was successful
     * @param address 起始地址
     * @param values 写入值
     * @return 带有成功标识的结果类对象
     */
    OperateResult Write(String address, long[] values);

    /**
     * 写入float数据，返回是否成功<br />
     * Write float data, return whether the write was successful
     * @param address 起始地址
     * @param value 写入值
     * @return 带有成功标识的结果类对象
     */
    OperateResult Write(String address, float value);

    /**
     * 写入float数组，返回是否成功<br />
     * Write float array, return whether the write was successful
     * @param address 起始地址
     * @param values 写入值
     * @return 带有成功标识的结果类对象
     */
    OperateResult Write(String address, float[] values);

    /**
     * 写入double数据，返回是否成功<br />
     * Write double data, return whether the write was successful
     * @param address 起始地址
     * @param value 写入值
     * @return 带有成功标识的结果类对象
     */
    OperateResult Write(String address, double value);

    /**
     * 写入double数组，返回是否成功<br />
     * Write double array, return whether the write was successful
     * @param address 起始地址
     * @param values 写入值
     * @return 带有成功标识的结果类对象
     */
    OperateResult Write(String address, double[] values);

    /**
     * 写入字符串信息，编码为ASCII<br />
     * Write string information, encoded as ASCII
     * @param address 起始地址
     * @param value 写入值
     * @return 带有成功标识的结果类对象
     */
    OperateResult Write(String address, String value);

    /**
     * 写入指定长度的字符串信息，如果超出，就截断字符串，如果长度不足，那就补0操作，编码为ASCII<br />
     * Write string information of the specified length. If it exceeds the value, the string is truncated.
     * If the length is not enough, it is filled with 0 and the encoding is ASCII.
     * @param address 起始地址
     * @param value 写入值
     * @param length 写入的字符串的长度
     * @return 带有成功标识的结果类对象
     */
    OperateResult Write(String address, String value, int length);

    /**
     * 写入字符串信息，需要指定的编码信息<br />
     * Write string information, need to specify the encoding information
     * @param address 起始地址
     * @param value 写入值
     * @param encoding 指定的编码信息
     * @return 带有成功标识的结果类对象
     */
    OperateResult Write( String address, String value, String encoding );

    /**
     * 写入指定长度的字符串信息，如果超出，就截断字符串，如果长度不足，那就补0操作，编码为指定的编码信息<br />
     * Write string information of the specified length. If it exceeds the value, the string is truncated. If the length is not enough,
     * then the operation is complemented with 0 , you should specified the encoding information
     * @param address 起始地址
     * @param value 写入值
     * @param length 字符串的长度
     * @param encoding 指定的编码信息
     * @return 带有成功标识的结果类对象
     */
    OperateResult Write( String address, String value, int length, String encoding );

    /**
     * 写入自定义类型的数据，该类型必须继承自IDataTransfer接口
     * @param address 起始地址
     * @param value 写入值
     * @param <T> 类型对象
     * @return 带有成功标识的结果类对象
     */
    <T extends IDataTransfer> OperateResult WriteCustomer(String address, T value);

}
