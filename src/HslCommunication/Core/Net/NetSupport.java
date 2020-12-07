package HslCommunication.Core.Net;

import HslCommunication.Core.Types.ActionOperateExTwo;
import HslCommunication.Core.Types.HslTimeOut;
import HslCommunication.Core.Types.RemoteCloseException;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Date;

/**
 * 静态的方法支持类，提供一些网络的静态支持，支持从套接字从同步接收指定长度的字节数据，并支持报告进度。<br />
 * The static method support class provides some static support for the network, supports receiving byte data of a specified length from the socket from synchronization, and supports reporting progress.
 */
public class NetSupport {

    /**
     * Socket传输中的缓冲池大小<br />
     * Buffer pool size in socket transmission
     */
    public final static int SocketBufferSize = 8192;

    /**
     * 从socket的网络中读取数据内容，需要指定数据长度和超时的时间，为了防止数据太大导致接收失败，所以此处接收到新的数据之后就更新时间。<br />
     * To read the data content from the socket network, you need to specify the data length and timeout period. In order to prevent the data from being too large and cause the reception to fail, the time is updated after new data is received here.
     *
     * @param socket         网络套接字
     * @param receive        接收的长度
     * @param reportProgress 当前接收数据的进度报告，有些协议支持传输非常大的数据内容，可以给与进度提示的功能
     * @return 最终接收的指定长度的byte[]数据
     * @throws IOException          网络套接字的异常信息
     * @throws RemoteCloseException 远程关闭的异常信息
     */
    public static byte[] ReadBytesFromSocket(Socket socket, int receive, ActionOperateExTwo<Long, Long> reportProgress) throws IOException, RemoteCloseException {
        byte[] bytes_receive = new byte[receive];
        int count_receive = 0;
        while (count_receive < receive) {
            // 分割成8KB来接收数据
            int receive_length = Math.min((receive - count_receive), SocketBufferSize);

            //socket.setSoTimeout(10_000);
            InputStream input = socket.getInputStream();
            int count = input.read(bytes_receive, count_receive, receive_length);
            if (count < 0) throw new RemoteCloseException();
            count_receive += count;

            if (count <= 0) throw new RemoteCloseException();

            if (reportProgress != null) {
                reportProgress.Action((long) count_receive, (long) receive);
            }
        }
        return bytes_receive;
    }
}
