package com.chxt.fantasticmonkey.controller.file;


import com.chxt.fantasticmonkey.model.file.FileInfo;
import com.chxt.fantasticmonkey.model.file.FileInfoParam;
import com.chxt.fantasticmonkey.infrastructure.fileServer.FileClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/file/sys")
public class FileSystemController {

    @Resource
    private FileClient client;

    @RequestMapping("/ls")
    public List<FileInfo> ls(String path) {
        return this.client.ls(path);
    }

    @RequestMapping("/getFile")
    public List<FileInfo> getFile(@RequestBody FileInfoParam param) {
        return this.client.getFile(param);
    }

    @RequestMapping("/getSize")
    public String getSize(String path) {
        return this.client.getSize(path);
    }

    @RequestMapping("/delete")
    public boolean delete(String path) throws IOException {
        return this.client.delete(path);
    }

    @RequestMapping("/rename")
     public boolean rename(String path, String name) {
        return this.client.rename(path, name);
    }

    @RequestMapping("/move")
    public boolean move(String newPath, String oldPath) throws IOException {
        return this.client.move(newPath, oldPath);
    }

    @RequestMapping("/addFolder")
    public boolean addFolder(String path){
        return this.client.addFolder(path);
    }

    @RequestMapping("/downloadFile")
    public void downloadFile(String path, HttpServletResponse response) throws IOException {
        this.client.downloadFile(path, response);
    }

    @RequestMapping("/upload")
    public void upload(@RequestParam("path") String path, @RequestParam("file") MultipartFile file) throws IOException {
        this.client.upload(path, file);

    }
}
