package HslCommunication.Core.Types;

/**
 * 一个匿名的委托对象，包含了一个action方法，传入一个对象，然后返回指定的类型<br />
 * An anonymous delegate object, including an action method, passing in an object, and then returning the specified type
 * @param <T> 传入的数据类型
 * @param <R> 返回的结果的数据类型
 */
public class FunctionOperateExOne<T,R> {
    public R Action(T content) {
        return null;
    }
}
