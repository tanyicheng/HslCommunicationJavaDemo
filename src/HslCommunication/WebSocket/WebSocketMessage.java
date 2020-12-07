package HslCommunication.WebSocket;

import HslCommunication.Utilities;

/**
 * websocket 协议下的单个消息的数据对象<br />
 * Data object for a single message under the websocket protocol
 */
public class WebSocketMessage {

    /**
     * 是否存在掩码<br />
     * Whether a mask exists
     */
    public boolean HasMask = false;

    /**
     * 当前的websocket的操作码<br />
     * The current websocket opcode
     */
    public int OpCode = 0;

    /**
     * 负载数据
     */
    public byte[] Payload = null;

    @Override
    public String toString() {
        return "WebSocketMessage{" +
                "HasMask=" + HasMask +
                ", OpCode=" + OpCode +
                ", Payload=" + Utilities.getString(Payload, "UTF-8") +
                '}';
    }

}
