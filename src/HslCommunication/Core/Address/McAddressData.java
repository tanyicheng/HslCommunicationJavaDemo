package HslCommunication.Core.Address;

import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Profinet.Melsec.MelsecMcDataType;
import HslCommunication.Profinet.Panasonic.PanasonicHelper;
import HslCommunication.StringResources;

/**
 * 三菱的数据地址表示形式
 * Mitsubishi's data address representation
 */
public class McAddressData extends DeviceAddressDataBase {

    public McAddressData(){

    }

    @Override
    public void Parse(String address, int length) {
        OperateResultExOne<McAddressData> addressData = ParseMelsecFrom( address, length );
        if (addressData.IsSuccess)
        {
            setAddressStart( addressData.Content.getAddressStart());
            setLength(addressData.Content.getLength());
            McDataType      = addressData.Content.McDataType;
        }
    }

    /**
     * 获取三菱的数据类型及地址信息
     * @return
     */
    public MelsecMcDataType getMcDataType() {
        return McDataType;
    }

    public void setMcDataType(MelsecMcDataType mcDataType) {
        McDataType = mcDataType;
    }

    private MelsecMcDataType McDataType = null;


    /**
     * 从实际三菱的地址里面解析出我们需要的地址类型
     * Resolve the type of address we need from the actual Mitsubishi address
     * @param address 三菱的地址数据信息
     * @param length 读取的数据长度
     * @return 是否成功的结果对象
     */
    public static OperateResultExOne<McAddressData> ParseMelsecFrom( String address, int length )
    {
        McAddressData addressData = new McAddressData( );
        addressData.setLength( length );
        try
        {
            switch (address.charAt(0))
            {
                case 'M':
                case 'm':
                {
                    addressData.McDataType = MelsecMcDataType.M;
                    addressData.setAddressStart( Integer.parseInt( address.substring( 1 ), MelsecMcDataType.M.getFromBase() ));
                    break;
                }
                case 'X':
                case 'x':
                {
                    addressData.McDataType = MelsecMcDataType.X;
                    address = address.substring( 1 );
                    if (address.startsWith( "0" ))
                        addressData.setAddressStart(Integer.parseInt( address, 8 ));
                    else
                        addressData.setAddressStart( Integer.parseInt( address, MelsecMcDataType.X.getFromBase() ) );
                    break;
                }
                case 'Y':
                case 'y':
                {
                    addressData.McDataType = MelsecMcDataType.Y;
                    address = address.substring( 1 );
                    if(address.startsWith( "0" ))
                        addressData.setAddressStart( Integer.parseInt( address, 8 ) );
                    else
                        addressData.setAddressStart( Integer.parseInt( address, MelsecMcDataType.Y.getFromBase() ) );
                    break;
                }
                case 'D':
                case 'd':
                {
                    if (address.charAt(1) == 'X' || address.charAt(1) == 'x')
                    {
                        addressData.McDataType = MelsecMcDataType.DX;
                        address = address.substring( 2 );
                        if (address.startsWith( "0" ))
                            addressData.setAddressStart( Integer.parseInt( address, 8 ) );
                        else
                            addressData.setAddressStart( Integer.parseInt( address, MelsecMcDataType.DX.getFromBase() ) );
                        break;
                    }
                    else if (address.charAt(1) == 'Y' || address.charAt(1) == 's')
                    {
                        addressData.McDataType = MelsecMcDataType.DY;
                        address = address.substring( 2 );
                        if (address.startsWith( "0" ))
                            addressData.setAddressStart( Integer.parseInt( address, 8 ) );
                        else
                            addressData.setAddressStart( Integer.parseInt( address, MelsecMcDataType.DY.getFromBase() ) );
                        break;
                    }
                    else
                    {
                        addressData.McDataType = MelsecMcDataType.D;
                        addressData.setAddressStart( Integer.parseInt( address.substring( 1 ), MelsecMcDataType.D.getFromBase() ) );
                        break;
                    }
                }
                case 'W':
                case 'w':
                {
                    addressData.McDataType = MelsecMcDataType.W;
                    addressData.setAddressStart( Integer.parseInt( address.substring( 1 ), MelsecMcDataType.W.getFromBase() ) );
                    break;
                }
                case 'L':
                case 'l':
                {
                    addressData.McDataType = MelsecMcDataType.L;
                    addressData.setAddressStart( Integer.parseInt( address.substring( 1 ), MelsecMcDataType.L.getFromBase() ) );
                    break;
                }
                case 'F':
                case 'f':
                {
                    addressData.McDataType = MelsecMcDataType.F;
                    addressData.setAddressStart( Integer.parseInt( address.substring( 1 ), MelsecMcDataType.F.getFromBase() ) );
                    break;
                }
                case 'V':
                case 'v':
                {
                    addressData.McDataType = MelsecMcDataType.V;
                    addressData.setAddressStart( Integer.parseInt( address.substring( 1 ), MelsecMcDataType.V.getFromBase() ) );
                    break;
                }
                case 'B':
                case 'b':
                {
                    addressData.McDataType = MelsecMcDataType.B;
                    addressData.setAddressStart( Integer.parseInt( address.substring( 1 ), MelsecMcDataType.B.getFromBase() ) );
                    break;
                }
                case 'R':
                case 'r':
                {
                    addressData.McDataType = MelsecMcDataType.R;
                    addressData.setAddressStart( Integer.parseInt( address.substring( 1 ), MelsecMcDataType.R.getFromBase() ) );
                    break;
                }
                case 'S':
                case 's':
                {
                    if (address.charAt(1) == 'N' || address.charAt(1) == 'n')
                    {
                        addressData.McDataType = MelsecMcDataType.SN;
                        addressData.setAddressStart( Integer.parseInt( address.substring( 2 ), MelsecMcDataType.SN.getFromBase() ) );
                        break;
                    }
                    else if (address.charAt(1) == 'S' || address.charAt(1) == 's')
                    {
                        addressData.McDataType = MelsecMcDataType.SS;
                        addressData.setAddressStart( Integer.parseInt( address.substring( 2 ), MelsecMcDataType.SS.getFromBase() ) );
                        break;
                    }
                    else if (address.charAt(1) == 'C' || address.charAt(1) == 'c')
                    {
                        addressData.McDataType = MelsecMcDataType.SC;
                        addressData.setAddressStart( Integer.parseInt( address.substring( 2 ), MelsecMcDataType.SC.getFromBase() ) );
                        break;
                    }
                    else if (address.charAt(1) == 'M' || address.charAt(1) == 'm')
                    {
                        addressData.McDataType = MelsecMcDataType.SM;
                        addressData.setAddressStart( Integer.parseInt( address.substring( 2 ), MelsecMcDataType.SM.getFromBase() ) );
                        break;
                    }
                    else if (address.charAt(1) == 'D' || address.charAt(1) == 'd')
                    {
                        addressData.McDataType = MelsecMcDataType.SD;
                        addressData.setAddressStart( Integer.parseInt( address.substring( 2 ), MelsecMcDataType.SD.getFromBase() ) );
                        break;
                    }
                    else if (address.charAt(1) == 'B' || address.charAt(1) == 'b')
                    {
                        addressData.McDataType = MelsecMcDataType.SB;
                        addressData.setAddressStart( Integer.parseInt( address.substring( 2 ), MelsecMcDataType.SB.getFromBase() ) );
                        break;
                    }
                    else if (address.charAt(1) == 'W' || address.charAt(1) == 'w')
                    {
                        addressData.McDataType = MelsecMcDataType.SW;
                        addressData.setAddressStart( Integer.parseInt( address.substring( 2 ), MelsecMcDataType.SW.getFromBase() ) );
                        break;
                    }
                    else
                    {
                        addressData.McDataType = MelsecMcDataType.S;
                        addressData.setAddressStart( Integer.parseInt( address.substring( 1 ), MelsecMcDataType.S.getFromBase() ) );
                        break;
                    }
                }
                case 'Z':
                case 'z':
                {
                    if (address.startsWith( "ZR" ) || address.startsWith( "zr" ))
                    {
                        addressData.McDataType = MelsecMcDataType.ZR;
                        addressData.setAddressStart( Integer.parseInt( address.substring( 2 ), MelsecMcDataType.ZR.getFromBase() ) );
                        break;
                    }
                    else
                    {
                        addressData.McDataType = MelsecMcDataType.Z;
                        addressData.setAddressStart( Integer.parseInt( address.substring( 1 ), MelsecMcDataType.Z.getFromBase() ) );
                        break;
                    }
                }
                case 'T':
                case 't':
                {
                    if (address.charAt(1) == 'N' || address.charAt(1) == 'n')
                    {
                        addressData.McDataType = MelsecMcDataType.TN;
                        addressData.setAddressStart( Integer.parseInt( address.substring( 2 ), MelsecMcDataType.TN.getFromBase() ) );
                        break;
                    }
                    else if (address.charAt(1) == 'S' || address.charAt(1) == 's')
                    {
                        addressData.McDataType = MelsecMcDataType.TS;
                        addressData.setAddressStart( Integer.parseInt( address.substring( 2 ), MelsecMcDataType.TS.getFromBase() ) );
                        break;
                    }
                    else if (address.charAt(1) == 'C' || address.charAt(1) == 'c')
                    {
                        addressData.McDataType = MelsecMcDataType.TC;
                        addressData.setAddressStart( Integer.parseInt( address.substring( 2 ), MelsecMcDataType.TC.getFromBase() ) );
                        break;
                    }
                    else
                    {
                        throw new Exception( StringResources.Language.NotSupportedDataType() );
                    }
                }
                case 'C':
                case 'c':
                {
                    if (address.charAt(1) == 'N' || address.charAt(1) == 'n')
                    {
                        addressData.McDataType = MelsecMcDataType.CN;
                        addressData.setAddressStart( Integer.parseInt( address.substring( 2 ), MelsecMcDataType.CN.getFromBase() ) );
                        break;
                    }
                    else if (address.charAt(1) == 'S' || address.charAt(1) == 's')
                    {
                        addressData.McDataType = MelsecMcDataType.CS;
                        addressData.setAddressStart( Integer.parseInt( address.substring( 2 ), MelsecMcDataType.CS.getFromBase() ) );
                        break;
                    }
                    else if (address.charAt(1) == 'C' || address.charAt(1) == 'c')
                    {
                        addressData.McDataType = MelsecMcDataType.CC;
                        addressData.setAddressStart( Integer.parseInt( address.substring( 2 ), MelsecMcDataType.CC.getFromBase() ) );
                        break;
                    }
                    else
                    {
                        throw new Exception( StringResources.Language.NotSupportedDataType() );
                    }
                }
                default: throw new Exception( StringResources.Language.NotSupportedDataType() );
            }
        }
        catch (Exception ex)
        {
            return new OperateResultExOne<>( ex.getMessage() );
        }

        return OperateResultExOne.CreateSuccessResult( addressData );
    }

