package com.tl_connect.dev.core.common.ultility;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import com.tl_connect.dev.core.common.dto.UploadResult;


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

    public boolean isXLSX(MultipartFile file) throws IOException {
        byte[] header = new byte[4];
        try (InputStream is = file.getInputStream()) {
            if (is.read(header) != 4) return false;
        }

        return  header[0] == 0x50 &&
                header[1] == 0x4B &&
                header[2] == 0x03 &&
                header[3] == 0x04;
    }

    public boolean isCSV(MultipartFile file) throws IOException {
        byte[] bom = new byte[3];
        try (InputStream is = file.getInputStream()) {
            if (is.read(bom) < 3) return false;
            if (bom[0] == 0x50 && bom[1] == 0x4B) return false;
        }
        return true;
    }

    public abstract UploadResult uploadFile(MultipartFile file) throws IOException;

    public abstract void deleteFile(String key);
    
}
