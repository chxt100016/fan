package com.chxt.domain.dongya.filter;

import com.chxt.cache.dongya.ActivityCacheData;
import com.chxt.client.dongya58.model.Activity;

public interface ActivityFilterStrategy {
    boolean test(Activity activity, ActivityCacheData cachedData);
}
