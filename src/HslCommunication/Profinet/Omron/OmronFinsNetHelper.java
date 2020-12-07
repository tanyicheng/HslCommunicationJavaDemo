package HslCommunication.Profinet.Omron;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.StringResources;
import HslCommunication.Utilities;

/**
 * Omron PLC的FINS协议相关的辅助类，主要是一些地址解析，读写的指令生成。<br />
 * The auxiliary classes related to the FINS protocol of Omron PLC are mainly some address resolution and the generation of read and write instructions.
 */
public class OmronFinsNetHelper {

    // region Static Method Helper

    /**
     * 解析欧姆龙的数据地址，参考来源是Omron手册第188页，比如D100， E1.100<br />
     * Analyze Omron's data address, the reference source is page 188 of the Omron manual, such as D100, E1.100
     * @param address 数据地址
     * @param isBit 是否是位地址
     * @return 解析后的结果地址对象
     */
    public static OperateResultExTwo<OmronFinsDataType, byte[]> AnalysisAddress(String address, boolean isBit ) {
        OperateResultExTwo<OmronFinsDataType, byte[]> result = new OperateResultExTwo<OmronFinsDataType, byte[]>();
        try {
            switch (address.charAt(0)) {
                case 'D':
                case 'd': {
                    // DM区数据
                    result.Content1 = OmronFinsDataType.DM;
                    break;
                }
                case 'C':
                case 'c': {
                    // CIO区数据
                    result.Content1 = OmronFinsDataType.CIO;
                    break;
                }
                case 'W':
                case 'w': {
                    // WR区
                    result.Content1 = OmronFinsDataType.WR;
                    break;
                }
                case 'H':
                case 'h': {
                    // HR区
                    result.Content1 = OmronFinsDataType.HR;
                    break;
                }
                case 'A':
                case 'a': {
                    // AR区
                    result.Content1 = OmronFinsDataType.AR;
                    break;
                }
                case 'E':
                case 'e': {
                    // E区，比较复杂，需要专门的计算
                    String[] splits = address.split("\\.");
                    int block = Integer.parseInt(splits[0].substring(1), 16);
                    if (block < 16) {
                        result.Content1 = new OmronFinsDataType((byte) (0x20 + block), (byte) (0xA0 + block));
                    } else {
                        result.Content1 = new OmronFinsDataType((byte) (0xE0 + block - 16), (byte) (0x60 + block - 16));
                    }
                    break;
                }
                default:
                    throw new Exception(StringResources.Language.NotSupportedDataType());
            }

            if (address.charAt(0) == 'E' || address.charAt(0) == 'e') {
                String[] splits = address.split("\\.");
                if (isBit) {
                    // 位操作
                    int addr = Integer.parseInt(splits[1]);
                    result.Content2 = new byte[3];
                    result.Content2[0] = Utilities.getBytes(addr)[1];
                    result.Content2[1] = Utilities.getBytes(addr)[0];

                    if (splits.length > 2) {
                        result.Content2[2] = Byte.parseByte(splits[2]);
                        if (result.Content2[2] > 15) {
                            throw new Exception(StringResources.Language.OmronAddressMustBeZeroToFifteen());
                        }
                    }
                } else {
                    // 字操作
                    int addr = Integer.parseInt(splits[1]);
                    result.Content2 = new byte[3];
                    result.Content2[0] = Utilities.getBytes(addr)[1];
                    result.Content2[1] = Utilities.getBytes(addr)[0];
                }
            } else {
                if (isBit) {
                    // 位操作
                    String[] splits = address.substring(1).split("\\.");
                    int addr = Integer.parseInt(splits[0]);
                    result.Content2 = new byte[3];
                    result.Content2[0] = Utilities.getBytes(addr)[1];
                    result.Content2[1] = Utilities.getBytes(addr)[0];

                    if (splits.length > 1) {
                        result.Content2[2] = Byte.parseByte(splits[1]);
                        if (result.Content2[2] > 15) {
                            throw new Exception(StringResources.Language.OmronAddressMustBeZeroToFifteen());
                        }
                    }
                } else {
                    // 字操作
                    int addr = Integer.parseInt(address.substring(1));
                    result.Content2 = new byte[3];
                    result.Content2[0] = Utilities.getBytes(addr)[1];
                    result.Content2[1] = Utilities.getBytes(addr)[0];
                }
            }
        } catch (Exception ex) {
            result.Message = ex.getMessage();
            return result;
        }

        result.IsSuccess = true;
        return result;
    }

