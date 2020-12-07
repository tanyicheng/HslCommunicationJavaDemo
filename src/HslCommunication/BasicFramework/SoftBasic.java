package HslCommunication.BasicFramework;


import HslCommunication.StringResources;
import HslCommunication.Utilities;
import org.omg.CORBA.Environment;

import javax.swing.plaf.synth.Region;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 一个软件基础类，提供常用的一些静态方法，比如字符串转换，字节转换的方法<br />
 * A software-based class that provides some common static methods，Such as string conversion, byte conversion method
 */
public class SoftBasic {

    // region MD5 Calculate

    /**
     * 获取文本字符串信息的Md5码，编码为UTF8<br />
     * Get the Md5 code of the text string information, using the utf-8 encoding
     * @param data 文本数据信息
     * @return Md5字符串
     */
    public static String CalculateStreamMD5( String data) {
        return CalculateStreamMD5( data, "utf8" );
    }

    /**
     * 获取文本字符串信息的Md5码，使用指定的编码<br />
     * Get the Md5 code of the text string information, using the specified encoding
     * @param data 文本数据信息
     * @param charsetName 编码信息
     * @return Md5字符串
     */
    public static String CalculateStreamMD5( String data, String charsetName ) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    Utilities.getBytes(data, charsetName));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("None md5!");
        }
        return ByteToHexString(secretBytes);
    }


    // endregion

    /**
     * 从一个字节大小返回带单位的描述，主要是用于显示操作<br />
     * Returns a description with units from a byte size, mainly for display operations
     * @param size 数据大小
     * @return 字符串文本
     */
    public static String GetSizeDescription(long size) {
        if (size < 1000) {
            return size + " B";
        } else if (size < 1000 * 1000) {
            float data = (float) size / 1024;
            return String.format("%.2f", data) + " Kb";
        } else if (size < 1000 * 1000 * 1000) {
            float data = (float) size / 1024 / 1024;
            return String.format("%.2f", data) + " Mb";
        } else {
            float data = (float) size / 1024 / 1024 / 1024;
            return String.format("%.2f", data) + " Gb";
        }
    }

    // region TimeSpan Format

    /**
     * 从一个时间差返回带单位的描述，主要是用于显示操作。<br />
     * Returns a description with units from a time difference, mainly for display operations.
     * @param secondTicks 当前的秒数信息
     * @return 最终的字符串值
     */
    public static String GetTimeSpanDescription( long secondTicks ) {
        if (secondTicks <= 60) {
            return secondTicks + StringResources.Language.TimeDescriptionSecond();
        } else if (secondTicks <= 60 * 60) {
            float data = (float) secondTicks / 60;
            return String.format("%.1f", data) + StringResources.Language.TimeDescriptionMinute();
        } else if (secondTicks <= 24 * 60 * 60) {
            float data = (float) secondTicks / 60 / 60;
            return String.format("%.2f", data) + StringResources.Language.TimeDescriptionHour();
        } else {
            float data = (float) secondTicks / 60 / 60 / 24;
            return String.format("%.2f", data) + StringResources.Language.TimeDescriptionDay();
        }
    }

    //endregion

    // region Array Format

    /**
     * 将数组格式化为显示的字符串的信息，支持所有的类型对象<br />
     * Formats the array into the displayed string information, supporting all types of objects
     * @param array 数组信息
     * @param <T> 数组的类型
     * @return 最终显示的信息
     */
    public static <T> String ArrayFormat(T[] array ){
        return ArrayFormat(array, "");
    }

    /**
     * 将数组格式化为显示的字符串的信息，支持所有的类型对象<br />
     * Formats the array into the displayed string information, supporting all types of objects
     * @param array 数组信息
     * @param format 格式化的信息
     * @param <T> 数组的类型
     * @return 最终显示的信息
     */
    public static <T> String ArrayFormat( T[] array, String format ){
        if (array == null) return "NULL";
        StringBuilder sb = new StringBuilder( "[" );
        for (int i = 0; i < array.length; i++)
        {
            sb.append( Utilities.IsStringNullOrEmpty( format ) ? array[i].toString( ) : String.format( format, array[i] ) );
            if (i != array.length - 1) sb.append( "," );
        }
        sb.append( "]" );
        return sb.toString( );
    }

    /**
     * 将数组格式化为显示的字符串的信息，支持所有的类型对象<br />
     * Formats the array into the displayed string information, supporting all types of objects
     * @param array 数组信息
     * @param <T> 数组的类型
     * @return 最终显示的信息
     */
    public static <T> String ArrayFormat( T array ){
        return  ArrayFormat(array, "");
    }

    /**
     * 将数组格式化为显示的字符串的信息，支持所有的类型对象<br />
     * Formats the array into the displayed string information, supporting all types of objects
     * @param array 数组信息
     * @param format 格式化的信息
     * @param <T> 数组的类型
     * @return 最终显示的信息
     */
    public static <T> String ArrayFormat( T array, String format ) {
        StringBuilder sb = new StringBuilder("[");
        if (array.getClass().isArray()) {
            for (int i = 0; i < Array.getLength(array); i++) {
                sb.append(Utilities.IsStringNullOrEmpty(format) ? Array.get(array, i).toString() : String.format(format, Array.get(array, i)));
                sb.append(",");
            }
            if (Array.getLength(array) > 0 && sb.charAt(sb.length() - 1) == ',')
                sb.delete(sb.length() - 1, sb.length());
        } else {
            sb.append(Utilities.IsStringNullOrEmpty(format) ? array.toString() : String.format(format, array));
        }
        sb.append("]");
        return sb.toString();
    }

    // endregion

    // region Array Expand

    /**
     * 一个通用的数组新增个数方法，会自动判断越界情况，越界的情况下，会自动的截断或是填充<br />
     * A common array of new methods, will automatically determine the cross-border situation, in the case of cross-border, will be automatically truncated or filled
     * @param tClass 类型信息
     * @param array 原数据
     * @param data 等待新增的数据
     * @param max 原数据的最大值
     * @param <T> 数据类型
     * @return 新的数组信息
     */
    public static <T> T[] AddArrayData(Class<T> tClass, T[] array, T[] data, int max) {
        if (data == null) return array;
        if (data.length == 0) return array;
        if (array.length == max) {
            System.arraycopy(array, data.length, array, 0, array.length - data.length);
            System.arraycopy(data, 0, array, array.length - data.length, data.length);
            return array;
        } else {
            if ((array.length + data.length) > max) {
                T[] tmp = (T[]) Array.newInstance(tClass, max);
                for (int i = 0; i < (max - data.length); i++) {
                    tmp[i] = array[i + (array.length - max + data.length)];
                }
                for (int i = 0; i < data.length; i++) {
                    tmp[tmp.length - data.length + i] = data[i];
                }
                // 更新数据
                return tmp;
            } else {
                T[] tmp = (T[]) Array.newInstance(tClass, array.length + data.length);
                for (int i = 0; i < array.length; i++) {
                    tmp[i] = array[i];
                }
                for (int i = 0; i < data.length; i++) {
                    tmp[tmp.length - data.length + i] = data[i];
                }
                return tmp;
            }
        }
    }

    /**
     * 将byte数组的长度扩充到指定长度
     * Extend an byte array to a specified length, or shorten to a specified length or fill
     * @param data 原先的数据长度
     * @param length 扩充或是缩短后的长度
     * @return 新的扩充后的数据对象
     */
    public static byte[] ArrayExpandToLength(byte[] data,int length){
        if (data == null) return new byte[0];
        byte[] buffer =  new byte[length];
        System.arraycopy( data,0, buffer,0, Math.min( data.length, buffer.length ) );
        return buffer;
    }

    /**
     * 将byte数组的长度扩充到偶数长度
     * Extend an byte array to even lengths
     * @param data 原先的数据长度
     * @return 新的扩充后的数据对象
     */
    public static byte[] ArrayExpandToLengthEven( byte[] data ) {
        if (data == null) data = new byte[0];
        if (data.length % 2 == 1) {
            return ArrayExpandToLength(data, data.length + 1);
        } else {
            return data;
        }
    }

    /**
     * 将一个数组进行扩充到指定长度，或是缩短到指定长度<br />
     * Extend an array to a specified length, or shorten to a specified length or fill
     * @param data 原先数据的数据
     * @param length 新数组的长度
     * @param <T> 数组的类型
     * @return 新数组长度信息
     */
    public static <T> T[] ArrayExpandToLength(Class<T> tClass, T[] data, int length ) {
        if (data == null) return (T[]) Array.newInstance(tClass, 0);
        if (data.length == length) return data;

        T[] buffer = (T[]) Array.newInstance(tClass, length);
        System.arraycopy(data, 0, buffer, 0, Math.min(data.length, buffer.length));
        return buffer;
    }

    /**
     * 将一个数组进行扩充到偶数长度<br />
     * Extend an array to even lengths
     * @param data 原先数据的数据
     * @param <T> 数组的类型
     * @return 新数组长度信息
     */
    public static <T> T[] ArrayExpandToLengthEven(Class<T> tClass,  T[] data ) {
        if (data == null) data = (T[]) Array.newInstance(tClass, 0);
        if (data.length % 2 == 1) {
            return ArrayExpandToLength(tClass, data, data.length + 1);
        } else {
            return data;
        }
    }

    /**
     * 将指定的数据按照指定长度进行分割，例如int[10]，指定长度4，就分割成int[4],int[4],int[2]，然后拼接list<br />
     * Divide the specified data according to the specified length, such as int [10], and specify the length of 4 to divide into int [4], int [4], int [2], and then concatenate the list
     * @param tClass 泛型的类型
     * @param array 等待分割的数组
     * @param length 指定的长度信息
     * @param <T> 数组的类型
     * @return 分割后结果内容
     */
    public static <T> ArrayList<T[]> ArraySplitByLength(Class<T> tClass, T[] array, int length ) {
        if (array == null) return new ArrayList<T[]>();

        ArrayList<T[]> result = new ArrayList<T[]>();
        int index = 0;
        while (index < array.length) {
            if (index + length < array.length) {
                T[] tmp = (T[]) Array.newInstance(tClass, length);
                System.arraycopy(array, index, tmp, 0, length);
                index += length;
                result.add(tmp);
            } else {
                T[] tmp = (T[]) Array.newInstance(tClass, array.length - index);
                System.arraycopy(array, index, tmp, 0, tmp.length);
                index += length;
                result.add(tmp);
            }
        }
        return result;
    }

    /**
     * 将整数进行有效的拆分成数组，指定每个元素的最大值<br />
     * Effectively split integers into arrays, specifying the maximum value for each element
     * @param integer 整数信息
     * @param everyLength 单个的数组长度
     * @return 拆分后的数组长度
     */
    public static int[] SplitIntegerToArray( int integer, int everyLength ) {
        int[] result = new int[(integer / everyLength) + ((integer % everyLength) == 0 ? 0 : 1)];
        for (int i = 0; i < result.length; i++) {
            if (i == result.length - 1) {
                result[i] = (integer % everyLength) == 0 ? everyLength : (integer % everyLength);
            } else {
                result[i] = everyLength;
            }
        }
        return result;
    }

    // endregion

    // region Byte Array compare
    /**
     * 判断两个字节数组是否是一致的，可以指定中间的某个区域<br />
     * Determines whether the specified portion of a two-byte is the same
     * @param b1 第一个字节数组
     * @param start1 起始字节
     * @param b2 第二个字节数组
     * @param start2 起始字节
     * @param length 对比数据的长度
     * @return 是否一致
     */
    public static boolean IsTwoBytesEquel(byte[] b1, int start1, byte[] b2, int start2, int length) {
        if (b1 == null || b2 == null) return false;
        for (int i = 0; i < length; i++) {
            if (b1[i + start1] != b2[i + start2]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断两个字节的指定部分是否相同<br />
     * Determines whether the specified portion of a two-byte is the same
     * @param b1 第一个字节
     * @param b2 第二个字节
     * @return 返回是否相等
     */
    public static boolean IsTwoBytesEquel( byte[] b1, byte[] b2){
        if (b1 == null || b2 == null) return false;
        if (b1.length != b2.length) return false;
        return IsTwoBytesEquel( b1, 0, b2, 0, b1.length );
    }

    /**
     * 判断两个数据的令牌是否相等<br />
     * Determines whether the tokens of two data are equals
     * @param head 字节数据
     * @param token GUID数据
     * @return 返回是否相等
     */
    public static boolean IsByteTokenEquel(byte[] head, UUID token) {
        return IsTwoBytesEquel(head, 12, Utilities.UUID2Byte(token), 0, 16);
    }

    /**
     * 判断两个数据的令牌是否相等<br />
     * Determines whether the tokens of two data are equal
     * @param token1 第一个令牌
     * @param token2 第二个令牌
     * @return 返回是否相等
     */
    public static boolean IsTwoTokenEquel( UUID token1, UUID token2 ) {
        return IsTwoBytesEquel(Utilities.UUID2Byte(token1), 0, Utilities.UUID2Byte(token2), 0, 16);
    }

    //endregion

    /**
     * 获取一串唯一的随机字符串，长度为20，由Guid码和4位数的随机数组成，保证字符串的唯一性
     * Gets a string of unique random strings with a length of 20, consisting of a GUID code and a 4-digit random number to guarantee the uniqueness of the string
     * @return 随机字符串数据
     */
    public static String GetUniqueStringByGuidAndRandom() {
        Random random = new Random();
        return UUID.randomUUID().toString() + (random.nextInt(9000) + 1000);
    }

    // region Hex string and Byte[] transform

    /**
     * 字节数据转化成16进制表示的字符串<br />
     * Byte data into a string of 16 binary representations
     * @param InBytes 字节数组
     * @return 返回的字符串
     */
    public static String ByteToHexString(byte[] InBytes)
    {
        return ByteToHexString(InBytes, (char)0);
    }

    /**
     * 字节数据转化成16进制表示的字符串<br />
     * Byte data into a string of 16 binary representations
     * @param InBytes 字节数组
     * @param segment 分割符
     * @return 返回的字符串
     */
    public static String ByteToHexString(byte[] InBytes, char segment)
    {
        return ByteToHexString(InBytes, segment, 0);
    }
    /**
     * 字节数据转化成16进制表示的字符串<br />
     * Byte data into a string of 16 binary representations
     * @param InBytes 字节数组
     * @param segment 分割符
     * @return 返回的字符串
     */
    public static String ByteToHexString(byte[] InBytes, char segment, int newLineCount) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (InBytes == null || InBytes.length <= 0) {
            return null;
        }
        long tick = 0;
        for (int i = 0; i < InBytes.length; i++) {
            String hv = Integer.toHexString(InBytes[i] & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            if (segment > 0) stringBuilder.append(segment);

            tick++;
            if (newLineCount > 0 && tick >= newLineCount) {
                stringBuilder.append("\r\n");
                tick = 0;
            }
        }
        if (segment != 0 && stringBuilder.length() > 1 && stringBuilder.charAt(stringBuilder.length() - 1) == segment) {
            stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
        }
        return stringBuilder.toString();
    }

    /**
     * 字符串数据转化成16进制表示的字符串<br />
     * String data into a string of 16 binary representations
     * @param InString 输入的字符串数据
     * @return 返回的字符串
     * @throws UnsupportedEncodingException 不支持的编码异常
     */
    public static String ByteToHexString(String InString) throws UnsupportedEncodingException
    {
        return ByteToHexString(InString.getBytes("unicode"));
    }

    /**
     * 实际的字符串
     * @param ch 字符信息
     * @return 返回索引信息
     */
    private static int GetHexCharIndex(char ch )
    {
        switch (ch)
        {
            case '0': return 0;
            case '1': return 1;
            case '2': return 2;
            case '3': return 3;
            case '4': return 4;
            case '5': return 5;
            case '6': return 6;
            case '7': return 7;
            case '8': return 8;
            case '9': return 9;
            case 'A':
            case 'a': return 10;
            case 'B':
            case 'b': return 11;
            case 'C':
            case 'c': return 12;
            case 'D':
            case 'd': return 13;
            case 'E':
            case 'e': return 14;
            case 'F':
            case 'f': return 15;
            default: return -1;
        }
    }

    /**
     * 将16进制的字符串转化成Byte数据，将检测每2个字符转化，也就是说，中间可以是任意字符<br />
     * Converts a 16-character string into byte data, which will detect every 2 characters converted, that is, the middle can be any character
     * @param hex 16进制表示的字符串数据
     * @return 字节数组
     */
    public static byte[] HexStringToBytes(String hex) {
        ByteArrayOutputStream ms = new ByteArrayOutputStream();
        for (int i = 0; i < hex.length(); i++) {
            if ((i + 1) < hex.length()) {
                if (GetHexCharIndex( hex.charAt(i) ) >= 0 && GetHexCharIndex( hex.charAt(i + 1) ) >= 0) {
                    // 这是一个合格的字节数据
                    ms.write((byte) (GetHexCharIndex( hex.charAt(i)  ) * 16 + GetHexCharIndex( hex.charAt(i + 1) )));
                    i++;
                }
            }
        }

        byte[] result = ms.toByteArray();
        try {
            ms.close();
        } catch (IOException ex) {

        }
        return result;
    }

    // endregion

    // region Reverse By Word

    /**
     * 将byte数组按照双字节进行反转，如果为单数的情况，则自动补齐<br />
     * Reverses the byte array by double byte, or if the singular is the case, automatically
     * @param inBytes 输入的字节信息
     * @return 反转后的数据
     */
    public static byte[] BytesReverseByWord(byte[] inBytes) {
        if (inBytes == null) return null;
        byte[] buffer = ArrayExpandToLengthEven(inBytes);

        for (int i = 0; i < buffer.length / 2; i++) {
            byte tmp = buffer[i * 2 + 0];
            buffer[i * 2 + 0] = buffer[i * 2 + 1];
            buffer[i * 2 + 1] = tmp;
        }

        return buffer;
    }

    // endregion

    // region Byte[] and AsciiByte[] transform

    /**
     * 将原始的byte数组转换成ascii格式的byte数组<br />
     * Converts the original byte array to an ASCII-formatted byte array
     * @param inBytes 等待转换的byte数组
     * @return 转换后的数组
     */
    public static byte[] BytesToAsciiBytes( byte[] inBytes ) {
        return Utilities.getBytes(ByteToHexString(inBytes), "ascii");
    }

    /**
     * 将ascii格式的byte数组转换成原始的byte数组<br />
     * Converts an ASCII-formatted byte array to the original byte array
     * @param inBytes 等待转换的byte数组
     * @return 转换后的数组
     */
    public static byte[] AsciiBytesToBytes( byte[] inBytes ){
        return HexStringToBytes( Utilities.getString( inBytes, "ascii" ) );
    }

    /**
     * 从字节构建一个ASCII格式的数据内容<br />
     * Build an ASCII-formatted data content from bytes
     * @param value 数据
     * @return ASCII格式的字节数组
     */
    public static byte[] BuildAsciiBytesFrom( byte value ){
        String hv = Integer.toHexString(value & 0xFF).toUpperCase();
        if (hv.length() < 2) {
            return Utilities.getBytes("0" + hv, "ascii");
        }
        else {
            return Utilities.getBytes(hv, "ascii");
        }
    }

    /**
     * 从short构建一个ASCII格式的数据内容<br />
     * Constructing an ASCII-formatted data content from a short
     * @param value 数据
     * @return ASCII格式的字节数组
     */
    public static byte[] BuildAsciiBytesFrom( short value ) {
        String hv = Integer.toHexString(value & 0xFFFF).toUpperCase();
        while (hv.length() < 4) {
            hv = "0" + hv;
        }
        return Utilities.getBytes(hv, "ascii");
    }

    /**
     * 从字节数组构建一个ASCII格式的数据内容<br />
     * Byte array to construct an ASCII format data content
     * @param value 字节信息
     * @return ASCII格式的地址
     */
    public static byte[] BuildAsciiBytesFrom( byte[] value ) {
        byte[] buffer = new byte[value.length * 2];
        for (int i = 0; i < value.length; i++) {
            System.arraycopy(SoftBasic.BuildAsciiBytesFrom(value[i]), 0, buffer, 2 * i, 2);
        }
        return buffer;
    }

    // endregion

    // region Bool and Byte transform

    private static byte GetDataByBitIndex( int offset ) {
        switch (offset) {
            case 0:
                return 0x01;
            case 1:
                return 0x02;
            case 2:
                return 0x04;
            case 3:
                return 0x08;
            case 4:
                return 0x10;
            case 5:
                return 0x20;
            case 6:
                return 0x40;
            case 7:
                return (byte) 0x80;
            default:
                return 0;
        }
    }

    /**
     * 获取byte数据类型的第offset位，是否为True<br />
     * Gets the index bit of the byte data type, whether it is True
     * @param value byte数值
     * @param offset 索引位置
     * @return 结果
     */
    public static boolean BoolOnByteIndex(byte value, int offset ) {
        byte temp = GetDataByBitIndex(offset);
        return (value & temp) == temp;
    }

    /**
     *将bool数组转换到byte数组<br />
     * Converting a bool array to a byte array
     * @param array bool数组
     * @return 字节数组
     */
    public static byte[] BoolArrayToByte(boolean[] array) {
        if (array == null) return null;

        int length = array.length % 8 == 0 ? array.length / 8 : array.length / 8 + 1;
        byte[] buffer = new byte[length];

        for (int i = 0; i < array.length; i++) {
            if (array[i]) buffer[i / 8] += GetDataByBitIndex(i % 8);
        }

        return buffer;
    }

    /**
     * 从Byte数组中提取位数组，length代表位数<br />
     * Extracts a bit array from a byte array, length represents the number of digits
     * @param InBytes 原先的字节数组
     * @param length 想要转换的长度，如果超出自动会缩小到数组最大长度
     * @return 结果对象
     */
    public static boolean[] ByteToBoolArray(byte[] InBytes, int length) {
        if (InBytes == null) return null;

        if (length > InBytes.length * 8) length = InBytes.length * 8;
        boolean[] buffer = new boolean[length];

        for (int i = 0; i < length; i++) {
            buffer[i] = BoolOnByteIndex(InBytes[i / 8], i % 8);
        }

        return buffer;
    }

    /**
     * 从Byte数组中提取所有的位数组<br />
     * Extracts a bit array from a byte array, length represents the number of digits
     * @param InBytes 原先的字节数组
     * @return 转换后的bool数组
     */
    public static boolean[] ByteToBoolArray( byte[] InBytes ){
        return InBytes == null ? null : ByteToBoolArray( InBytes, InBytes.length * 8 );
    }

    // endregion

    /**
     * 拼接2个字节数组的数据<br />
     * Splicing 2 bytes to to an array
     * @param bytes1 数组一
     * @param bytes2 数组二
     * @return 拼接后的数组
     */
    public static byte[] SpliceTwoByteArray( byte[] bytes1, byte[] bytes2 )
    {
        if (bytes1 == null && bytes2 == null) return null;
        if (bytes1 == null) return bytes2;
        if (bytes2 == null) return bytes1;

        byte[] buffer = new byte[bytes1.length + bytes2.length];
        System.arraycopy(bytes1,0,buffer,0,bytes1.length);
        System.arraycopy(bytes2,0,buffer,bytes1.length,bytes2.length);
        return buffer;
    }

    /**
     * 拼接任意个字节数组为一个总的字节数组。<br />
     * Concatenate any number of byte arrays into a total byte array.
     * @param bytes 字节数组
     * @return 拼接后的数组
     */
    public static byte[] SpliceTwoByteArray( byte[]... bytes ){
        int count = 0;
        for (int i = 0; i < bytes.length; i++)
        {
            if( bytes[i] != null && bytes[i].length > 0)
            {
                count += bytes[i].length;
            }
        }
        int index = 0;
        byte[] buffer = new byte[count];
        for (int i = 0; i < bytes.length; i++)
        {
            if (bytes[i] != null && bytes[i].length > 0)
            {
                System.arraycopy(buffer, 0, buffer, index, buffer.length);
                index += bytes[i].length;
            }
        }
        return buffer;
    }

    /**
     * 将一个string的数组和多个string类型的对象整合成一个数组<br />
     * Combine an array of string and multiple objects of type string into an array
     * @param first 第一个数组对象
     * @param array 字符串数组信息
     * @return 总的数组对象
     */
    public static String[] SpliceStringArray( String first, String[] array ) {
        ArrayList<String> list = new ArrayList<String>();
        list.add(first);
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                list.add(array[i]);
            }
        }
        return (String[]) list.toArray();
    }

    /**
     * 将两个string的数组和多个string类型的对象整合成一个数组<br />
     * Combine two arrays of string and multiple objects of type string into one array
     * @param first 第一个数组对象
     * @param second 第二个数组对象
     * @param array 字符串数组信息
     * @return 总的数组对象
     */
    public static String[] SpliceStringArray( String first, String second, String[] array ){
        ArrayList<String> list = new ArrayList<String>();
        list.add( first );
        list.add( second );
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                list.add(array[i]);
            }
        }
        return (String[]) list.toArray();
    }

    /**
     * 将一个数组的前后移除指定位数，返回新的一个数组<br/>
     * Removes the preceding specified number of bits in a array, returning a new array
     * @param value 字节数组
     * @param length 等待移除的长度
     * @return 新的数据
     */
    public static byte[] BytesArrayRemoveBegin( byte[] value, int length )
    {
        return BytesArrayRemoveDouble( value, length, 0 );
    }

    /**
     * 将一个数组的后面指定位数移除，返回新的一个数组<br />
     * Removes the specified number of digits after a array, returning a new array
     * @param value 字节数组
     * @param length 等待移除的长度
     * @return 新的数据
     */
    public static byte[] BytesArrayRemoveLast( byte[] value, int length )
    {
        return BytesArrayRemoveDouble( value, 0, length );
    }

    /**
     * 将一个byte数组的前后移除指定位数，返回新的一个数组<br />
     * Removes a array before and after the specified number of bits, returning a new array
     * @param value 字节数组
     * @param leftLength 前面的位数
     * @param rightLength 后面的位数
     * @return 新的数据
     */
    public static byte[] BytesArrayRemoveDouble( byte[] value, int leftLength, int rightLength )
    {
        if (value == null) return null;
        if (value.length <= (leftLength + rightLength)) return new byte[0];

        byte[] buffer = new byte[value.length - leftLength - rightLength];
        System.arraycopy( value, leftLength, buffer, 0, buffer.length );

        return buffer;
    }

    /**
     * 获取到数组里面的中间指定长度的数组<br />
     * Get an array of the specified length in the array
     * @param value 数组
     * @param index 起始索引
     * @param length 数据的长度
     * @return 新的数组值
     */
    public static byte[] BytesArraySelectMiddle( byte[] value, int index, int length ){
        if (value == null) return null;
        byte[] buffer = new byte[Math.min( value.length, length )];
        System.arraycopy( value, index, buffer, 0, buffer.length );
        return buffer;
    }

    /**
     * 选择一个数组的前面的几个数据信息<br />
     * Select the begin few items of data information of a array
     * @param value 数组
     * @param length 数据的长度
     * @return 新的数组
     */
    public static byte[] BytesArraySelectBegin( byte[] value, int length ){
        byte[] buffer = new byte[Math.min( value.length, length )];
        if (buffer.length > 0) System.arraycopy( value, 0, buffer, 0, buffer.length );
        return buffer;
    }

    /**
     * 选择一个数组的后面的几个数据信息<br />
     * Select the last few items of data information of a array
     * @param value 数组
     * @param length 数据的长度
     * @return 新的数组信息
     */
    public static byte[] BytesArraySelectLast( byte[] value, int length ){
        byte[] buffer = new byte[Math.min( value.length, length )];
        System.arraycopy( value, value.length - length, buffer, 0, buffer.length );
        return buffer;
    }

    /**
     * 设置或获取系统框架的版本号<br />
     * Set or get the version number of the system framework
     */
    public static SystemVersion FrameworkVersion = new SystemVersion("2.0.1");

}

