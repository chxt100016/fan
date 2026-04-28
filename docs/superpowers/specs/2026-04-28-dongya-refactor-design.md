# 动呀网球监控重构设计文档

## 背景

当前动呀网球监控的业务逻辑分散在 `fan-app` 和 `fan-adapter` 层，配置依赖 YAML 文件。为了提升代码的可维护性和架构规范性，需要将业务逻辑收纳到 domain 层，并通过策略模式实现过滤条件，同时清理配置依赖。

## 目标

1. 将业务逻辑迁移到 `fan-domain` 层的 `dongya` 包下
2. 使用策略模式实现过滤条件，每个过滤条件作为独立实现类
3. 将过滤配置从 YAML 改为各策略类的内部静态属性（完全硬编码）
4. 重构通知格式化逻辑，符合新的展示要求
5. 通过 Gateway 模式解耦 domain 层与 infrastructure 层

## 架构设计

### 新增/修改的文件结构

```
fan-domain/src/main/java/com/chxt/domain/
└── dongya/
    ├── ActivityQueryService.java          # 查询活动服务
    ├── ActivityMonitorService.java        # 活动监控服务（主流程编排）
    ├── filter/
    │   ├── ActivityFilterStrategy.java   # 过滤策略接口
    │   ├── NewMatchFilterStrategy.java   # 新比赛过滤策略
    │   ├── TimeFilterStrategy.java       # 时间过滤策略
    │   ├── PlaceFilterStrategy.java      # 地点过滤策略
    │   └── NewFemaleFilterStrategy.java  # 新女生过滤策略
    ├── notification/
    │   ├── NotificationFormatter.java    # 通知格式化接口
    │   └── DongyaNotificationFormatter.java  # 动呀通知格式化实现
    └── gateway/
        ├── ActivityQueryGateway.java     # 活动查询Gateway接口
        └── NotificationGateway.java      # 通知发送Gateway接口

fan-infrastructure/src/main/java/com/chxt/
└── dongya/
    ├── ActivityQueryGatewayImpl.java     # 活动查询Gateway实现
    └── NotificationGatewayImpl.java      # 通知发送Gateway实现
```

### 核心组件设计

#### 1. 查询活动（ActivityQueryService + ActivityQueryGateway）

**职责**：负责从外部API查询活动数据

- `ActivityQueryGateway`（domain层接口）
  ```java
  public interface ActivityQueryGateway {
      List<Activity> queryActivities(ActivityQueryRequest request);
  }
  ```

- `ActivityQueryGatewayImpl`（infrastructure层实现）
  - 注入 `Dongya58Client`
  - 调用 `getActivities()` 方法
  - 处理异常和空结果

- `ActivityQueryService`（domain层服务）
  - 封装查询逻辑
  - 处理异常和日志

#### 2. 过滤活动（策略模式）

**职责**：使用策略模式实现多种过滤条件

- `ActivityFilterStrategy` 接口
  ```java
  public interface ActivityFilterStrategy {
      boolean test(Activity activity, ActivityCacheData cachedData);
  }
  ```

- 四个策略实现类，每个策略内部硬编码配置：

| 策略类 | 硬编码配置 | 说明 |
|--------|-----------|------|
| `NewMatchFilterStrategy` | `TIME_WINDOW_HOURS = 96` | 新比赛时间窗口（小时） |
| `TimeFilterStrategy` | `Map<DayOfWeek, LocalTime> MIN_TIMES` | 各星期最小开始时间 |
| `PlaceFilterStrategy` | `List<String> PLACES = List.of()` | 地点白名单（空列表表示全部通过） |
| `NewFemaleFilterStrategy` | 无配置 | 比较participants数组检测新女生 |

- `ActivityMonitorService` 组合所有策略
  - 注入所有 `ActivityFilterStrategy` 实现类
  - 提供 `shouldMonitor()` 方法综合判断
  - 逻辑：所有基础过滤（时间、地点）通过后，满足新比赛或新女生任一条件即监控

#### 3. 生成发送内容文本（NotificationFormatter）

**职责**：格式化通知消息，符合新的展示要求

- `NotificationFormatter` 接口
  ```java
  public interface NotificationFormatter {
      String formatNewMatchNotification(Activity activity);
      String formatNewFemaleJoinedNotification(Activity activity, ActivityCacheData cachedData);
  }
  ```

- `DongyaNotificationFormatter` 实现

**展示文本修改要点**：
- 删除地点、地址字段的展示
- 人数的"已有人数"改为通过 `participants.size()` 计算
- 时间格式改为：`M/d(EEE) HH:mm`，今天/明天/后天用中文表示

