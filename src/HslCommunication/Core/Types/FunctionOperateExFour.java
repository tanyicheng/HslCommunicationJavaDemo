package HslCommunication.Core.Types;

/**
 * 一个匿名的委托对象，包含了一个action方法，传入四对象，然后返回指定的类型<br />
 * An anonymous delegate object, including an action method, passing in four objects, and then returning the specified type
 * @param <T1> 传入的数据类型一
 * @param <T2> 传入的数据类型二
 * @param <T3> 传入的数据类型三
 * @param <T4> 传入的数据类型四
 * @param <R> 返回的结果的数据类型
 */
public class FunctionOperateExFour <T1, T2, T3, T4, R> {
    public R Action(T1 content1, T2 content2, T3 content3, T4 content4) {
        return null;
    }
}
