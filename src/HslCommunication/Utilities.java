package HslCommunication;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;


/**
 * 一个工具类，为了支持和C#程序通信交互的转换工具类
 */
public class Utilities {

    /**
     * 将short数据类型转化成Byte数组
     * @param data short值
     * @return byte[]数组
     */
    public static byte[] getBytes(short data) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data & 0xff00) >> 8);
        return bytes;
    }

    /**
     * 将int数据类型转化成Byte数组
     * @param data int值
     * @return byte[]数组
     */
    public static byte[] getBytes(int data) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data >> 8) & 0xff);
        bytes[2] = (byte) ((data >> 16) & 0xff);
        bytes[3] = (byte) ((data >> 24) & 0xff);
        return bytes;
    }

    /**
     * 将long数据类型转化成Byte数组
     * @param data long值
     * @return byte[]数组
     */
    public static byte[] getBytes(long data) {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data >> 8) & 0xff);
        bytes[2] = (byte) ((data >> 16) & 0xff);
        bytes[3] = (byte) ((data >> 24) & 0xff);
        bytes[4] = (byte) ((data >> 32) & 0xff);
        bytes[5] = (byte) ((data >> 40) & 0xff);
        bytes[6] = (byte) ((data >> 48) & 0xff);
        bytes[7] = (byte) ((data >> 56) & 0xff);
        return bytes;
    }

    /**
     * 将float数据类型转化成Byte数组
     * @param data float值
     * @return byte[]数组
     */
    public static byte[] getBytes(float data) {
        int intBits = Float.floatToIntBits(data);
        return getBytes(intBits);
    }

    /**
     * 将double数据类型转化成Byte数组
     * @param data double值
     * @return byte[]数组
     */
    public static byte[] getBytes(double data) {
        long intBits = Double.doubleToLongBits(data);
        return getBytes(intBits);
    }


    /**
     * 将字符串转换成byte[]数组
     * @param data 字符串值
     * @param charsetName 编码方式
     * @return 字节数组
     */
    public static byte[] getBytes(String data, String charsetName) {
        Charset charset = Charset.forName(charsetName);
        return data.getBytes(charset);
    }

    /**
     * 将ArrayList数组转换为byte[]数据
     * @param data 等待转换的数据信息
     * @return 转换结果
     */
    public static byte[] getBytes(ArrayList<Byte> data){
        if(data == null) return  null;
        byte[] array = new byte[data.size()];
        for(int i = 0; i< data.size(); i++){
            array[i] =  data.get(i);
        }
        return array;
    }

    /**
     * 将ArrayList数组转换为string[]数据
     * @param data 等待转换的数据信息
     * @return 转换结果
     */
    public static String[] getStrings(ArrayList<String> data){
        if(data == null) return  null;
        String[] array = new String[data.size()];
        for(int i = 0; i< data.size(); i++){
            array[i] =  data.get(i);
        }
        return array;
    }

    /**
     * 将int转换成一个字节的数据
     * @param num int数据
     * @return 一个字节的数据
     */
    public static byte int2OneByte(int num) {
        return (byte) (num & 0x000000ff);
    }


    /**
     * 将一个有符号的byte转换成一个int数据对象
     * @param byteNum 有符号的字节对象
     * @return int数据类型
     */
    public static int oneByte2Int(byte byteNum) {
        //针对正数的int
        return byteNum > 0 ? byteNum : 256+ byteNum;
    }

    /**
     * 将字节数组转换成short数据
     * Convert byte array into short data
     * @param bytes 字节数组
     * @param index 起始位置
     * @return short值
     */
    public static short getShort(byte[] bytes,int index) {
        return (short) ((0xff & bytes[0 + index]) | (0xff00 & (bytes[1 + index] << 8)));
    }
    
    /**
     * 将字节数组转换成short数据，采用倒序的表达方式
     * Convert byte array into short data in reverse order
     * @param bytes 字节数组
     * @param index 起始位置
     * @return short值
     */
    public static short getShortReverse(byte[] bytes,int index) {
        return (short) ((0xff & bytes[1 + index]) | (0xff00 & (bytes[0 + index] << 8)));
    }

    /**
     * 将字节数组转换成 ushort 数据
     * Convert byte array into ushort data
     * @param bytes 字节数组
     * @param index 起始位置
     * @return short值
     */
    public static int getUShort(byte[] bytes,int index) {
        return ((0xff & bytes[0 + index]) | (0xff00 & (bytes[1 + index] << 8)));
    }
    
    /**
     * 将字节数组转换成 ushort 数据，采用倒序的方式
     * Convert byte array into ushort data in reverse order
     * @param bytes 字节数组
     * @param index 起始位置
     * @return short值
     */
    public static int getUShortReverse(byte[] bytes,int index) {
        return ((0xff & bytes[1 + index]) | (0xff00 & (bytes[0 + index] << 8)));
    }

    /**
     * 将字节数组转换成int数据
     * @param bytes 字节数组
     * @param index 起始位置
     * @return int值
     */
    public static int getInt(byte[] bytes,int index) {
        return (0xff & bytes[0 + index]) |
                (0xff00 & (bytes[1 + index] << 8)) |
                (0xff0000 & (bytes[2 + index] << 16)) |
                (0xff000000 & (bytes[3 + index] << 24));
    }

    /**
     * 将字节数组转换成uint数据
     * @param bytes 字节数组
     * @param index 起始位置
     * @return int值
     */
    public static long getUInt(byte[] bytes,int index) {
        int value = getInt(bytes, index);
        if (value >= 0) return value;
        return 65536L * 65536L + value;
    }

    /**
     * 将字节数组转换成int数据，采用倒序的方式
     * @param bytes 字节数组
     * @param index 起始位置
     * @return int值
     */
    public static int getIntReverse(byte[] bytes,int index) {
        return (0xff &         bytes[3 + index]) |
                (0xff00 &     (bytes[2 + index] << 8)) |
                (0xff0000 &   (bytes[1 + index] << 16)) |
                (0xff000000 & (bytes[0 + index] << 24));
    }

    /**
     * 将字节数组转换成uint数据
     * @param bytes 字节数组
     * @param index 起始位置
     * @return int值
     */
    public static long getUIntReverse(byte[] bytes,int index) {
        int value = getIntReverse(bytes, index);
        if (value >= 0) return value;
        return 65536L * 65536L + value;
    }

    /**
     * 将字节数组转换成long数据
     * @param bytes 字节数组
     * @param index 起始位置
     * @return long值
     */
    public static long getLong(byte[] bytes,int index) {
        return (0xffL & (long) bytes[0 + index]) |
                (0xff00L & ((long) bytes[1 + index] << 8)) |
                (0xff0000L & ((long) bytes[2 + index] << 16)) |
                (0xff000000L & ((long) bytes[3 + index] << 24)) |
                (0xff00000000L & ((long) bytes[4 + index] << 32)) |
                (0xff0000000000L & ((long) bytes[5 + index] << 40)) |
                (0xff000000000000L & ((long) bytes[6 + index] << 48)) |
                (0xff00000000000000L & ((long) bytes[7 + index] << 56));
    }

    /**
     * 将字节数组转换成long数据
     * @param bytes 字节数组
     * @param index 起始位置
     * @return long值
     */
    public static long getLongReverse(byte[] bytes,int index) {
        return (0xffL & (long) bytes[7 + index]) |
                (0xff00L & ((long) bytes[6 + index] << 8)) |
                (0xff0000L & ((long) bytes[5 + index] << 16)) |
                (0xff000000L & ((long) bytes[4 + index] << 24)) |
                (0xff00000000L & ((long) bytes[3 + index] << 32)) |
                (0xff0000000000L & ((long) bytes[2 + index] << 40)) |
                (0xff000000000000L & ((long) bytes[1 + index] << 48)) |
                (0xff00000000000000L & ((long) bytes[0 + index] << 56));
    }

    /**
     * 将字节数组转换成float数据
     * @param bytes 字节数组
     * @param index 起始位置
     * @return float值
     */
    public static float getFloat(byte[] bytes,int index) {
        return Float.intBitsToFloat(getInt(bytes,index));
    }

    /**
     * 将字节数组转换成double数据
     * @param bytes 字节数组
     * @param index 起始位置
     * @return double值
     */
    public static double getDouble(byte[] bytes, int index) {
        long l = getLong(bytes, index);
        System.out.println(l);
        return Double.longBitsToDouble(l);
    }

    /**
     * 将字节数组转换成string数据
     * @param bytes 字节数组
     * @param charsetName 字符编码
     * @return string值
     */
    public static String getString(byte[] bytes, String charsetName) {
        return new String(bytes, Charset.forName(charsetName));
    }

    /**
     * 将字节数组转换成string数据
     * @param bytes 字节数组
     * @param index 起始位置
     * @param length 数据长度
     * @param charsetName 字符编码
     * @return string值
     */
    public static String getString(byte[] bytes,int index, int length, String charsetName) {
        return new String(bytes,index,length,Charset.forName(charsetName));
    }

    /**
     * 将一个byte[]数组转换成uuid对象
     * @param data 字节数组
     * @return uuid对象
     */
    public static UUID Byte2UUID(byte[] data) {
        if (data.length != 16) {
            throw new IllegalArgumentException("Invalid UUID byte[]");
        }
        long msb = 0;
        long lsb = 0;
        for (int i = 0; i < 8; i++)
            msb = (msb << 8) | (data[i] & 0xff);
        for (int i = 8; i < 16; i++)
            lsb = (lsb << 8) | (data[i] & 0xff);

        return new UUID(msb, lsb);
    }


    /**
     * 将一个uuid对象转化成byte[]
     * @param uuid uuid对象
     * @return 字节数组
     */
    public static byte[] UUID2Byte(UUID uuid) {
        ByteArrayOutputStream ba = new ByteArrayOutputStream(16);
        DataOutputStream da = new DataOutputStream(ba);
        try {
            da.writeLong(uuid.getMostSignificantBits());
            da.writeLong(uuid.getLeastSignificantBits());
            ba.close();
            da.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        byte[] buffer = ba.toByteArray();
        // 进行错位
        byte temp=buffer[0];
        buffer[0] = buffer[3];
        buffer[3] =temp;
        temp=buffer[1];
        buffer[1]=buffer[2];
        buffer[2]=temp;

        temp = buffer[4];
        buffer[4]=buffer[5];
        buffer[5] =temp;

        temp = buffer[6];
        buffer[6]=buffer[7];
        buffer[7] =temp;

        return buffer;
    }

    /**
     * 将字符串数据转换成字节数组，主要转换由C#的字符串的数据
     * @param str 字符串信息
     * @return 转化后的字节数组
     */
    public static byte[] csharpString2Byte(String str) {
        if (str == null) {
            return null;
        }
        byte[] byteArray;
        try {
            byteArray = str.getBytes("unicode");
        } catch (Exception ex) {
            byteArray = str.getBytes();
        }

        if (byteArray.length >= 2) {
            if (byteArray[0] == -1 && byteArray[1] == -2) {
                byte[] newArray = new byte[byteArray.length - 2];
                System.arraycopy(byteArray, 2, newArray, 0, newArray.length);
                byteArray = newArray;
            } else if (byteArray[0] == -2 && byteArray[1] == -1) {
                for (int i = 0; i < byteArray.length; i++) {
                    byte temp = byteArray[i];
                    byteArray[i] = byteArray[i + 1];
                    byteArray[i + 1] = temp;
                    i++;
                }

                byte[] newArray = new byte[byteArray.length - 2];
                System.arraycopy(byteArray, 2, newArray, 0, newArray.length);
                byteArray = newArray;
            }
        }

        return byteArray;
    }

    /**
     * 将字节数组转换成字符串对象，主要转换由C#的字符串的数据
     * @param byteArray 缓存数据
     * @return 结果
     */
    public static String byte2CSharpString(byte[] byteArray) {
        return byte2CSharpString(byteArray, 0, byteArray.length);
    }

    /**
     * 将字节数组转换成字符串对象，主要转换由C#的字符串的数据
     * @param byteArray 缓存数据
     * @param charsetName 编码信息
     * @return 结果
     */
    public static String byte2CSharpString(byte[] byteArray, String charsetName) {
        return byte2CSharpString(byteArray, 0, byteArray.length, charsetName);
    }

    /**
     * 将字节数组转换成字符串对象，主要转换由C#的字符串的数据
     * @param byteArray 缓存数据
     * @return 结果
     */
    public static String byte2CSharpString(byte[] byteArray, int index, int length) {
        return byte2CSharpString(byteArray, index, length, "unicode");
    }

    /**
     * 将字节数组转换成字符串对象，主要转换由C#的字符串的数据
     * @param byteArray 缓存数据
     * @param index 起始的位置
     * @param length 长度
     * @param charsetName 编码
     * @return 结果
     */
    public static String byte2CSharpString(byte[] byteArray, int index, int length, String charsetName) {
        if (byteArray == null) return null;

        byte[] buffer = new byte[length];
        for (int i = index; i < index + length; i++) {
            if (i < byteArray.length) buffer[i - index] = byteArray[i];
        }
        for (int i = 0; i < buffer.length; i++) {
            byte temp = buffer[i];
            buffer[i] = buffer[i + 1];
            buffer[i + 1] = temp;
            i++;
        }
        String str;
        try {
            str = new String(buffer, charsetName);
        } catch (Exception ex) {
            str = new String(byteArray);
        }
        return str;
    }

    /**
     * 将byte[]数组的数据进行翻转
     * @param reverse 等待反转的字符串
     */
    public static void bytesReverse(byte[] reverse) {
        if (reverse != null) {
            byte tmp = 0;
            for (int i = 0; i < reverse.length / 2; i++) {
                tmp = reverse[i];
                reverse[i] = reverse[reverse.length - 1 - i];
                reverse[reverse.length - 1 - i] = tmp;
            }
        }
    }

    /**
     * 往列表 arrayList 里添加 byte[] 数组数据
     * @param arrayList 数组信息
     * @param bytes 实际的数据
     */
    public static void ArrayListAddArray(ArrayList<Byte> arrayList, byte[] bytes){
        if(bytes!=null){
            for(int i = 0;i<bytes.length;i++){
                arrayList.add(bytes[i]);
            }
        }
    }

    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};


    /**
     * 将字节数组转换成十六进制的字符串形式
     * @param bytes 原始的字节数组
     * @return 字符串信息
     */
    public static String bytes2HexString(byte[] bytes) {
        char[] buf = new char[bytes.length * 2];
        int index = 0;
        for(byte b : bytes) { // 利用位运算进行转换，可以看作方法一的变种
            buf[index++] = HEX_CHAR[b >>> 4 & 0xf];
            buf[index++] = HEX_CHAR[b & 0xf];
        }

        return new String(buf);
    }

    /**
     * 获取指定时间的指定格式的字符串
     * @param date 指定的时间
     * @param format 指定的格式
     * @return 最后字符串信息
     */
    public static String getStringDateShort(Date date,String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String dateString = formatter.format(date);
        return dateString;
    }

    /**
     * 判断字符串是否为NULL或是空字符串
     * @param string 字符串
     * @return 判断的结果
     */
    public static boolean IsStringNullOrEmpty(String string){
        return (string == null || string.length() <= 0);
    }
}
