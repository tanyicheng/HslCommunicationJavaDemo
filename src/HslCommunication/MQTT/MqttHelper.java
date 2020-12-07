package HslCommunication.MQTT;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.StringResources;
import HslCommunication.Utilities;

import java.util.ArrayList;

/**
 * Mqtt协议的辅助类，提供了一些协议相关的基础方法，方便客户端和服务器端一起调用。<br />
 * The auxiliary class of the Mqtt protocol provides some protocol-related basic methods for the client and server to call together.
 */
public class MqttHelper {
    // region Static Helper Method

    /**
     * 根据数据的总长度，计算出剩余的数据长度信息<br />
     * According to the total length of the data, calculate the remaining data length information
     * @param length 数据的总长度
     * @return 计算结果
     */
    public static OperateResultExOne<byte[]> CalculateLengthToMqttLength(int length ) {
        if (length > 268_435_455) return new OperateResultExOne<byte[]>(StringResources.Language.MQTTDataTooLong());
        if (length < 128) return OperateResultExOne.CreateSuccessResult(new byte[]{(byte) length});

        if (length < 128 * 128) {
            byte[] buffer = new byte[2];
            buffer[0] = (byte) (length % 128 + 0x80);
            buffer[1] = (byte) (length / 128);
            return OperateResultExOne.CreateSuccessResult(buffer);
        }

        if (length < 128 * 128 * 128) {
            byte[] buffer = new byte[3];
            buffer[0] = (byte) (length % 128 + 0x80);
            buffer[1] = (byte) (length / 128 % 128 + 0x80);
            buffer[2] = (byte) (length / 128 / 128);
            return OperateResultExOne.CreateSuccessResult(buffer);
        } else {
            byte[] buffer = new byte[4];
            buffer[0] = (byte) (length % 128 + 0x80);
            buffer[1] = (byte) (length / 128 % 128 + 0x80);
            buffer[2] = (byte) (length / 128 / 128 % 128 + 0x80);
            buffer[3] = (byte) (length / 128 / 128 / 128);
            return OperateResultExOne.CreateSuccessResult(buffer);
        }
    }

    /**
     * 将一个数据打包成一个mqtt协议的内容<br />
     * Pack a piece of data into a mqtt protocol
     * @param control 控制码
     * @param flags 标记
     * @param variableHeader 可变头的字节内容
     * @param payLoad 负载数据
     * @return 带有是否成功的结果对象
     */
    public static OperateResultExOne<byte[]> BuildMqttCommand( byte control, byte flags, byte[] variableHeader, byte[] payLoad ) {
        if (variableHeader == null) variableHeader = new byte[0];
        if (payLoad == null) payLoad = new byte[0];

        control = (byte) (control << 4);
        byte head = (byte) (control | flags);

        // 先计算长度
        OperateResultExOne<byte[]> bufferLength = CalculateLengthToMqttLength(variableHeader.length + payLoad.length);
        if (!bufferLength.IsSuccess) return bufferLength;

        ArrayList<Byte> ms = new ArrayList<Byte>();
        ms.add(head);
        Utilities.ArrayListAddArray(ms, bufferLength.Content);
        if (variableHeader.length > 0) Utilities.ArrayListAddArray(ms, variableHeader);
        if (payLoad.length > 0) Utilities.ArrayListAddArray(ms, payLoad);
        byte[] result = Utilities.getBytes(ms);
        return OperateResultExOne.CreateSuccessResult(result);
    }

    /**
     * 将字符串打包成utf8编码，并且带有2个字节的表示长度的信息<br />
     * Pack the string into utf8 encoding, and with 2 bytes of length information
     * @param message 文本消息
     * @return 打包之后的信息
     */
    public static byte[] BuildSegCommandByString( String message ) {
        byte[] buffer = Utilities.IsStringNullOrEmpty(message) ? new byte[0] : Utilities.getBytes(message, "UTF-8");
        byte[] result = new byte[buffer.length + 2];
        System.arraycopy(buffer, 0, result, 2, buffer.length);
        result[0] = (byte) (buffer.length / 256);
        result[1] = (byte) (buffer.length % 256);
        return result;
    }

    /**
     * 从MQTT的缓存信息里，提取文本信息<br />
     * Extract text information from MQTT cache information
     * @param buffer Mqtt的报文
     * @param index 索引
     * @return 值
     */
    public static OperateResultExTwo<String, Integer> ExtraMsgFromBytes( byte[] buffer, int index ) {
        String tmp = Utilities.getString(buffer, "UTF-8");
        int indexTmp = index;
        int length = buffer[index] * 256 + buffer[index + 1];
        index = index + 2 + length;
        return OperateResultExTwo.CreateSuccessResult(Utilities.getString(buffer, indexTmp + 2, length, "UTF-8"), index);
    }

