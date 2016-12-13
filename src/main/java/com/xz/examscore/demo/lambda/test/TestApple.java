package com.xz.examscore.demo.lambda.test;

import com.xz.examscore.demo.lambda.pojo.Apple;
import com.xz.examscore.demo.lambda.predicate.ApplePredicate;

import java.util.*;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2016/12/12.
 */
public class TestApple<T> {
    public static void main(String[] args) {
        Apple a1 = new Apple("red", 100);
        Apple a2 = new Apple("red", 200);
        Apple a3 = new Apple("green", 400);
        Apple a4 = new Apple("yellow", 150);
        List<Apple> list1 = Arrays.asList(a1, a2, a3, a4);
        list1.forEach(l -> System.out.println(l.getColor()));
        //Predicate<Apple> applePredicate = (Apple apple) -> apple.getColor().equals("red") && apple.getWeight() > 150;
        List<Integer> length = keyLength(list1, apple -> apple.getColor().length());
        System.out.println(length.toString());
        Predicate<Apple> applePredicate = new ApplePredicate();

        IntPredicate p1 = (int i) -> i % 2 == 0;
        IntPredicate p2 = new IntPredicate() {
            @Override
            public boolean test(int value) {
                return value / 2 == 0;
            }
        };

        System.out.println(new TestApple<Apple>().filter(list1, applePredicate).toString());

    }

    public List<T> filter(List<T> l, Predicate<T> p){
        List<T> result = l.stream().filter(t -> p.test(t)).collect(Collectors.toList());
        return result;
    }

    public static <T, R> List<R> keyLength(List<T> list, java.util.function.Function<T, R> f){
        List<R> result = list.stream().map(f::apply).collect(Collectors.toList());
        return result;
    }
}
