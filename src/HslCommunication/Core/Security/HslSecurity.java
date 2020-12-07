package HslCommunication.Core.Security;

public class HslSecurity {

    /**
     * 加密方法，只对当前的程序集开放
     * @param enBytes 等待加密的数据
     * @return 加密后的数据
     */
    public static byte[] ByteEncrypt(byte[] enBytes) {
        if (enBytes == null) return null;
        byte[] result = new byte[enBytes.length];
        for (int i = 0; i < enBytes.length; i++) {
            result[i] = (byte) (enBytes[i] ^ 0xB5);
        }
        return result;
    }

    /**
     * 解密方法，只对当前的程序集开放
     * @param deBytes 等待解密的数据
     * @return 解密后的数据
     */
    public static byte[] ByteDecrypt(byte[] deBytes) {
        return ByteEncrypt(deBytes);
    }

}