    /**
     * 从MQTT的缓存信息里，提取长度信息<br />
     * Extract length information from MQTT cache information
     * @param buffer Mqtt的报文
     * @param index 索引
     * @return 值
     */
    public static OperateResultExTwo<Integer, Integer> ExtraIntFromBytes( byte[] buffer, int index ) {
        int length = buffer[index] * 256 + buffer[index + 1];
        index += 2;
        return OperateResultExTwo.CreateSuccessResult(length, index);
    }

    /**
     * 从MQTT的缓存信息里，提取长度信息<br />
     * Extract length information from MQTT cache information
     * @param data 数据信息
     * @return 值
     */
    public static byte[] BuildIntBytes( int data ) {
        return new byte[]{Utilities.getBytes(data)[1], Utilities.getBytes(data)[0]};
    }

    /**
     * 创建MQTT连接服务器的报文信息<br />
     * Create MQTT connection server message information
     * @param connectionOptions 连接配置
     * @param protocol 协议的内容
     * @return 返回是否成功的信息
     */
    public static OperateResultExOne<byte[]> BuildConnectMqttCommand( MqttConnectionOptions connectionOptions, String protocol ) {
        ArrayList<Byte> variableHeader = new ArrayList<Byte>();
        Utilities.ArrayListAddArray(variableHeader, new byte[]{0x00, 0x04});
        Utilities.ArrayListAddArray(variableHeader, Utilities.getBytes(protocol, "US-ASCII"));    // 协议版本，3.1.1
        variableHeader.add((byte) 0x04);
        byte connectFlags = 0x00;
        if (connectionOptions.Credentials != null)                                                            // 是否需要验证用户名和密码
        {
            connectFlags = (byte) (connectFlags | 0x80);
            connectFlags = (byte) (connectFlags | 0x40);
        }
        if (connectionOptions.CleanSession) {
            connectFlags = (byte) (connectFlags | 0x02);
        }
        variableHeader.add(connectFlags);
        if (connectionOptions.KeepAlivePeriod < 1) connectionOptions.KeepAlivePeriod = 1;
        byte[] keepAlivePeriod = Utilities.getBytes(connectionOptions.KeepAlivePeriod);
        variableHeader.add(keepAlivePeriod[1]);
        variableHeader.add(keepAlivePeriod[0]);

        ArrayList<Byte> payLoad = new ArrayList<Byte>();
        Utilities.ArrayListAddArray(payLoad, BuildSegCommandByString(connectionOptions.ClientId));       // 添加客户端的id信息

        if (connectionOptions.Credentials != null)                                                         // 根据需要选择是否添加用户名和密码
        {
            Utilities.ArrayListAddArray(payLoad, BuildSegCommandByString(connectionOptions.Credentials.getUserName()));
            Utilities.ArrayListAddArray(payLoad, BuildSegCommandByString(connectionOptions.Credentials.getPassword()));
        }

        return BuildMqttCommand(MqttControlMessage.CONNECT, (byte) 0x00, Utilities.getBytes(variableHeader), Utilities.getBytes(payLoad));
    }

    /**
     * 根据服务器返回的信息判断当前的连接是否是可用的<br />
     * According to the information returned by the server to determine whether the current connection is available
     * @param code 功能码
     * @param data 数据内容
     * @return 是否可用的连接
     */
    public static OperateResult CheckConnectBack(byte code, byte[] data ) {
        if (code >> 4 != MqttControlMessage.CONNACK) return new OperateResult("MQTT Connection Back Is Wrong: " + code);
        if (data.length < 2)
            return new OperateResult("MQTT Connection Data Is Short: " + SoftBasic.ByteToHexString(data, ' '));
        int status = data[0] * 256 + data[1];

        if (status > 0) return new OperateResult(status, GetMqttCodeText(status));
        return OperateResult.CreateSuccessResult();
    }

    /**
     * 获取当前的错误的描述信息<br />
     * Get a description of the current error
     * @param status 状态信息
     * @return 描述信息
     */
    public static String GetMqttCodeText( int status ) {
        switch (status) {
            case 1:
                return StringResources.Language.MQTTStatus01();
            case 2:
                return StringResources.Language.MQTTStatus02();
            case 3:
                return StringResources.Language.MQTTStatus03();
            case 4:
                return StringResources.Language.MQTTStatus04();
            case 5:
                return StringResources.Language.MQTTStatus05();
            default:
                return StringResources.Language.UnknownError();
        }
    }

