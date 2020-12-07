package HslCommunication.Core.Address;

import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.StringResources;

/**
 * 西门子的地址数据信息，主要包含数据代码，DB块，偏移地址，当处于写入时，Length无效
 * Address data information of Siemens, mainly including data code, DB block, offset address, when writing, Length is invalid
 */
public class S7AddressData extends DeviceAddressDataBase {

    /**
     * 获取等待读取的数据的代码
     * Get the code of the data waiting to be read
     * @return code
     */
    public int getDataCode() {
        return DataCode;
    }

    /**
     * 设置等待读取的数据的代码
     * Set the code of the data waiting to be read
     * @param dataCode 数据代码
     */
    public void setDataCode(int dataCode) {
        DataCode = dataCode;
    }

    private int DataCode = 0;

    /**
     * 获取PLC的DB块数据信息
     * Get PLC DB data information
     * @return int
     */
    public int getDbBlock() {
        return DbBlock;
    }

    /**
     * 设置PLC的DB块数据信息
     * Set PLC DB data information
     * @param dbBlock value
     */
    public void setDbBlock(int dbBlock) {
        DbBlock = dbBlock;
    }

    private int DbBlock = 0;

    @Override
    public void Parse(String address, int length) {
        OperateResultExOne<S7AddressData> addressData = ParseFrom(address, length);
        if (addressData.IsSuccess) {
            setAddressStart(addressData.Content.getAddressStart());
            setLength(addressData.Content.getLength());
            DataCode = addressData.Content.getDataCode();
            DbBlock = addressData.Content.getDbBlock();
        }
    }

    /**
     * 计算特殊的地址信息
     * Calculate Special Address information
     * @param address 字符串地址
     * @param isCT 是否是定时器和计数器的地址
     * @return 实际值
     */
    public static int CalculateAddressStarted( String address, boolean isCT )
    {
        if (address.indexOf( '.' ) < 0)
        {
            if (isCT)
                return Integer.parseInt( address );
            else
                return Integer.parseInt( address ) * 8;
        }
        else
        {
            String[] temp = address.split( "\\." );
            return Integer.parseInt( temp[0] ) * 8 + Integer.parseInt( temp[1] );
        }
    }

    /**
     * 从实际的西门子的地址里面解析出地址对象
     * Resolve the address object from the actual Siemens address
     * @param address 西门子的地址数据信息
     * @return 是否成功的结果对象
     */
    public static OperateResultExOne<S7AddressData> ParseFrom( String address )
    {
        return ParseFrom( address, 0 );
    }

    /**
     * 从实际的西门子的地址里面解析出地址对象
     * Resolve the address object from the actual Siemens address
     * @param address 西门子的地址数据信息
     * @param length 读取的数据长度
     * @return 是否成功的结果对象
     */
    public static OperateResultExOne<S7AddressData> ParseFrom(String address, int length )
    {
        S7AddressData addressData = new S7AddressData( );
        try
        {
            addressData.setLength(length);
            addressData.DbBlock = 0;
            if (address.charAt(0) == 'I')
            {
                addressData.DataCode = 0x81;
                addressData.setAddressStart( CalculateAddressStarted( address.substring( 1 ), false ) );
            }
            else if (address.charAt(0) == 'Q')
            {
                addressData.DataCode = 0x82;
                addressData.setAddressStart( CalculateAddressStarted( address.substring( 1 ), false ) );
            }
            else if (address.charAt(0) == 'M')
            {
                addressData.DataCode = 0x83;
                addressData.setAddressStart( CalculateAddressStarted( address.substring( 1 ), false) );
            }
            else if (address.charAt(0) == 'D' || address.substring(0, 2).equals("DB"))
            {
                addressData.DataCode = 0x84;
                String[] adds = address.split( "\\." );
                if (address.charAt(1) == 'B')
                {
                    addressData.DbBlock = Integer.parseInt( adds[0].substring( 2 ) );
                }
                else
                {
                    addressData.DbBlock = Integer.parseInt( adds[0].substring( 1 ) );
                }

                addressData.setAddressStart(  CalculateAddressStarted( address.substring( address.indexOf( '.' ) + 1 ), false ) );
            }
            else if (address.charAt(0) == 'T')
            {
                addressData.DataCode = 0x1D;
                addressData.setAddressStart(  CalculateAddressStarted( address.substring( 1 ), true ) );
            }
            else if (address.charAt(0) == 'C')
            {
                addressData.DataCode = 0x1C;
                addressData.setAddressStart(  CalculateAddressStarted( address.substring( 1 ), true ) );
            }
            else if (address.charAt(0) == 'V')
            {
                addressData.DataCode = 0x84;
                addressData.DbBlock = 1;
                addressData.setAddressStart(  CalculateAddressStarted( address.substring( 1 ), false ) );
            }
            else
            {
                return new OperateResultExOne<S7AddressData>( StringResources.Language.NotSupportedDataType() );
            }
        }
        catch (Exception ex)
        {
            return new OperateResultExOne<S7AddressData>( ex.getMessage() );
        }

        return OperateResultExOne.CreateSuccessResult( addressData );
    }
}
