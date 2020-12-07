package HslCommunication.MQTT;

/**
 * Mqtt的一次完整消息内容，包含主题，负载数据，消息等级。<br />
 * Mqtt's complete message content, including subject, payload data, message level.
 */
public class MqttApplicationMessage {

    /**
     * 这个字段表示应用消息分发的服务质量等级保证。分为，最多一次，最少一次，正好一次，只发不推送。<br />
     * This field indicates the quality of service level guarantee for application message distribution. Divided into, at most once, at least once, exactly once
     */
    public MqttQualityOfServiceLevel QualityOfServiceLevel = MqttQualityOfServiceLevel.AtMostOnce;

    /**
     * 主题名（Topic Name）用于识别有效载荷数据应该被发布到哪一个信息通道。<br />
     * The Topic Name is used to identify which information channel the payload data should be published to.
     */
    public String Topic = null;

    /**
     * 有效载荷包含将被发布的应用消息。数据的内容和格式是应用特定的。<br />
     * The payload contains application messages to be published. The content and format of the data is application specific.
     */
    public byte[] Payload = null;

    /**
     * 该消息是否在服务器端进行保留，详细的说明参照文档的备注<br />
     * Whether the message is retained on the server. For details, refer to the remarks of the document.
     */
    public boolean Retain = false;

    @Override
    public String toString() {
        return Topic;
    }
}
