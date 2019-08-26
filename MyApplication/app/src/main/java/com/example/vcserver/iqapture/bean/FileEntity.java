package com.example.vcserver.iqapture.bean;

import java.io.Serializable;

/**
 * Created by VCServer on 2018/5/24.
 */

public class FileEntity implements Serializable{
    public enum Type{
        FLODER,FILE
    }
    private String filePath;
    private String fileName;
    private String fileSize;
    private Type fileType;
    private boolean isChecked;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public Type getFileType() {
        return fileType;
    }

    public void setFileType(Type fileType) {
        this.fileType = fileType;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
