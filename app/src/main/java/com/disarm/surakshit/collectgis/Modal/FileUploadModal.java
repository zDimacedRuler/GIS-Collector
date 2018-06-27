package com.disarm.surakshit.collectgis.Modal;

/**
 * Created by AmanKumar on 6/26/2018.
 */

public class FileUploadModal {
    private String name;
    private String fileName;

    public FileUploadModal() {
    }

    public FileUploadModal(String name, String fileName) {
        this.name = name;
        this.fileName = fileName;
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
