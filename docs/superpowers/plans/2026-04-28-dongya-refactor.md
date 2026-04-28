# 动呀网球监控重构实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-step. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将动呀网球监控业务逻辑重构到 domain 层，使用策略模式实现过滤条件，通过 Gateway 模式解耦 domain 与 infrastructure 层。

**Architecture:** 采用分层架构，domain 层包含业务逻辑和 Gateway 接口，infrastructure 层实现 Gateway，adapter 层简化为调用 domain 服务。

**Tech Stack:** Spring Boot, Java 17+, Lombok, 策略模式, Gateway 模式

---

## File Structure Overview

**Create Files (Domain Layer):**
- `fan-domain/src/main/java/com/chxt/domain/dongya/gateway/ActivityQueryGateway.java`
- `fan-domain/src/main/java/com/chxt/domain/dongya/gateway/NotificationGateway.java`
- `fan-domain/src/main/java/com/chxt/domain/dongya/filter/ActivityFilterStrategy.java`
- `fan-domain/src/main/java/com/chxt/domain/dongya/filter/NewMatchFilterStrategy.java`
- `fan-domain/src/main/java/com/chxt/domain/dongya/filter/TimeFilterStrategy.java`
- `fan-domain/src/main/java/com/chxt/domain/dongya/filter/PlaceFilterStrategy.java`
- `fan-domain/src/main/java/com/chxt/domain/dongya/filter/NewFemaleFilterStrategy.java`
- `fan-domain/src/main/java/com/chxt/domain/dongya/notification/NotificationFormatter.java`
- `fan-domain/src/main/java/com/chxt/domain/dongya/notification/DongyaNotificationFormatter.java`
- `fan-domain/src/main/java/com/chxt/domain/dongya/ActivityQueryService.java`
- `fan-domain/src/main/java/com/chxt/domain/dongya/ActivityMonitorService.java`

**Create Files (Infrastructure Layer):**
- `fan-infrastructure/src/main/java/com/chxt/dongya/ActivityQueryGatewayImpl.java`
- `fan-infrastructure/src/main/java/com/chxt/dongya/NotificationGatewayImpl.java`

**Modify Files:**
- `fan-adapter/src/main/java/com/chxt/schedule/DongYaJob.java`
- `start/src/main/resources/application.yml`

**Delete Files:**
- `fan-app/src/main/java/com/chxt/service/dongya/ActivityFilterService.java`
- `fan-app/src/main/java/com/chxt/service/dongya/DongyaNotificationFormatter.java`

---

## Task 1: 创建 Gateway 接口

**Files:**
- Create: `fan-domain/src/main/java/com/chxt/domain/dongya/gateway/ActivityQueryGateway.java`
- Create: `fan-domain/src/main/java/com/chxt/domain/dongya/gateway/NotificationGateway.java`

- [ ] **Step 1: 创建包结构**

```bash
mkdir -p fan-domain/src/main/java/com/chxt/domain/dongya/gateway
```

- [ ] **Step 2: 创建 ActivityQueryGateway 接口**

```java
package com.chxt.domain.dongya.gateway;

import com.chxt.client.dongya58.model.Activity;

import java.util.List;

public interface ActivityQueryGateway {
    List<Activity> queryActivities();
}
```

- [ ] **Step 3: 创建 NotificationGateway 接口**

```java
package com.chxt.domain.dongya.gateway;

public interface NotificationGateway {
    void sendNotification(String message);
}
```

- [ ] **Step 4: 提交 Gateway 接口**

```bash
git add fan-domain/src/main/java/com/chxt/domain/dongya/gateway/
git commit -m "feat(dongya): 添加 Gateway 接口"
```

---

## Task 2: 创建 Gateway 实现类

**Files:**
- Create: `fan-infrastructure/src/main/java/com/chxt/dongya/ActivityQueryGatewayImpl.java`
- Create: `fan-infrastructure/src/main/java/com/chxt/dongya/NotificationGatewayImpl.java`

- [ ] **Step 1: 创建 infrastructure dongya 包结构**

```bash
mkdir -p fan-infrastructure/src/main/java/com/chxt/dongya
```

- [ ] **Step 2: 创建 ActivityQueryGatewayImpl 实现**