    /**
     * 从实际基恩士的地址里面解析出
     * Resolve the address information we need from the actual Keyence address
     * @param address 地址数据
     * @param length 长度信息
     * @return 实际的地址对象
     */
    public static OperateResultExOne<McAddressData> ParseKeyenceFrom(String address, int length )
    {
        McAddressData addressData = new McAddressData( );
        addressData.setLength(length);
        try
        {
            switch (address.charAt(0))
            {
                case 'M':
                case 'm':
                {
                    addressData.McDataType = MelsecMcDataType.Keyence_M;
                    addressData.setAddressStart( Integer.parseInt( address.substring( 1 ), MelsecMcDataType.Keyence_M.getFromBase() ) );
                    break;
                }
                case 'X':
                case 'x':
                {
                    addressData.McDataType = MelsecMcDataType.Keyence_X;
                    addressData.setAddressStart( Integer.parseInt( address.substring( 1 ), MelsecMcDataType.Keyence_X.getFromBase() ) );
                    break;
                }
                case 'Y':
                case 'y':
                {
                    addressData.McDataType = MelsecMcDataType.Keyence_Y;
                    addressData.setAddressStart( Integer.parseInt( address.substring( 1 ), MelsecMcDataType.Keyence_Y.getFromBase() ) );
                    break;
                }
                case 'B':
                case 'b':
                {
                    addressData.McDataType = MelsecMcDataType.Keyence_B;
                    addressData.setAddressStart( Integer.parseInt( address.substring( 1 ), MelsecMcDataType.Keyence_B.getFromBase() ) );
                    break;
                }
                case 'L':
                case 'l':
                {
                    addressData.McDataType = MelsecMcDataType.Keyence_L;
                    addressData.setAddressStart( Integer.parseInt( address.substring( 1 ), MelsecMcDataType.Keyence_L.getFromBase() ) );
                    break;
                }
                case 'S':
                case 's':
                {
                    if (address.charAt(1) == 'M' || address.charAt(1) == 'm')
                    {
                        addressData.McDataType = MelsecMcDataType.Keyence_SM;
                        addressData.setAddressStart( Integer.parseInt( address.substring( 2 ), MelsecMcDataType.Keyence_SM.getFromBase() ) );
                        break;
                    }
                    else if (address.charAt(1) == 'D' || address.charAt(1) == 'd')
                    {
                        addressData.McDataType = MelsecMcDataType.Keyence_SD;
                        addressData.setAddressStart( Integer.parseInt( address.substring( 2 ), MelsecMcDataType.Keyence_SD.getFromBase() ) );
                        break;
                    }
                    else
                    {
                        throw new Exception( StringResources.Language.NotSupportedDataType() );
                    }
                }
                case 'D':
                case 'd':
                {
                    addressData.McDataType = MelsecMcDataType.Keyence_D;
                    addressData.setAddressStart( Integer.parseInt( address.substring( 1 ), MelsecMcDataType.Keyence_D.getFromBase() ) );
                    break;
                }
                case 'R':
                case 'r':
                {
                    addressData.McDataType = MelsecMcDataType.Keyence_R;
                    addressData.setAddressStart( Integer.parseInt( address.substring( 1 ), MelsecMcDataType.Keyence_R.getFromBase() ) );
                    break;
                }
                case 'Z':
                case 'z':
                {
                    if (address.charAt(1) == 'R' || address.charAt(1) == 'r')
                    {
                        addressData.McDataType = MelsecMcDataType.Keyence_ZR;
                        addressData.setAddressStart( Integer.parseInt( address.substring( 2 ), MelsecMcDataType.Keyence_ZR.getFromBase() ) );
                        break;
                    }
                    else
                    {
                        throw new Exception( StringResources.Language.NotSupportedDataType() );
                    }
                }
                case 'W':
                case 'w':
                {
                    addressData.McDataType = MelsecMcDataType.Keyence_W;
                    addressData.setAddressStart( Integer.parseInt( address.substring( 1 ), MelsecMcDataType.Keyence_W.getFromBase() ) );
                    break;
                }
                case 'T':
                case 't':
                {
                    if (address.charAt(1) == 'N' || address.charAt(1) == 'n')
                    {
                        addressData.McDataType = MelsecMcDataType.Keyence_TN;
                        addressData.setAddressStart( Integer.parseInt( address.substring( 2 ), MelsecMcDataType.Keyence_TN.getFromBase() ) );
                        break;
                    }
                    else if (address.charAt(1) == 'S' || address.charAt(1) == 's')
                    {
                        addressData.McDataType = MelsecMcDataType.Keyence_TS;
                        addressData.setAddressStart( Integer.parseInt( address.substring( 2 ), MelsecMcDataType.Keyence_TS.getFromBase() ) );
                        break;
                    }
                    else
                    {
                        throw new Exception( StringResources.Language.NotSupportedDataType() );
                    }
                }
                case 'C':
                case 'c':
                {
                    if (address.charAt(1) == 'N' || address.charAt(1) == 'n')
                    {
                        addressData.McDataType = MelsecMcDataType.Keyence_CN;
                        addressData.setAddressStart( Integer.parseInt( address.substring( 2 ), MelsecMcDataType.Keyence_CN.getFromBase() ) );
                        break;
                    }
                    else if (address.charAt(1) == 'S' || address.charAt(1) == 's')
                    {
                        addressData.McDataType = MelsecMcDataType.Keyence_CS;
                        addressData.setAddressStart( Integer.parseInt( address.substring( 2 ), MelsecMcDataType.Keyence_CS.getFromBase() ) );
                        break;
                    }
                    else
                    {
                        throw new Exception( StringResources.Language.NotSupportedDataType() );
                    }
                }
                default: throw new Exception( StringResources.Language.NotSupportedDataType() );
            }
        }
        catch (Exception ex)
        {
            return new OperateResultExOne<>( ex.getMessage() );
        }

        return OperateResultExOne.CreateSuccessResult( addressData );
    }

