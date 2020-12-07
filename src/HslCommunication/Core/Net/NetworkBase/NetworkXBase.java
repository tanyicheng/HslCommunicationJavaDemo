package HslCommunication.Core.Net.NetworkBase;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Net.HslProtocol;
import HslCommunication.Core.Net.StateOne.AppSession;
import HslCommunication.Core.Types.*;
import HslCommunication.LogNet.Core.ILogNet;
import HslCommunication.StringResources;
import HslCommunication.Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

/**
 * 包含了主动异步接收的方法实现和文件类异步读写的实现<br />
 * Contains the implementation of the active asynchronous receiving method and the implementation of asynchronous reading and writing of the file class
 */
public class NetworkXBase extends NetworkBase
{

    /**
     * 默认的无参构造方法<br />
     * The default parameter less constructor
     */
    public NetworkXBase()
    {
    }

    /**
     * 发送数据的方法
     * @param session 通信用的核心对象
     * @param content 完整的字节信息
     */
    void SendBytesAsync(AppSession session, byte[] content )
    {
        if (content == null) return;
        try
        {
            // 进入发送数据的锁，然后开启异步的数据发送
            session.getHybirdLockSend().lock();
            OutputStream outputStream = session.getWorkSocket().getOutputStream();
            outputStream.write(content);
        }
        catch (Exception ex)
        {
            if (!ex.getMessage().contains( StringResources.Language.SocketRemoteCloseException() ))
            {
                if(LogNet!=null) LogNet.WriteException( toString( ), StringResources.Language.SocketSendException(), ex );
            }
        }
        finally {
            session.getHybirdLockSend().unlock();
        }
    }


    protected Thread thread;  // 后台线程

    /**
     * 开始接受数据
     * @param session 会话信息
     */
    protected void BeginReceiveBackground(final AppSession session){
        thread = new Thread(){
            @Override
            public void run(){
                while (true){
                    OperateResultExOne<byte[]> readHeadBytes = Receive(session.getWorkSocket(),HslProtocol.HeadByteLength);
                    if(!readHeadBytes.IsSuccess){
                        SocketReceiveException( session );
                        return;
                    }

                    int length = Utilities.getInt(readHeadBytes.Content,28);
                    OperateResultExOne<byte[]> readContent = Receive(session.getWorkSocket(),length);
                    if(!readContent.IsSuccess){
                        SocketReceiveException( session );
                        return;
                    }

                    if (CheckRemoteToken( readHeadBytes.Content ))
                    {
                        byte[] head = readHeadBytes.Content;
                        byte[] content = HslProtocol.CommandAnalysis(head,readContent.Content);
                        int protocol = Utilities.getInt( head, 0 );
                        int customer = Utilities.getInt( head, 4 );

                        DataProcessingCenter(session,protocol,customer,content);
                    }
                    else {
                        if(LogNet!=null) LogNet.WriteWarn( toString( ), StringResources.Language.TokenCheckFailed() );
                        AppSessionRemoteClose( session );
                    }
                }
            }
        };
        thread.start();
    }

    /**
     * 数据处理中心，应该继承重写
     * @param session 连接状态
     * @param protocol 协议头
     * @param customer 用户自定义
     * @param content 数据内容
     */
    protected void DataProcessingCenter( AppSession session, int protocol, int customer, byte[] content ) {

    }

    /**
     * 接收出错的时候进行处理
     * @param session 会话内容
     */
    protected void SocketReceiveException( AppSession session ) {

    }

    /**
     * 当远端的客户端关闭连接时触发
     * @param session 会话内容
     */
    protected void AppSessionRemoteClose( AppSession session ) {

    }

    /**
     * [自校验] 将文件数据发送至套接字，如果结果异常，则结束通讯<br />
     * [Self-check] Send the file data to the socket. If the result is abnormal, the communication is ended.
     * @param socket 网络套接字
     * @param filename 完整的文件路径
     * @param fileLength 文件的长度
     * @param report 进度报告器
     * @return 是否发送成功
     */
    protected OperateResult SendFileStreamToSocket( Socket socket, String filename, long fileLength, ActionOperateExTwo<Long, Long> report ) {
        try {
            OperateResult result = new OperateResult();
            FileInputStream stream = new FileInputStream(filename);
            result = SendStreamToSocket(socket, stream, fileLength, report, true);
            return result;
        } catch (Exception ex) {
            CloseSocket(socket);
            ILogNet logNet = LogNet;
            if (logNet != null) logNet.WriteException(toString(), ex);
            return new OperateResult(ex.getMessage());
        }
    }