```java
package com.chxt.dongya;

import com.chxt.client.dongya58.Dongya58Client;
import com.chxt.client.dongya58.model.Activity;
import com.chxt.client.dongya58.model.ActivityRequest;
import com.chxt.client.dongya58.model.ActivityResponse;
import com.chxt.domain.dongya.gateway.ActivityQueryGateway;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ActivityQueryGatewayImpl implements ActivityQueryGateway {

    @Resource
    private Dongya58Client dongya58Client;

    @Override
    public List<Activity> queryActivities() {
        try {
            ActivityRequest request = ActivityRequest.builder()
                    .page(1)
                    .limit(100)
                    .filterSportType(0)
                    .filterMinMaxLevel("4.5,4.5")
                    .filterType(4)
                    .filterDivisionFormat(1)
                    .city("杭州")
                    .build();

            ActivityResponse response = dongya58Client.getActivities(request);

            if (response == null || response.getData() == null) {
                return new ArrayList<>();
            }

            return response.getData();
        } catch (Exception e) {
            log.error("获取活动数据失败", e);
            return new ArrayList<>();
        }
    }
}
```

- [ ] **Step 3: 创建 NotificationGatewayImpl 实现**

```java
package com.chxt.dongya;

import com.chxt.client.bluebubbles.BlueBubblesClient;
import com.chxt.domain.dongya.gateway.NotificationGateway;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationGatewayImpl implements NotificationGateway {

    @Resource
    private BlueBubblesClient blueBubblesClient;

    @Override
    public void sendNotification(String message) {
        try {
            blueBubblesClient.send(message);
            log.info("发送通知成功");
        } catch (Exception e) {
            log.error("发送通知失败", e);
        }
    }
}
```

- [ ] **Step 4: 提交 Gateway 实现**

```bash
git add fan-infrastructure/src/main/java/com/chxt/dongya/
git commit -m "feat(dongya): 添加 Gateway 实现类"
```

---

## Task 3: 创建过滤策略接口

**Files:**
- Create: `fan-domain/src/main/java/com/chxt/domain/dongya/filter/ActivityFilterStrategy.java`

- [ ] **Step 1: 创建 filter 包结构**

```bash
mkdir -p fan-domain/src/main/java/com/chxt/domain/dongya/filter
```

- [ ] **Step 2: 创建 ActivityFilterStrategy 接口**

```java
package com.chxt.domain.dongya.filter;

import com.chxt.cache.dongya.ActivityCacheData;
import com.chxt.client.dongya58.model.Activity;

public interface ActivityFilterStrategy {
    boolean test(Activity activity, ActivityCacheData cachedData);
}
```

- [ ] **Step 3: 提交过滤策略接口**

```bash
git add fan-domain/src/main/java/com/chxt/domain/dongya/filter/ActivityFilterStrategy.java
git commit -m "feat(dongya): 添加过滤策略接口"
```

---

## Task 4: 实现新比赛过滤策略

**Files:**
- Create: `fan-domain/src/main/java/com/chxt/domain/dongya/filter/NewMatchFilterStrategy.java`

- [ ] **Step 1: 创建 NewMatchFilterStrategy**

```java
package com.chxt.domain.dongya.filter;

import com.chxt.cache.dongya.ActivityCacheData;
import com.chxt.client.dongya58.model.Activity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class NewMatchFilterStrategy implements ActivityFilterStrategy {

    private static final int TIME_WINDOW_HOURS = 96;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public boolean test(Activity activity, ActivityCacheData cachedData) {
        if (activity.getCreatedAt() == null || activity.getCreatedAt().isEmpty()) {
            return false;
        }

        try {
            LocalDateTime createdAt = LocalDateTime.parse(activity.getCreatedAt(), DATE_TIME_FORMATTER);
            LocalDateTime threshold = LocalDateTime.now().minusHours(TIME_WINDOW_HOURS);

            boolean isNew = createdAt.isAfter(threshold);
            log.debug("活动 {} 是否为新比赛: {} (createdAt: {}, threshold: {})",
                    activity.getActivityId(), isNew, createdAt, threshold);
            return isNew;
        } catch (Exception e) {
            log.error("解析活动创建时间失败: activityId={}, createdAt={}",
                    activity.getActivityId(), activity.getCreatedAt(), e);
            return false;
        }
    }
}
```

- [ ] **Step 2: 提交新比赛过滤策略**

