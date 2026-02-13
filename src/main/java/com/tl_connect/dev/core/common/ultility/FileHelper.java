package com.tl_connect.dev.core.common.ultility;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;


public abstract class FileHelper {

    public FileHelper() {
        
    }

    public boolean isPDF(MultipartFile file) throws IOException {
        byte[] header = new byte[4];
        try(InputStream is = file.getInputStream()){
            if(is.read(header) != 4) return false;
            
        }

        return  header[0] == 0x25 &&
                header[1] == 0x50 &&
                header[2] == 0x44 &&
                header[3] == 0x46;
    }

    public abstract String uploadFile(MultipartFile file) throws IOException;

    
}
