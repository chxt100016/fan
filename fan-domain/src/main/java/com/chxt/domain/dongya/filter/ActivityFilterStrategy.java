package com.chxt.domain.dongya.filter;


import com.chxt.domain.dongya.model.Activity;

/**
 * 活动过滤策略接口
 *
 * <p>定义了活动过滤条件的统一契约，所有具体的过滤策略都需要实现此接口。
 * 使用策略模式将不同的过滤逻辑封装在独立的策略类中，便于扩展和维护。</p>
 */
public interface ActivityFilterStrategy {
    /**
     * 测试活动是否符合过滤条件
     *
     * <p>实现类应该捕获并处理所有受检异常，避免向调用者抛出异常。
     * 如果发生解析错误等异常情况，应该返回 false 并记录适当的日志。</p>
     *
     * @param activity 待测试的活动
     * @return true 表示活动符合过滤条件，false 表示不符合
     */
    boolean test(Activity activity);
}
