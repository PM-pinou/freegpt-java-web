package com.chat.base.bean.common;

/**
 * 参数与结果转换器
 * @param <T> 输入需要转换的参数类型
 * @param <M> 参数转换后的类型。
 * @param <K> 结果转换器的参数类型
 * @param <V> 结果转换器最终的类型
 */
public interface IMethodParamAndResultAdapter<T, M , K, V> {

    M convertParam(T args);

    V convertResult(K result);
}
