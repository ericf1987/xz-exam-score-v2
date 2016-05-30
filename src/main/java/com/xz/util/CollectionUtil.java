package com.xz.util;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * (description)
 * created at 16/03/07
 *
 * @author yiding_he
 */
public class CollectionUtil {

    public static <T> Map<String, T> toMap(List<T> list, Function<T, String> function) {
        return list.stream().collect(Collectors.toMap(
                (Function<T, String>) function::apply,
                (Function<T, T>) t -> t
        ));
    }
}
