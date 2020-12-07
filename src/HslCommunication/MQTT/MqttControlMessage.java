package HslCommunication.MQTT;

/**
 * 定义了Mqtt的相关的控制报文的信息
 */
public class MqttControlMessage {
    /**
     * 操作失败的信息返回
     */
    public static final byte FAILED = 0x00;
    /**
     * 连接标识
     */
    public static final byte CONNECT = 0x01;

    /**
     * 连接返回的标识
     */
    public static final byte CONNACK = 0x02;

    /**
     * 发布消息
     */
    public static final byte PUBLISH = 0x03;

    /**
     * QoS 1消息发布收到确认
     */
    public static final byte PUBACK = 0x04;

    /**
     *  发布收到（保证交付第一步）
     */
    public static final byte PUBREC = 0x05;

    /**
     * 发布释放（保证交付第二步）
     */
    public static final byte PUBREL = 0x06;

    /**
     * QoS 2消息发布完成（保证交互第三步）
     */
    public static final byte PUBCOMP = 0x07;

    /**
     * 客户端订阅请求
     */
    public static final byte SUBSCRIBE = 0x08;

    /**
     * 订阅请求报文确认
     */
    public static final byte SUBACK = 0x09;

    /**
     * 客户端取消订阅请求
     */
    public static final byte UNSUBSCRIBE = 0x0A;

    /**
     * 取消订阅报文确认
     */
    public static final byte UNSUBACK = 0x0B;

    /**
     * 心跳请求
     */
    public static final byte PINGREQ = 0x0C;

    /**
     * 心跳响应
     */
    public static final byte PINGRESP = 0x0D;

    /**
     * 客户端断开连接
     */
    public static final byte DISCONNECT = 0x0E;

    /**
     * 报告进度
     */
    public static final byte REPORTPROGRESS = 0x0F;
}