```bash
git add fan-domain/src/main/java/com/chxt/domain/dongya/filter/NewMatchFilterStrategy.java
git commit -m "feat(dongya): 添加新比赛过滤策略"
```

---

## Task 5: 实现时间过滤策略

**Files:**
- Create: `fan-domain/src/main/java/com/chxt/domain/dongya/filter/TimeFilterStrategy.java`

- [ ] **Step 1: 创建 TimeFilterStrategy**

```java
package com.chxt.domain.dongya.filter;

import com.chxt.cache.dongya.ActivityCacheData;
import com.chxt.client.dongya58.model.Activity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Component
public class TimeFilterStrategy implements ActivityFilterStrategy {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final Map<DayOfWeek, LocalTime> MIN_TIMES = Map.of(
            DayOfWeek.MONDAY, LocalTime.of(9, 0),
            DayOfWeek.TUESDAY, LocalTime.of(10, 0),
            DayOfWeek.WEDNESDAY, LocalTime.of(9, 0),
            DayOfWeek.THURSDAY, LocalTime.of(10, 0),
            DayOfWeek.FRIDAY, LocalTime.of(9, 0),
            DayOfWeek.SATURDAY, LocalTime.of(8, 0),
            DayOfWeek.SUNDAY, LocalTime.of(8, 0)
    );

    @Override
    public boolean test(Activity activity, ActivityCacheData cachedData) {
        if (activity.getBeginTime() == null || activity.getBeginTime().isEmpty()) {
            return false;
        }

        try {
            LocalDateTime beginTime = LocalDateTime.parse(activity.getBeginTime(), DATE_TIME_FORMATTER);
            DayOfWeek dayOfWeek = beginTime.getDayOfWeek();
            LocalTime beginTimeOfDay = beginTime.toLocalTime();

            LocalTime minTime = MIN_TIMES.get(dayOfWeek);
            if (minTime == null) {
                log.debug("星期 {} 未配置时间筛选，跳过", dayOfWeek);
                return false;
            }

            boolean matches = !beginTimeOfDay.isBefore(minTime);

            log.debug("活动 {} 时间筛选: {} (星期: {} - 实际: {} - 最小: {})",
                    activity.getActivityId(), matches, dayOfWeek, beginTimeOfDay, minTime);
            return matches;
        } catch (Exception e) {
            log.error("解析活动开始时间失败: activityId={}, beginTime={}",
                    activity.getActivityId(), activity.getBeginTime(), e);
            return false;
        }
    }
}
```

- [ ] **Step 2: 提交时间过滤策略**

```bash
git add fan-domain/src/main/java/com/chxt/domain/dongya/filter/TimeFilterStrategy.java
git commit -m "feat(dongya): 添加时间过滤策略"
```

---

## Task 6: 实现地点过滤策略

**Files:**
- Create: `fan-domain/src/main/java/com/chxt/domain/dongya/filter/PlaceFilterStrategy.java`

- [ ] **Step 1: 创建 PlaceFilterStrategy**

```java
package com.chxt.domain.dongya.filter;

import com.chxt.cache.dongya.ActivityCacheData;
import com.chxt.client.dongya58.model.Activity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PlaceFilterStrategy implements ActivityFilterStrategy {

    private static final List<String> PLACES = List.of();

    @Override
    public boolean test(Activity activity, ActivityCacheData cachedData) {
        if (activity.getPlacename() == null || activity.getPlacename().isEmpty()) {
            return false;
        }

        if (PLACES.isEmpty()) {
            log.debug("未配置地点筛选，全部通过");
            return true;
        }

        boolean matches = PLACES.stream()
                .anyMatch(configuredPlace -> activity.getPlacename().contains(configuredPlace));

        log.debug("活动 {} 地点筛选: {} (placename: {})",
                activity.getActivityId(), matches, activity.getPlacename());
        return matches;
    }
}
```

- [ ] **Step 2: 提交地点过滤策略**

```bash
git add fan-domain/src/main/java/com/chxt/domain/dongya/filter/PlaceFilterStrategy.java
git commit -m "feat(dongya): 添加地点过滤策略"
```

---

## Task 7: 实现新女生过滤策略

**Files:**
- Create: `fan-domain/src/main/java/com/chxt/domain/dongya/filter/NewFemaleFilterStrategy.java`

