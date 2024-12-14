package com.himoyi.fault.tolerant;

import java.util.Map;

/**
 * 故障自动转移到其他服务节点
 */
public class FailOverTolerantStrategy implements TolerantStrategy {
    @Override
    public Object doTolerant(Map<String, Object> context, Exception e) {
        // todo 转移到其他同等节点进行处理
        return null;
    }
}