    /**
     * 创建Mqtt发送消息的命令<br />
     * Create Mqtt command to send messages
     * @param message 封装后的消息内容
     * @return 结果内容
     */
    public static OperateResultExOne<byte[]> BuildPublishMqttCommand( MqttPublishMessage message ) {
        byte flag = 0x00;
        if (!message.IsSendFirstTime) flag = (byte) (flag | 0x08);
        if (message.Message.Retain) flag = (byte) (flag | 0x01);
        if (message.Message.QualityOfServiceLevel == MqttQualityOfServiceLevel.AtLeastOnce) flag = (byte) (flag | 0x02);
        else if (message.Message.QualityOfServiceLevel == MqttQualityOfServiceLevel.ExactlyOnce)
            flag = (byte) (flag | 0x04);
        else if (message.Message.QualityOfServiceLevel == MqttQualityOfServiceLevel.OnlyTransfer)
            flag = (byte) (flag | 0x06);

        ArrayList<Byte> variableHeader = new ArrayList<Byte>();
        Utilities.ArrayListAddArray(variableHeader, BuildSegCommandByString(message.Message.Topic));
        if (message.Message.QualityOfServiceLevel != MqttQualityOfServiceLevel.AtMostOnce) {
            variableHeader.add(Utilities.getBytes(message.Identifier)[1]);
            variableHeader.add(Utilities.getBytes(message.Identifier)[0]);
        }

        return BuildMqttCommand(MqttControlMessage.PUBLISH, flag, Utilities.getBytes(variableHeader), message.Message.Payload);
    }

    /**
     * 创建Mqtt发送消息的命令<br />
     * Create Mqtt command to send messages
     * @param topic 主题消息内容
     * @param payload 数据负载
     * @return 结果内容
     */
    public static OperateResultExOne<byte[]> BuildPublishMqttCommand( String topic, byte[] payload ) {
        return BuildMqttCommand(MqttControlMessage.PUBLISH, (byte) 0x00, BuildSegCommandByString(topic), payload);
    }

    /**
     * 创建MQTT订阅消息的命名<br />
     * Command to create Mqtt subscription message
     * @param message 订阅的主题
     * @return 结果内容
     */
    public static OperateResultExOne<byte[]> BuildSubscribeMqttCommand( MqttSubscribeMessage message ) {
        ArrayList<Byte> variableHeader = new ArrayList<Byte>();
        ArrayList<Byte> payLoad = new ArrayList<Byte>();

        variableHeader.add(Utilities.getBytes(message.Identifier)[1]);
        variableHeader.add(Utilities.getBytes(message.Identifier)[0]);

        for (int i = 0; i < message.Topics.length; i++) {
            Utilities.ArrayListAddArray(payLoad, BuildSegCommandByString(message.Topics[i]));

            if (message.QualityOfServiceLevel == MqttQualityOfServiceLevel.AtMostOnce)
                payLoad.add((byte) 0x00);
            else if (message.QualityOfServiceLevel == MqttQualityOfServiceLevel.AtLeastOnce)
                payLoad.add((byte) 0x01);
            else
                payLoad.add((byte) 0x02);
        }

        return BuildMqttCommand(MqttControlMessage.SUBSCRIBE, (byte) 0x02, Utilities.getBytes(variableHeader), Utilities.getBytes(payLoad));
    }

    /**
     * 创建Mqtt取消订阅消息的命令<br />
     * Create Mqtt unsubscribe message command
     * @param message 订阅的主题
     * @return 结果内容
     */
    public static OperateResultExOne<byte[]> BuildUnSubscribeMqttCommand( MqttSubscribeMessage message ) {
        ArrayList<Byte> variableHeader = new ArrayList<Byte>();
        ArrayList<Byte> payLoad = new ArrayList<Byte>();

        variableHeader.add(Utilities.getBytes(message.Identifier)[1]);
        variableHeader.add(Utilities.getBytes(message.Identifier)[0]);

        for (int i = 0; i < message.Topics.length; i++) {
            Utilities.ArrayListAddArray(payLoad, BuildSegCommandByString(message.Topics[i]));
        }

        return BuildMqttCommand(MqttControlMessage.UNSUBSCRIBE, (byte) 0x02, Utilities.getBytes(variableHeader), Utilities.getBytes(payLoad));
    }

    /**
     * 解析从MQTT接受的客户端信息，解析成实际的Topic数据及Payload数据<br />
     * Parse the client information received from MQTT and parse it into actual Topic data and Payload data
     * @param mqttCode MQTT的命令码
     * @param data 接收的MQTT原始的消息内容
     * @return 解析的数据结果信息
     */
    public static OperateResultExTwo<String, byte[]> ExtraMqttReceiveData(byte mqttCode, byte[] data ) {
        if (data.length < 2)
            return new OperateResultExTwo<String, byte[]>(StringResources.Language.ReceiveDataLengthTooShort() + data.length);

        int topicLength = (data[0] & 0xFF) * 256 + (data[1]& 0xFF);
        if (data.length < 2 + topicLength)
            return new OperateResultExTwo<String, byte[]>("Code[" + mqttCode + "] Subscribe Error: " + SoftBasic.ByteToHexString(data, ' '));

        String topic = topicLength > 0 ? Utilities.getString(data, 2, topicLength, "UTF-8") : "";
        byte[] payload = new byte[data.length - topicLength - 2];
        System.arraycopy(data, topicLength + 2, payload, 0, payload.length);

        return OperateResultExTwo.CreateSuccessResult(topic, payload);
    }

    // endregion
}
