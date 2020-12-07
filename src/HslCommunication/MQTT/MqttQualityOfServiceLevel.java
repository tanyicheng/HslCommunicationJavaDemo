package HslCommunication.MQTT;

public enum  MqttQualityOfServiceLevel {
    /**
     * 最多一次
     */
    AtMostOnce,

    /**
     * 最少一次
     */
    AtLeastOnce,

    /**
     * 只有一次
     */
    ExactlyOnce,

    /**
     * 消息只发送到服务器而不触发发布订阅，该消息质量等级只对HSL的MQTT服务器有效<br />
     * The message is only sent to the server without triggering publish and subscribe, the message quality level is only valid for the HSL MQTT server
     */
    OnlyTransfer
}
