package com.chxt.domain.dongya.model;


import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ActivityCacheData {

    private Activity activity;

    private Set<Integer> femaleParticipantIds;

    private Long lastUpdateTime;

    public static ActivityCacheData of(Activity activity) {
        ActivityCacheData data = new ActivityCacheData();
        data.setActivity(activity);
        data.setLastUpdateTime(System.currentTimeMillis());

        if (activity.getParticipants() != null) {
            Set<Integer> femaleIds = activity.getParticipants().stream()
                    .filter(p -> p.getGender() != null && p.getGender() == 2)
                    .map(Participant::getId)
                    .collect(Collectors.toSet());
            data.setFemaleParticipantIds(femaleIds);
        }

        return data;
    }
}
