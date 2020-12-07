package HslCommunication.Profinet.AllenBradley;

import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExThree;
import HslCommunication.StringResources;
import HslCommunication.Utilities;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * AB PLC的辅助类，用来辅助生成基本的指令信息
 */
public class AllenBradleyHelper {

    //region Static Service Code

    /**
     * CIP命令中的读取数据的服务
     */
    public static final byte CIP_READ_DATA = 0x4C;

    /**
     * CIP命令中的写数据的服务
     */
    public static final int CIP_WRITE_DATA = 0x4D;

    /**
     * CIP命令中的读并写的数据服务
     */
    public static final int CIP_READ_WRITE_DATA = 0x4E;

    /**
     * CIP命令中的读片段的数据服务
     */
    public static final int CIP_READ_FRAGMENT = 0x52;

    /**
     * CIP命令中的写片段的数据服务
     */
    public static final int CIP_WRITE_FRAGMENT = 0x53;

    /**
     * CIP命令中的对数据读取服务
     */
    public static final int CIP_MULTIREAD_DATA = 0x1000;

    //endregion

    //region DataType Code

    /**
     * bool型数据，一个字节长度
     */
    public static final int CIP_Type_Bool = 0xC1;

    /**
     * byte型数据，一个字节长度
     */
    public static final int CIP_Type_Byte = 0xC2;

    /**
     * 整型，两个字节长度
     */
    public static final int CIP_Type_Word = 0xC3;

    /**
     * 长整型，四个字节长度
     */
    public static final int CIP_Type_DWord = 0xC4;

    /**
     * 特长整型，8个字节
     */
    public static final int CIP_Type_LInt = 0xC5;

    /**
     * 实数数据，四个字节长度
     */
    public static final int CIP_Type_Real = 0xCA;

    /**
     * 实数数据，八个字节的长度
     */
    public static final int CIP_Type_Double = 0xCB;

    /**
     * 结构体数据，不定长度
     */
    public static final int CIP_Type_Struct = 0xCC;

    /**
     * 字符串数据内容
     */
    public static final int CIP_Type_String = 0xD0;

    /**
     * 二进制数据内容
     */
    public static final int CIP_Type_BitArray = 0xD3;

    //endregion

    /**
     * 创建包含路径的报文
     * @param address 地址信息
     * @return 报文信息
     */
    private static byte[] BuildRequestPathCommand( String address ) throws IOException {
        ByteArrayOutputStream ms = new ByteArrayOutputStream();
        String[] tagNames = address.split("\\.");

        for (int i = 0; i < tagNames.length; i++) {
            String strIndex = "";
            int indexFirst = tagNames[i].indexOf('[');
            int indexSecond = tagNames[i].indexOf(']');
            if (indexFirst > 0 && indexSecond > 0 && indexSecond > indexFirst) {
                strIndex = tagNames[i].substring(indexFirst + 1, indexSecond );
                tagNames[i] = tagNames[i].substring(0, indexFirst);
            }

            ms.write(0x91);                        // 固定
            byte[] nameBytes = Utilities.getBytes(tagNames[i], "UTF-8");
            ms.write((byte) nameBytes.length);    // 节点的长度值
            ms.write(nameBytes, 0, nameBytes.length);
            if (nameBytes.length % 2 == 1) ms.write(0x00);

            if (!Utilities.IsStringNullOrEmpty(strIndex)) {
                String[] indexes = strIndex.split(",");
                for (int j = 0; j < indexes.length; j++) {
                    int index = Integer.parseInt(indexes[j]);
                    if (index < 256) {
                        ms.write(0x28);
                        ms.write((byte) index);
                    } else {
                        ms.write(0x29);
                        ms.write(0x00);
                        ms.write(Utilities.getBytes(index)[0]);
                        ms.write(Utilities.getBytes(index)[1]);
                    }
                }
            }
        }

        byte[] ret = ms.toByteArray();
        ms.close();
        return ret;
    }

    /**
     * 打包生成一个请求读取数据的节点信息，CIP指令信息
     * @param address 地址
     * @param length 指代数组的长度
     * @return CIP的指令信息
     */
    public static byte[] PackRequsetRead( String address, int length ) throws IOException {
        byte[] buffer = new byte[1024];
        int offset = 0;
        buffer[offset++] = CIP_READ_DATA;
        offset++;

        byte[] requestPath = BuildRequestPathCommand( address );
        System.arraycopy(requestPath, 0, buffer, offset, requestPath.length);
        offset += requestPath.length;

        buffer[1] = (byte)((offset - 2) / 2);
        buffer[offset++] = Utilities.getBytes( length )[0];
        buffer[offset++] = Utilities.getBytes( length )[1];

        byte[] data = new byte[offset];
        System.arraycopy( buffer, 0, data, 0, offset );
        return data;
    }

