package com.baremind.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by fixopen on 19/11/2016.
 */
public interface PostProcessor<T> {
    default List<T> filter(List<T> origin, Predicate<T> accept) {
        final List<T> result = new ArrayList<>();
        origin.forEach((item) -> {
            if (accept.test(item)) {
                result.add(item);
            }
        });
        return result;
    }

    default List<Map<String, Object>> process(List<T> entities, Predicate<T> accept) {
        List<T> filtered = entities;
        if (accept != null) {
            filtered = filter(entities, accept);
        }
        return filtered.stream().map(this::convert).filter(Objects::nonNull).collect(Collectors.toList());
    }

    Map<String, Object> convert(T entity);
}