    /**
     * 根据读取的地址，长度，是否位读取创建Fins协议的核心报文<br />
     * According to the read address, length, whether to read the core message that creates the Fins protocol
     * @param address 地址，具体格式请参照示例说明
     * @param length 读取的数据长度
     * @param isBit 是否使用位读取
     * @return 带有成功标识的Fins核心报文
     */
    public static OperateResultExOne<byte[]> BuildReadCommand(String address, short length, boolean isBit ) {
        OperateResultExTwo<OmronFinsDataType, byte[]> analysis = OmronFinsNetHelper.AnalysisAddress(address, isBit);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        byte[] _PLCCommand = new byte[8];
        _PLCCommand[0] = 0x01;    // 读取存储区数据
        _PLCCommand[1] = 0x01;
        if (isBit)
            _PLCCommand[2] = analysis.Content1.getBitCode();
        else
            _PLCCommand[2] = analysis.Content1.getWordCode();
        System.arraycopy(analysis.Content2, 0, _PLCCommand, 3, analysis.Content2.length);
        _PLCCommand[6] = Utilities.getBytes(length)[1];                       // 长度
        _PLCCommand[7] = Utilities.getBytes(length)[0];

        return OperateResultExOne.CreateSuccessResult(_PLCCommand);
    }

    /**
     * 根据写入的地址，数据，是否位写入生成Fins协议的核心报文<br />
     * According to the written address, data, whether the bit is written to generate the core message of the Fins protocol
     * @param address 地址内容，具体格式请参照示例说明
     * @param value 实际的数据
     * @param isBit 是否位数据
     * @return 带有成功标识的Fins核心报文
     */
    public static OperateResultExOne<byte[]> BuildWriteWordCommand( String address, byte[] value, boolean isBit ) {
        OperateResultExTwo<OmronFinsDataType, byte[]> analysis = OmronFinsNetHelper.AnalysisAddress(address, isBit);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        byte[] _PLCCommand = new byte[8 + value.length];
        _PLCCommand[0] = 0x01;
        _PLCCommand[1] = 0x02;

        if (isBit)
            _PLCCommand[2] = analysis.Content1.getBitCode();
        else
            _PLCCommand[2] = analysis.Content1.getWordCode();

        System.arraycopy(analysis.Content2, 0, _PLCCommand, 3, analysis.Content2.length);
        if (isBit) {
            _PLCCommand[6] = (byte) (value.length / 256);
            _PLCCommand[7] = (byte) (value.length % 256);
        } else {
            _PLCCommand[6] = (byte) (value.length / 2 / 256);
            _PLCCommand[7] = (byte) (value.length / 2 % 256);
        }

        System.arraycopy(value, 0, _PLCCommand, 8, value.length);

        return OperateResultExOne.CreateSuccessResult(_PLCCommand);
    }

