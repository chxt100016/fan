package com.chxt.domain.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class Excel {

    private final byte[] data;

    private ExcelTypeEnum excelType;

    private final String marker;

    private List<Map<String, String>> dataMap;


    public Excel(byte[] data, String marker) {
        this.data = data;
        this.marker = marker;
    }

    public Excel xlsx() {
        this.excelType = ExcelTypeEnum.XLSX;
        return this;
    }

    /**
     * 解析CSV字节数组
     * 从包含marker的行开始解析，其后第一行为表头，再往后为数据
     * @return 解析后的数据列表
     */
    public List<Map<String, String>> parseBytes() {
        if (dataMap != null) {
            return dataMap;
        }
        dataMap = new ArrayList<>();

        
        try {
            // 检测字节数组的编码
            String encoding = "UTF-8";
            if (isValidUTF8(data)) {
                encoding = "UTF-8";
            } else {
                encoding = "GBK";
            }
     
            // 使用EasyExcel解析CSV内容
            EasyExcel.read(new ByteArrayInputStream(data))
                .excelType(this.excelType != null ? this.excelType : ExcelTypeEnum.CSV)
                .charset(Charset.forName(encoding))
                .sheet()
                .registerReadListener(new AnalysisEventListener<Map<Integer, String>>() {
                    private boolean foundMarker = false;
                    private boolean headerRow = false;
                    private final Map<Integer, String> headers = new HashMap<>();
                    
                    @Override
                    public void invoke(Map<Integer, String> data, AnalysisContext context) {
                        // 检查是否找到标记行
                        if (!foundMarker) {
                            for (String value : data.values()) {
                                if (value != null && value.contains(marker)) {
                                    foundMarker = true;
                                    return;
                                }
                            }
                        } 
                        // 找到标记行后的第一行是表头
                        else if (!headerRow) {
                            headerRow = true;
                            // 保存表头信息
                            headers.putAll(data);
                        } 
                        // 表头之后的行是数据
                        else {
                            // 将数据与表头对应
                            Map<String, String> row = new HashMap<>();
                            for (Map.Entry<Integer, String> entry : data.entrySet()) {
                                String headerName = headers.get(entry.getKey());
                                if (headerName != null && !headerName.isEmpty()) {
                                    row.put(headerName, entry.getValue());
                                }
                            }
                            dataMap.add(row);
                        }
                    }
                    
                    @Override
                    public void doAfterAllAnalysed(AnalysisContext context) {
                        log.debug("CSV字节数组解析完成，共解析到{}条记录", dataMap.size());
                    }
                })
                .doRead();
            
            return dataMap;
        } catch (Exception e) {
            log.error("解析CSV字节数组失败", e);
            return new ArrayList<>();
        }
    }

    @SneakyThrows
    public void download(String path) {
        try {
            Files.write(Paths.get(path), data);
        } catch (IOException e) {
            log.error("下载文件失败", e);
        }
    }

    /**
     * 检验字节数组是否为有效的UTF-8编码
     */
    private static boolean isValidUTF8(byte[] input) {
        int i = 0;
        // 检查每个字节
        while (i < input.length) {
            int b = input[i] & 0xFF;
            if (b <= 0x7F) {
                // 单字节字符
                i += 1;
            } else if (b >= 0xC0 && b <= 0xDF) {
                // 双字节字符
                if (i + 1 >= input.length || (input[i + 1] & 0xC0) != 0x80) {
                    return false;
                }
                i += 2;
            } else if (b >= 0xE0 && b <= 0xEF) {
                // 三字节字符
                if (i + 2 >= input.length || (input[i + 1] & 0xC0) != 0x80 || (input[i + 2] & 0xC0) != 0x80) {
                    return false;
                }
                i += 3;
            } else if (b >= 0xF0 && b <= 0xF7) {
                // 四字节字符
                if (i + 3 >= input.length || (input[i + 1] & 0xC0) != 0x80 || 
                    (input[i + 2] & 0xC0) != 0x80 || (input[i + 3] & 0xC0) != 0x80) {
                    return false;
                }
                i += 4;
            } else {
                return false;
            }
        }
        return true;
    }
   

}
