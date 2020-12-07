package HslCommunication.Core.IMessage;

import HslCommunication.Utilities;

public class AdsNetMessage implements INetMessage {

    public int ProtocolHeadBytesLength(){
        return 6;
    }

    public int GetContentLengthByHeadBytes() {
        if (HeadBytes == null) return 0;
        if (HeadBytes.length >= 6) {
            return Utilities.getInt(HeadBytes, 2);
        } else
            return 0;
    }

    public boolean CheckHeadBytesLegal(byte[] token)
    {
        return true;
    }

    public int GetHeadBytesIdentity(){
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

    public void setHeadBytes(byte[] headBytes){
        HeadBytes = headBytes;
    }

    public void setContentBytes(byte[] contentBytes){
        ContentBytes = contentBytes;
    }

    public void setSendBytes(byte[] sendBytes){
        SendBytes = sendBytes;
    }

    private byte[] HeadBytes = null;

    private byte[] ContentBytes = null;

    private byte[] SendBytes = null;
}
