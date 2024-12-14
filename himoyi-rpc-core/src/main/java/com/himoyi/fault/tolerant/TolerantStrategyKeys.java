package com.himoyi.fault.tolerant;

/**
 * 系统中定义的容错机制
 */
public interface TolerantStrategyKeys {

    /**
     * 快速失败
     */
    String FAIL_FAST = "failFast";

    /**
     * 故障静默
     */
    String FAIL_SAFE = "failSafe";

    /**
     * 故障转移
     */
    String FAIL_OVER = "failOver";

    /**
     * 故障恢复，故障降级
     */
    String FAIL_BACK = "failBack";
}
