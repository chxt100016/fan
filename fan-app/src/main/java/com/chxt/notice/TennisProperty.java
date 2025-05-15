// package com.chxt.notice;

// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import org.apache.commons.lang3.StringUtils;

// import com.chxt.domain.pic.TimetableEnum;

// import java.util.Arrays;
// import java.util.List;
// import java.util.stream.Collectors;

// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// public class TennisProperty {

//     private Integer dayRange;

//     private List<Integer> outDoorWeekdayHours;

//     private List<Integer> outDoorWeekendHours;

//     private List<Integer> inDoorWeekdayHours;

//     private List<Integer> intDoorWeekendHours;

//     public TennisProperty (List<Environment> environments) {
//         this.outDoorWeekdayHours = this.getContent("outDoorWeekdayHours", environments);
//         this.outDoorWeekendHours = this.getContent("outDoorWeekendHours", environments);
//         this.inDoorWeekdayHours = this.getContent("inDoorWeekdayHours", environments);
//         this.intDoorWeekendHours = this.getContent("intDoorWeekendHours", environments);
//         try {
//             this.dayRange = environments.stream().filter(item -> "dayRange".equals(item.getName())).map(item -> Integer.parseInt(item.getContent())).findFirst().orElse(3);
//         } catch (Exception e) {
//             this.dayRange = 3;
//         }

//     }

//     public boolean checkLimit(Boolean isWeekend, TimetableEnum timetableEnum, Integer hour) {
//         if (isWeekend) {
//             return timetableEnum.equals(TimetableEnum.HL_INDOOR) ? this.intDoorWeekendHours.contains(hour) : this.outDoorWeekendHours.contains(hour);
//         } else {
//             return timetableEnum.equals(TimetableEnum.HL_INDOOR) ? this.inDoorWeekdayHours.contains(hour) : this.outDoorWeekdayHours.contains(hour);
//         }
//     }


//     private List<Integer> getContent(String name, List<Environment> environments){
//         String[] arr = environments.stream().filter(item -> name.equals(item.getName())).map(Environment::getContent).findFirst().orElse("").split(",");
//         return Arrays.stream(arr).filter(StringUtils::isNotBlank).map(Integer::parseInt).collect(Collectors.toList());

//     }

// }