**时间格式化逻辑**：
```java
// 日期差计算逻辑：
// diffDays == 0 -> "今天"
// diffDays == 1 -> "明天"
// diffDays == 2 -> "后天"
// 其他 -> "M/d" 格式

// 示例：
// 2026-04-28 12:00 (今天，周二) -> 今天(周二) 12:00
// 2026-04-29 21:00 (明天，周三) -> 明天(周三) 21:00
// 2026-04-30 14:00 (后天，周四) -> 后天(周四) 14:00
// 2026-05-02 18:00 -> 5/2(周六) 18:00
```

#### 4. Gateway 接口设计

**ActivityQueryGateway**（domain层）：
```java
public interface ActivityQueryGateway {
    List<Activity> queryActivities(ActivityQueryRequest request);
}
```

**NotificationGateway**（domain层）：
```java
public interface NotificationGateway {
    void sendNotification(String message);
}
```

#### 5. 主流程编排（ActivityMonitorService）

**职责**：编排监控流程

```java
public class ActivityMonitorService {
    @Resource
    private ActivityQueryService activityQueryService;

    @Resource
    private List<ActivityFilterStrategy> filterStrategies;

    @Resource
    private NotificationFormatter notificationFormatter;

    @Resource
    private NotificationGateway notificationGateway;

    // 缓存管理
    private final ConcurrentHashMap<Integer, ActivityCacheData> activityCache = new ConcurrentHashMap<>();

    // 主方法：监控活动
    public void monitorActivities() {
        List<Activity> activities = activityQueryService.queryActivities();
        for (Activity activity : activities) {
            ActivityCacheData cachedData = activityCache.get(activity.getActivityId());
            if (shouldMonitor(activity, cachedData)) {
                sendNotification(activity, cachedData);
            }
            updateCache(activity);
        }
    }
}
```

### 数据流

```
DongYaJob (adapter层)
    ↓ 调用
ActivityMonitorService.monitorActivities() (domain层)
    ↓ 查询
ActivityQueryService.queryActivities()
    ↓
ActivityQueryGateway → ActivityQueryGatewayImpl → Dongya58Client
    ↓ 过滤
ActivityFilterStrategy 策略链
    ↓ 格式化
NotificationFormatter
    ↓ 发送
NotificationGateway → NotificationGatewayImpl → BlueBubblesClient
```

### 配置清理

删除 `DongyaMonitorConfig` 中不再使用的配置项：

- **保留**：`schedule.cron`（Job调度使用）
- **删除**：`time-filter`、`place-filter`、`new-match`、`notice` 配置

### 需要修改的现有文件

1. **DongYaJob.java** - 简化为调用 `ActivityMonitorService`
2. **application.yml** - 清理 `dongya58.monitor` 下的配置
3. **ActivityFilterService.java** - 删除（逻辑迁移到domain层）
4. **DongyaNotificationFormatter.java** - 删除（逻辑迁移到domain层）
5. **DongyaMonitorConfig.java** - 简化，只保留调度配置

### 过滤策略配置详情

#### TimeFilterStrategy 配置

```java
private static final Map<DayOfWeek, LocalTime> MIN_TIMES = Map.of(
    DayOfWeek.MONDAY, LocalTime.of(9, 0),
    DayOfWeek.TUESDAY, LocalTime.of(10, 0),
    DayOfWeek.WEDNESDAY, LocalTime.of(9, 0),
    DayOfWeek.THURSDAY, LocalTime.of(10, 0),
    DayOfWeek.FRIDAY, LocalTime.of(9, 0),
    DayOfWeek.SATURDAY, LocalTime.of(8, 0),
    DayOfWeek.SUNDAY, LocalTime.of(8, 0)
);
```

#### PlaceFilterStrategy 配置

```java
private static final List<String> PLACES = List.of(); // 空列表表示全部通过
```

#### NewMatchFilterStrategy 配置

```java
private static final int TIME_WINDOW_HOURS = 96; // 4天
```

### 测试验证

1. **查询功能验证**
   - 验证能成功查询到活动数据
   - 验证异常处理逻辑

2. **过滤功能验证**
   - 测试新比赛过滤（创建时间在96小时内）
   - 测试时间过滤（各星期最小开始时间）
   - 测试地点过滤（白名单为空时全部通过）
   - 测试新女生过滤（新增女生时触发）

3. **通知格式化验证**
   - 验证新比赛通知格式
   - 验证女生加入通知格式
   - 验证时间格式化逻辑（今天/明天/后天/日期）
   - 验证人数计算（participants.size()）

4. **端到端验证**
   - 运行 DongYaJob 调度任务
   - 验证通知发送是否正常
