package HslCommunication.MQTT;

import java.util.Date;

/**
 * 来自客户端的一次消息的内容，当前类主要是在MQTT的服务端进行使用<br />
 * The content of a message from the client. The current class is mainly used on the MQTT server
 */
public class MqttClientApplicationMessage extends MqttApplicationMessage {
    /**
     * 实例化一个默认的对象<br />
     * Instantiate a default object
     */
    public MqttClientApplicationMessage(){
        createTime = new Date();
    }

    /**
     * 获取客户端的Id信息<br />
     * get Client Id information
     * @return Id information
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * 设置客户端的Id信息<br />
     * set Client Id information
     * @param clientId id信息
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * 获取当前的客户端的用户名<br />
     * Get Username of the current client
     * @return User name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置的客户端的用户名<br />
     *  Set Username of the current client
     * @param userName 用户名
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 获取当前的消息是否取消发布，默认False，也就是发布出去<br />
     * Get whether the current message is unpublished, the default is False, which means it is published
     * @return 是否发布出去的标记
     */
    public boolean isCancelPublish() {
        return isCancelPublish;
    }

    /**
     * 设置当前的消息是否取消发布，默认False，也就是发布出去<br />
     * Set whether the current message is unpublished, the default is False, which means it is published
     * @param cancelPublish 设置值
     */
    public void setCancelPublish(boolean cancelPublish) {
        isCancelPublish = cancelPublish;
    }

    /**
     * 获取当前消息的生成时间<br />
     * Get The generation time of the current message
     * @return 生成时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置当前消息的生成时间<br />
     * Set The generation time of the current message
     * @param createTime 设置值
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    private String clientId = "";
    private String userName = "";
    private boolean isCancelPublish = false;
    private Date createTime = null;
}
