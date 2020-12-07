package HslCommunication.Profinet.Keyence;

import HslCommunication.Core.Address.McAddressData;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Profinet.Melsec.MelsecHelper;
import HslCommunication.Profinet.Melsec.MelsecMcDataType;
import HslCommunication.Profinet.Melsec.MelsecMcNet;

/**
 * 基恩士PLC的数据通信类
 */
public class KeyenceMcNet extends MelsecMcNet {

    /**
     * 实例化基恩士的Qna兼容3E帧协议的通讯对象
     */
    public KeyenceMcNet( )
    {
        super();
    }

    /**
     * 实例化一个基恩士的Qna兼容3E帧协议的通讯对象
     * @param ipAddress PLC的Ip地址
     * @param port PLC的端口
     */
    public KeyenceMcNet( String ipAddress, int port ) {
        super(ipAddress, port);
    }


    protected OperateResultExOne<McAddressData> McAnalysisAddress(String address, short length) {
        return McAddressData.ParseKeyenceFrom(address, length);
    }

    /**
     * 返回表示当前对象的字符串
     * @return 字符串信息
     */
    @Override
    public String toString()
    {
        return String.format("KeyenceMcNet[%s:%d]", getIpAddress(), getPort());
    }
}
