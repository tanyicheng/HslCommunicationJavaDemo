package HslCommunication.Core.IMessage;

public class FetchWriteMessage implements INetMessage {
    public int ProtocolHeadBytesLength() {
        return 16;
    }

    public int GetContentLengthByHeadBytes() {
        if (HeadBytes == null) return 0;

        if (SendBytes == null) return 16;

        if (HeadBytes[5] == 0x04) {
            return 0;
        } else {
            return (HeadBytes[12] & 0xff) * 256 + (HeadBytes[13] & 0xff);
        }
    }

    public boolean CheckHeadBytesLegal(byte[] token) {
        if (HeadBytes == null) return false;
        if (HeadBytes[0] == 0x53 && HeadBytes[1] == 0x35) {
            return true;
        } else {
            return false;
        }
    }

    public int GetHeadBytesIdentity() {
        return HeadBytes[3];
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
