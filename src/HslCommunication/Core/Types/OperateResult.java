package HslCommunication.Core.Types;

import HslCommunication.StringResources;

/**
 * 一个结果操作类的基类
 */
public class OperateResult {


    /**
     * 默认的无参构造方法
     */
    public OperateResult(){

    }

    /**
     * 使用指定的消息实例化默认的对象
     * @param msg 错误的消息
     */
    public OperateResult(String msg){
        this.Message = msg;
    }

    /**
     * 使用指定的错误号和消息实例化默认的对象
     * @param err 错误码
     * @param msg 错误消息
     */
    public OperateResult(int err,String msg){
        this.ErrorCode = err;
        this.Message = msg;
    }


    /**
     * 指示本次访问是否成功
     */
    public boolean IsSuccess = false;


    /**
     * 具体的错误描述
     */
    public String Message = StringResources.Language.UnknownError();


    /**
     * 具体的错误代码
     */
    public int ErrorCode = 10000;


    /**
     * @return 获取错误代号及文本描述
     */
    public String ToMessageShowString() {
        return StringResources.Language.ErrorCode() + ":" + ErrorCode + "\r\n" + StringResources.Language.TextDescription() + ":" + Message;
    }


    /**
     * 从另一个结果类中拷贝错误信息
     *
     * @param result 支持结果类及派生类
     */
    public void CopyErrorFromOther(OperateResult result) {
        if (result != null) {
            ErrorCode = result.ErrorCode;
            Message = result.Message;
        }
    }

    /**
     * 创建一个成功的结果类对象
     *
     * @return 结果类对象
     */
    public static OperateResult CreateSuccessResult() {
        OperateResult result = new OperateResult();
        result.IsSuccess = true;
        result.Message = StringResources.Language.SuccessText();
        return result;
    }

    /**
     * 将当前的结果对象转换到指定泛型的结果类对象，如果当前结果为失败，则返回指定泛型的失败结果类对象<br />
     * Convert the current result object to the result class object of the specified generic type,
     * if the current result is a failure, then return the result class object of the specified generic type failure
     * @param content 如果操作成功将赋予的结果内容
     * @param <T> 结果类型
     * @return 最终的结果类对象
     */
    public <T> OperateResultExOne<T> Convert( T content ) {
        return IsSuccess ? OperateResultExOne.CreateSuccessResult(content) : OperateResultExOne.CreateFailedResult(this);
    }

    /**
     * 将当前的结果对象转换到指定泛型的结果类对象，如果当前结果为失败，则返回指定泛型的失败结果类对象<br />
     * Convert the current result object to the result class object of the specified generic type,
     * if the current result is a failure, then return the result class object of the specified generic type failure
     * @param content1 如果操作成功将赋予的结果内容一
     * @param content2 如果操作成功将赋予的结果内容二
     * @param <T1> 泛型参数一
     * @param <T2> 泛型参数二
     * @return 最终的结果类对象
     */
    public <T1,T2> OperateResultExTwo<T1,T2> Convert( T1 content1, T2 content2 ){
        return IsSuccess ? OperateResultExTwo.CreateSuccessResult(content1, content2) : OperateResultExTwo.CreateFailedResult(this);
    }

    /**
     * 将当前的结果对象转换到指定泛型的结果类对象，如果当前结果为失败，则返回指定泛型的失败结果类对象<br />
     * Convert the current result object to the result class object of the specified generic type,
     * if the current result is a failure, then return the result class object of the specified generic type failure
     * @param content1 如果操作成功将赋予的结果内容一
     * @param content2 如果操作成功将赋予的结果内容二
     * @param content3 如果操作成功将赋予的结果内容三
     * @param <T1> 泛型参数一
     * @param <T2> 泛型参数二
     * @param <T3> 泛型参数三
     * @return 最终的结果类对象
     */
    public <T1,T2,T3> OperateResultExThree<T1,T2,T3> Convert( T1 content1, T2 content2, T3 content3 ){
        return IsSuccess ? OperateResultExThree.CreateSuccessResult(content1, content2, content3) : OperateResultExThree.CreateFailedResult(this);
    }

