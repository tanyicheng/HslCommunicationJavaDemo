package HslCommunication.Core.Transfer;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Utilities;

/**
 * 以字节为单位的变换操作
 */
public class ReverseWordTransform extends ByteTransformBase {
    /**
     * 实例化一个默认的对象
     */
    public ReverseWordTransform() {
        this.setDataFormat(DataFormat.ABCD);
    }


    /**
     * 实例化一个默认的对象
     */
    public ReverseWordTransform(DataFormat dataFormat) {
        super(dataFormat);
    }


    /**
     * 按照字节错位的方法
     * @param buffer 实际的字节数据
     * @param index  起始字节位置
     * @param length 数据长度
     * @return 反转后的结果字节数组
     */
    private byte[] ReverseBytesByWord(byte[] buffer, int index, int length) {
        byte[] tmp = new byte[length];

        for (int i = 0; i < length; i++) {
            tmp[i] = buffer[index + i];
        }

        for (int i = 0; i < length / 2; i++) {
            byte b = tmp[i * 2 + 0];
            tmp[i * 2 + 0] = tmp[i * 2 + 1];
            tmp[i * 2 + 1] = b;
        }

        return tmp;
    }

    private byte[] ReverseBytesByWord(byte[] buffer) {
        return ReverseBytesByWord(buffer, 0, buffer.length);
    }


    public short TransInt16(byte[] buffer, int index) {
        return Utilities.getShort(ReverseBytesByWord(buffer, index, 2), 0);
    }

    public int TransUInt16(byte[] buffer, int index) {
        return Utilities.getUShort(ReverseBytesByWord(buffer, index, 2), 0);
    }

    public String TransString(byte[] buffer, int index, int length, String encoding) {
        byte[] tmp = TransByte(buffer, index, length);

        if (getIsStringReverse()) {
            return Utilities.getString(ReverseBytesByWord(tmp), "US-ASCII");
        } else {
            return Utilities.getString(tmp, "US-ASCII");
        }
    }

    public byte[] TransByte(boolean[] values) {
        return SoftBasic.BoolArrayToByte(values);
    }

    public byte[] TransByte(short[] values) {
        if (values == null) return null;

        byte[] buffer = new byte[values.length * 2];
        for (int i = 0; i < values.length; i++) {
            byte[] tmp = Utilities.getBytes(values[i]);
            System.arraycopy(tmp, 0, buffer, 2 * i, tmp.length);
        }

        return ReverseBytesByWord(buffer);
    }

    public byte[] TransByte(String value, String encoding) {
        if (value == null) return null;
        byte[] buffer = Utilities.getBytes(value, encoding);
        buffer = SoftBasic.ArrayExpandToLengthEven(buffer);
        if (getIsStringReverse()) {
            return ReverseBytesByWord(buffer);
        } else {
            return buffer;
        }
    }
}
