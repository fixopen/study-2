package com.baremind.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by fixopen on 19/11/2016.
 */
public interface PostProcessor<T> {
    default List<Map<String, Object>> toMaps(List<T> entities) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (T entity : entities) {
            result.add(toMap(entity));
        }
        return result;
    }

    Map<String, Object> toMap(T entity);
}
