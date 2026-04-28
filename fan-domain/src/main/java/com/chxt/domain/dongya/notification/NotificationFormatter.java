package com.chxt.domain.dongya.notification;


import com.chxt.domain.dongya.model.Activity;
import com.chxt.domain.dongya.model.ActivityCacheData;

public interface NotificationFormatter {

    String formatNewMatchNotification(Activity activity);

    String formatNewFemaleJoinedNotification(Activity activity, ActivityCacheData cachedData);

}
