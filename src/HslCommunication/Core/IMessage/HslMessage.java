package HslCommunication.Core.IMessage;


import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Utilities;

/**
 * 本组件系统使用的默认的消息规则，说明解析和反解析规则的
 */
public class HslMessage implements INetMessage {
    public int ProtocolHeadBytesLength() {
        return 32;
    }

    public int GetContentLengthByHeadBytes() {
        if (HeadBytes == null) return 0;
        if (HeadBytes.length != 32) return 0;

        return Utilities.getInt(HeadBytes, 28);
    }

    public boolean CheckHeadBytesLegal(byte[] token) {
        return SoftBasic.IsTwoBytesEquel(HeadBytes, 12, token, 0, 16);
    }

    public int GetHeadBytesIdentity() {
        return Utilities.getInt(HeadBytes, 0);
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