    /// <summary>
    /// [自校验] 将文件数据发送至套接字，具体发送细节将在继承类中实现，如果结果异常，则结束通讯
    /// </summary>
    /// <param name="socket">套接字</param>
    /// <param name="filename">文件名称，文件必须存在</param>
    /// <param name="servername">远程端的文件名称</param>
    /// <param name="filetag">文件的额外标签</param>
    /// <param name="fileupload">文件的上传人</param>
    /// <param name="sendReport">发送进度报告</param>
    /// <returns>是否发送成功</returns>
//    protected OperateResult SendFileAndCheckReceive(
//            Socket socket,
//            String filename,
//            String servername,
//            String filetag,
//            String fileupload,
//            BiConsumer<Long, Long> sendReport
//    )
//    {
//        // 发送文件名，大小，标签
//        File file = new File(filename);
//
//        if (!file.exists())
//        {
//            // 如果文件不存在
//            OperateResult stringResult = SendStringAndCheckReceive( socket, 0, "" );
//            if (!stringResult.IsSuccess)
//            {
//                return stringResult;
//            }
//            else
//            {
//                CloseSocket(socket);
//                OperateResult result = new OperateResult();
//                result.Message = StringResources.FileNotExist;
//                return  result;
//            }
//        }
//
//        // 文件存在的情况
//        Newtonsoft.Json.Linq.JObject json = new Newtonsoft.Json.Linq.JObject
//        {
//            { "FileName", new Newtonsoft.Json.Linq.JValue(servername) },
//            { "FileSize", new Newtonsoft.Json.Linq.JValue(file.length()) },
//            { "FileTag", new Newtonsoft.Json.Linq.JValue(filetag) },
//            { "FileUpload", new Newtonsoft.Json.Linq.JValue(fileupload) }
//        };
//
//        // 先发送文件的信息到对方
//        OperateResult sendResult = SendStringAndCheckReceive( socket, 1, json.ToString( ) );
//        if (!sendResult.IsSuccess)
//        {
//            return sendResult;
//        }
//
//        // 最后发送
//        return SendFileStreamToSocket( socket, filename, file.length(), sendReport );
//    }



    /// <summary>
    /// [自校验] 将流数据发送至套接字，具体发送细节将在继承类中实现，如果结果异常，则结束通讯
    /// </summary>
    /// <param name="socket">套接字</param>
    /// <param name="stream">文件名称，文件必须存在</param>
    /// <param name="servername">远程端的文件名称</param>
    /// <param name="filetag">文件的额外标签</param>
    /// <param name="fileupload">文件的上传人</param>
    /// <param name="sendReport">发送进度报告</param>
    /// <returns></returns>
//    protected OperateResult SendFileAndCheckReceive(
//            Socket socket,
//            Stream stream,
//            String servername,
//            String filetag,
//            String fileupload,
//            BiConsumer<Long, Long> sendReport
//    )
//    {
//        // 文件存在的情况
//        Newtonsoft.Json.Linq.JObject json = new Newtonsoft.Json.Linq.JObject
//        {
//            { "FileName", new Newtonsoft.Json.Linq.JValue(servername) },
//            { "FileSize", new Newtonsoft.Json.Linq.JValue(stream.Length) },
//            { "FileTag", new Newtonsoft.Json.Linq.JValue(filetag) },
//            { "FileUpload", new Newtonsoft.Json.Linq.JValue(fileupload) }
//        };
//
//
//        // 发送文件信息
//        OperateResult fileResult = SendStringAndCheckReceive( socket, 1, json.ToString( ) );
//        if (!fileResult.IsSuccess) return fileResult;
//
//
//        return SendStream( socket, stream, stream.count(), sendReport, true );
//    }


