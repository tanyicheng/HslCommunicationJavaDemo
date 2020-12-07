package HslCommunication.LogNet.Core;

/**
 * 日志的存储对象
 */
public interface ILogNet {

    /**
     * 文件存储模式，1:单文件，2:根据大小，3:根据时间
     * @return 存储模式的值
     */
    int LogSaveMode();


    /**
     * 自定义的消息存储方法
     * @param degree 消息等级
     * @param keyWord 关键字
     * @param text 文本消息
     */
    void RecordMessage(HslMessageDegree degree, String keyWord, String text);


    /**
     * 写入一条调试的日志
     * @param text 文本信息
     */
    void WriteDebug(String text);


    /**
     * 写入一条调试的日志
     * @param keyWord 关键字
     * @param text 文本消息
     */
    void WriteDebug(String keyWord, String text);


    /**
     * 写入一条解释性的信息
     * @param description 描述信息
     */
    void WriteDescription(String description);


    /**
     * 写入一条错误信息
     * @param text 文本信息
     */
    void WriteError(String text);


    /**
     * 写入一条错误信息
     * @param keyWord 关键字
     * @param text 文本信息
     */
    void WriteError(String keyWord, String text);


    /**
     * 写入一条异常的信息
     * @param keyWord 关键字
     * @param ex 异常信息
     */
    void WriteException(String keyWord, Exception ex);

    /**
     * 写入一条异常的信息
     * @param keyWord 关键字
     * @param text 文本信息
     * @param ex 异常信息
     */
    void WriteException(String keyWord, String text, Exception ex);


    /**
     * 写入一条致命的信息
     * @param text 文本信息
     */
    void WriteFatal(String text);


    /**
     * 写入一条致命的信息
     * @param keyWord 关键字
     * @param text 文本信息
     */
    void WriteFatal(String keyWord, String text);


    /**
     * 写入一条普通的信息
     * @param text 文本信息
     */
    void WriteInfo(String text);

    /**
     * 写入一条普通的信息
     * @param keyWord 关键字
     * @param text 文本信息
     */
    void WriteInfo(String keyWord, String text);


    /**
     * 写入一个空行
     */
    void WriteNewLine();


    /**
     * 写入一条警告信息
     * @param text 文本信息
     */
    void WriteWarn(String text);


    /**
     * 写入一条警告信息
     * @param keyWord 关键字
     * @param text 文本信息
     */
    void WriteWarn(String keyWord, String text);


    /**
     * 设置日志的存储等级，高于该等级的才会被存储
     * set the storage level of the logs. Only the logs above this level will be stored.
     * @param degree 消息的及
     */
    void SetMessageDegree(HslMessageDegree degree);


    /**
     * 过滤掉指定的关键字的日志，该信息不存储，但仍然触发BeforeSaveToFile事件
     * @param keyword
     */
    void FiltrateKeyword( String keyword );


    /**
     * 获取已存在的日志文件名称
     * @return
     */
    String[] GetExistLogFileNames();
}
