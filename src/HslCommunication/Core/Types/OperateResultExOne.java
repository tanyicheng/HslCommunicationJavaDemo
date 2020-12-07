package HslCommunication.Core.Types;


/**
 * 带一个参数的结果类对象
 * @param <T>
 */
public class OperateResultExOne<T> extends  OperateResult {

    /**
     * 默认的无参构造方法
     */
    public OperateResultExOne() {
        super();
    }

    /**
     * 使用指定的消息实例化默认的对象
     *
     * @param msg 错误消息
     */
    public OperateResultExOne(String msg) {
        super(msg);
    }

    /**
     * 使用指定的错误号和消息实例化默认的对象
     *
     * @param err 错误码
     * @param msg 错误消息
     */
    public OperateResultExOne(int err, String msg) {
        super(err, msg);
    }

    /**
     * 泛型参数对象
     */
    public T Content = null;


    /**
     * 创建一个失败的对象
     *
     * @param result 失败的结果
     * @param <T>    类型参数
     * @return 结果类对象
     */
    public static <T> OperateResultExOne<T> CreateFailedResult(OperateResult result) {
        OperateResultExOne<T> resultExOne = new OperateResultExOne<T>();
        resultExOne.CopyErrorFromOther(result);
        return resultExOne;
    }


    /**
     * 创建一个成功的泛型类结果对象
     *
     * @param content 内容
     * @param <T>     类型
     * @return 结果类对象
     */
    public static <T> OperateResultExOne<T> CreateSuccessResult(T content) {
        OperateResultExOne<T> result = new OperateResultExOne<T>();
        result.IsSuccess = true;
        result.Content = content;
        result.Message = "success";
        return result;
    }

    /**
     * 返回一个检查结果对象，可以进行自定义的数据检查。<br />
     * Returns a check result object that allows you to perform custom data checks.
     *
     * @param check   检查的委托方法
     * @param message 检查失败的错误消息
     * @return 如果检查成功，则返回对象本身，如果失败，返回错误信息。
     */
    public OperateResultExOne<T> Check(FunctionOperateExOne<T, Boolean> check, String message) {
        if (!IsSuccess) return this;

        if (check.Action(Content)) return this;
        return new OperateResultExOne<>(message);
    }

    /**
     * 返回一个检查结果对象，可以进行自定义的数据检查。<br />
     * Returns a check result object that allows you to perform custom data checks.
     *
     * @param check 检查的委托方法
     * @return 如果检查成功，则返回对象本身，如果失败，返回错误信息。
     */
    public OperateResultExOne<T> Check(FunctionOperateExOne<T, OperateResult> check) {
        if (!IsSuccess) return this;

        OperateResult checkResult = check.Action(Content);
        if (!checkResult.IsSuccess) return OperateResultExOne.CreateFailedResult(checkResult);

        return this;
    }

    /**
     * 指定接下来要做的是内容，当前对象如果成功，就返回接下来的执行结果，如果失败，就返回当前对象本身。<br />
     * Specify what you want to do next, return the result of the execution of the current object if it succeeds, and return the current object itself if it fails.
     *
     * @param func 等待当前对象成功后执行的内容
     * @return 返回整个方法链最终的成功失败结果
     */
    public OperateResult Then(FunctionOperateExOne<T, OperateResult> func) {
        if (IsSuccess) return func.Action(Content);
        return this;
    }

    /**
     * 指定接下来要做的是内容，当前对象如果成功，就返回接下来的执行结果，如果失败，就返回当前对象本身。<br />
     * Specify what you want to do next, return the result of the execution of the current object if it succeeds, and return the current object itself if it fails.
     *
     * @param func      等待当前对象成功后执行的内容
     * @param <TResult> 泛型参数
     * @return 返回整个方法链最终的成功失败结果
     */
    public <TResult> OperateResultExOne<TResult> ThenExOne(FunctionOperateExOne<T, OperateResultExOne<TResult>> func) {
        return IsSuccess ? func.Action(Content) : OperateResultExOne.CreateFailedResult(this);
    }

    /**
     * 指定接下来要做的是内容，当前对象如果成功，就返回接下来的执行结果，如果失败，就返回当前对象本身。<br />
     * Specify what you want to do next, return the result of the execution of the current object if it succeeds, and return the current object itself if it fails.
     *
     * @param func       等待当前对象成功后执行的内容
     * @param <TResult1> 泛型参数一
     * @param <TResult2> 泛型参数二
     * @return 返回整个方法链最终的成功失败结果
     */
    public <TResult1, TResult2> OperateResultExTwo<TResult1, TResult2> ThenExTwo(FunctionOperateExOne<T, OperateResultExTwo<TResult1, TResult2>> func) {
        return IsSuccess ? func.Action(Content) : OperateResultExTwo.CreateFailedResult(this);
    }

    /**
     * 指定接下来要做的是内容，当前对象如果成功，就返回接下来的执行结果，如果失败，就返回当前对象本身。<br />
     * Specify what you want to do next, return the result of the execution of the current object if it succeeds, and return the current object itself if it fails.
     *
     * @param func       等待当前对象成功后执行的内容
     * @param <TResult1> 泛型参数一
     * @param <TResult2> 泛型参数二
     * @param <TResult3> 泛型参数三
     * @return 返回整个方法链最终的成功失败结果
     */
    public <TResult1, TResult2, TResult3> OperateResultExThree<TResult1, TResult2, TResult3> ThenExThree(FunctionOperateExOne<T, OperateResultExThree<TResult1, TResult2, TResult3>> func) {
        return IsSuccess ? func.Action(Content) : OperateResultExThree.CreateFailedResult(this);
    }

    /**
     * 指定接下来要做的是内容，当前对象如果成功，就返回接下来的执行结果，如果失败，就返回当前对象本身。<br />
     * Specify what you want to do next, return the result of the execution of the current object if it succeeds, and return the current object itself if it fails.
     *
     * @param func       等待当前对象成功后执行的内容
     * @param <TResult1> 泛型参数一
     * @param <TResult2> 泛型参数二
     * @param <TResult3> 泛型参数三
     * @param <TResult4> 泛型参数四
     * @return 返回整个方法链最终的成功失败结果
     */
    public <TResult1, TResult2, TResult3, TResult4> OperateResultExFour<TResult1, TResult2, TResult3, TResult4> ThenExFour(FunctionOperateExOne<T, OperateResultExFour<TResult1, TResult2, TResult3, TResult4>> func) {
        return IsSuccess ? func.Action(Content) : OperateResultExFour.CreateFailedResult(this);
    }

}