    /**
     * 从实际松下的地址里面解析出
     * @param address 松下的地址数据信息
     * @param length 读取的数据长度
     * @return 是否成功的结果对象
     */
    public static OperateResultExOne<McAddressData> ParsePanasonicFrom( String address, int length )
    {
        McAddressData addressData = new McAddressData( );
        addressData.setLength(length);
        try
        {
            switch (address.charAt(0))
            {
                case 'R':
                case 'r':
                {
                    int add = PanasonicHelper.CalculateComplexAddress( address.substring( 1 ) );
                    if (add < 14400)
                    {
                        addressData.McDataType = MelsecMcDataType.Panasonic_R;
                        addressData.setAddressStart( add );
                    }
                    else
                    {
                        addressData.McDataType = MelsecMcDataType.Panasonic_SM;
                        addressData.setAddressStart( add - 14400 );
                    }
                    break;
                }
                case 'X':
                case 'x':
                {
                    addressData.McDataType = MelsecMcDataType.Panasonic_X;
                    addressData.setAddressStart( PanasonicHelper.CalculateComplexAddress( address.substring( 1 ) ) );
                    break;
                }
                case 'Y':
                case 'y':
                {
                    addressData.McDataType = MelsecMcDataType.Panasonic_Y;
                    addressData.setAddressStart( PanasonicHelper.CalculateComplexAddress( address.substring( 1 ) ) );
                    break;
                }
                case 'L':
                case 'l':
                {
                    if (address.charAt(1) == 'D' || address.charAt(1) == 'd')
                    {
                        addressData.McDataType = MelsecMcDataType.Panasonic_LD;
                        addressData.setAddressStart( Integer.parseInt( address.substring( 2 ) ) );
                        break;
                    }
                    else
                    {
                        addressData.McDataType = MelsecMcDataType.Panasonic_L;
                        addressData.setAddressStart( PanasonicHelper.CalculateComplexAddress( address.substring( 1 ) ) );
                    }
                    break;
                }
                case 'D':
                case 'd':
                {
                    int add = Integer.parseInt( address.substring( 1 ) );
                    if (add < 90000)
                    {
                        addressData.McDataType = MelsecMcDataType.Panasonic_DT;
                        addressData.setAddressStart( Integer.parseInt( address.substring( 1 ) ) );
                    }
                    else
                    {
                        addressData.McDataType = MelsecMcDataType.Panasonic_SD;
                        addressData.setAddressStart( Integer.parseInt( address.substring( 1 ) ) - 90000 );
                    }
                    break;
                }
                case 'T':
                case 't':
                {
                    if (address.charAt(1) == 'N' || address.charAt(1) == 'n')
                    {
                        addressData.McDataType = MelsecMcDataType.Panasonic_TN;
                        addressData.setAddressStart( Integer.parseInt( address.substring( 2 ) ) );
                        break;
                    }
                    else if (address.charAt(1) == 'S' || address.charAt(1) == 's')
                    {
                        addressData.McDataType = MelsecMcDataType.Panasonic_TS;
                        addressData.setAddressStart( Integer.parseInt( address.substring( 2 ) ) );
                        break;
                    }
                    else
                    {
                        throw new Exception( StringResources.Language.NotSupportedDataType() );
                    }
                }
                case 'C':
                case 'c':
                {
                    if (address.charAt(1) == 'N' || address.charAt(1) == 'n')
                    {
                        addressData.McDataType = MelsecMcDataType.Panasonic_CN;
                        addressData.setAddressStart( Integer.parseInt( address.substring( 2 ) ) );
                        break;
                    }
                    else if (address.charAt(1) == 'S' || address.charAt(1) == 's')
                    {
                        addressData.McDataType = MelsecMcDataType.Panasonic_CS;
                        addressData.setAddressStart( Integer.parseInt( address.substring( 2 ) ) );
                        break;
                    }
                    else
                    {
                        throw new Exception( StringResources.Language.NotSupportedDataType() );
                    }
                }
                default: throw new Exception( StringResources.Language.NotSupportedDataType() );
            }
        }
        catch (Exception ex)
        {
            return new OperateResultExOne<McAddressData>( ex.getMessage() );
        }

        return OperateResultExOne.CreateSuccessResult( addressData );
    }

}
