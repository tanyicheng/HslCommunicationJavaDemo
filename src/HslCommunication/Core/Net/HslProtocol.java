package HslCommunication.Core.Net;

import HslCommunication.BasicFramework.*;
import HslCommunication.Core.Security.HslSecurity;

import java.util.UUID;

import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Utilities;

public class HslProtocol
{
    /**
     * 规定所有的网络传输的指令头长度
     */
    public static final int HeadByteLength = 32;

    /**
     * 所有网络通信中的缓冲池的数据信息
     */
    public static final int ProtocolBufferSize = 1024;

    /**
     * 用于心跳程序的暗号信息
     */
    public static final int ProtocolCheckSecends = 1;

    /**
     * 客户端退出的消息
     */
    public static final int ProtocolClientQuit = 2;

    /**
     * 因为客户端达到上限而拒绝登录
     */
    public static final int ProtocolClientRefuseLogin = 3;

    /**
     * 允许客户端登录到服务器
     */
    public static final int ProtocolClientAllowLogin = 4;

    /**
     * 客户端登录的暗号信息
     */
    public static final int ProtocolAccountLogin = 5;
    /**
     * 客户端拒绝登录的暗号信息
     */
    public static final int ProtocolAccountRejectLogin = 6;

    /**
     * 说明发送的信息是文本数据
     */
    public static final int ProtocolUserString = 1001;

    /**
     * 说明发送的信息是字节数组数据
     */
    public static final int ProtocolUserBytes = 1002;

    /**
     * 发送的数据是普通的图片数据
     */
    public static final int ProtocolUserBitmap = 1003;

    /**
     * 发送的数据是一条异常的数据，字符串为异常消息
     */
    public static final int ProtocolUserException = 1004;

    /**
     * 说明发送的数据是字符串的数组
     */
    public static final int ProtocolUserStringArray = 1005;

    /**
     * 请求文件下载的暗号
     */
    public static final int ProtocolFileDownload = 2001;

    /**
     * 请求文件上传的暗号
     */
    public static final int ProtocolFileUpload = 2002;

    /**
     * 请求删除文件的暗号
     */
    public static final int ProtocolFileDelete = 2003;

    /**
     * 文件校验成功
     */
    public static final int ProtocolFileCheckRight = 2004;

    /**
     * 文件校验失败
     */
    public static final int ProtocolFileCheckError = 2005;

    /**
     * 文件保存失败
     */
    public static final int ProtocolFileSaveError = 2006;

    /**
     * 请求文件的列表的暗号
     */
    public static final int ProtocolFileDirectoryFiles = 2007;

    /**
     * 请求子文件的列表暗号
     */
    public static final int ProtocolFileDirectories = 2008;

    /**
     * 进度返回暗号
     */
    public static final int ProtocolProgressReport = 2009;

    /**
     * 返回的错误信息
     */
    public static final int ProtocolErrorMsg = 2010;

    /**
     * 不压缩字节数据
     */
    public static final int ProtocolNoZipped = 3001;

    /**
     * 压缩字节数据
     */
    public static final int ProtocolZipped  = 3002;


    /**
     * 生成终极传送指令的方法，所有的数据均通过该方法出来
     * @param command 命令头
     * @param customer 自用自定义
     * @param token 令牌
     * @param data 字节数据
     * @return 发送的消息数据
     */
    public static byte[] CommandBytes(int command, int customer, UUID token, byte[] data ) {
        byte[] _temp = null;
        int _zipped = ProtocolNoZipped;
        int _sendLength = 0;
        if (data == null) {
            _temp = new byte[HeadByteLength];
        } else {
            // 加密
            data = HslSecurity.ByteEncrypt(data);
            if (data.length > 10240) {
                // 10K以上的数据，进行数据压缩
                data = SoftZipped.CompressBytes(data);
                _zipped = ProtocolZipped;
            }
            _temp = new byte[HeadByteLength + data.length];
            _sendLength = data.length;
        }
        System.arraycopy(Utilities.getBytes(command), 0, _temp, 0, 4);
        System.arraycopy(Utilities.getBytes(customer), 0, _temp, 4, 4);
        System.arraycopy(Utilities.getBytes(_zipped), 0, _temp, 8, 4);
        System.arraycopy(Utilities.UUID2Byte(token), 0, _temp, 12, 16);
        System.arraycopy(Utilities.getBytes(_sendLength), 0, _temp, 28, 4);
        if (_sendLength > 0) {
            System.arraycopy(data, 0, _temp, 32, _sendLength);
        }
        return _temp;
    }

