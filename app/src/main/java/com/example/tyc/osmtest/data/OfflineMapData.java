package com.example.tyc.osmtest.data;

/**
 * Created by biji on 2018/7/27.
 */

public class OfflineMapData {
    private String name;
    private int mapType;
    private String filePath;
    private String fileSize;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMapType() {
        return mapType;
    }

    public void setMapType(int   mapType) {
        this.mapType = mapType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }
}
