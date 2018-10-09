package com.ballistic.fserver.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "account")
public class Account {

    @Id
    private String id;
    private String email;
    private String password;
    private FileInfo fileInfo;
    private String status = "save";

    public Account() { }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public FileInfo getFileInfo() { return fileInfo; }
    public void setFileInfo(FileInfo fileInfo) { this.fileInfo = fileInfo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' + ", email='" + email + '\'' +
                ", password='" + password + '\'' + ", fileInfo=" + fileInfo + '}';
    }
}
