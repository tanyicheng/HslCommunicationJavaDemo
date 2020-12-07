package HslCommunication.Core.Net.NetworkBase;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Net.IReadWriteNet;
import HslCommunication.Core.Transfer.ByteTransformHelper;
import HslCommunication.Core.Types.FunctionOperateExOne;
import HslCommunication.Core.Types.IDataTransfer;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.StringResources;
import HslCommunication.Utilities;


/**
 * 设备交互类的基类，实现了 {@link IReadWriteNet} 接口的基础方法方法，需要使用继承重写来实现字节读写，bool读写操作。<br />
 * The base class of the device interaction class, which implements the basic methods of the {@link IReadWriteNet} interface,
 * requires inheritance rewriting to implement byte read and write, and bool read and write operations.
 */
public class NetworkDeviceBase extends NetworkDoubleBase implements IReadWriteNet {
    /**
     * 一个字单位的数据表示的地址长度，西门子为2，三菱，欧姆龙，modbusTcp就为1，AB PLC无效<br />
     * The address length represented by one word of data, Siemens is 2, Mitsubishi, Omron, modbusTcp is 1, AB PLC is invalid
     */
    protected short WordLength = 1;

    // region Read Write Bytes bool

    public OperateResultExOne<byte[]> Read(String address, short length) {
        return new OperateResultExOne<byte[]>(StringResources.Language.NotSupportedFunction());
    }

