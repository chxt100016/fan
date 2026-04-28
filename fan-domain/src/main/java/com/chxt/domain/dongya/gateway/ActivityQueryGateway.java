package com.chxt.domain.dongya.gateway;

import com.chxt.client.dongya58.model.Activity;

import java.util.List;

public interface ActivityQueryGateway {
    List<Activity> queryActivities();
}