- [ ] **Step 1: 创建 NewFemaleFilterStrategy**

```java
package com.chxt.domain.dongya.filter;

import com.chxt.cache.dongya.ActivityCacheData;
import com.chxt.client.dongya58.model.Activity;
import com.chxt.client.dongya58.model.Participant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

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
```

- [ ] **Step 2: 提交新女生过滤策略**

```bash
git add fan-domain/src/main/java/com/chxt/domain/dongya/filter/NewFemaleFilterStrategy.java
git commit -m "feat(dongya): 添加新女生过滤策略"
```

---

## Task 8: 创建通知格式化接口

**Files:**
- Create: `fan-domain/src/main/java/com/chxt/domain/dongya/notification/NotificationFormatter.java`

- [ ] **Step 1: 创建 notification 包结构**

```bash
mkdir -p fan-domain/src/main/java/com/chxt/domain/dongya/notification
```

- [ ] **Step 2: 创建 NotificationFormatter 接口**

```java
package com.chxt.domain.dongya.notification;

import com.chxt.cache.dongya.ActivityCacheData;
import com.chxt.client.dongya58.model.Activity;

public interface NotificationFormatter {
    String formatNewMatchNotification(Activity activity);
    String formatNewFemaleJoinedNotification(Activity activity, ActivityCacheData cachedData);
}
```

- [ ] **Step 3: 提交通知格式化接口**

```bash
git add fan-domain/src/main/java/com/chxt/domain/dongya/notification/NotificationFormatter.java
git commit -m "feat(dongya): 添加通知格式化接口"
```

---

## Task 9: 实现通知格式化类

**Files:**
- Create: `fan-domain/src/main/java/com/chxt/domain/dongya/notification/DongyaNotificationFormatter.java`

- [ ] **Step 1: 创建 DongyaNotificationFormatter 实现**

