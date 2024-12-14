package com.himoyi.fault.retry;

import com.himoyi.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 不重试策略
 */
public class NoRetryStrategy implements RetryStrategy {
    @Override
    public Object doRetry(Callable<Object> callable) throws Exception {
        // 执行任务
        return callable.call();
    }
}
