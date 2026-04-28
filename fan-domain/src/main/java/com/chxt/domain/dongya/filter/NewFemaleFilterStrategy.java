package com.chxt.domain.dongya.filter;


import com.chxt.domain.dongya.model.Activity;
import com.chxt.domain.dongya.model.ActivityCacheData;
import com.chxt.domain.dongya.model.Participant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 新女生过滤策略
 *
 * <p>检测是否有新女生加入到活动中。
 * 通过比较当前活动的女生参与者和缓存中的女生参与者，识别新加入的女生。</p>
 */
@Slf4j
@Component
public class NewFemaleFilterStrategy implements ActivityFilterStrategy {

    @Override
    public boolean test(Activity activity, ActivityCacheData cachedData) {
        if (cachedData == null || cachedData.getFemaleParticipantIds() == null) {
            log.debug("活动 {} 无缓存数据，无法检测新女生", activity.getActivityId());
            return false;
        }

        if (activity.getParticipants() == null || activity.getParticipants().isEmpty()) {
            return false;
        }

        Set<Integer> currentFemaleIds = activity.getParticipants().stream()
                .filter(p -> p.getGender() != null && p.getGender() == 2)
                .map(Participant::getId)
                .collect(Collectors.toSet());

        Set<Integer> newFemaleIds = Set.copyOf(currentFemaleIds);
        newFemaleIds.removeAll(cachedData.getFemaleParticipantIds());

        boolean hasNew = !newFemaleIds.isEmpty();

        if (hasNew) {
            List<String> newFemaleNames = activity.getParticipants().stream()
                    .filter(p -> newFemaleIds.contains(p.getId()))
                    .map(Participant::getName)
                    .toList();
            log.info("活动 {} 检测到新女生加入: {}", activity.getActivityId(), newFemaleNames);
        }

        return hasNew;
    }
}