    /// <summary>
    /// [自校验] 从套接字中接收文件头信息
    /// </summary>
    /// <param name="socket"></param>
    /// <returns></returns>
//    protected OperateResult<FileBaseInfo> ReceiveFileHeadFromSocket( Socket socket )
//    {
//        // 先接收文件头信息
//        OperateResult<int, string> receiveString = ReceiveStringContentFromSocket( socket );
//        if (!receiveString.IsSuccess) return OperateResult.CreateFailedResult<FileBaseInfo>( receiveString );
//
//        // 判断文件是否存在
//        if (receiveString.Content1 == 0)
//        {
//            socket?.Close( );
//            LogNet?.WriteWarn( ToString( ), "对方文件不存在，无法接收！" );
//            return new OperateResult<FileBaseInfo>( )
//            {
//                Message = StringResources.FileNotExist
//            };
//        }
//
//        OperateResult<FileBaseInfo> result = new OperateResult<FileBaseInfo>( );
//        result.Content = new FileBaseInfo( );
//        try
//        {
//            // 提取信息
//            Newtonsoft.Json.Linq.JObject json = Newtonsoft.Json.Linq.JObject.Parse( receiveString.Content2 );
//            result.Content.Name = SoftBasic.GetValueFromJsonObject( json, "FileName", "" );
//            result.Content.Size = SoftBasic.GetValueFromJsonObject( json, "FileSize", 0L );
//            result.Content.Tag = SoftBasic.GetValueFromJsonObject( json, "FileTag", "" );
//            result.Content.Upload = SoftBasic.GetValueFromJsonObject( json, "FileUpload", "" );
//            result.IsSuccess = true;
//        }
//        catch (Exception ex)
//        {
//            socket?.Close( );
//            result.Message = "提取信息失败，" + ex.Message;
//        }
//
//        return result;
//    }

    /// <summary>
    /// [自校验] 从网络中接收一个文件，如果结果异常，则结束通讯
    /// </summary>
    /// <param name="socket">网络套接字</param>
    /// <param name="savename">接收文件后保存的文件名</param>
    /// <param name="receiveReport">接收进度报告</param>
    /// <returns></returns>
//    protected OperateResult<FileBaseInfo> ReceiveFileFromSocket( Socket socket, string savename, Action<long, long> receiveReport )
//    {
//        // 先接收文件头信息
//        OperateResult<FileBaseInfo> fileResult = ReceiveFileHeadFromSocket( socket );
//        if (!fileResult.IsSuccess) return fileResult;
//
//        try
//        {
//            using (FileStream fs = new FileStream( savename, FileMode.Create, FileAccess.Write ))
//            {
//                WriteStream( socket, fs, fileResult.Content.Size, receiveReport, true );
//            }
//            return fileResult;
//        }
//        catch (Exception ex)
//        {
//            LogNet?.WriteException( ToString( ), ex );
//            socket?.Close( );
//            return new OperateResult<FileBaseInfo>( )
//            {
//                Message = ex.Message
//            };
//        }
//    }


    /// <summary>
    /// [自校验] 从网络中接收一个文件，写入数据流，如果结果异常，则结束通讯，参数顺序文件名，文件大小，文件标识，上传人
    /// </summary>
    /// <param name="socket">网络套接字</param>
    /// <param name="stream">等待写入的数据流</param>
    /// <param name="receiveReport">接收进度报告</param>
    /// <returns></returns>
//    protected OperateResult<FileBaseInfo> ReceiveFileFromSocket( Socket socket, Stream stream, Action<long, long> receiveReport )
//    {
//        // 先接收文件头信息
//        OperateResult<FileBaseInfo> fileResult = ReceiveFileHeadFromSocket( socket );
//        if (!fileResult.IsSuccess) return fileResult;
//
//        try
//        {
//            WriteStream( socket, stream, fileResult.Content.Size, receiveReport, true );
//            return fileResult;
//        }
//        catch (Exception ex)
//        {
//            LogNet?.WriteException( ToString( ), ex );
//            socket?.Close( );
//            return new OperateResult<FileBaseInfo>( )
//            {
//                Message = ex.Message
//            };
//        }
//    }

    /**
     * 删除文件的操作
     * @param filename 文件的名称
     * @return 是否删除成功
     */
    protected boolean DeleteFileByName( String filename )
    {
        try
        {
            File file = new File(filename);

            if (!file.exists()) return true;
            file.delete();
            return true;
        }
        catch (Exception ex)
        {
            if(LogNet!=null) LogNet.WriteException( toString( ), "delete file failed:" + filename, ex );
            return false;
        }
    }

    /**
     * 预处理文件夹的名称，除去文件夹名称最后一个'\'，如果有的话
     * @param folder 文件夹名称
     * @return 结果数据
     */
    protected String PreprocessFolderName( String folder ) {
        if (folder.endsWith("\\")) {
            return folder.substring(0, folder.length() - 1);
        } else {
            return folder;
        }
    }

    // region Object Override

    @Override
    public String toString()
    {
        return "NetworkXBase";
    }

    // endregion
}
