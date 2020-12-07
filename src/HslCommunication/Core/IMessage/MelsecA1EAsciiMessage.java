package HslCommunication.Core.IMessage;

import HslCommunication.Utilities;

public class MelsecA1EAsciiMessage implements INetMessage {

    public int ProtocolHeadBytesLength(){
        return 4;
    }

    public int GetContentLengthByHeadBytes() {
        if (HeadBytes[2] == 0x35 && HeadBytes[3] == 0x42) return 4;   // 异常代码 + 0x00
        else if (HeadBytes[2] == 0x30 && HeadBytes[3] == 0x30)
        {
            int length = Integer.parseInt( Utilities.getString( SendBytes, 20, 2, "US-ASCII" ), 16 );
            switch (HeadBytes[1])
            {
                case 0x30: return length;                             // 位单位成批读出后，回复副标题
                case 0x31: return length * 4;                         // 字单位成批读出后，回复副标题
                case 0x32:                                            // 位单位成批写入后，回复副标题
                case 0x33: return 0;                                  // 字单位成批写入后，回复副标题
                default: return 0;
            }
        }
        else
            return 0;

        //在A兼容1E协议中，写入值后，若不发生异常，只返回副标题 + 结束代码(0x00)
        //这已经在协议头部读取过了，后面要读取的长度为0（contentLength=0）
    }

    public boolean CheckHeadBytesLegal(byte[] token)
    {
        if (HeadBytes != null) return ((HeadBytes[0] - SendBytes[0]) == 0x08);
        return false;
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
