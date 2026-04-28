package com.chxt.cache;

public class DongyaCacheKeys {

    private DongyaCacheKeys() {}

    public static final String ACTIVITY_PREFIX = "DONGYA_ACTIVITY:";

    public static final String PARTICIPANTS_PREFIX = "DONGYA_PARTICIPANTS:";

    public static final String LAST_CHECK_TIME = "DONGYA_LAST_CHECK_TIME";

    public static String buildActivityKey(Integer activityId) {
        return ACTIVITY_PREFIX + activityId;
    }

    public static String buildParticipantsKey(Integer activityId) {
        return PARTICIPANTS_PREFIX + activityId;
    }
}
