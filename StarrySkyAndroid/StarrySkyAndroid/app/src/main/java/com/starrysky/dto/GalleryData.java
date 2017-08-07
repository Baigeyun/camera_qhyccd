package com.starrysky.dto;

public class GalleryData {
    public static final Integer TYPE_IMAGE = 1;
    public static final Integer TYPE_VIDEO = 2;

    private Integer type;
    private String sourceFilePath;
    private byte[] previewImageBytes;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getSourceFilePath() {
        return sourceFilePath;
    }

    public void setSourceFilePath(String sourceFilePath) {
        this.sourceFilePath = sourceFilePath;
    }

    public byte[] getPreviewImageBytes() {
        return previewImageBytes;
    }

    public void setPreviewImageBytes(byte[] previewImageBytes) {
        this.previewImageBytes = previewImageBytes;
    }
}
