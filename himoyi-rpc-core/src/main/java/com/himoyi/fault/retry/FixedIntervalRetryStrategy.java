package com.himoyi.fault.retry;

import com.github.rholder.retry.*;
import com.himoyi.RpcApplication;
import com.himoyi.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 固定时间间隔的重试策略
 */

@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy {
    @Override
    public Object doRetry(Callable<Object> callable) throws Exception {

        // 在Java中，静态方法的泛型参数必须在方法名之前指定，这是由于Java的语法规则和编译器的类型推断机制所决定的。
        // 构造一个重试的执行器
        Retryer<Object> retryer = RetryerBuilder.newBuilder()
                .retryIfException() // 当碰到指定类型的错误时进行重试
                .withWaitStrategy(WaitStrategies.fixedWait(3, TimeUnit.SECONDS)) // 重试策略，固定时间间隔，3s
                .withStopStrategy(StopStrategies.stopAfterAttempt(RpcApplication.getRpcConfig().getRetryAttemptNumber()))  //重试停止策略，重试次数，固定3次
                .withRetryListener(new RetryListener() {
                    // 重试工作，使用监听器监听，在重试的时候除了重试，还可以打印重试次数
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.error("重试次数 {}", attempt.getAttemptNumber());
                    }
                }).build();


        // 执行
        return retryer.call(callable);
    }
}
