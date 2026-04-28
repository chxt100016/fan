package com.chxt.domain.dongya.notification;

import com.chxt.cache.dongya.ActivityCacheData;
import com.chxt.client.dongya58.model.Activity;

public interface NotificationFormatter {
    String formatNewMatchNotification(Activity activity);
    String formatNewFemaleJoinedNotification(Activity activity, ActivityCacheData cachedData);
}
