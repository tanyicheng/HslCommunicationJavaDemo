package HslCommunication.BasicFramework;

import HslCommunication.Core.Thread.SimpleHybirdLock;
import HslCommunication.Core.Transfer.IByteTransform;
import HslCommunication.Core.Transfer.RegularByteTransform;
import HslCommunication.Core.Types.IDataTransfer;

/**
 * 一个线程安全的缓存数据块，支持批量动态修改，添加，并获取快照
 * A thread-safe cache data block that supports batch dynamic modification, addition, and snapshot acquisition
 */
public class SoftBuffer {
    private int capacity = 10;                      // 缓存的容量
    private byte[] buffer;                          // 缓存的数据
    private SimpleHybirdLock hybirdLock;            // 高效的混合锁
    private IByteTransform byteTransform;           // 数据转换类
    private boolean isBoolReverseByWord = false;    // Bool的操作是否根据字进行反转


    /**
     * 使用默认的大小初始化缓存空间
     * Initialize cache space with default size
     */
    public SoftBuffer() {
        buffer = new byte[capacity];
        hybirdLock = new SimpleHybirdLock();
        byteTransform = new RegularByteTransform();
    }

    /**
     * 使用指定的容量初始化缓存数据块
     * Initialize the cache data block with the specified capacity
     *
     * @param capacity 初始化的容量
     */
    public SoftBuffer(int capacity) {
        buffer = new byte[capacity];
        this.capacity = capacity;
        hybirdLock = new SimpleHybirdLock();
        byteTransform = new RegularByteTransform();
    }


    /**
     * 设置指定的位置bool值，如果超出，则丢弃数据，该位置是指按照位为单位排序的
     * Set the bool value at the specified position, if it is exceeded, the data is discarded, the position refers to sorting in units of bits
     *
     * @param value     数据块信息
     * @param destIndex 目标存储的索引
     */
    public void SetBool(boolean value, int destIndex) {
        SetBool(new boolean[]{value}, destIndex);
    }

    /**
     * 设置指定的位置的bool数组，如果超出，则丢弃数据，该位置是指按照位为单位排序的
     * Set the bool array at the specified position, if it is exceeded, the data is discarded, the position refers to sorting in units of bits
     *
     * @param value     数据块信息
     * @param destIndex 目标存储的索引
     */
    public void SetBool(boolean[] value, int destIndex) {
        if (value != null) {
            try {
                hybirdLock.Enter();
                for (int i = 0; i < value.length; i++) {
                    int byteIndex = (destIndex + i) / 8;
                    int offset = (destIndex + i) % 8;

                    if (isBoolReverseByWord) {
                        if (byteIndex % 2 == 0) {
                            byteIndex += 1;
                        } else {
                            byteIndex -= 1;
                        }
                    }

                    if (value[i]) {
                        buffer[byteIndex] = (byte) (buffer[byteIndex] | getOrByte(offset));
                    } else {
                        buffer[byteIndex] = (byte) (buffer[byteIndex] & getAndByte(offset));
                    }
                }

                hybirdLock.Leave();
            } catch (Exception ex) {
                hybirdLock.Leave();
                throw ex;
            }
        }
    }

    /**
     * 获取指定的位置的bool值，如果超出，则引发异常
     * Get the bool value at the specified position, if it exceeds, an exception is thrown
     *
     * @param destIndex 目标存储的索引
     * @return 获取索引位置的bool数据值
     * @throws IndexOutOfBoundsException 索引超出异常
     */
    public boolean GetBool(int destIndex) throws IndexOutOfBoundsException {
        return GetBool(destIndex, 1)[0];
    }