    public OperateResult Write(String address, byte[] value) {
        return new OperateResult(StringResources.Language.NotSupportedFunction());
    }


    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        return new OperateResultExOne<boolean[]>(StringResources.Language.NotSupportedFunction());
    }

    public OperateResultExOne<Boolean> ReadBool(String address) {
        OperateResultExOne<boolean[]> read = ReadBool(address, (short) 1);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(read.Content[0]);
    }

    public OperateResult Write(String address, boolean[] value) {
        return new OperateResult(StringResources.Language.NotSupportedFunction());
    }

    public OperateResult Write(String address, boolean value) {
        return Write(address, new boolean[]{value});
    }

    // endregion

    public <T extends IDataTransfer> OperateResultExOne<T> ReadCustomer(String address, Class<T> tClass) {
        OperateResultExOne<T> result = new OperateResultExOne<T>();
        T Content;
        try {
            Content = tClass.newInstance();
        } catch (Exception ex) {
            Content = null;
        }
        OperateResultExOne<byte[]> read = Read(address, Content.getReadCount());
        if (read.IsSuccess) {
            Content.ParseSource(read.Content);
            result.Content = Content;
            result.IsSuccess = true;
        } else {
            result.ErrorCode = read.ErrorCode;
            result.Message = read.Message;
        }
        return result;
    }

    public <T extends IDataTransfer> OperateResult WriteCustomer(String address, T data) {
        return Write(address, data.ToSource());
    }

    public OperateResultExOne<Short> ReadInt16(String address) {
        OperateResultExOne<short[]> read = ReadInt16(address, (short) 1);
        if (!read.IsSuccess)
            return OperateResultExOne.CreateFailedResult(read);
        else
            return OperateResultExOne.<Short>CreateSuccessResult(read.Content[0]);
    }

    public OperateResultExOne<short[]> ReadInt16(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(Read(address, (short) (length * WordLength)), new FunctionOperateExOne<byte[], short[]>() {
            @Override
            public short[] Action(byte[] content) {
                return getByteTransform().TransInt16(content, 0, length);
            }
        });
    }

    public OperateResultExOne<Integer> ReadUInt16(String address) {
        OperateResultExOne<int[]> read = ReadUInt16(address, (short) 1);
        if (!read.IsSuccess)
            return OperateResultExOne.CreateFailedResult(read);
        else
            return OperateResultExOne.<Integer>CreateSuccessResult(read.Content[0]);
    }

    public OperateResultExOne<int[]> ReadUInt16(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(Read(address, (short) (length * WordLength)), new FunctionOperateExOne<byte[], int[]>() {
            @Override
            public int[] Action(byte[] content) {
                return getByteTransform().TransUInt16(content, 0, length);
            }
        });
    }

    public OperateResultExOne<Integer> ReadInt32(String address) {
        OperateResultExOne<int[]> read = ReadInt32(address, (short) 1);
        if (!read.IsSuccess)
            return OperateResultExOne.CreateFailedResult(read);
        else
            return OperateResultExOne.<Integer>CreateSuccessResult(read.Content[0]);
    }

    public OperateResultExOne<int[]> ReadInt32(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(Read(address, (short) (length * WordLength * 2)), new FunctionOperateExOne<byte[], int[]>() {
            @Override
            public int[] Action(byte[] content) {
                return getByteTransform().TransInt32(content, 0, length);
            }
        });
    }

    public OperateResultExOne<Long> ReadUInt32(String address) {
        OperateResultExOne<long[]> read = ReadUInt32(address, (short) 1);
        if (!read.IsSuccess)
            return OperateResultExOne.CreateFailedResult(read);
        else
            return OperateResultExOne.<Long>CreateSuccessResult(read.Content[0]);
    }

    public OperateResultExOne<long[]> ReadUInt32(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(Read(address, (short) (length * WordLength * 2)), new FunctionOperateExOne<byte[], long[]>() {
            @Override
            public long[] Action(byte[] content) {
                return getByteTransform().TransUInt32(content, 0, length);
            }
        });
    }

    public OperateResultExOne<Float> ReadFloat(String address) {
        OperateResultExOne<float[]> read = ReadFloat(address, (short) 1);
        if (!read.IsSuccess)
            return OperateResultExOne.CreateFailedResult(read);
        else
            return OperateResultExOne.<Float>CreateSuccessResult(read.Content[0]);
    }

    public OperateResultExOne<float[]> ReadFloat(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(Read(address, (short) (length * WordLength * 2)), new FunctionOperateExOne<byte[], float[]>() {
            @Override
            public float[] Action(byte[] content) {
                return getByteTransform().TransSingle(content, 0, length);
            }
        });
    }

    public OperateResultExOne<Long> ReadInt64(String address) {
        OperateResultExOne<long[]> read = ReadInt64(address, (short) 1);
        if (!read.IsSuccess)
            return OperateResultExOne.CreateFailedResult(read);
        else
            return OperateResultExOne.<Long>CreateSuccessResult(read.Content[0]);
    }

    public OperateResultExOne<long[]> ReadInt64(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(Read(address, (short) (length * WordLength * 4)), new FunctionOperateExOne<byte[], long[]>() {
            @Override
            public long[] Action(byte[] content) {
                return getByteTransform().TransInt64(content, 0, length);
            }
        });
    }

    public OperateResultExOne<Double> ReadDouble(String address) {
        OperateResultExOne<double[]> read = ReadDouble(address, (short) 1);
        if (!read.IsSuccess)
            return OperateResultExOne.CreateFailedResult(read);
        else
            return OperateResultExOne.<Double>CreateSuccessResult(read.Content[0]);
    }

    public OperateResultExOne<double[]> ReadDouble(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(Read(address, (short) (length * WordLength * 4)), new FunctionOperateExOne<byte[], double[]>() {
            @Override
            public double[] Action(byte[] content) {
                return getByteTransform().TransDouble(content, 0, length);
            }
        });
    }

    public OperateResultExOne<String> ReadString(String address, short length) {
        return ByteTransformHelper.GetResultFromBytes(Read(address, length), new FunctionOperateExOne<byte[], String>() {
            @Override
            public String Action(byte[] content) {
                return getByteTransform().TransString(content, 0, content.length, "ascii");
            }
        });
    }

    public OperateResultExOne<String> ReadString(String address, short length, final String encoding) {
        return ByteTransformHelper.GetResultFromBytes(Read(address, length), new FunctionOperateExOne<byte[], String>() {
            @Override
            public String Action(byte[] content) {
                return getByteTransform().TransString(content, 0, content.length, encoding);
            }
        });
    }

    public OperateResult Write(String address, short[] values) {
        return Write(address, super.getByteTransform().TransByte(values));
    }

    public OperateResult Write(String address, short value) {
        return Write(address, new short[]{value});
    }

    public OperateResult Write(String address, int[] values) {
        return Write(address, super.getByteTransform().TransByte(values));
    }

    public OperateResult Write(String address, int value) {
        return Write(address, new int[]{value});
    }

    public OperateResult Write(String address, float[] values) {
        return Write(address, super.getByteTransform().TransByte(values));
    }

    public OperateResult Write(String address, float value) {
        return Write(address, new float[]{value});
    }

    public OperateResult Write(String address, long[] values) {
        return Write(address, getByteTransform().TransByte(values));
    }

    public OperateResult Write(String address, long value) {
        return Write(address, new long[]{value});
    }

    public OperateResult Write(String address, double[] values) {
        return Write(address, getByteTransform().TransByte(values));
    }

    public OperateResult Write(String address, double value) {
        return Write(address, new double[]{value});
    }

    public OperateResult Write(String address, String value) {
        return Write(address, value, "US-ASCII");
    }

    public OperateResult Write(String address, String value, String encoding) {
        byte[] temp = getByteTransform().TransByte(value, encoding);
        if (WordLength == 1) temp = SoftBasic.ArrayExpandToLengthEven(temp);
        return Write(address, temp);
    }

    public OperateResult Write(String address, String value, int length) {
        return Write(address, value, length, "US-ASCII");
    }

    public OperateResult Write(String address, String value, int length, String encoding) {
        byte[] temp = getByteTransform().TransByte(value, encoding);
        if (WordLength == 1) temp = SoftBasic.ArrayExpandToLengthEven(temp);
        temp = SoftBasic.ArrayExpandToLength(temp, length);
        return Write(address, temp);
    }

    public OperateResult WriteUnicodeString(String address, String value) {
        byte[] temp = Utilities.csharpString2Byte(value);
        return Write(address, temp);
    }

    public OperateResult WriteUnicodeString(String address, String value, int length) {
        byte[] temp = Utilities.csharpString2Byte(value);
        temp = SoftBasic.ArrayExpandToLength(temp, length * 2);
        return Write(address, temp);
    }

    @Override
    public String toString() {
        return "NetworkDeviceBase<" + GetNewNetMessage().getClass().toString() + ", " +
                getByteTransform().getClass().toString() + ">[" + getIpAddress() + ":" + getPort() + "]";
    }
}
