// package com.chxt.fantasticmonkey.controller.alarm;

// import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
// import com.chxt.fantasticmonkey.model.alarm.Alarm;
// import com.chxt.fantasticmonkey.infrastructure.alarm.AlarmMapper;
// import com.chxt.fantasticmonkey.infrastructure.fileServer.ImageClient;
// import com.chxt.fantasticmonkey.service.alarm.AlarmService;
// import lombok.SneakyThrows;
// import org.apache.commons.lang3.time.DateFormatUtils;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import javax.annotation.Resource;
// import javax.servlet.http.HttpServletResponse;
// import java.util.Date;

// @RestController
// @RequestMapping("/alarm")
// public class AlarmController {

//     @Resource
//     private AlarmService alarmService;

//     @Resource
//     private ImageClient imageClient;

//     @Resource
//     private AlarmMapper alarmMapper;


//     @GetMapping("/outdoor")
//     public void outdoor(String str){
//         System.out.println(str);
//         new Thread(()->{
//             this.alarmService.outdoorAlarm();
//         }).start();
//     }

//     @GetMapping("/pic/latest/{deviceId}")
//     @SneakyThrows
//     public void freshPic(@PathVariable("deviceId") String deviceId, HttpServletResponse response) {
//         LambdaQueryWrapper<Alarm> query = new LambdaQueryWrapper<>();
//         query.eq(Alarm::getDeviceId, deviceId);
//         query.orderByDesc(Alarm::getAlarmTime);
//         query.last("limit 1");
//         Alarm alarm = this.alarmMapper.selectOne(query);
//         Date date = alarm.getAlarmTime();
//         String namespace = "outdoor" + DateFormatUtils.format(date, "yyyy-MM");
//         byte[] data = this.imageClient.getImageBinary("alarm", namespace, alarm.getName());
//         response.getOutputStream().write(data);
//     }
// }
