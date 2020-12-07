package HslCommunication.MQTT;

/**
 * 连接MQTT服务器的一些参数信息，适用 {@link MqttClient} 消息发布订阅客户端以及 {@link MqttSyncClient} 同步请求客户端。<br />
 * Some parameter information for connecting to the MQTT server is applicable to the {@link MqttClient} message publishing and subscription client and the {@link MqttSyncClient} synchronization request client.
 */
public class MqttConnectionOptions {
    // region Constructor

    /**
     * 实例化一个默认的对象<br />
     * Instantiate a default object
     */
    public MqttConnectionOptions( )
    {
        ClientId                  = "";
        IpAddress                 = "127.0.0.1";
        Port                      = 1883;
        KeepAlivePeriod           = 100;
        KeepAliveSendInterval     = 30;
        CleanSession              = true;
        ConnectTimeout            = 5000;
    }

    // endregion

    /**
     * Mqtt服务器的ip地址<br />
     * IP address of Mqtt server
     */
    public String IpAddress = "";

    /**
     * 端口号。默认1883<br />
     * The port number. Default 1883
     */
    public int Port = 1883;

    /**
     * 客户端的id的标识<br />
     * ID of the client
     */
    public String ClientId = "";

    /**
     * 连接到服务器的超时时间，默认是5秒，单位是毫秒<br />
     * The timeout period for connecting to the server, the default is 5 seconds, the unit is milliseconds
     */
    public int ConnectTimeout = 2000;

    /**
     * 登录服务器的凭证<br />
     * The credentials for logging in to the server, including the username and password, can be null
     */
    public MqttCredential Credentials = null;

    /**
     * 设置的参数，最小单位为1s，当超过设置的时间间隔没有发送数据的时候，必须发送PINGREQ报文，否则服务器认定为掉线。<br />
     * The minimum unit of the set parameter is 1s. When no data is sent beyond the set time interval, the PINGREQ message must be sent, otherwise the server considers it to be offline.
     */
    public int KeepAlivePeriod = 10;

    /**
     * 获取或是设置心跳时间的发送间隔。默认30秒钟<br />
     * Get or set the heartbeat time interval. 30 seconds by default
     */
    public int KeepAliveSendInterval = 30;

    /**
     * 是否清理会话，如果清理会话（CleanSession）标志被设置为1，客户端和服务端必须丢弃之前的任何会话并开始一个新的会话。
     * 会话仅持续和网络连接同样长的时间。与这个会话关联的状态数据不能被任何之后的会话重用 [MQTT-3.1.2-6]。默认为清理会话。<br />
     * Whether to clean the session. If the CleanSession flag is set to 1, the client and server must discard any previous session and start a new session.
     * The session only lasts as long as the network connection. The state data associated with this session cannot be reused by any subsequent sessions [MQTT-3.1.2-6].
     * The default is to clean up the session.
     */
    public boolean CleanSession = false;
}
