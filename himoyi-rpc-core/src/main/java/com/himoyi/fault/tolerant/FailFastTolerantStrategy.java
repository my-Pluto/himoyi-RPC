package com.himoyi.fault.tolerant;

import java.util.Map;

/**
 * 快速失败容错策略
 */
public class FailFastTolerantStrategy implements TolerantStrategy{
    @Override
    public Object doTolerant(Map<String, Object> context, Exception e) {
        throw new RuntimeException("服务错误！", e);
    }
}