    /**
     * 打包生成一个请求读取数据片段的节点信息，CIP指令信息
     * @param address 节点的名称
     * @param startIndex 起始的索引位置
     * @param length 读取的数据长度，对于short来说，最大是489长度
     * @return CIP的指令信息
     */
    public static byte[] PackRequestReadSegment(String address, int startIndex, int length ) throws IOException {
        byte[] buffer = new byte[1024];
        int offset = 0;
        buffer[offset++] = CIP_READ_FRAGMENT;
        offset++;


        byte[] requestPath = BuildRequestPathCommand( address );
        System.arraycopy(requestPath, 0, buffer, offset, requestPath.length);
        offset += requestPath.length;

        buffer[1] = (byte)((offset - 2) / 2);
        buffer[offset++] = Utilities.getBytes( length )[0];
        buffer[offset++] = Utilities.getBytes( length )[1];
        buffer[offset++] = Utilities.getBytes( startIndex )[0];
        buffer[offset++] = Utilities.getBytes( startIndex )[1];
        buffer[offset++] = Utilities.getBytes( startIndex )[2];
        buffer[offset++] = Utilities.getBytes( startIndex )[3];

        byte[] data = new byte[offset];
        System.arraycopy( buffer, 0, data, 0, offset );
        return data;
    }

    /**
     * 根据指定的数据和类型，生成对应的数据
     * @param address 地址信息
     * @param typeCode 数据类型
     * @param value 字节值
     * @return CIP的指令信息
     */
    public static byte[] PackRequestWrite( String address, int typeCode, byte[] value ) throws IOException {
        return PackRequestWrite(address,typeCode,value,1);
    }

    /**
     * 根据指定的数据和类型，生成对应的数据
     * @param address 地址信息
     * @param typeCode 数据类型
     * @param value 字节值
     * @param length 如果节点为数组，就是数组长度
     * @return CIP的指令信息
     */
    public static byte[] PackRequestWrite( String address, int typeCode, byte[] value, int length ) throws IOException {
        byte[] buffer = new byte[1024];
        int offset = 0;
        buffer[offset++] = CIP_WRITE_DATA;
        offset++;

        byte[] requestPath = BuildRequestPathCommand( address );
        System.arraycopy(requestPath, 0, buffer, offset, requestPath.length);
        offset += requestPath.length;

        buffer[1] = (byte)((offset - 2) / 2);

        buffer[offset++] = Utilities.getBytes( typeCode )[0];     // 数据类型
        buffer[offset++] = Utilities.getBytes( typeCode )[1];

        buffer[offset++] = Utilities.getBytes( length )[0];       // 固定
        buffer[offset++] = Utilities.getBytes( length )[1];

        System.arraycopy(value,0,buffer,offset,value.length);
        offset += value.length;

        byte[] data = new byte[offset];
        System.arraycopy( buffer, 0, data, 0, offset );
        return data;
    }

    /**
     * 将所有的cip指定进行打包操作。
     * @param portSlot PLC所在的面板槽号
     * @param cips 所有的cip打包指令信息
     * @return 包含服务的信息
     * @throws IOException
     */
    public static byte[] PackCommandService( byte[] portSlot, List<byte[]> cips ) throws IOException {
        ByteArrayOutputStream ms = new ByteArrayOutputStream();
        ms.write( 0x52 );     // 服务
        ms.write( 0x02 );     // 请求路径大小
        ms.write( 0x20 );     // 请求路径
        ms.write( 0x06 );
        ms.write( 0x24 );
        ms.write( 0x01 );
        ms.write( 0x0A );     // 超时时间
        ms.write( 0xF0 );
        ms.write( 0x00 );     // CIP指令长度
        ms.write( 0x00 );

        int count = 0;
        if (cips.size() == 1)
        {
            ms.write( cips.get(0), 0, cips.get(0).length );
            count += cips.get(0).length;
        }
        else
        {
            ms.write( 0x0A );   // 固定
            ms.write( 0x02 );
            ms.write( 0x20 );
            ms.write( 0x02 );
            ms.write( 0x24 );
            ms.write( 0x01 );
            count += 8;

            ms.write( Utilities.getBytes( (short) cips.size() ), 0, 2 );  // 写入项数
            short offect = (short) (0x02 + 2 * cips.size());
            count += 2 * cips.size();

            for (int i = 0; i < cips.size(); i++)
            {
                ms.write( Utilities.getBytes( offect ), 0, 2 );
                offect = (short) (offect + cips.get(i).length);
            }

            for (int i = 0; i < cips.size(); i++)
            {
                ms.write( cips.get(i), 0, cips.get(i).length );
                count += cips.get(i).length;
            }
        }

        ms.write( (byte)((portSlot.length + 1) / 2) );     // Path Size
        ms.write( 0x00 );
        ms.write( portSlot, 0, portSlot.length );
        if (portSlot.length % 2 == 1) ms.write( 0x00 );

        byte[] data = ms.toByteArray( );
        ms.close( );
        data[8] = Utilities.getBytes(count)[0];
        data[9] = Utilities.getBytes(count)[1];
        return data;
    }

