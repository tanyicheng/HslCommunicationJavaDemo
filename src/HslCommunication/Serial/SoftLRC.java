package HslCommunication.Serial;

import HslCommunication.BasicFramework.SoftBasic;

/**
 * 用于LRC验证的类，提供了标准的验证方法<br />
 * The class used for LRC verification provides a standard verification method
 */
public class SoftLRC {
    /**
     * 获取对应的数据的LRC校验码<br />
     * Class for LRC validation that provides a standard validation method
     * @param value 需要校验的数据，不包含LRC字节
     * @return 返回带LRC校验码的字节数组，可用于串口发送
     */
    public static byte[] LRC( byte[] value ) {
        if (value == null) return null;

        int sum = 0;
        for (int i = 0; i < value.length; i++) {
            sum += value[i] & 0xFF;
        }

        sum = sum % 256;
        sum = 256 - sum;

        byte[] LRC = new byte[]{(byte) sum};
        return SoftBasic.SpliceTwoByteArray(value, LRC);
    }

    /**
     * 检查数据是否符合LRC的验证<br />
     * Check data for compliance with LRC validation
     * @param value 等待校验的数据，是否正确
     * @return 是否校验成功
     */
    public static boolean CheckLRC( byte[] value ) {
        if (value == null) return false;

        int length = value.length;
        byte[] buf = new byte[length - 1];
        System.arraycopy(value, 0, buf, 0, buf.length);

        byte[] LRCbuf = LRC(buf);
        if (LRCbuf[length - 1] == value[length - 1]) {
            return true;
        }
        return false;
    }
}
