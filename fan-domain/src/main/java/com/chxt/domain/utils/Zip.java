package com.chxt.domain.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.LocalFileHeader;

@Data
@Slf4j

public class Zip {

    private Map<String, byte[]> fileMap;

	private boolean isWrongPassword;

    @SneakyThrows
    public Zip(byte[] data, String password) {
        fileMap = new HashMap<>();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        int readLen;
        byte[] readBuffer = new byte[4096];
        try (ZipInputStream zipInputStream = new ZipInputStream(bais, password.toCharArray())) {
            LocalFileHeader localFileHeader;
            while ((localFileHeader = zipInputStream.getNextEntry()) != null) {
				baos.reset();
                String fileName = localFileHeader.getFileName();
                while ((readLen = zipInputStream.read(readBuffer)) != -1) {
                    baos.write(readBuffer, 0, readLen);
                }
                fileMap.put(fileName, baos.toByteArray());
			}
	
        } catch(ZipException e) {
			if (e.getMessage().contains("Wrong password!")) {
				this.isWrongPassword = true;
			} else {
				throw e;
			}
		} 
    }


    public byte[] getOne() {
        return fileMap.values().iterator().next();
    }

    public byte[] getOne(String extension) {
        return fileMap.entrySet().stream().filter(data -> data.getKey().endsWith(extension)).findFirst().orElse(null).getValue();
    }

    public String getOneName(String extension) {
        return fileMap.entrySet().stream().filter(data -> data.getKey().endsWith(extension)).findFirst().orElse(null).getKey();
    }
}