    /**
     * 解析接收到数据，先解压缩后进行解密
     * @param head 指令头
     * @param content 内容字节
     * @return 真实的数据内容
     */
    public static byte[] CommandAnalysis( byte[] head, byte[] content ) {
        if (content != null) {
            // 获取是否压缩的情况
            int _zipped = Utilities.getInt(head, 8);

            // 先进行解压
            if (_zipped == ProtocolZipped) {
                content = SoftZipped.Decompress(content);
            }
            // 进行解密
            return HslSecurity.ByteDecrypt(content);
        } else {
            return null;
        }
    }

    /**
     * 获取发送字节数据的实际数据，带指令头
     * @param customer 用户数据
     * @param token 令牌
     * @param data 字节数据
     * @return 包装后的指令信息
     */
    public static byte[] CommandBytes( int customer, UUID token, byte[] data ) {
        return CommandBytes(ProtocolUserBytes, customer, token, data);
    }

    /**
     * 获取发送字节数据的实际数据，带指令头
     * @param customer 用户数据
     * @param token 令牌
     * @param data 字符串数据信息
     * @return 包装后的指令信息
     */
    public static byte[] CommandBytes( int customer, UUID token, String data ) {
        if (data == null) return CommandBytes(ProtocolUserString, customer, token, null);
        else return CommandBytes(ProtocolUserString, customer, token, Utilities.csharpString2Byte(data));
    }

    /**
     * 获取发送字节数据的实际数据，带指令头
     * @param customer 用户数据
     * @param token 令牌
     * @param data 字符串数据信息
     * @return 包装后的指令信息
     */
    public static byte[] CommandBytes( int customer, UUID token, String[] data ) {
        return CommandBytes(ProtocolUserStringArray, customer, token, PackStringArrayToByte(data));
    }

    /**
     * 将字符串打包成字节数组内容
     * @param data 字符串数组
     * @return 打包后的原始数据内容
     */
    public static byte[] PackStringArrayToByte( String[] data ) {
        if (data == null) data = new String[0];

        byte[] buffer = Utilities.getBytes(data.length);

        for (int i = 0; i < data.length; i++) {
            if (!Utilities.IsStringNullOrEmpty(data[i])) {
                byte[] tmp = Utilities.csharpString2Byte(data[i]);
                buffer = SoftBasic.SpliceTwoByteArray(buffer, Utilities.getBytes(tmp.length));
                buffer = SoftBasic.SpliceTwoByteArray(buffer, tmp);
            } else {
                buffer = SoftBasic.SpliceTwoByteArray(buffer, Utilities.getBytes(0));
            }
        }

        return buffer;
    }

    /**
     * 将字节数组还原成真实的字符串数组
     * @param content 原始字节数组
     * @return 解析后的字符串内容
     */
    public static String[] UnPackStringArrayFromByte( byte[] content ) {
        if (content == null) return null;
        if (content.length < 4) return null;

        int index = 0;
        int count = Utilities.getInt(content, index);
        String[] result = new String[count];
        index += 4;
        for (int i = 0; i < count; i++) {
            int length = Utilities.getInt(content, index);
            index += 4;
            if (length > 0) result[i] = Utilities.byte2CSharpString(content, index, length);
            else result[i] = "";
            index += length;
        }

        return result;
    }

    /**
     * 从接收的数据内容提取出用户的暗号和数据内容
     * @param content 数据内容
     * @return 包含结果对象的信息
     */
    public static OperateResultExTwo<NetHandle, byte[]> ExtractHslData(byte[] content ) {
        if (content.length == 0) {
            // 没有数据接收的时候，直接返回成功
            return OperateResultExTwo.CreateSuccessResult(new NetHandle(0), new byte[0]);
        } else {
            // 提炼数据信息
            byte[] headBytes = new byte[HslProtocol.HeadByteLength];
            byte[] contentBytes = new byte[content.length - HslProtocol.HeadByteLength];

            System.arraycopy(content, 0, headBytes, 0, HslProtocol.HeadByteLength);
            if (contentBytes.length > 0)
                System.arraycopy(content, HslProtocol.HeadByteLength, contentBytes, 0, content.length - HslProtocol.HeadByteLength);

            if (Utilities.getInt(headBytes, 0) == HslProtocol.ProtocolErrorMsg) {
                return new OperateResultExTwo<NetHandle, byte[]>(Utilities.byte2CSharpString(contentBytes));
            }

            int code = Utilities.getInt(headBytes, 0);
            int customer = Utilities.getInt(headBytes, 4);
            contentBytes = HslProtocol.CommandAnalysis(headBytes, contentBytes);

            if (code == HslProtocol.ProtocolAccountRejectLogin) {
                return new OperateResultExTwo<NetHandle, byte[]>(Utilities.byte2CSharpString(contentBytes));
            }
            return OperateResultExTwo.CreateSuccessResult(new NetHandle(customer), contentBytes);
        }
    }
}
