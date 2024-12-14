package com.himoyi.fault.tolerant;

import com.himoyi.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 静默处理，容错策略
 */
@Slf4j
public class FailSafeTolerantStrategy implements TolerantStrategy {
    @Override
    public Object doTolerant(Map<String, Object> context, Exception e) {
        log.info("服务错误，静默处理！", e);
        return new RpcResponse();
    }
}
