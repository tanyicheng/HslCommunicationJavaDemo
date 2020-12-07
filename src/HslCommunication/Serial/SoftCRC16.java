package HslCommunication.Serial;

/**
 * 用于CRC16验证的类，提供了标准的验证方法，可以方便快速的对数据进行CRC校验<br />
 * The class for CRC16 validation provides a standard validation method that makes it easy to CRC data quickly
 */
public class SoftCRC16 {

    /**
     * 来校验对应的接收数据的CRC校验码，默认多项式码为0xA001<br />
     * To verify the CRC check code corresponding to the received data, the default polynomial code is 0xA001
     * @param value 需要校验的数据，带CRC校验码
     * @return 返回校验成功与否
     */
    public static boolean CheckCRC16( byte[] value ) {
        return CheckCRC16( value, (byte) 0xA0, (byte) 0x01 );
    }

    /**
     * 指定多项式码来校验对应的接收数据的CRC校验码<br />
     * Specifies a polynomial code to validate the corresponding CRC check code for the received data
     * @param value 需要校验的数据，带CRC校验码
     * @param CH 多项式码高位
     * @param CL 多项式码低位
     * @return 返回校验成功与否
     */
    public static boolean CheckCRC16( byte[] value, byte CH, byte CL ) {
        if (value == null) return false;
        if (value.length < 2) return false;

        int length = value.length;
        byte[] buf = new byte[length - 2];
        System.arraycopy(value, 0, buf, 0, buf.length);

        byte[] CRCbuf = CRC16(buf, CH, CL);
        if (CRCbuf[length - 2] == value[length - 2] &&
                CRCbuf[length - 1] == value[length - 1]) {
            return true;
        }
        return false;
    }

    /**
     * 获取对应的数据的CRC校验码，默认多项式码为0xA001<br />
     * Get the CRC check code of the corresponding data, the default polynomial code is 0xA001
     * @param value 需要校验的数据，不包含CRC字节
     * @return 返回带CRC校验码的字节数组，可用于串口发送
     */
    public static byte[] CRC16( byte[] value ) {
        return CRC16(value, (byte) 0xA0, (byte) 0x01);
    }

    /**
     * 通过指定多项式码来获取对应的数据的CRC校验码<br />
     * The CRC check code of the corresponding data is obtained by specifying the polynomial code
     * @param value 需要校验的数据，不包含CRC字节
     * @param CH 多项式码地位
     * @param CL 多项式码高位
     * @return 返回带CRC校验码的字节数组，可用于串口发送
     */
    public static byte[] CRC16( byte[] value, byte CH, byte CL ) {
        byte[] buf = new byte[value.length + 2];
        System.arraycopy(value, 0, buf, 0, value.length);

        int CRC16Lo;
        int CRC16Hi;           // CRC寄存器
        int SaveHi;
        int SaveLo;
        byte[] tmpData;
        int Flag;

        // 预置寄存器
        CRC16Lo = 0xFF;
        CRC16Hi = 0xFF;

        tmpData = value;
        for (int i = 0; i < tmpData.length; i++) {
            CRC16Lo = tmpData[i] >= 0 ? CRC16Lo ^ tmpData[i] : CRC16Lo ^ (tmpData[i] + 256); // 每一个数据与CRC寄存器低位进行异或，结果返回CRC寄存器
            for (Flag = 0; Flag <= 7; Flag++) {
                SaveHi = CRC16Hi;
                SaveLo = CRC16Lo;
                CRC16Hi = CRC16Hi >> 1;      // 高位右移一位
                CRC16Lo = CRC16Lo >> 1;      // 低位右移一位
                if ((SaveHi & 0x01) == 0x01) // 如果高位字节最后一位为1
                {
                    // 则低位字节右移后前面补1
                    CRC16Lo = CRC16Lo | 0x80;
                }
                // 否则自动补0

                // 如果最低位为1，则将CRC寄存器与预设的固定值进行异或运算
                if ((SaveLo & 0x01) == 0x01) {
                    CRC16Hi = CH >= 0 ? CRC16Hi ^ CH : CRC16Hi ^ (CH + 256);
                    CRC16Lo = CL >= 0 ? CRC16Lo ^ CL : CRC16Lo ^ (CL + 256);
                }
            }
        }

        buf[buf.length - 2] = (byte) CRC16Lo;
        buf[buf.length - 1] = (byte) CRC16Hi;

        // 返回最终带有CRC校验码结尾的信息
        return buf;
    }
}
