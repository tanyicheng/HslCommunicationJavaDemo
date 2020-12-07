package HslCommunication.Core.Types;

/**
 * 带三个参数的泛型类对象
 * @param <T1> 参数一
 * @param <T2> 参数二
 * @param <T3> 参数三
 * @param <T4> 参数四
 */
public class OperateResultExFour<T1,T2,T3,T4> extends OperateResult {
    /**
     * 默认的无参构造方法
     */
    public OperateResultExFour() {
        super();
    }

    /**
     * 使用指定的消息实例化默认的对象
     *
     * @param msg 错误消息
     */
    public OperateResultExFour(String msg) {
        super(msg);
    }

    /**
     * 使用指定的错误号和消息实例化默认的对象
     *
     * @param err 错误码
     * @param msg 错误消息
     */
    public OperateResultExFour(int err, String msg) {
        super(err, msg);
    }


    /**
     * 泛型对象1
     */
    public T1 Content1 = null;


    /**
     * 泛型对象二
     */
    public T2 Content2 = null;


    /**
     * 泛型对象三
     */
    public T3 Content3 = null;

    /**
     * 泛型对象四
     */
    public T4 Content4 = null;


    /**
     * 创建一个成功的泛型类结果对象
     *
     * @param content1 内容一
     * @param content2 内容二
     * @param content3 内容三
     * @param content4 内容四
     * @param <T1>     类型一
     * @param <T2>     类型二
     * @param <T3>     类型三
     * @param <T4>     类型四
     * @return 结果类对象
     */
    public static <T1, T2, T3, T4> OperateResultExFour<T1, T2, T3, T4> CreateSuccessResult(T1 content1, T2 content2, T3 content3, T4 content4) {
        OperateResultExFour<T1, T2, T3, T4> result = new OperateResultExFour<>();
        result.IsSuccess = true;
        result.Content1 = content1;
        result.Content2 = content2;
        result.Content3 = content3;
        result.Content4 = content4;
        result.Message = "success";
        return result;
    }


    /**
     * 创建一个失败的泛型类结果对象
     *
     * @param result 复制的结果对象
     * @param <T1>   类型一
     * @param <T2>   类型二
     * @param <T3>   类型三
     * @param <T4>   类型四
     * @return 结果类对象
     */
    public static <T1, T2, T3, T4> OperateResultExFour<T1, T2, T3, T4> CreateFailedResult(OperateResult result) {
        OperateResultExFour resultExFour = new OperateResultExFour();
        resultExFour.CopyErrorFromOther(result);
        return resultExFour;
    }

    /**
     * 返回一个检查结果对象，可以进行自定义的数据检查。<br />
     * Returns a check result object that allows you to perform custom data checks.
     *
     * @param check   检查的委托方法
     * @param message 检查失败的错误消息
     * @return 如果检查成功，则返回对象本身，如果失败，返回错误信息。
     */
    public OperateResultExFour<T1, T2, T3, T4> Check(FunctionOperateExFour<T1, T2, T3, T4, Boolean> check, String message) {
        if (!IsSuccess) return this;

        if (check.Action(Content1, Content2, Content3, Content4)) return this;
        return new OperateResultExFour<>(message);
    }

    /**
     * 返回一个检查结果对象，可以进行自定义的数据检查。<br />
     * Returns a check result object that allows you to perform custom data checks.
     *
     * @param check 检查的委托方法
     * @return 如果检查成功，则返回对象本身，如果失败，返回错误信息。
     */
    public OperateResultExFour<T1, T2, T3, T4> Check(FunctionOperateExFour<T1, T2, T3, T4, OperateResult> check) {
        if (!IsSuccess) return this;

        OperateResult checkResult = check.Action(Content1, Content2, Content3, Content4);
        if (!checkResult.IsSuccess) return OperateResultExFour.CreateFailedResult(checkResult);

        return this;
    }

    /**
     * 指定接下来要做的是内容，当前对象如果成功，就返回接下来的执行结果，如果失败，就返回当前对象本身。<br />
     * Specify what you want to do next, return the result of the execution of the current object if it succeeds, and return the current object itself if it fails.
     *
     * @param func 等待当前对象成功后执行的内容
     * @return 返回整个方法链最终的成功失败结果
     */
    public OperateResult Then(FunctionOperateExFour<T1, T2, T3, T4, OperateResult> func) {
        if (IsSuccess) return func.Action(Content1, Content2, Content3, Content4);
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
    public <TResult> OperateResultExOne<TResult> ThenExOne(FunctionOperateExFour<T1, T2, T3, T4, OperateResultExOne<TResult>> func) {
        return IsSuccess ? func.Action(Content1, Content2, Content3, Content4) : OperateResultExOne.CreateFailedResult(this);
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
    public <TResult1, TResult2> OperateResultExTwo<TResult1, TResult2> ThenExTwo(FunctionOperateExFour<T1, T2, T3, T4, OperateResultExTwo<TResult1, TResult2>> func) {
        return IsSuccess ? func.Action(Content1, Content2, Content3, Content4) : OperateResultExTwo.CreateFailedResult(this);
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
    public <TResult1, TResult2, TResult3> OperateResultExThree<TResult1, TResult2, TResult3> ThenExThree(FunctionOperateExFour<T1, T2, T3, T4, OperateResultExThree<TResult1, TResult2, TResult3>> func) {
        return IsSuccess ? func.Action(Content1, Content2, Content3, Content4) : OperateResultExThree.CreateFailedResult(this);
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
    public <TResult1, TResult2, TResult3, TResult4> OperateResultExFour<TResult1, TResult2, TResult3, TResult4> ThenExFour(FunctionOperateExFour<T1, T2, T3, T4, OperateResultExFour<TResult1, TResult2, TResult3, TResult4>> func) {
        return IsSuccess ? func.Action(Content1, Content2, Content3, Content4) : OperateResultExFour.CreateFailedResult(this);
    }
}
