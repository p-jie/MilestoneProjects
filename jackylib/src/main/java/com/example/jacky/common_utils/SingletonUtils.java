package com.example.jacky.common_utils;

/**
 * @Description:主要功能:Singleton helper class for lazily initialization
 * @version: 1.0.0
 */

public abstract class SingletonUtils<T> {

    private T instance;

    protected abstract T newInstance();

    public final T getInstance() {
        if (instance == null) {
            synchronized (SingletonUtils.class) {
                if (instance == null) {
                    instance = newInstance();
                }
            }
        }
        return instance;
    }
}
