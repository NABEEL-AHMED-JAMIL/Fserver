package com.ballistic.fserver.dto;

import com.ballistic.fserver.validation.ValidImage;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

// done test 100%
@JsonPropertyOrder({ "email", "password", "file" })
public class AccountBean {

    @ApiModelProperty(notes = "Enter Email", name = "email", value = "nabeel.amd93@gmail.com", required = true)
    @NotNull(message = "Email contain null")
    @NotBlank(message = "Email Contain blank")
    @Email(message = "Email wrong type")
    private String email;

    @ApiModelProperty(notes = "Enter Password", name = "password", value = "ballistic", required = true)
    @NotNull(message = "Password contain null")
    @NotBlank(message = "Password contain blank")
    private String password;

    @ApiModelProperty(notes = "Enter file", name = "file", value = "xyz", required = true)
    @NotNull(message = "File contain null")
    @ValidImage(message = "File support type => \"image/jpeg\", \"image/png\", \"image/jpg\"")
    private MultipartFile file;

    public AccountBean() { }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public MultipartFile getFile() { return file; }
    public void setFile(MultipartFile file) { this.file = file; }

    @Override
    public String toString() { return "AccountBean{" + "email='" + email + '\'' + ", password='" + password + '\'' + ", repository=" + file + '}'; }

}
