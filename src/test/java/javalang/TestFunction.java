package javalang;

import java.util.function.Function;

/**
 * @author by fengye on 2017/1/11.
 */
public class TestFunction {
    public static void main(String[] args) {
        TestFunction tf = new TestFunction();
        Function<Integer, String> converter = i -> String.valueOf(i);
        Function<Integer, String> printResult = i -> {
            int result = i * i;
            return "求平方运算结果为：" + result;
        };
        System.out.println(converter.apply(100));
        System.out.println(converter.apply(1000).length());
        System.out.println(tf.calc(100, printResult));
    }

    public String calc (int i, Function<Integer, String> func){
        return func.apply(i);
    }


}
