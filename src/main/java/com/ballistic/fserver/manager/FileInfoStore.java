package com.ballistic.fserver.manager;

/**
 * Note:- This info give us store detail
 * */
public class FileInfoStore {

    private String fileName; // repository-Name
    private String fileUrl; // proper path
    private Long size; // size-check (for-qr size =>?, for-user-image=> what)
    private String width;
    private String height;
    private String contentType; // jpg,png

    public FileInfoStore() { }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public Long getSize() { return size; }
    public void setSize(Long size) { this.size = size; }

    public String getWidth() { return width; }
    public void setWidth(String width) { this.width = width; }

    public String getHeight() { return height; }
    public void setHeight(String height) { this.height = height; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    @Override
    public String toString() {
        return "FileInfo{" +
                "fileName='" + fileName + '\'' + ", fileUrl='" + fileUrl + '\'' +
                ", size=" + size + ", width='" + width + '\'' +
                ", height='" + height + '\'' + ", contentType='" + contentType + '\'' + '}';
    }
}
