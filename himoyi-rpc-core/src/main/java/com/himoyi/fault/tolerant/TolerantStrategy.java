package com.himoyi.fault.tolerant;

import java.util.Map;

/**
 * 容错机制策略
 */
public interface TolerantStrategy {

    /**
     * 容错
     *
     * @param context 上下文，用于传递数据
     * @param e 异常
     * @return
     */
    Object doTolerant(Map<String, Object> context, Exception e);
}
