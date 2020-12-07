package HslCommunication.Enthernet.UdpNet;

import HslCommunication.Core.Net.HslProtocol;
import HslCommunication.Core.Net.NetHandle;
import HslCommunication.Core.Net.NetworkBase.NetworkUdpBase;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Utilities;

/**
 * UDP客户端的类，负责发送数据到服务器，然后从服务器接收对应的数据信息，该数据经过HSL封装<br />
 * UDP client class, responsible for sending data to the server, and then receiving the corresponding data information from the server, the data is encapsulated by HSL
 */
public class NetUdpClient extends NetworkUdpBase {
    /**
     * 实例化对象，指定发送的服务器地址和端口号<br />
     * Instantiated object, specifying the server address and port number to send
     *
     * @param ipAddress 服务器的Ip地址
     * @param port      端口号
     */
    public NetUdpClient(String ipAddress, int port) {
        setIpAddress(ipAddress);
        setPort(port);
    }

    /**
     * 客户端向服务器进行请求，请求字符串数据，忽略了自定义消息反馈<br />
     * The client makes a request to the server, requesting string data, and ignoring custom message feedback
     *
     * @param customer 用户的指令头
     * @param send     发送数据
     * @return 带返回消息的结果对象
     */
    public OperateResultExOne<String> ReadFromServer(NetHandle customer, String send) {
        OperateResultExOne<byte[]> read = ReadFromServerBase(HslProtocol.CommandBytes(customer.get_CodeValue(), Token, send));
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(Utilities.byte2CSharpString(read.Content));
    }

    /**
     * 客户端向服务器进行请求，请求字节数据<br />
     * The client makes a request to the server, requesting byte data
     *
     * @param customer 用户的指令头
     * @param send     发送的字节内容
     * @return 带返回消息的结果对象
     */
    public OperateResultExOne<byte[]> ReadFromServer(NetHandle customer, byte[] send) {
        return ReadFromServerBase(HslProtocol.CommandBytes(customer.get_CodeValue(), Token, send));
    }

    /**
     * 客户端向服务器进行请求，请求字符串数据，并返回状态信息<br />
     * The client makes a request to the server, requests string data, and returns status information
     *
     * @param customer 用户的指令头
     * @param send     发送数据
     * @return 带返回消息的结果对象
     */
    public OperateResultExTwo<NetHandle, String> ReadCustomerFromServer(NetHandle customer, String send) {
        OperateResultExTwo<NetHandle, byte[]> read = ReadCustomerFromServerBase(HslProtocol.CommandBytes(customer.get_CodeValue(), Token, send));
        if (!read.IsSuccess) return OperateResultExTwo.<NetHandle, String>CreateFailedResult(read);

        return OperateResultExTwo.CreateSuccessResult(read.Content1, Utilities.byte2CSharpString(read.Content2));
    }

    /**
     * 客户端向服务器进行请求，请求字节数据，并返回状态信息<br />
     * The client makes a request to the server, requests byte data, and returns status information
     *
     * @param customer 用户的指令头
     * @param send     发送数据
     * @return 带返回消息的结果对象
     */
    public OperateResultExTwo<NetHandle, byte[]> ReadCustomerFromServer(NetHandle customer, byte[] send) {
        return ReadCustomerFromServerBase(HslProtocol.CommandBytes(customer.get_CodeValue(), Token, send));
    }

    /**
     * 发送的底层数据，然后返回结果数据<br />
     * Send the underlying data and then return the result data
     *
     * @param send 需要发送的底层数据
     * @return 带返回消息的结果对象
     */
    private OperateResultExOne<byte[]> ReadFromServerBase(byte[] send) {
        OperateResultExTwo<NetHandle, byte[]> read = ReadCustomerFromServerBase(send);
        if (!read.IsSuccess) return OperateResultExOne.<byte[]>CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(read.Content2);
    }

    /**
     * 发送的底层数据，然后返回结果数据，该结果是带Handle信息的。<br />
     * Send the underlying data, and then return the result data, the result is with Handle information.
     *
     * @param send 需要发送的底层数据
     * @return 带返回消息的结果对象
     */
    private OperateResultExTwo<NetHandle, byte[]> ReadCustomerFromServerBase(byte[] send) {
        // 核心数据交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(send);
        if (!read.IsSuccess) return OperateResultExTwo.<NetHandle, byte[]>CreateFailedResult(read);

        // 提炼数据信息
        return HslProtocol.ExtractHslData(read.Content);
    }

    //region Object Override

    public String toString() {
        return "NetUdpClient[" + getIpAddress() + ":" + getPort() + "]";
    }

    //endregion
}
