package com.ballistic.fserver.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "fileInfo")
public class FileInfo {

    @Id
    private String id;
    private String fileName; // file-Name
    private String fileUrl; // proper path
    private Long size; // size-check (for-qr size =>?, for-user-image=> what)
    private String width;
    private String height;
    private String contentType; // jpg,png
    private String status = "Save";

    public FileInfo() { }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "FileInfo{" +
                "fileName='" + fileName + '\'' + ", fileUrl='" + fileUrl + '\'' +
                ", size=" + size + ", width='" + width + '\'' + ", height='" + height + '\'' +
                ", contentType='" + contentType + '\'' +
                '}';
    }
}
