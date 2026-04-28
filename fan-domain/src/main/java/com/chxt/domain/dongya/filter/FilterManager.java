package com.chxt.domain.dongya.filter;


import com.chxt.domain.dongya.filter.impl.NewFemaleFilterStrategy;
import com.chxt.domain.dongya.filter.impl.NewMatchFilterStrategy;
import com.chxt.domain.dongya.filter.impl.PlaceFilterStrategy;
import com.chxt.domain.dongya.filter.impl.TimeFilterStrategy;
import com.chxt.domain.dongya.model.Activity;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 活动过滤器管理器
 *
 * <p>统一管理所有过滤策略，提供统一的活动过滤判断逻辑。</p>
 * <p>过滤逻辑：(PlaceFilterStrategy && TimeFilterStrategy) && (NewFemaleFilterStrategy || NewMatchFilterStrategy)</p>
 */
@Slf4j
@Component
public class FilterManager {

    @Resource
    private NewMatchFilterStrategy newMatchFilterStrategy;

    @Resource
    private NewFemaleFilterStrategy newFemaleFilterStrategy;

    @Resource
    private PlaceFilterStrategy placeFilterStrategy;

    @Resource
    private TimeFilterStrategy timeFilterStrategy;

    /**
     * 判断活动是否满足监控条件
     * 逻辑：(PlaceFilterStrategy && TimeFilterStrategy) && (NewFemaleFilterStrategy || NewMatchFilterStrategy)
     *
     * @param activity 活动
     * @return true 满足条件应发送通知，false 不满足
     */
    public boolean shouldMonitor(Activity activity) {
        boolean placeMatch = placeFilterStrategy.test(activity);
        boolean timeMatch = timeFilterStrategy.test(activity);

        if (!placeMatch || !timeMatch) {
            return false;
        }

        boolean isNewMatch = newMatchFilterStrategy.test(activity);
        boolean hasNewFemale = newFemaleFilterStrategy.test(activity);

        return isNewMatch || hasNewFemale;
    }


}
