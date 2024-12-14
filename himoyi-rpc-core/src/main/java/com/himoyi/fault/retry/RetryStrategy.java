package com.himoyi.fault.retry;

import com.himoyi.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 重试策略通用接口
 */
public interface RetryStrategy {

    /**
     * 重试
     * @param callable 使用Callable作为参数，意味着doRetry方法可以处理异步操作，因为Callable可以被提交到ExecutorService中执行
     * @return
     * @throws Exception
     */
    Object doRetry(Callable<Object> callable) throws Exception;
}
