package com.quatrix.api.utils;

import com.google.common.base.Function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class CollectionUtils {

    /**
     * Avoid instantiation of util class.
     */
    private CollectionUtils() {
    }

    public static <S, R> List<R> map(Collection<S> values, Function<S, R> mapper) {
        if (values == null) {
            return Collections.emptyList();
        }

        final List<R> result = new ArrayList<>(values.size());
        for (S value : values) {
            result.add(mapper.apply(value));
        }

        return result;
    }
}