    /**
     * 获取指定位置的bool数组值，如果超过，则引发异常
     * Get the bool array value at the specified position, if it exceeds, an exception is thrown
     *
     * @param destIndex 目标存储的索引
     * @param length    读取的数组长度
     * @return bool数组值
     */
    public boolean[] GetBool(int destIndex, int length) {
        boolean[] result = new boolean[length];
        try {
            hybirdLock.Enter();
            for (int i = 0; i < length; i++) {
                int byteIndex = (destIndex + i) / 8;
                int offect = (destIndex + i) % 8;

                if (isBoolReverseByWord) {
                    if (byteIndex % 2 == 0) {
                        byteIndex += 1;
                    } else {
                        byteIndex -= 1;
                    }
                }

                result[i] = (buffer[byteIndex] & getOrByte(offect)) == getOrByte(offect);
            }

            hybirdLock.Leave();
        } catch (Exception ex) {
            hybirdLock.Leave();
            throw ex;
        }
        return result;
    }

    private byte getAndByte(int offset) {
        switch (offset) {
            case 0:
                return (byte) 0xFE;
            case 1:
                return (byte) 0xFD;
            case 2:
                return (byte) 0xFB;
            case 3:
                return (byte) 0xF7;
            case 4:
                return (byte) 0xEF;
            case 5:
                return (byte) 0xDF;
            case 6:
                return (byte) 0xBF;
            case 7:
                return (byte) 0x7F;
            default:
                return (byte) 0xFF;
        }
    }


    private byte getOrByte(int offset) {
        switch (offset) {
            case 0:
                return (byte) 0x01;
            case 1:
                return (byte) 0x02;
            case 2:
                return (byte) 0x04;
            case 3:
                return (byte) 0x08;
            case 4:
                return (byte) 0x10;
            case 5:
                return (byte) 0x20;
            case 6:
                return (byte) 0x40;
            case 7:
                return (byte) 0x80;
            default:
                return (byte) 0x00;
        }
    }

    /**
     * 设置指定的位置的数据块，如果超出，则丢弃数据
     * Set the data block at the specified position, if it is exceeded, the data is discarded
     *
     * @param data      数据块信息
     * @param destIndex 目标存储的索引
     */
    public void SetBytes(byte[] data, int destIndex) {
        if (destIndex < capacity && destIndex >= 0 && data != null) {
            hybirdLock.Enter();

            if ((data.length + destIndex) > buffer.length) {
                System.arraycopy(data, 0, buffer, destIndex, (buffer.length - destIndex));
            } else {
                System.arraycopy(data, 0, buffer, destIndex, data.length);
            }

            hybirdLock.Leave();
        }
    }


    /**
     * 设置指定的位置的数据块，如果超出，则丢弃数据
     * Set the data block at the specified position, if it is exceeded, the data is discarded
     *
     * @param data      数据块信息
     * @param destIndex 目标存储的索引
     * @param length    准备拷贝的数据长度
     */
    public void SetBytes(byte[] data, int destIndex, int length) {
        if (destIndex < capacity && destIndex >= 0 && data != null) {
            if (length > data.length) length = data.length;

            hybirdLock.Enter();

            if ((length + destIndex) > buffer.length) {
                System.arraycopy(data, 0, buffer, destIndex, (buffer.length - destIndex));
            } else {
                System.arraycopy(data, 0, buffer, destIndex, length);
            }

            hybirdLock.Leave();
        }
    }

    /**
     * 设置指定的位置的数据块，如果超出，则丢弃数据
     * Set the data block at the specified position, if it is exceeded, the data is discarded
     *
     * @param data        数据块信息
     * @param sourceIndex Data中的起始位置
     * @param destIndex   目标存储的索引
     * @param length      准备拷贝的数据长度
     */
    public void SetBytes(byte[] data, int sourceIndex, int destIndex, int length) {
        if (destIndex < capacity && destIndex >= 0 && data != null) {
            if (length > data.length) length = data.length;

            hybirdLock.Enter();

            System.arraycopy(data, sourceIndex, buffer, destIndex, length);

            hybirdLock.Leave();
        }
    }

    /**
     * 获取内存指定长度的数据信息
     * Get data information of specified length in memory
     *
     * @param index  起始位置
     * @param length 数组长度
     * @return 返回实际的数据信息
     */
    public byte[] GetBytes(int index, int length) {
        byte[] result = new byte[length];
        if (length > 0) {
            hybirdLock.Enter();
            if (index >= 0 && (index + length) <= buffer.length) {
                System.arraycopy(buffer, index, result, 0, length);
            }
            hybirdLock.Leave();
        }
        return result;
    }