    /**
     * 生成读取直接节点数据信息的内容
     * @param service cip指令内容
     * @return 最终的指令值
     * @throws IOException
     */
    public static byte[] PackCommandSpecificData( byte[] service ) throws IOException {
        ByteArrayOutputStream ms = new ByteArrayOutputStream();
        ms.write( 0x00 );
        ms.write( 0x00 );
        ms.write( 0x00 );
        ms.write( 0x00 );
        ms.write( 0x01 );     // 超时
        ms.write( 0x00 );
        ms.write( 0x02 );     // 项数
        ms.write( 0x00 );
        ms.write( 0x00 );     // 连接的地址项
        ms.write( 0x00 );
        ms.write( 0x00 );     // 长度
        ms.write( 0x00 );
        ms.write( 0xB2 );     // 连接的项数
        ms.write( 0x00 );
        ms.write( 0x00 );     // 后面数据包的长度，等全部生成后在赋值
        ms.write( 0x00 );
        ms.write( service, 0, service.length );

        byte[] data = ms.toByteArray( );
        ms.close( );
        data[14] = Utilities.getBytes( (short)(data.length - 16) )[0];
        data[15] = Utilities.getBytes( (short)(data.length - 16) )[1];
        return data;
    }

    /**
     * 将CommandSpecificData的命令，打包成可发送的数据指令
     * @param command 实际的命令暗号
     * @param session 当前会话的id
     * @param commandSpecificData CommandSpecificData命令
     * @return 最终可发送的数据命令
     */
    public static byte[] PackRequestHeader( int command, int session, byte[] commandSpecificData )
    {
        byte[] buffer = new byte[commandSpecificData.length + 24];
        System.arraycopy( commandSpecificData, 0, buffer, 24, commandSpecificData.length );
        System.arraycopy( Utilities.getBytes(command), 0, buffer, 0, 2 );
        System.arraycopy( Utilities.getBytes(commandSpecificData.length ), 0, buffer, 2, 2 );
        System.arraycopy( Utilities.getBytes(session), 0, buffer, 4, 4 );
        return buffer;
    }

    /**
     * 从PLC反馈的数据解析
     * @param response PLC的反馈数据
     * @param isRead 是否是返回的操作
     * @return 带有结果标识的最终数据
     */
    public static OperateResultExThree<byte[], Short, Boolean> ExtractActualData(byte[] response, boolean isRead )
    {
        ArrayList<Byte> data = new ArrayList<>( );

        int offset = 38;
        boolean hasMoreData = false;
        short dataType = 0;
        short count = Utilities.getShort( response, 38 );    // 剩余总字节长度，在剩余的字节里，有可能是一项数据，也有可能是多项
        if (Utilities.getInt( response, 40 ) == 0x8A)
        {
            // 多项数据
            offset = 44;
            int dataCount = Utilities.getShort( response, offset );
            for (int i = 0; i < dataCount; i++)
            {
                int offectStart = Utilities.getShort( response, offset + 2 + i * 2 ) + offset;
                int offectEnd = (i == dataCount - 1) ? response.length : (Utilities.getShort( response, (offset + 4 + i * 2) ) + offset);
                short err = Utilities.getShort( response, offectStart + 2 );
                switch (err)
                {
                    case 0x04: return new OperateResultExThree<>( err, StringResources.Language.AllenBradley04() );
                    case 0x05: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley05() );
                    case 0x06:
                    {
                        // 06的错误码通常是数据长度太多了
                        // CC是符号返回，D2是符号片段返回
                        if (response[offset + 2] == (byte)0xD2 || response[offset + 2] == (byte) 0xCC)
                            return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley06() );
                        break;
                    }
                    case 0x0A: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley0A() );
                    case 0x13: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley13() );
                    case 0x1C: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley1C() );
                    case 0x1E: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley1E() );
                    case 0x26: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley26() );
                    case 0x00: break;
                    default: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.UnknownError() );
                }

                if (isRead)
                {
                    for (int j = offectStart + 6; j < offectEnd; j++)
                    {
                        data.add( response[j] );
                    }
                }
            }
        }
        else
        {
            // 单项数据
            byte err = response[offset + 4];
            switch (err)
            {
                case 0x04: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley04() );
                case 0x05: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley05() );
                case 0x06:
                {
                    hasMoreData = true;
                    break;
                }
                case 0x0A: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley0A() );
                case 0x13: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley13() );
                case 0x1C: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley1C() );
                case 0x1E: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley1E() );
                case 0x26: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley26() );
                case 0x00: break;
                default: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.UnknownError() );
            }

            if (response[offset + 2] == (byte)0xCD || response[offset + 2] == (byte)0xD3) return OperateResultExThree.CreateSuccessResult( new byte[0], dataType, hasMoreData );

            if (response[offset + 2] == (byte)0xCC || response[offset + 2] == (byte)0xD2)
            {
                for (int i = offset + 8; i < offset + 2 + count; i++)
                {
                    data.add( response[i] );
                }
                dataType = Utilities.getShort( response, offset + 6 );
            }
            else if (response[offset + 2] == (byte)0xD5)
            {
                for (int i = offset + 6; i < offset + 2 + count; i++)
                {
                    data.add( response[i] );
                }
            }
        }

        byte[] buffer = new byte[ data.size()];
        for (int i = 0; i< buffer.length; i++){
            buffer[i] = (byte) data.get(i);
        }
        return OperateResultExThree.CreateSuccessResult( buffer , dataType, hasMoreData);
    }

}
