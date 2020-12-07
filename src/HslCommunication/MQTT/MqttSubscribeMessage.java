package HslCommunication.MQTT;

import HslCommunication.BasicFramework.SoftBasic;

/**
 * 订阅的消息类，用于客户端向服务器请求订阅的信息<br />
 * Subscribed message class, used by the client to request subscription information from the server
 */
public class MqttSubscribeMessage {
    /**
     * 实例化一个默认的对象<br />
     * Instantiate a default object
     */
    public MqttSubscribeMessage( )
    {
        QualityOfServiceLevel = MqttQualityOfServiceLevel.AtMostOnce;
    }

    /**
     * 这个字段表示应用消息分发的服务质量等级保证。分为，最多一次，最少一次，正好一次<br />
     * This field indicates the quality of service guarantee for application message distribution. Divided into, at most once, at least once, exactly once
     */
    public MqttQualityOfServiceLevel QualityOfServiceLevel;

    /**
     * 当前的消息的标识符，当质量等级为0的时候，不需要重发以及考虑标识情况<br />
     * The identifier of the current message, when the quality level is 0, do not need to retransmit and consider the identification situation
     */
    public int Identifier = 0;

    /**
     * 当前订阅的所有的主题的数组信息<br />
     * Array information of all topics currently subscribed
     */
    public String[] Topics = null;

    public String toString( ) {
        return  "MqttSubcribeMessage" + SoftBasic.ArrayFormat( Topics ) + "}";
    }
}
