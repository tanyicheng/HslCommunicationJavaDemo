package HslCommunication.Core.IMessage;

import HslCommunication.Utilities;

public class SAMMessage implements INetMessage {

    public int ProtocolHeadBytesLength(){
        return 7;
    }

    public int GetContentLengthByHeadBytes() {
        if (HeadBytes == null) return 0;
        if (HeadBytes.length >= 7) {
            return HeadBytes[5] * 256 + HeadBytes[6];
        } else
            return 0;
    }

    public boolean CheckHeadBytesLegal(byte[] token)
    {
        if (HeadBytes == null) return false;

        return HeadBytes[0] == (byte) 0xAA && HeadBytes[1] == (byte)0xAA && HeadBytes[2] == (byte)0xAA && HeadBytes[3] == (byte)0x96 && HeadBytes[4] == (byte)0x69;
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
