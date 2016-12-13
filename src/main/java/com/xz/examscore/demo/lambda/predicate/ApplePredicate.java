package com.xz.examscore.demo.lambda.predicate;

import com.xz.examscore.demo.lambda.pojo.Apple;

import java.util.function.Predicate;

/**
 * @author by fengye on 2016/12/12.
 */
public class ApplePredicate implements Predicate<Apple>{
    @Override
    public boolean test(Apple apple) {
        return apple.getColor().equals("red") && apple.getWeight() > 150;
    }
}
