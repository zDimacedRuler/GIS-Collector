package com.disarm.surakshit.collectgis.Model;

/**
 * Created by AmanKumar on 6/26/2018.
 */

public class FileUploadModel {
    private String name;
    private String fileName;
    private Boolean isImagePresent;

    public FileUploadModel() {
    }

    public FileUploadModel(String name, String fileName, Boolean isPresent) {
        this.name = name;
        this.fileName = fileName;
        isImagePresent = isPresent;
    }

    public Boolean getImagePresent() {
        return isImagePresent;
    }

    public void setImagePresent(Boolean imagePresent) {
        isImagePresent = imagePresent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}


