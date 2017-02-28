package com.alipay.aiml.loaders;

import java.util.Map;

/**
 * Abstract interface for any types of loader specified by two types: type of source and type of results
 * Created by mujian on 25/10/16.
 *
 * @param <S> source type of data
 * @param <R> result type of data
 * @author mujian
 */
public interface Loader<R, S> {
    /**
     * @param source of data
     * @return data from ${source}
     */
    R load(S source);

    /**
     * Load from collection of sources
     *
     * @param sources of data
     * @return map with sources names as keys and result data as values
     */
    Map<String, R> loadAll(S... sources);
}
