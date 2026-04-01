package com.chxt.web.video;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/video")
public class FileFixController {

    private static final String path = "/etc/download/xunlei/downloads";

    @GetMapping("/fix")
    public Map<String, File> fix() {
        Map<String, File> map = getFile(new File(path));
        for (Map.Entry<String, File> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue().getName());
        }
        return map;
    }

    private Map<String, File> getFile(File file) {
        File[] files = file.listFiles();
        Map<String, File> map = new HashMap<>();
        if (files == null) {
            return map;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                Map<String, File> subMap = getFile(f);
                if (MapUtils.isEmpty(subMap)) {
                    f.delete();
                } else {
                    map.putAll(subMap);
                }
            } else {
                // print size in MB, delete if size is less than 100MB
                if (f.length() / 1024 / 1024 < 100) {
                    f.delete();
                } else {
                    System.out.println(f.getName() + " " + f.length() / 1024 / 1024 + "MB");
                }
                map.put(f.getName(), f);
            }
        }
        return map;
    }

}