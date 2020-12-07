package HslCommunication.Profinet.Melsec;

import HslCommunication.Core.Address.McAddressData;
import HslCommunication.Core.Net.NetworkBase.NetworkUdpDeviceBase;
import HslCommunication.Core.Transfer.RegularByteTransform;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.StringResources;
import HslCommunication.Utilities;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MelsecMcAsciiUdp extends NetworkUdpDeviceBase {

    //region Constructor

    public MelsecMcAsciiUdp() {
        this.WordLength = 1;
        this.setByteTransform(new RegularByteTransform());
    }

    public MelsecMcAsciiUdp(String ipAddress, int port) {
        this.WordLength = 1;
        this.setIpAddress(ipAddress);
        this.setPort(port);
        this.setByteTransform(new RegularByteTransform());
    }

    //endregion

    //region Public Member

    private byte NetworkNumber = 0x00;                       // 网络号
    private byte NetworkStationNumber = 0x00;                // 网络站号

    /**
     * 获取网络号
     *
     * @return 网络号
     */
    public byte getNetworkNumber() {
        return NetworkNumber;
    }

    /**
     * 设置网络号
     *
     * @param networkNumber 网络号
     */
    public void setNetworkNumber(byte networkNumber) {
        NetworkNumber = networkNumber;
    }

    /**
     * 获取网络站号
     *
     * @return 网络站号
     */
    public byte getNetworkStationNumber() {
        return NetworkStationNumber;
    }

    /**
     * 设置网络站号
     *
     * @param networkStationNumber 网络站号
     */
    public void setNetworkStationNumber(byte networkStationNumber) {
        NetworkStationNumber = networkStationNumber;
    }

    //endregion

    //region Address Analysis

    protected OperateResultExOne<McAddressData> McAnalysisAddress(String address, short length) {
        return McAddressData.ParseMelsecFrom(address, length);
    }

    //endregion

    //region Read Write Override

    public OperateResultExOne<byte[]> Read(String address, short length) {
        // 分析地址
        OperateResultExOne<McAddressData> addressResult = McAnalysisAddress(address, length);
        if (!addressResult.IsSuccess) return OperateResultExOne.CreateFailedResult(addressResult);

        ArrayList<Byte> bytesContent = new ArrayList<Byte>();
        short alreadyFinished = 0;
        while (alreadyFinished < length) {
            short readLength = (short) Math.min(length - alreadyFinished, 450);
            addressResult.Content.setLength(readLength);
            OperateResultExOne<byte[]> read = ReadAddressData(addressResult.Content);
            if (!read.IsSuccess) return read;

            Utilities.ArrayListAddArray(bytesContent, read.Content);
            alreadyFinished += readLength;

            // 字的话就是正常的偏移位置，如果是位的话，就转到位的数据
            if (addressResult.Content.getMcDataType().getDataType() == 0)
                addressResult.Content.setAddressStart(addressResult.Content.getAddressStart() + readLength);
            else
                addressResult.Content.setAddressStart(addressResult.Content.getAddressStart() + readLength * 16);
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.getBytes(bytesContent));
    }

    private OperateResultExOne<byte[]> ReadAddressData(McAddressData addressData) {
        // 地址分析
        byte[] coreResult = MelsecHelper.BuildAsciiReadMcCoreCommand(addressData, false);

        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(MelsecMcAsciiNet.PackMcCommand(coreResult, NetworkNumber, NetworkStationNumber));
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        // 错误代码验证
        OperateResult check = MelsecMcAsciiNet.CheckResponseContent(read.Content);
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

        // 数据解析，需要传入是否使用位的参数
        return MelsecMcAsciiNet.ExtractActualData(read.Content, false);
    }

    public OperateResult Write(String address, byte[] value) {
        // 分析地址
        OperateResultExOne<McAddressData> addressResult = McAnalysisAddress(address, (short) 0);
        if (!addressResult.IsSuccess) return OperateResultExOne.CreateFailedResult(addressResult);

        // 地址分析
        byte[] coreResult = MelsecHelper.BuildAsciiWriteWordCoreCommand(addressResult.Content, value);

        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(MelsecMcAsciiNet.PackMcCommand(coreResult, NetworkNumber, NetworkStationNumber));
        if (!read.IsSuccess) return read;

        // 错误码验证
        OperateResult check = MelsecMcAsciiNet.CheckResponseContent(read.Content);
        if (!check.IsSuccess) return check;

        // 写入成功
        return OperateResult.CreateSuccessResult();
    }

    //endregion

    //region Read Random

    public OperateResultExOne<byte[]> ReadRandom(String[] address) {
        McAddressData[] mcAddressDatas = new McAddressData[address.length];
        for (int i = 0; i < address.length; i++) {
            OperateResultExOne<McAddressData> addressResult = McAddressData.ParseMelsecFrom(address[i], 1);
            if (!addressResult.IsSuccess) return OperateResultExOne.CreateFailedResult(addressResult);

            mcAddressDatas[i] = addressResult.Content;
        }

        // 地址分析
        byte[] coreResult = MelsecHelper.BuildAsciiReadRandomWordCommand(mcAddressDatas);

        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(MelsecMcAsciiNet.PackMcCommand(coreResult, this.NetworkNumber, this.NetworkStationNumber));
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        // 错误代码验证
        OperateResult check = MelsecMcAsciiNet.CheckResponseContent(read.Content);
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

        // 数据解析，需要传入是否使用位的参数
        return MelsecMcAsciiNet.ExtractActualData(read.Content, false);
    }

    public OperateResultExOne<byte[]> ReadRandom(String[] address, short[] length) {
        if (length.length != address.length)
            return new OperateResultExOne<byte[]>(StringResources.Language.TwoParametersLengthIsNotSame());

        McAddressData[] mcAddressDatas = new McAddressData[address.length];
        for (int i = 0; i < address.length; i++) {
            OperateResultExOne<McAddressData> addressResult = McAddressData.ParseMelsecFrom(address[i], length[i]);
            if (!addressResult.IsSuccess) return OperateResultExOne.CreateFailedResult(addressResult);

            mcAddressDatas[i] = addressResult.Content;
        }

        // 地址分析
        byte[] coreResult = MelsecHelper.BuildAsciiReadRandomCommand(mcAddressDatas);

        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(MelsecMcAsciiNet.PackMcCommand(coreResult, this.NetworkNumber, this.NetworkStationNumber));
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        // 错误代码验证
        OperateResult check = MelsecMcAsciiNet.CheckResponseContent(read.Content);
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

        // 数据解析，需要传入是否使用位的参数
        return MelsecMcAsciiNet.ExtractActualData(read.Content, false);
    }

    /// <inheritdoc cref="ReadRandomInt16(string[])"/>
    public OperateResultExOne<short[]> ReadRandomInt16(String[] address) {
        OperateResultExOne<byte[]> read = ReadRandom(address);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(getByteTransform().TransInt16(read.Content, 0, address.length));
    }

    //endregion

    //region Bool Operate Support

    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        // 分析地址
        OperateResultExOne<McAddressData> addressResult = McAnalysisAddress(address, length);
        if (!addressResult.IsSuccess) return OperateResultExOne.CreateFailedResult(addressResult);

        // 地址分析
        byte[] coreResult = MelsecHelper.BuildAsciiReadMcCoreCommand(addressResult.Content, true);

        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(MelsecMcAsciiNet.PackMcCommand(coreResult, NetworkNumber, NetworkStationNumber));
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        // 错误代码验证
        OperateResult check = MelsecMcAsciiNet.CheckResponseContent(read.Content);
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

        // 数据解析，需要传入是否使用位的参数
        OperateResultExOne<byte[]> extract = MelsecMcAsciiNet.ExtractActualData(read.Content, true);
        if (!extract.IsSuccess) return OperateResultExOne.CreateFailedResult(extract);

        // 转化bool数组
        boolean[] result = new boolean[length];
        for (int i = 0; i < length; i++) {
            if (extract.Content[i] == 0x01) {
                result[i] = true;
            }
        }
        return OperateResultExOne.CreateSuccessResult(result);
    }

    public OperateResult Write(String address, boolean[] values) {
        // 分析地址
        OperateResultExOne<McAddressData> addressResult = McAnalysisAddress(address, (short) 0);
        if (!addressResult.IsSuccess) return addressResult;

        // 解析指令
        byte[] coreResult = MelsecHelper.BuildAsciiWriteBitCoreCommand(addressResult.Content, values);

        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(MelsecMcAsciiNet.PackMcCommand(coreResult, NetworkNumber, NetworkStationNumber));
        if (!read.IsSuccess) return read;

        // 错误码验证
        OperateResult check = MelsecMcAsciiNet.CheckResponseContent(read.Content);
        if (!check.IsSuccess) return check;

        // 写入成功
        return OperateResult.CreateSuccessResult();
    }

    //endregion

    //region Remote Operate

    public OperateResult RemoteRun() {
        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(MelsecMcAsciiNet.PackMcCommand("1001000000010000".getBytes(StandardCharsets.US_ASCII), NetworkNumber, NetworkStationNumber));
        if (!read.IsSuccess) return read;

        // 错误码校验
        OperateResult check = MelsecMcAsciiNet.CheckResponseContent(read.Content);
        if (!check.IsSuccess) return check;

        // 成功
        return OperateResult.CreateSuccessResult();
    }

    public OperateResult RemoteStop() {
        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(MelsecMcAsciiNet.PackMcCommand("100200000001".getBytes(StandardCharsets.US_ASCII), NetworkNumber, NetworkStationNumber));
        if (!read.IsSuccess) return read;

        // 错误码校验
        OperateResult check = MelsecMcAsciiNet.CheckResponseContent(read.Content);
        if (!check.IsSuccess) return check;

        // 成功
        return OperateResult.CreateSuccessResult();
    }

    public OperateResult RemoteReset() {
        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(MelsecMcAsciiNet.PackMcCommand("100600000001".getBytes(StandardCharsets.US_ASCII), NetworkNumber, NetworkStationNumber));
        if (!read.IsSuccess) return read;

        // 错误码校验
        OperateResult check = MelsecMcAsciiNet.CheckResponseContent(read.Content);
        if (!check.IsSuccess) return check;

        // 成功
        return OperateResult.CreateSuccessResult();
    }

    public OperateResultExOne<String> ReadPlcType() {
        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(MelsecMcAsciiNet.PackMcCommand("01010000".getBytes(StandardCharsets.US_ASCII), NetworkNumber, NetworkStationNumber));
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        // 错误码校验
        OperateResult check = MelsecMcAsciiNet.CheckResponseContent(read.Content);
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

        // 成功
        return OperateResultExOne.CreateSuccessResult(new String(read.Content, 22, 16, StandardCharsets.US_ASCII).trim());
    }

    public OperateResult ErrorStateReset() {
        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(MelsecMcAsciiNet.PackMcCommand("01010000".getBytes(StandardCharsets.US_ASCII), NetworkNumber, NetworkStationNumber));
        if (!read.IsSuccess) return read;

        // 错误码校验
        OperateResult check = MelsecMcAsciiNet.CheckResponseContent(read.Content);
        if (!check.IsSuccess) return check;

        // 成功
        return OperateResult.CreateSuccessResult();
    }

    //endregion

    //region Object Override


    public String toString() {
        return "MelsecMcAsciiUdp[" + getIpAddress() + ":" + getPort() + "]";
    }

    //endregion
}
