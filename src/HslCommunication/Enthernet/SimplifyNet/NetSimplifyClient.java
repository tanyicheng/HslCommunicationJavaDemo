package HslCommunication.Enthernet.SimplifyNet;

import HslCommunication.Core.IMessage.HslMessage;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.Net.HslProtocol;
import HslCommunication.Core.Net.NetHandle;
import HslCommunication.Core.Net.NetworkBase.NetworkDoubleBase;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Utilities;

import java.util.UUID;


/**
 * 同步访问数据的客户端类，用于向服务器请求一些确定的数据信息
 * 详细的使用说明，请参照博客<a href="http://www.cnblogs.com/dathlin/p/7697782.html">http://www.cnblogs.com/dathlin/p/7697782.html</a>
 */
public class NetSimplifyClient extends NetworkDoubleBase
{
    /**
     * 实例化一个客户端的对象，用于和服务器通信
     * @param ipAddress Ip地址
     * @param port 端口号
     */
    public NetSimplifyClient(String ipAddress, int port)
    {
        this.setIpAddress(ipAddress);
        this.setPort( port);
    }

    /**
     * 实例化一个客户端的对象，用于和服务器通信
     * @param ipAddress Ip地址
     * @param port 端口号
     * @param token 令牌
     */
    public NetSimplifyClient(String ipAddress, int port, UUID token)
    {
        this.setIpAddress(ipAddress);
        this.setPort( port);
        this.Token = token;
    }


    /**
     * 实例化一个客户端对象，需要手动指定Ip地址和端口
     */
    public NetSimplifyClient(){
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new HslMessage();
    }


    /**
     * 客户端向服务器进行请求，请求字符串数据，忽略了自定义消息反馈
     * @param customer 用户的指令头
     * @param send 发送数据
     * @return 带返回消息的结果对象
     */
    public OperateResultExOne<String> ReadFromServer( NetHandle customer, String send ) {
        OperateResultExOne<byte[]> read = ReadFromServerBase(HslProtocol.CommandBytes(customer.get_CodeValue(), Token, send));
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(Utilities.byte2CSharpString(read.Content));
    }

    /**
     * 客户端向服务器进行请求，请求字符串数组，忽略了自定义消息反馈
     * @param customer 用户的指令头
     * @param send 发送数据
     * @return 带返回消息的结果对象
     */
    public OperateResultExOne<String[]> ReadFromServer( NetHandle customer, String[] send )
    {
        OperateResultExOne<byte[]> read = ReadFromServerBase( HslProtocol.CommandBytes( customer.get_CodeValue(), Token, send ) );
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult( read );

        return OperateResultExOne.CreateSuccessResult( HslProtocol.UnPackStringArrayFromByte( read.Content ) );
    }

    /**
     * 客户端向服务器进行请求，请求字节数据
     * @param customer 用户的指令头
     * @param send 发送的字节内容
     * @return 带返回消息的结果对象
     */
    public OperateResultExOne<byte[]> ReadFromServer( NetHandle customer, byte[] send ) {
        return ReadFromServerBase(HslProtocol.CommandBytes(customer.get_CodeValue(), Token, send));
    }

    /**
     * 客户端向服务器进行请求，请求字符串数据，并返回状态信息
     * @param customer 用户的指令头
     * @param send 发送数据
     * @return 带返回消息的结果对象
     */
    public OperateResultExTwo<NetHandle, String> ReadCustomerFromServer( NetHandle customer, String send )
    {
        OperateResultExTwo<NetHandle, byte[]> read = ReadCustomerFromServerBase( HslProtocol.CommandBytes( customer.get_CodeValue(), Token, send ) );
        if (!read.IsSuccess) return OperateResultExTwo.CreateFailedResult( read );

        return OperateResultExTwo.CreateSuccessResult( read.Content1, Utilities.byte2CSharpString( read.Content2 ) );
    }

    /**
     * 客户端向服务器进行请求，请求字符串数据，并返回状态信息
     * @param customer 用户的指令头
     * @param send 发送数据
     * @return 带返回消息的结果对象
     */
    public OperateResultExTwo<NetHandle, String[]> ReadCustomerFromServer( NetHandle customer, String[] send ) {
        OperateResultExTwo<NetHandle, byte[]> read = ReadCustomerFromServerBase(HslProtocol.CommandBytes(customer.get_CodeValue(), Token, send));
        if (!read.IsSuccess) return OperateResultExTwo.CreateFailedResult(read);

        return OperateResultExTwo.CreateSuccessResult(read.Content1, HslProtocol.UnPackStringArrayFromByte(read.Content2));
    }

    /**
     * 客户端向服务器进行请求，请求字符串数据，并返回状态信息
     * @param customer 用户的指令头
     * @param send 发送数据
     * @return 带返回消息的结果对象
     */
    public OperateResultExTwo<NetHandle, byte[]> ReadCustomerFromServer( NetHandle customer, byte[] send ) {
        return ReadCustomerFromServerBase(HslProtocol.CommandBytes(customer.get_CodeValue(), Token, send));
    }

    /**
     * 需要发送的底层数据
     * @param send 需要发送的底层数据
     * @return 带返回消息的结果对象
     */
    private OperateResultExOne<byte[]> ReadFromServerBase( byte[] send ) {
        OperateResultExTwo<NetHandle, byte[]> read = ReadCustomerFromServerBase(send);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(read.Content2);
    }

    /**
     * 需要发送的底层数据
     * @param send 需要发送的底层数据
     * @return 带返回消息的结果对象
     */
    private OperateResultExTwo<NetHandle, byte[]> ReadCustomerFromServerBase(byte[] send ) {
        // 核心数据交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(send);
        if (!read.IsSuccess) return OperateResultExTwo.CreateFailedResult(read);

        return HslProtocol.ExtractHslData(read.Content);
    }

    @Override
    public String toString() {
        return "NetSimplifyClient[" + getIpAddress() +":"+getPort()+"]" ;
    }

}