    /**
     * 获取内存所有的数据信息
     * Get all data information in memory
     *
     * @return 实际的数据信息
     */
    public byte[] GetBytes() {
        return GetBytes(0, capacity);
    }


    /**
     * 设置byte类型的数据到缓存区
     * Set byte type data to the cache area
     *
     * @param value byte数值
     * @param index 索引位置
     */
    public void SetValue(byte value, int index) {
        SetBytes(new byte[]{value}, index);
    }

    /**
     * 设置short数组的数据到缓存区
     * Set short array data to the cache area
     *
     * @param values short数组
     * @param index  索引位置
     */
    public void SetValue(short[] values, int index) {
        SetBytes(byteTransform.TransByte(values), index);
    }

    /**
     * 设置short类型的数据到缓存区
     * Set short type data to the cache area
     *
     * @param value short数值
     * @param index 索引位置
     */
    public void SetValue(short value, int index) {
        SetValue(new short[]{value}, index);
    }

    /**
     * 设置int数组的数据到缓存区
     * Set int array data to the cache area
     *
     * @param values int数组
     * @param index  索引位置
     */
    public void SetValue(int[] values, int index) {
        SetBytes(byteTransform.TransByte(values), index);
    }

    /**
     * 设置int类型的数据到缓存区
     * Set int type data to the cache area
     *
     * @param value int数值
     * @param index 索引位置
     */
    public void SetValue(int value, int index) {
        SetValue(new int[]{value}, index);
    }

    /**
     * 设置float数组的数据到缓存区
     * Set float array data to the cache area
     *
     * @param values float数组
     * @param index  索引位置
     */
    public void SetValue(float[] values, int index) {
        SetBytes(byteTransform.TransByte(values), index);
    }

    /**
     * 设置float类型的数据到缓存区
     * Set float type data to the cache area
     *
     * @param value float数值
     * @param index 索引位置
     */
    public void SetValue(float value, int index) {
        SetValue(new float[]{value}, index);
    }

    /**
     * 设置long数组的数据到缓存区
     * Set long array data to the cache area
     *
     * @param values long数组
     * @param index  索引位置
     */
    public void SetValue(long[] values, int index) {
        SetBytes(byteTransform.TransByte(values), index);
    }

    /**
     * 设置long类型的数据到缓存区
     * Set long type data to the cache area
     *
     * @param value long数值
     * @param index 索引位置
     */
    public void SetValue(long value, int index) {
        SetValue(new long[]{value}, index);
    }

    /**
     * 设置double数组的数据到缓存区
     * Set double array data to the cache area
     *
     * @param values double数组
     * @param index  索引位置
     */
    public void SetValue(double[] values, int index) {
        SetBytes(byteTransform.TransByte(values), index);
    }

    /**
     * 设置double类型的数据到缓存区
     * Set double type data to the cache area
     *
     * @param value double数值
     * @param index 索引位置
     */
    public void SetValue(double value, int index) {
        SetValue(new double[]{value}, index);
    }

    /**
     * 获取byte类型的数据
     * Get byte data
     *
     * @param index 索引位置
     * @return byte数值
     */
    public byte GetByte(int index) {
        return GetBytes(index, 1)[0];
    }

    /**
     * 获取short类型的数组到缓存区
     * Get short type array to cache
     *
     * @param index  索引位置
     * @param length 数组长度
     * @return short数组
     */
    public short[] GetInt16(int index, int length) {
        byte[] tmp = GetBytes(index, length * 2);
        return byteTransform.TransInt16(tmp, 0, length);
    }

    /**
     * 获取short类型的数据到缓存区
     * Get short data to the cache
     *
     * @param index 索引位置
     * @return short数据
     */
    public short GetInt16(int index) {
        return GetInt16(index, 1)[0];
    }