    /**
     * 将当前的结果对象转换到指定泛型的结果类对象，如果当前结果为失败，则返回指定泛型的失败结果类对象<br />
     * Convert the current result object to the result class object of the specified generic type,
     * if the current result is a failure, then return the result class object of the specified generic type failure
     * @param content1 如果操作成功将赋予的结果内容一
     * @param content2 如果操作成功将赋予的结果内容二
     * @param content3 如果操作成功将赋予的结果内容三
     * @param content4 如果操作成功将赋予的结果内容四
     * @param <T1> 泛型参数一
     * @param <T2> 泛型参数二
     * @param <T3> 泛型参数三
     * @param <T4> 泛型参数四
     * @return 最终的结果类对象
     */
    public <T1,T2,T3,T4> OperateResultExFour<T1,T2,T3,T4> Convert( T1 content1, T2 content2, T3 content3, T4 content4 ){
        return IsSuccess ? OperateResultExFour.CreateSuccessResult(content1, content2, content3, content4) : OperateResultExFour.CreateFailedResult(this);
    }

    /**
     * 指定接下来要做的是内容，当前对象如果成功，就返回接下来的执行结果，如果失败，就返回当前对象本身。<br />
     * Specify what you want to do next, return the result of the execution of the current object if it succeeds, and return the current object itself if it fails.
     * @param func 等待当前对象成功后执行的内容
     * @return 返回整个方法链最终的成功失败结果
     */
    public OperateResult Then( FunctionOperate<OperateResult> func ) {
        return IsSuccess ? func.Action() : this;
    }

    /**
     * 指定接下来要做的是内容，当前对象如果成功，就返回接下来的执行结果，如果失败，就返回当前对象本身。<br />
     * Specify what you want to do next, return the result of the execution of the current object if it succeeds, and return the current object itself if it fails.
     * @param func 等待当前对象成功后执行的内容
     * @param <T> 泛型参数
     * @return 返回整个方法链最终的成功失败结果
     */
    public <T> OperateResultExOne<T> ThenExOne( FunctionOperate<OperateResultExOne<T>> func ) {
        return IsSuccess ? func.Action() : OperateResultExOne.CreateFailedResult(this);
    }
    /**
     * 指定接下来要做的是内容，当前对象如果成功，就返回接下来的执行结果，如果失败，就返回当前对象本身。<br />
     * Specify what you want to do next, return the result of the execution of the current object if it succeeds, and return the current object itself if it fails.
     * @param func 等待当前对象成功后执行的内容
     * @param <T1> 泛型参数一
     * @param <T2> 泛型参数二
     * @return 返回整个方法链最终的成功失败结果
     */
    public <T1, T2> OperateResultExTwo<T1, T2> ThenExTwo( FunctionOperate<OperateResultExTwo<T1, T2>> func ) {
        return IsSuccess ? func.Action() : OperateResultExTwo.CreateFailedResult(this);
    }

    /**
     * 指定接下来要做的是内容，当前对象如果成功，就返回接下来的执行结果，如果失败，就返回当前对象本身。<br />
     * Specify what you want to do next, return the result of the execution of the current object if it succeeds, and return the current object itself if it fails.
     * @param func 等待当前对象成功后执行的内容
     * @param <T1> 泛型参数一
     * @param <T2> 泛型参数二
     * @param <T3> 泛型参数三
     * @return 返回整个方法链最终的成功失败结果
     */
    public <T1, T2, T3> OperateResultExThree<T1, T2, T3> ThenExThree( FunctionOperate<OperateResultExThree<T1, T2, T3>> func ) {
        return IsSuccess ? func.Action() : OperateResultExThree.CreateFailedResult(this);
    }

    /**
     * 指定接下来要做的是内容，当前对象如果成功，就返回接下来的执行结果，如果失败，就返回当前对象本身。<br />
     * Specify what you want to do next, return the result of the execution of the current object if it succeeds, and return the current object itself if it fails.
     * @param func 等待当前对象成功后执行的内容
     * @param <T1> 泛型参数一
     * @param <T2> 泛型参数二
     * @param <T3> 泛型参数三
     * @param <T4> 泛型参数四
     * @return 返回整个方法链最终的成功失败结果
     */
    public <T1, T2, T3, T4> OperateResultExFour<T1, T2, T3, T4> ThenExFour( FunctionOperate<OperateResultExFour<T1, T2, T3, T4>> func ) {
        return IsSuccess ? func.Action() : OperateResultExFour.CreateFailedResult(this);
    }


}

