package HslCommunication.Core.Transfer;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Types.FunctionOperateExOne;
import HslCommunication.Core.Types.OperateResultExOne;

public class ByteTransformHelper {

    /**
     * 结果转换操作的基础方法，需要支持类型，及转换的委托
     *
     * @param result     数据源
     * @param translator 转换方式
     * @param <TResult>  结果类型
     * @return 最新的结果对象
     */
    public static <TResult> OperateResultExOne<TResult> GetResultFromBytes(OperateResultExOne<byte[]> result, FunctionOperateExOne<byte[], TResult> translator) {
        try {
            if (result.IsSuccess) {
                return OperateResultExOne.CreateSuccessResult(translator.Action(result.Content));
            } else {
                return OperateResultExOne.<TResult>CreateFailedResult(result);
            }
        } catch (Exception ex) {
            return new OperateResultExOne<>("数据转化失败，源数据：" + SoftBasic.ByteToHexString(result.Content) + " 消息：" + ex.getMessage());
        }
    }

    /**
     * 结果转换操作的基础方法，需要支持类型，及转换的委托
     *
     * @param result    结果类型
     * @param <TResult> 源结果
     * @return 转换结果
     */
    public static <TResult> OperateResultExOne<TResult> GetResultFromArray(OperateResultExOne<TResult[]> result) {
        if (!result.IsSuccess) return OperateResultExOne.<TResult>CreateFailedResult(result);

        return OperateResultExOne.CreateSuccessResult(result.Content[0]);
    }


    /**
     * 结果转换操作的基础方法，需要支持类型，及转换的委托
     *
     * @param result     结果类型
     * @param <TResult1> 目标结果类型
     * @param <TResult2> 源结果类型
     * @return 转换结果
     */
    public static <TResult1, TResult2> OperateResultExOne<TResult1> GetTResultFromArray(OperateResultExOne<TResult2[]> result) {
        if (!result.IsSuccess) return OperateResultExOne.<TResult1>CreateFailedResult(result);

        return OperateResultExOne.CreateSuccessResult((TResult1) result.Content[0]);
    }

    /**
     * 将指定的OperateResult类型转化
     *
     * @param result        原始的类型
     * @param byteTransform 数据转换方法
     * @return 转化后的类型
     */
    public static OperateResultExOne<Boolean> GetBoolResultFromBytes(OperateResultExOne<byte[]> result, final IByteTransform byteTransform) {
        return GetResultFromBytes(result, new FunctionOperateExOne<byte[], Boolean>() {
            @Override
            public Boolean Action(byte[] content) {
                return byteTransform.TransBool(content, 0);
            }
        });
    }


    /**
     * 将指定的OperateResult类型转化
     *
     * @param result        原始的类型
     * @param byteTransform 数据转换方法
     * @return 转化后的类型
     */
    public static OperateResultExOne<Byte> GetByteResultFromBytes(OperateResultExOne<byte[]> result, final IByteTransform byteTransform) {
        return GetResultFromBytes(result, new FunctionOperateExOne<byte[], Byte>() {
            @Override
            public Byte Action(byte[] content) {
                return byteTransform.TransByte(content, 0);
            }
        });
    }


    /**
     * 将指定的OperateResult类型转化
     *
     * @param result        原始的类型
     * @param byteTransform 数据转换方法
     * @return 转化后的类型
     */
    public static OperateResultExOne<Short> GetInt16ResultFromBytes(OperateResultExOne<byte[]> result, final IByteTransform byteTransform) {
        return GetResultFromBytes(result, new FunctionOperateExOne<byte[], Short>() {
            @Override
            public Short Action(byte[] content) {
                return byteTransform.TransInt16(content, 0);
            }
        });
    }


    /**
     * 将指定的OperateResult类型转化
     *
     * @param result        原始的类型
     * @param byteTransform 数据转换方法
     * @return 转化后的类型
     */
    public static OperateResultExOne<Integer> GetInt32ResultFromBytes(OperateResultExOne<byte[]> result, final IByteTransform byteTransform) {
        return GetResultFromBytes(result, new FunctionOperateExOne<byte[], Integer>() {
            @Override
            public Integer Action(byte[] content) {
                return byteTransform.TransInt32(content, 0);
            }
        });
    }


    /**
     * 将指定的OperateResult类型转化
     *
     * @param result        原始的类型
     * @param byteTransform 数据转换方法
     * @return 转化后的类型
     */
    public static OperateResultExOne<Long> GetInt64ResultFromBytes(OperateResultExOne<byte[]> result, final IByteTransform byteTransform) {
        return GetResultFromBytes(result, new FunctionOperateExOne<byte[], Long>() {
            @Override
            public Long Action(byte[] content) {
                return byteTransform.TransInt64(content, 0);
            }
        });
    }


    /**
     * 将指定的OperateResult类型转化
     *
     * @param result        原始的类型
     * @param byteTransform 数据转换方法
     * @return 转化后的类型
     */
    public static OperateResultExOne<Float> GetSingleResultFromBytes(OperateResultExOne<byte[]> result, final IByteTransform byteTransform) {
        return GetResultFromBytes(result, new FunctionOperateExOne<byte[], Float>() {
            @Override
            public Float Action(byte[] content) {
                return byteTransform.TransSingle(content, 0);
            }
        });
    }


    /**
     * 将指定的OperateResult类型转化
     *
     * @param result        原始的类型
     * @param byteTransform 数据转换方法
     * @return 转化后的类型
     */
    public static OperateResultExOne<Double> GetDoubleResultFromBytes(OperateResultExOne<byte[]> result, final IByteTransform byteTransform) {
        return GetResultFromBytes(result, new FunctionOperateExOne<byte[], Double>() {
            @Override
            public Double Action(byte[] content) {
                return byteTransform.TransDouble(content, 0);
            }
        });
    }


    /**
     * 将指定的OperateResult类型转化
     *
     * @param result        原始的类型
     * @param byteTransform 数据转换方法
     * @return 转化后的类型
     */
    public static OperateResultExOne<String> GetStringResultFromBytes(OperateResultExOne<byte[]> result, final IByteTransform byteTransform) {
        return GetResultFromBytes(result, new FunctionOperateExOne<byte[], String>() {
            @Override
            public String Action(byte[] content) {
                return byteTransform.TransString(content, 0, content.length, "US-ASCII");
            }
        });
    }

}