    /**
     * 获取int类型的数组到缓存区
     * Get int type array to cache
     *
     * @param index  索引位置
     * @param length 数组长度
     * @return int数组
     */
    public int[] GetInt32(int index, int length) {
        byte[] tmp = GetBytes(index, length * 4);
        return byteTransform.TransInt32(tmp, 0, length);
    }

    /**
     * 获取int类型的数据到缓存区
     * Get int type data to cache
     *
     * @param index 索引位置
     * @return int数据
     */
    public int GetInt32(int index) {
        return GetInt32(index, 1)[0];
    }

    /**
     * 获取float类型的数组到缓存区
     * Get float type array to cache
     *
     * @param index  索引位置
     * @param length 数组长度
     * @return float数组
     */
    public float[] GetSingle(int index, int length) {
        byte[] tmp = GetBytes(index, length * 4);
        return byteTransform.TransSingle(tmp, 0, length);
    }

    /**
     * 获取float类型的数据到缓存区
     * Get float type data to cache
     *
     * @param index 索引位置
     * @return float数据
     */
    public float GetSingle(int index) {
        return GetSingle(index, 1)[0];
    }

    /**
     * 获取long类型的数组到缓存区
     * Get long type array to cache
     *
     * @param index  索引位置
     * @param length 数组长度
     * @return long数组
     */
    public long[] GetInt64(int index, int length) {
        byte[] tmp = GetBytes(index, length * 8);
        return byteTransform.TransInt64(tmp, 0, length);
    }

    /**
     * 获取long类型的数据到缓存区
     * Get long type data to cache
     *
     * @param index 索引位置
     * @return long数据
     */
    public long GetInt64(int index) {
        return GetInt64(index, 1)[0];
    }

    /**
     * 获取double类型的数组到缓存区
     * Get double type array to cache
     *
     * @param index  索引位置
     * @param length 数组长度
     * @return double数组
     */
    public double[] GetDouble(int index, int length) {
        byte[] tmp = GetBytes(index, length * 8);
        return byteTransform.TransDouble(tmp, 0, length);
    }

    /**
     * 获取double类型的数据到缓存区
     * Get double type data to cache
     *
     * @param index 索引位置
     * @return double数据
     */
    public double GetDouble(int index) {
        return GetDouble(index, 1)[0];
    }

    /**
     * 读取自定义类型的数据，需要规定解析规则
     * Read custom types of data, need to specify the parsing rules
     * @param type  类型名称
     * @param index 起始索引
     * @param <T>   类型对象
     * @return 自定义的数据类型
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public <T extends IDataTransfer> T GetCustomer(Class<T> type, int index) throws InstantiationException, IllegalAccessException {
        T Content = type.newInstance();
        byte[] read = GetBytes(index, Content.getReadCount());
        Content.ParseSource(read);
        return Content;
    }

    /**
     * 写入自定义类型的数据到缓存中去，需要规定生成字节的方法
     * Write custom type data to the cache, need to specify the method of generating bytes
     * @param data  自定义类型
     * @param index 实例对象
     * @param <T>   起始地址
     */
    public <T extends IDataTransfer> void SetCustomer(T data, int index) {
        SetBytes(data.ToSource(), index);
    }

    /**
     * 当前的数据变换机制，当你需要从字节数据转换类型数据的时候需要。
     * The current data transformation mechanism is required when you need to convert type data from byte data.
     * @return 变换的对象
     */
    public IByteTransform getByteTransform() {
        return byteTransform;
    }

    /**
     * 设置字节转换关系
     * @param byteTransform 字节转换关系
     */
    public void setByteTransform(IByteTransform byteTransform) {
        this.byteTransform = byteTransform;
    }

    /**
     * 获取当前的bool操作是否按照字节反转
     * @return
     */
    public boolean getIsBoolReverseByWord() {
        return isBoolReverseByWord;
    }

    /**
     * 设置当前的bool操作是否按照字节反转
     * @param value 设置值
     */
    public void setIsBoolReverseByWord(boolean value) {
        isBoolReverseByWord = value;
    }

    @Override
    public String toString() {
        return "SoftBuffer[" + capacity + "][" + byteTransform.toString() + "]";
    }
}