```java
package com.chxt.domain.dongya.notification;

import com.chxt.cache.dongya.ActivityCacheData;
import com.chxt.client.dongya58.model.Activity;
import com.chxt.client.dongya58.model.Participant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class DongyaNotificationFormatter implements NotificationFormatter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter MONTH_DAY_FORMATTER = DateTimeFormatter.ofPattern("M/d");

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private static final String[] WEEKDAYS = {"", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};

    private static final String TITLE_PREFIX = "[动呀网球]";

    @Override
    public String formatNewMatchNotification(Activity activity) {
        StringBuilder sb = new StringBuilder();

        sb.append(TITLE_PREFIX).append(" 新比赛通知\n\n");

        sb.append("【比赛信息】\n");
        sb.append(formatBasicInfo(activity)).append("\n");

        sb.append(formatParticipants(activity));

        return sb.toString();
    }

    @Override
    public String formatNewFemaleJoinedNotification(Activity activity, ActivityCacheData cachedData) {
        StringBuilder sb = new StringBuilder();

        sb.append(TITLE_PREFIX).append(" 女生加入通知\n\n");

        sb.append("【比赛信息】\n");
        sb.append(formatBasicInfo(activity)).append("\n");

        sb.append("【新加入的女生】\n");
        Set<Integer> cachedFemaleIds = cachedData.getFemaleParticipantIds();
        List<String> newFemaleNames = activity.getParticipants().stream()
                .filter(p -> p.getGender() != null && p.getGender() == 2)
                .filter(p -> !cachedFemaleIds.contains(p.getId()))
                .map(this::formatParticipantDetail)
                .toList();

        newFemaleNames.forEach(name -> sb.append("• ").append(name).append("\n"));

        return sb.toString();
    }

    private String formatBasicInfo(Activity activity) {
        StringBuilder sb = new StringBuilder();

        String timeInfo = formatTimeInfo(activity.getBeginTime(), activity.getFinishTime());
        sb.append("时间: ").append(timeInfo).append("\n");

        sb.append("活动: ").append(activity.getName()).append("\n");

        int currentCount = activity.getParticipants() != null ? activity.getParticipants().size() : 0;
        int maxCount = activity.getParticipantMax() != null ? activity.getParticipantMax() : 0;
        sb.append("人数: ").append(currentCount).append("/").append(maxCount).append("\n");

        return sb.toString();
    }

    private String formatTimeInfo(String beginTime, String finishTime) {
        if (beginTime == null || beginTime.isEmpty()) {
            return "未设置";
        }

        try {
            LocalDateTime begin = LocalDateTime.parse(beginTime, DATE_TIME_FORMATTER);
            LocalDateTime now = LocalDateTime.now();
            long diffDays = ChronoUnit.DAYS.between(now, begin);

            String dateStr;
            if (diffDays == 0) {
                dateStr = "今天";
            } else if (diffDays == 1) {
                dateStr = "明天";
            } else if (diffDays == 2) {
                dateStr = "后天";
            } else {
                dateStr = begin.format(MONTH_DAY_FORMATTER);
            }

            String weekday = WEEKDAYS[begin.getDayOfWeek().getValue()];

            if (finishTime != null && !finishTime.isEmpty()) {
                LocalDateTime finish = LocalDateTime.parse(finishTime, DATE_TIME_FORMATTER);
                String finishStr = finish.format(TIME_FORMATTER);
                return String.format("%s(%s) %s ~ %s", dateStr, weekday, begin.format(TIME_FORMATTER), finishStr);
            }

            return String.format("%s(%s) %s", dateStr, weekday, begin.format(TIME_FORMATTER));
        } catch (Exception e) {
            log.error("解析时间失败: beginTime={}", beginTime, e);
            return beginTime;
        }
    }

    private String formatParticipants(Activity activity) {
        if (activity.getParticipants() == null || activity.getParticipants().isEmpty()) {
            return "【参与者】\n暂无参与者\n";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("【参与者】\n");

        List<String> males = new ArrayList<>();
        List<String> females = new ArrayList<>();

        for (Participant p : activity.getParticipants()) {
            if (p.getGender() != null && p.getGender() == 2) {
                females.add(formatParticipantDetail(p));
            } else {
                males.add(formatParticipantDetail(p));
            }
        }

        if (!males.isEmpty()) {
            sb.append("男生:\n");
            males.forEach(name -> sb.append("• ").append(name).append("\n"));
        }

        if (!females.isEmpty()) {
            sb.append("女生:\n");
            females.forEach(name -> sb.append("• ").append(name).append("\n"));
        }

        return sb.toString();
    }

    private String formatParticipantDetail(Participant p) {
        StringBuilder sb = new StringBuilder();
        sb.append(p.getName());

        sb.append(" ").append(getGenderText(p.getGender()));

        if (p.getSinglesUtr() != null && p.getSinglesUtr() > 0) {
            sb.append(String.format(" UTR:%.2f", p.getSinglesUtr()));
        } else if (p.getTennisLevel() != null && !p.getTennisLevel().isEmpty()) {
            sb.append(String.format(" 等级:%s", p.getTennisLevel()));
        }

        return sb.toString();
    }

    private String getGenderText(Integer gender) {
        if (gender == null) {
            return "";
        }
        return switch (gender) {
            case 1 -> "男";
            case 2 -> "女";
            default -> "";
        };
    }
}
```

- [ ] **Step 2: 提交通知格式化实现**

```bash
git add fan-domain/src/main/java/com/chxt/domain/dongya/notification/DongyaNotificationFormatter.java
git commit -m "feat(dongya): 添加通知格式化实现"
```

---

## Task 10: 创建活动查询服务

**Files:**
- Create: `fan-domain/src/main/java/com/chxt/domain/dongya/ActivityQueryService.java`

- [ ] **Step 1: 创建 ActivityQueryService**

```java
package com.chxt.domain.dongya;

import com.chxt.client.dongya58.model.Activity;
import com.chxt.domain.dongya.gateway.ActivityQueryGateway;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ActivityQueryService {

    @Resource
    private ActivityQueryGateway activityQueryGateway;

    public List<Activity> queryActivities() {
        log.info("开始查询活动数据");
        List<Activity> activities = activityQueryGateway.queryActivities();
        log.info("查询到 {} 个活动", activities.size());
        return activities;
    }
}
```

- [ ] **Step 2: 提交活动查询服务**

```bash
git add fan-domain/src/main/java/com/chxt/domain/dongya/ActivityQueryService.java
git commit -m "feat(dongya): 添加活动查询服务"
```

---

## Task 11: 创建活动监控服务

**Files:**
- Create: `fan-domain/src/main/java/com/chxt/domain/dongya/ActivityMonitorService.java`