    /**
     * 验证欧姆龙的Fins-TCP返回的数据是否正确的数据，如果正确的话，并返回所有的数据内容<br />
     * Verify that the data returned by Omron's Fins-TCP is correct data, if correct, and return all data content
     * @param response 来自欧姆龙返回的数据内容
     * @param isRead 是否读取
     * @return 带有是否成功的结果对象
     */
    public static OperateResultExOne<byte[]> ResponseValidAnalysis( byte[] response, boolean isRead ) {
        if (response.length >= 16) {
            // 提取错误码 -> Extracting error Codes
            byte[] buffer = new byte[4];
            buffer[0] = response[15];
            buffer[1] = response[14];
            buffer[2] = response[13];
            buffer[3] = response[12];

            int err = Utilities.getInt(buffer, 0);
            if (err > 0) return new OperateResultExOne<byte[]>(err, GetStatusDescription(err));

            byte[] result = new byte[response.length - 16];
            System.arraycopy(response, 16, result, 0, result.length);
            return UdpResponseValidAnalysis(result, isRead);
            //if (response.Length >= 30)
            //{
            //    err = response[28] * 256 + response[29];
            //    // if (err > 0) return new OperateResult<byte[]>( err, StringResources.Language.OmronReceiveDataError );

            //    if (!isRead)
            //    {
            //        OperateResult<byte[]> success = OperateResult.CreateSuccessResult( new byte[0] );
            //        success.ErrorCode = err;
            //        success.Message = GetStatusDescription( err );
            //        return success;
            //    }
            //    else
            //    {
            //        // 读取操作 -> read operate
            //        byte[] content = new byte[response.Length - 30];
            //        if (content.Length > 0) Array.Copy( response, 30, content, 0, content.Length );

            //        OperateResult<byte[]> success = OperateResult.CreateSuccessResult( content );
            //        if (content.Length == 0) success.IsSuccess = false;
            //        success.ErrorCode = err;
            //        success.Message = GetStatusDescription( err );
            //        return success;
            //    }
            //}
        }

        return new OperateResultExOne<byte[]>(StringResources.Language.OmronReceiveDataError());
    }

    /**
     * 验证欧姆龙的Fins-Udp返回的数据是否正确的数据，如果正确的话，并返回所有的数据内容<br />
     * Verify that the data returned by Omron's Fins-Udp is correct data, if correct, and return all data content
     * @param response 来自欧姆龙返回的数据内容
     * @param isRead 是否读取
     * @return 带有是否成功的结果对象
     */
    public static OperateResultExOne<byte[]> UdpResponseValidAnalysis( byte[] response, boolean isRead ) {
        if (response.length >= 14) {
            int err = response[12] * 256 + response[13];
            // if (err > 0) return new OperateResult<byte[]>( err, StringResources.Language.OmronReceiveDataError );

            if (!isRead) {
                OperateResultExOne<byte[]> success = OperateResultExOne.CreateSuccessResult(new byte[0]);
                success.ErrorCode = err;
                success.Message = GetStatusDescription(err) + " Received:" + SoftBasic.ByteToHexString(response, ' ');
                return success;
            } else {
                // 读取操作 -> read operate
                byte[] content = new byte[response.length - 14];
                if (content.length > 0) System.arraycopy(response, 14, content, 0, content.length);

                OperateResultExOne<byte[]> success = OperateResultExOne.CreateSuccessResult(content);
                if (content.length == 0) success.IsSuccess = false;
                success.ErrorCode = err;
                success.Message = GetStatusDescription(err) + " Received:" + SoftBasic.ByteToHexString(response, ' ');
                return success;
            }
        }

        return new OperateResultExOne<byte[]>(StringResources.Language.OmronReceiveDataError());
    }

    /**
     * 根据欧姆龙返回的错误码，获取错误信息的字符串描述文本<br />
     * According to the error code returned by Omron, get the string description text of the error message
     * @param err 错误码
     * @return 文本描述
     */
    public static String GetStatusDescription( int err ) {
        switch (err) {
            case 0:
                return StringResources.Language.OmronStatus0();
            case 1:
                return StringResources.Language.OmronStatus1();
            case 2:
                return StringResources.Language.OmronStatus2();
            case 3:
                return StringResources.Language.OmronStatus3();
            case 20:
                return StringResources.Language.OmronStatus20();
            case 21:
                return StringResources.Language.OmronStatus21();
            case 22:
                return StringResources.Language.OmronStatus22();
            case 23:
                return StringResources.Language.OmronStatus23();
            case 24:
                return StringResources.Language.OmronStatus24();
            case 25:
                return StringResources.Language.OmronStatus25();
            default:
                return StringResources.Language.UnknownError();
        }
    }

    // endregion

}
