package com.himoyi.fault.tolerant;

import java.util.Map;

/**
 * 故障恢复策略
 * 即降级策略
 */
public class FailBackTolerantStrategy implements TolerantStrategy {
    @Override
    public Object doTolerant(Map<String, Object> context, Exception e) {
        // todo 降级到其他服务并调用
        return null;
    }
}
