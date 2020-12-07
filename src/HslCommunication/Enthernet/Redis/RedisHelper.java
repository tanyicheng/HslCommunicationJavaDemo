package HslCommunication.Enthernet.Redis;

import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Utilities;

import java.util.ArrayList;

public class RedisHelper {

    // region Parse Helper

    /**
     * 将字符串数组打包成一个redis的报文信息
     * @param commands 字节数据信息
     * @return 结果报文信息
     */
    public static byte[] PackStringCommand( String[] commands ) {
        StringBuilder sb = new StringBuilder();
        sb.append('*');
        sb.append(commands.length);
        sb.append("\r\n");
        for (int i = 0; i < commands.length; i++) {
            sb.append('$');
            sb.append(Utilities.getBytes(commands[i], "utf-8").length);
            sb.append("\r\n");
            sb.append(commands[i]);
            sb.append("\r\n");
        }
        return Utilities.getBytes(sb.toString(), "utf-8");
    }

    /**
     * 从原始的结果数据对象中提取出数字数据
     * @param commandLine 原始的字节数据
     * @return 带有结果对象的数据信息
     */
    public static OperateResultExOne<Integer> GetNumberFromCommandLine(byte[] commandLine ) {
        try {
            String command = Utilities.getString(commandLine, "utf-8");
            if (command.endsWith("\r\n")) {
                command = command.substring(0, command.length() - 2);
            }
            return OperateResultExOne.CreateSuccessResult(Integer.parseInt(command.substring(1)));
        } catch (Exception ex) {
            return new OperateResultExOne<Integer>(ex.getMessage());
        }
    }

    /**
     * 从原始的结果数据对象中提取出数字数据
     * @param commandLine 原始的字节数据
     * @return 带有结果对象的数据信息
     */
    public static OperateResultExOne<Long> GetLongNumberFromCommandLine( byte[] commandLine ) {
        try {
            String command = Utilities.getString(commandLine, "utf-8");
            if (command.endsWith("\r\n")) {
                command = command.substring(0, command.length() - 2);
            }
            return OperateResultExOne.CreateSuccessResult(Long.parseLong(command.substring(1)));
        } catch (Exception ex) {
            return new OperateResultExOne<Long>(ex.getMessage());
        }
    }

    /**
     * 从结果的数据对象里提取字符串的信息
     * @param commandLine 原始的字节数据
     * @return 带有结果对象的数据信息
     */
    public static OperateResultExOne<String> GetStringFromCommandLine( byte[] commandLine ) {
        try {
            if (commandLine[0] != '$')
                return new OperateResultExOne<String>(Utilities.getString(commandLine, "UTF-8"));

            // 先找到换行符
            int index_start = -1;
            int index_end = -1;
            // 下面的判断兼容windows系统及linux系统
            for (int i = 0; i < commandLine.length; i++) {
                if (commandLine[i] == '\r' || commandLine[i] == '\n') {
                    index_start = i;
                }

                if (commandLine[i] == '\n') {
                    index_end = i;
                    break;
                }
            }

            int length = Integer.parseInt(Utilities.getString(commandLine, 1, index_start - 1, "UTF-8"));
            if (length < 0) return new OperateResultExOne<String>("(nil) None Value");

            return OperateResultExOne.CreateSuccessResult(Utilities.getString(commandLine, index_end + 1, length, "UTF-8"));
        } catch (Exception ex) {
            return new OperateResultExOne<String>(ex.getMessage());
        }
    }

    /**
     * 从redis的结果数据中分析出所有的字符串信息
     * @param commandLine 结果数据
     * @return 带有结果对象的数据信息
     */
    public static OperateResultExOne<String[]> GetStringsFromCommandLine( byte[] commandLine ) {
        try {
            ArrayList<String> lists = new ArrayList<String>();
            if (commandLine[0] != '*')
                return new OperateResultExOne<String[]>(Utilities.getString(commandLine, "UTF-8"));

            int index = 0;
            for (int i = 0; i < commandLine.length; i++) {
                if (commandLine[i] == '\r' || commandLine[i] == '\n') {
                    index = i;
                    break;
                }
            }

            int length = Integer.parseInt(Utilities.getString(commandLine, 1, index - 1, "UTF-8"));
            for (int i = 0; i < length; i++) {
                // 提取所有的字符串内容
                int index_end = -1;
                for (int j = index; j < commandLine.length; j++) {
                    if (commandLine[j] == '\n') {
                        index_end = j;
                        break;
                    }
                }
                index = index_end + 1;
                if (commandLine[index] == '$') {
                    // 寻找子字符串
                    int index_start = -1;
                    for (int j = index; j < commandLine.length; j++) {
                        if (commandLine[j] == '\r' || commandLine[j] == '\n') {
                            index_start = j;
                            break;
                        }
                    }
                    int stringLength = Integer.parseInt(Utilities.getString(commandLine, index + 1, index_start - index - 1, "UTF-8"));
                    if (stringLength >= 0) {
                        for (int j = index; j < commandLine.length; j++) {
                            if (commandLine[j] == '\n') {
                                index_end = j;
                                break;
                            }
                        }
                        index = index_end + 1;

                        lists.add(Utilities.getString(commandLine, index, stringLength, "UTF-8"));
                        index = index + stringLength;
                    } else {
                        lists.add(null);
                    }
                } else {
                    int index_start = -1;
                    for (int j = index; j < commandLine.length; j++) {
                        if (commandLine[j] == '\r' || commandLine[j] == '\n') {
                            index_start = j;
                            break;
                        }
                    }
                    lists.add(Utilities.getString(commandLine, index, index_start - index - 1, "UTF-8"));
                }
            }

            return OperateResultExOne.CreateSuccessResult(Utilities.getStrings(lists));
        } catch (Exception ex) {
            return new OperateResultExOne<String[]>(ex.getMessage());
        }
    }


    // endregion
}
