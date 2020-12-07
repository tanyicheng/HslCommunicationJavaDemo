package HslCommunication.Profinet.Omron;

import HslCommunication.Core.Net.NetworkBase.NetworkUdpDeviceBase;
import HslCommunication.Core.Transfer.DataFormat;
import HslCommunication.Core.Transfer.ReverseWordTransform;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;

public class OmronFinsUdp extends NetworkUdpDeviceBase {
    //region Constructor

    public OmronFinsUdp(String ipAddress, int port) {
        WordLength = 1;
        setIpAddress(ipAddress);
        setPort(port);
        ;
        ReverseWordTransform transform = new ReverseWordTransform();
        transform.setDataFormat(DataFormat.CDAB);
        transform.setIsStringReverse(true);
        setByteTransform(transform);
    }

    /// <inheritdoc cref="OmronFinsNet()"/>
    public OmronFinsUdp() {
        WordLength = 1;
        ReverseWordTransform transform = new ReverseWordTransform();
        transform.setDataFormat(DataFormat.CDAB);
        transform.setIsStringReverse(true);
        setByteTransform(transform);
    }

    //endregion

    //region IpAddress Override

    /**
     * 重写Ip地址的赋值的实现
     *
     * @param ipAddress IP地址
     */
    @Override
    public void setIpAddress(String ipAddress) {
        super.setIpAddress(ipAddress);
        DA1 = (byte) Integer.parseInt(ipAddress.substring(ipAddress.lastIndexOf(".") + 1));
    }

    //endregion

    //region Public Member

    /**
     * 信息控制字段，默认0x80
     * Information control field, default 0x80
     */
    public byte ICF = (byte) 0x80;

    /**
     * 系统使用的内部信息
     * Internal information used by the system
     */
    public byte RSV = 0x00;

    /**
     * 网络层信息，默认0x02，如果有八层消息，就设置为0x07
     * Network layer information, default is 0x02, if there are eight layers of messages, set to 0x07
     */
    public byte GCT = 0x02;

    /**
     * PLC的网络号地址，默认0x00
     * PLC network number address, default 0x00
     */
    public byte DNA = 0x00;


    /**
     * PLC的节点地址，这个值在配置了ip地址之后是默认赋值的，默认为Ip地址的最后一位<br />
     * PLC node address. This value is assigned by default after the IP address is configured. The default is the last bit of the IP address.
     */
    public byte DA1 = 0x13;

    /**
     * PLC的单元号地址，通常都为0<br />
     * PLC unit number address, usually 0
     */
    public byte DA2 = 0x00;

    /**
     * 上位机的网络号地址<br />
     * Network number and address of the computer
     */
    public byte SNA = 0x00;

    /**
     * 上位机的节点地址，假如你的电脑的Ip地址为192.168.0.13，那么这个值就是13<br />
     * The node address of the upper computer. If your computer's IP address is 192.168.0.13, then this value is 13
     */
    public byte SA1 = 0x0B;

    /**
     * 上位机的单元号地址<br />
     * Unit number and address of the computer
     */
    public byte SA2 = 0x00;

    /**
     * 设备的标识号<br />
     * Device identification number
     */
    public byte SID = 0x00;


    //endregion

    //region Build Command

    private byte[] PackCommand(byte[] cmd) {
        byte[] buffer = new byte[10 + cmd.length];
        buffer[0] = ICF;
        buffer[1] = RSV;
        buffer[2] = GCT;
        buffer[3] = DNA;
        buffer[4] = DA1;
        buffer[5] = DA2;
        buffer[6] = SNA;
        buffer[7] = SA1;
        buffer[8] = SA2;
        buffer[9] = SID;
        System.arraycopy(cmd, 0, buffer, 10, cmd.length);

        return buffer;
    }

    public OperateResultExOne<byte[]> BuildReadCommand(String address, short length, boolean isBit) {
        OperateResultExOne<byte[]> command = OmronFinsNetHelper.BuildReadCommand(address, length, isBit);
        if (!command.IsSuccess) return command;

        return OperateResultExOne.CreateSuccessResult(PackCommand(command.Content));
    }

    public OperateResultExOne<byte[]> BuildWriteCommand(String address, byte[] value, boolean isBit) {
        OperateResultExOne<byte[]> command = OmronFinsNetHelper.BuildWriteWordCommand(address, value, isBit);
        if (!command.IsSuccess) return command;

        return OperateResultExOne.CreateSuccessResult(PackCommand(command.Content));
    }

    //endregion

    //region Read Write Support

    public OperateResultExOne<byte[]> Read(String address, short length) {
        // 获取指令
        OperateResultExOne<byte[]> command = BuildReadCommand(address, length, false);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        // 核心数据交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        // 数据有效性分析
        OperateResultExOne<byte[]> valid = OmronFinsNetHelper.UdpResponseValidAnalysis(read.Content, true);
        if (!valid.IsSuccess) return OperateResultExOne.CreateFailedResult(valid);

        // 读取到了正确的数据
        return OperateResultExOne.CreateSuccessResult(valid.Content);
    }

    public OperateResult Write(String address, byte[] value) {
        // 获取指令
        OperateResultExOne<byte[]> command = BuildWriteCommand(address, value, false);
        if (!command.IsSuccess) return command;

        // 核心数据交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return read;

        // 数据有效性分析
        OperateResultExOne<byte[]> valid = OmronFinsNetHelper.UdpResponseValidAnalysis(read.Content, false);
        if (!valid.IsSuccess) return valid;

        // 成功
        return OperateResult.CreateSuccessResult();
    }

    //endregion

    //region Read Write bool

    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        // 获取指令
        OperateResultExOne<byte[]> command = BuildReadCommand(address, length, true);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        // 核心数据交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        // 数据有效性分析
        OperateResultExOne<byte[]> valid = OmronFinsNetHelper.UdpResponseValidAnalysis(read.Content, true);
        if (!valid.IsSuccess) return OperateResultExOne.CreateFailedResult(valid);

        // 返回正确的数据信息
        boolean[] result = new boolean[valid.Content.length];
        for (int i = 0; i < result.length; i++) {
            if (valid.Content[i] != 0x00) {
                result[i] = true;
            }
        }
        return OperateResultExOne.CreateSuccessResult(result);
    }

    public OperateResult Write(String address, boolean[] values) {
        byte[] result = new byte[values.length];
        for (int i = 0; i < result.length; i++) {
            if (values[i]) {
                result[i] = 0x01;
            }
        }
        // 获取指令
        OperateResultExOne<byte[]> command = BuildWriteCommand(address, result, true);
        if (!command.IsSuccess) return command;

        // 核心数据交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return read;

        // 数据有效性分析
        OperateResultExOne<byte[]> valid = OmronFinsNetHelper.UdpResponseValidAnalysis(read.Content, false);
        if (!valid.IsSuccess) return valid;

        // 写入成功
        return OperateResult.CreateSuccessResult();
    }

    //endregion

    //region Object Override

    public String ToString() {
        return "OmronFinsUdp[" + getIpAddress() + ":" + getPort() + "]";
    }

    //endregion
}
