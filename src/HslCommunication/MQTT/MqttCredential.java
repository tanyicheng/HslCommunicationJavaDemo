package HslCommunication.MQTT;

/**
 * Mqtt协议的验证对象，包含用户名和密码<br />
 * Authentication object of Mqtt protocol, including username and password
 */
public class MqttCredential {
    // region Constructor

    /**
     * 实例化一个默认的对象<br />
     * Instantiate a default object
     */
    public MqttCredential( ) { }

    /**
     * 实例化指定的用户名和密码的对象<br />
     * Instantiates an object with the specified username and password
     * @param name 用户名
     * @param pwd 密码
     */
    public MqttCredential(String name, String pwd )
    {
        UserName = name;
        Password = pwd;
    }

    // endregion

    /**
     * 获取当前连接的用户名<br />
     * Get username
     * @return 用户名数据
     */
    public String getUserName() {
        return UserName;
    }

    /**
     * 设置当前的连接的用户名<br />
     * Set username
     * @param userName 用户名数据
     */
    public void setUserName(String userName) {
        UserName = userName;
    }

    /**
     * 获取当前连接的密码<br />
     * Get password
     * @return 密码信息
     */
    public String getPassword() {
        return Password;
    }

    /**
     * 设置当前连接的密码<br />
     * Set password
     * @param password 密码数据
     */
    public void setPassword(String password) {
        Password = password;
    }

    private String UserName = null;
    private String Password = null;

    public String toString( ) {
        return UserName;
    }
}