- [ ] **Step 1: 创建 ActivityMonitorService**

```java
package com.chxt.domain.dongya;

import com.chxt.cache.dongya.ActivityCacheData;
import com.chxt.client.dongya58.model.Activity;
import com.chxt.domain.dongya.filter.ActivityFilterStrategy;
import com.chxt.domain.dongya.filter.NewFemaleFilterStrategy;
import com.chxt.domain.dongya.filter.NewMatchFilterStrategy;
import com.chxt.domain.dongya.gateway.NotificationGateway;
import com.chxt.domain.dongya.notification.NotificationFormatter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ActivityMonitorService {

    @Resource
    private ActivityQueryService activityQueryService;

    @Resource
    private List<ActivityFilterStrategy> filterStrategies;

    @Resource
    private NotificationFormatter notificationFormatter;

    @Resource
    private NotificationGateway notificationGateway;

    @Resource
    private NewMatchFilterStrategy newMatchFilterStrategy;

    @Resource
    private NewFemaleFilterStrategy newFemaleFilterStrategy;

    private final ConcurrentHashMap<Integer, ActivityCacheData> activityCache = new ConcurrentHashMap<>();

    public void monitorActivities() {
        log.info("开始执行动呀网球比赛监控任务");

        try {
            List<Activity> activities = activityQueryService.queryActivities();

            if (activities.isEmpty()) {
                log.info("未获取到活动数据");
                return;
            }

            log.info("获取到 {} 个活动", activities.size());

            int notificationCount = 0;
            for (Activity activity : activities) {
                try {
                    ActivityCacheData cachedData = activityCache.get(activity.getActivityId());

                    if (shouldMonitor(activity, cachedData)) {
                        sendNotification(activity, cachedData);
                        notificationCount++;
                    }
                    updateCache(activity);
                } catch (Exception e) {
                    log.error("处理活动失败: activityId={}", activity.getActivityId(), e);
                }
            }

            log.info("动呀网球比赛监控任务完成，发送通知: {} 条", notificationCount);

        } catch (Exception e) {
            log.error("动呀网球比赛监控任务执行失败", e);
        }
    }

    private boolean shouldMonitor(Activity activity, ActivityCacheData cachedData) {
        boolean isNewMatch = newMatchFilterStrategy.test(activity, cachedData);
        boolean hasNewFemale = newFemaleFilterStrategy.test(activity, cachedData);

        boolean shouldMonitor = isNewMatch || hasNewFemale;

        log.info("活动 {} 监控决策: {} (新比赛: {}, 新女生: {})",
                activity.getActivityId(), shouldMonitor, isNewMatch, hasNewFemale);

        return shouldMonitor;
    }

    private void sendNotification(Activity activity, ActivityCacheData cachedData) {
        try {
            String message;

            boolean isNewMatch = newMatchFilterStrategy.test(activity, cachedData);
            boolean hasNewFemale = newFemaleFilterStrategy.test(activity, cachedData);

            if (isNewMatch) {
                message = notificationFormatter.formatNewMatchNotification(activity);
            } else if (hasNewFemale) {
                message = notificationFormatter.formatNewFemaleJoinedNotification(activity, cachedData);
            } else {
                log.warn("活动 {} 既不是新比赛也没有新女生，不应发送通知", activity.getActivityId());
                return;
            }

            notificationGateway.sendNotification(message);
            log.info("发送通知成功: activityId={}", activity.getActivityId());

        } catch (Exception e) {
            log.error("发送通知失败: activityId={}", activity.getActivityId(), e);
        }
    }

    private void updateCache(Activity activity) {
        ActivityCacheData cacheData = ActivityCacheData.of(activity);
        activityCache.put(activity.getActivityId(), cacheData);
        log.debug("更新缓存: activityId={}", activity.getActivityId());
    }
}
```

- [ ] **Step 2: 提交活动监控服务**

```bash
git add fan-domain/src/main/java/com/chxt/domain/dongya/ActivityMonitorService.java
git commit -m "feat(dongya): 添加活动监控服务"
```

---

## Task 12: 修改 DongYaJob 调用新服务

**Files:**
- Modify: `fan-adapter/src/main/java/com/chxt/schedule/DongYaJob.java`

- [ ] **Step 1: 修改 DongYaJob 简化为调用 ActivityMonitorService**

