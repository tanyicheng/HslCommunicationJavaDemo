package HslCommunication.Core.IMessage;

/**
 * Modbus Tcp协议的消息对象，用来确定接收规则的
 */
public class ModbusTcpMessage implements INetMessage {
    public int ProtocolHeadBytesLength() {
        return 8;
    }

    public int GetContentLengthByHeadBytes() {
        if (HeadBytes == null) return 0;
        if (HeadBytes.length >= ProtocolHeadBytesLength()) {
            int length = (HeadBytes[4] & 0xff) * 256 + (HeadBytes[5] & 0xff);
            if (length == 0) {
                byte[] buffer = new byte[ProtocolHeadBytesLength() - 1];
                for (int i = 0; i < buffer.length; i++) {
                    buffer[i] = HeadBytes[i + 1];
                }
                HeadBytes = buffer;
                return (HeadBytes[5] & 0xff) * 256 + (HeadBytes[6] & 0xff) - 1;
            } else {
                return length - 2;
            }
        }
        return 0;
    }

    public boolean CheckHeadBytesLegal(byte[] token) {
        if (HeadBytes == null) return false;
        if (HeadBytes[2] == 0x00 && HeadBytes[3] == 0x00) {
            return true;
        } else {
            return false;
        }
    }

    public int GetHeadBytesIdentity() {
        return 0;
    }

    public byte[] getHeadBytes() {
        return HeadBytes;
    }

    public byte[] getContentBytes() {
        return ContentBytes;
    }

    public byte[] getSendBytes() {
        return SendBytes;
    }

    public void setHeadBytes(byte[] headBytes) {
        HeadBytes = headBytes;
    }

    public void setContentBytes(byte[] contentBytes) {
        ContentBytes = contentBytes;
    }

    public void setSendBytes(byte[] sendBytes) {
        SendBytes = sendBytes;
    }

    private byte[] HeadBytes = null;

    private byte[] ContentBytes = null;

    private byte[] SendBytes = null;
}
