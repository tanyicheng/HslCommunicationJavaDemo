package HslCommunication.Core.Net.StateOne;

import java.net.Socket;
import java.util.Date;

/**
 * 异形客户端的对象
 */
public class AlienSession {

    /**
     * 实例化一个默认对象
     */
    public AlienSession()
    {
        this.isStatusOk = true;
        this.OnlineTime = new Date();
    }

    /**
     * 获取套接字
     * @return
     */
    public java.net.Socket getSocket() {
        return socket;
    }

    /**
     * 设置套接字信息
     * @param socket 当前的值
     */
    public void setSocket(java.net.Socket socket) {
        this.socket = socket;
    }

    /**
     * 获取设备唯一的DTU对象
     * @return
     */
    public String getDTU() {
        return DTU;
    }

    /**
     * 设置设备的唯一的DTU信息
     * @param DTU
     */
    public void setDTU(String DTU) {
        this.DTU = DTU;
    }

    /**
     * 获取当前的连接状态是否正常
     * @return
     */
    public boolean getIsStatusOk() {
        return this.isStatusOk;
    }

    /**
     * 设置当前的连接状态
     * @param isStatusOk
     */
    public void setIsStatusOk(boolean isStatusOk) {
        this.isStatusOk = isStatusOk;
    }

    /**
     * 获取密码信息
     * @return 字符串数据
     */
    public String getPWD() {
        return PWD;
    }

    /**
     * 设置密码信息
     * @param PWD 字符串数据
     */
    public void setPWD(String PWD) {
        this.PWD = PWD;
    }

    /**
     * 进行下线操作
     */
    public void Offline( ) {
        if (isStatusOk) {
            isStatusOk = false;
        }
    }

    private Socket socket = null;               // 网络套接字
    private String DTU = "";                    // 唯一的标识
    private boolean isStatusOk = false;         // 当前的网络状态
    private String PWD = "";
    private Date OnlineTime = null;

}