```java
package com.chxt.schedule;

import com.chxt.domain.dongya.ActivityMonitorService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DongYaJob {

    @Resource
    private ActivityMonitorService activityMonitorService;

    @Scheduled(cron = "${dongya58.monitor.schedule.cron:0 0 */2 * * *}")
    public void monitorTennisMatches() {
        activityMonitorService.monitorActivities();
    }
}
```

- [ ] **Step 2: 提交修改后的 DongYaJob**

```bash
git add fan-adapter/src/main/java/com/chxt/schedule/DongYaJob.java
git commit -m "refactor(dongya): 简化 DongYaJob 调用新服务"
```

---

## Task 13: 清理配置文件

**Files:**
- Modify: `start/src/main/resources/application.yml`

- [ ] **Step 1: 清理 application.yml 中的 dongya58 配置**

```yaml
server:
    port: 8888
    servlet:
        context-path: /api/fantastic
spring:
    application:
        name: fantastic

    datasource:
        url: jdbc:mysql://localhost:3306/fan?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
        username: root
        password: root
        driver-class-name: com.mysql.cj.jdbc.Driver
        type: com.zaxxer.hikari.HikariDataSource

dongya58:
  monitor:
    schedule:
      cron: "0 0 */2 * * *"
```

- [ ] **Step 2: 提交配置清理**

```bash
git add start/src/main/resources/application.yml
git commit -m "refactor(dongya): 清理配置文件"
```

---

## Task 14: 删除旧的 app 层服务

**Files:**
- Delete: `fan-app/src/main/java/com/chxt/service/dongya/ActivityFilterService.java`
- Delete: `fan-app/src/main/java/com/chxt/service/dongya/DongyaNotificationFormatter.java`

- [ ] **Step 1: 删除 ActivityFilterService**

```bash
rm fan-app/src/main/java/com/chxt/service/dongya/ActivityFilterService.java
```

- [ ] **Step 2: 删除 DongyaNotificationFormatter**

```bash
rm fan-app/src/main/java/com/chxt/service/dongya/DongyaNotificationFormatter.java
```

- [ ] **Step 3: 检查 dongya 包是否为空，如果为空则删除**

```bash
rmdir fan-app/src/main/java/com/chxt/service/dongya 2>/dev/null || echo "包不为空或不存在的提示"
```

- [ ] **Step 4: 提交删除操作**

```bash
git add -A
git commit -m "refactor(dongya): 删除旧的 app 层服务"
```

---

## Task 15: 验证编译

**Files:** None

- [ ] **Step 1: 编译项目**

```bash
mvn clean compile
```

Expected: BUILD SUCCESS

- [ ] **Step 2: 运行测试（如果有）**

```bash
mvn test
```

Expected: Tests pass

- [ ] **Step 3: 提交验证**

```bash
git commit --allow-empty -m "test(dongya): 验证编译通过"
```

---

## Task 16: 端到端验证

**Files:** None

- [ ] **Step 1: 启动应用**

```bash
mvn spring-boot:run
```

- [ ] **Step 2: 检查日志输出**

Expected: 日志显示应用启动成功，没有依赖注入错误

- [ ] **Step 3: 手动触发监控任务（可选）**

通过调用接口或等待定时任务执行，验证：
1. 能成功查询活动数据
2. 过滤逻辑正确执行
3. 通知格式符合预期
4. 时间格式化正确（今天/明天/后天/日期）
5. 人数计算正确（participants.size()）
6. 不再显示地点和地址

- [ ] **Step 4: 提交验证结果**

```bash
git commit --allow-empty -m "test(dongya): 端到端验证通过"
```

---

## Summary

重构完成后，项目结构变为：

- **Domain 层**：包含所有业务逻辑（查询、过滤、格式化、监控编排）
- **Infrastructure 层**：实现 Gateway 接口，封装外部客户端调用
- **Adapter 层**：DongYaJob 简化为调用 domain 服务
- **配置**：清理了 YAML 中的业务配置，改为代码内部硬编码

**关键改进：**
1. 业务逻辑集中在 domain 层，符合 DDD 分层架构
2. 使用策略模式实现过滤条件，易于扩展
3. 配置硬编码在策略类内部，简化配置管理
4. Gateway 模式解耦 domain 与 infrastructure 层
5. 通知格式化符合新的展示要求
