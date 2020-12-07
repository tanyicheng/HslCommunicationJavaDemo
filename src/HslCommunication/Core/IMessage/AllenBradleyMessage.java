package HslCommunication.Core.IMessage;

import HslCommunication.Utilities;

public class AllenBradleyMessage implements INetMessage {

    public int ProtocolHeadBytesLength(){
        return 24;
    }

    public int GetContentLengthByHeadBytes(){
        if(HeadBytes == null) return 0;

        return Utilities.getShort(HeadBytes,2);
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
