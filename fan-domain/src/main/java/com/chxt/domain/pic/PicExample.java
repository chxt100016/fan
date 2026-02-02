package com.chxt.domain.pic;

import java.util.ArrayList;

import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import lombok.SneakyThrows;

public class PicExample {

    @SneakyThrows
    public static void main(String[] args) {
        
        
        List<TimeCell> sampleData = createSampleData();
        // 日 单元格
        byte[] imageBytes = new DayTable().getByte(sampleData);

        // 时间 单元格
        // byte[] imageBytes = new TimeTable().getByte(sampleData);


        
        
        showPic(imageBytes);
    
    }


    @SneakyThrows
    private static void showPic(byte[] data) {
        JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new JLabel(new ImageIcon(data)));
            frame.pack();
            frame.setVisible(true);
    }


    private static List<TimeCell> createSampleData() {
        List<TimeCell> dataList = new ArrayList<>();
        
        // 周一 9时 会议
        dataList.add(new TimeCell("2024-11-10 09:00", TimetableEnum.HL_IN));
        dataList.add(new TimeCell("2024-11-10 11:00", TimetableEnum.HL_IN));
        dataList.add(new TimeCell("2024-11-10 14:00", TimetableEnum.HL_IN));
        
        // 周二 14时 培训
        dataList.add(new TimeCell("2024-11-11 14:00", TimetableEnum.HL_IN));
        
        // 周三 10时 开发
        dataList.add(new TimeCell("2024-11-12 10:00", TimetableEnum.HL_OUT));
        dataList.add(new TimeCell("2024-11-12 16:00", TimetableEnum.HL_OUT));
        
        // 周四 15时 测试
        dataList.add(new TimeCell("2024-11-13 15:00", TimetableEnum.HL_OUT));
        
        // 周五 11时 部署
        dataList.add(new TimeCell("2024-11-14 11:00", TimetableEnum.HL_IN));
        
        // 周六 9时 休息
        dataList.add(new TimeCell("2024-11-15 09:00", TimetableEnum.HL_OUT));
        
        // 周日 16时 复盘
        dataList.add(new TimeCell("2024-11-16 16:00", TimetableEnum.HL_IN));
        
        return dataList;
    }

}
