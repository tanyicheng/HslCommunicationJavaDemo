package HslCommunication.Core.IMessage;

/**
 * 本系统的消息类，包含了各种解析规则，数据信息提取规则
 * The message class of this system contains various parsing rules and data information extraction rules
 */
public interface INetMessage {

    /**
     * 消息头的指令长度，第一次接受数据的长度
     * Instruction length of the message header, the length of the first received data
     */
    int ProtocolHeadBytesLength();

    /**
     * 从当前的头子节文件中提取出接下来需要接收的数据长度
     * Extract the length of the data to be received from the current header file
     * @return 返回接下来的数据内容长度
     */
    int GetContentLengthByHeadBytes();

    /**
     * 检查头子节的合法性
     * Check the legitimacy of the head subsection
     * @param token 特殊的令牌，有些特殊消息的验证
     * @return 是否合法的验证
     */
    boolean CheckHeadBytesLegal(byte[] token);

    /**
     * 获取头子节里的消息标识
     * Get the message ID in the header subsection
     * @return 消息标识
     */
    int GetHeadBytesIdentity();

    /**
     * 设置消息头子节
     * Set Message header byte
     * @param headBytes 字节数据
     */
    void setHeadBytes(byte[] headBytes);

    /**
     * 获取消息头字节
     * Get Message header byte
     * @return byte[]
     */
    byte[] getHeadBytes();

    /**
     * 获取消息内容字节
     * Get Message content byte
     * @return
     */
    byte[] getContentBytes();

    /**
     * 设置消息内容字节
     * Set Message content byte
     * @param contentBytes 内容字节
     */
    void setContentBytes(byte[] contentBytes);

    /**
     * 获取发送的消息
     * Get Byte information sent
     * @return byte[]
     */
    byte[] getSendBytes();

    /**
     * 设置发送的字节信息
     * Set Byte information sent
     * @param sendBytes 发送的字节信息
     */
    void setSendBytes(byte[] sendBytes);

}
