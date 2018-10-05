package com.ballistic.fserver.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


//@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.CUSTOM, property = "error", visible = true)
//@JsonTypeIdResolver(ApiError.LowerCaseClassNameResolver.class)
public class ApiError {

    @ApiModelProperty(notes = "API-Error status message.")
    private HttpStatus status;
    @ApiModelProperty(notes = "API-Error timestamp message.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    @ApiModelProperty(notes = "API-Error message.")
    private String message;
    @ApiModelProperty(notes = "API-Error debug message.")
    private String debugMessage;
    @ApiModelProperty(notes = "API-Error sub-errors messages.")
    private List<ApiSubError> subErrors;

    private ApiError() {
        this.setTimestamp(LocalDateTime.now());
    }

    public ApiError(HttpStatus status) {
        this(); // call super constructor
        this.setStatus(status);
    }

    public ApiError(HttpStatus status, Throwable ex) {
        this();
        this.setStatus(status);
        this.setMessage(message);
        this.setDebugMessage(ex.getLocalizedMessage());
    }

    public ApiError(HttpStatus status, String message, Throwable ex) {
        this();
        this.setStatus(status);
        this.setMessage(message);
        this.setDebugMessage(ex.getLocalizedMessage());
    }

    private void addSubError(ApiSubError subError) {
        if(this.getSubErrors().size() < 0 || this.getSubErrors() == null) {
            this.setSubErrors(new ArrayList<>());
        }
        this.getSubErrors().add(subError);
    }

    public HttpStatus getStatus() { return status; }
    public void setStatus(HttpStatus status) { this.status = status; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getDebugMessage() { return debugMessage; }
    public void setDebugMessage(String debugMessage) { this.debugMessage = debugMessage; }

    public List<ApiSubError> getSubErrors() { return subErrors; }
    public void setSubErrors(List<ApiSubError> subErrors) { this.subErrors = subErrors; }

    /**
     * Note:- handle this validation error for object field wrong related
     * */
    // # 3
    private void addValidationError(String object, String field, Object rejectedValue, String message) {
        this.addSubError(new ApiValidationError(object, field, rejectedValue, message));
    }
    // # 2
    private void addValidationError(FieldError fieldError) {
        this.addValidationError(fieldError.getObjectName(), fieldError.getField(), fieldError.getRejectedValue(),
                fieldError.getDefaultMessage());
    }
    // # 1
    public void addValidationErrors(List<FieldError> fieldErrors) { fieldErrors.forEach(this::addValidationError); }

    /**
     * Note:- handle this validation error for object name wrong related
     * */
    // # 3
    private void addValidationError(String object , String message) {
        this.addSubError(new ApiValidationError(object, message));
    }
    // # 2
    private void addValidationError(ObjectError objectError) {
        this.addValidationError(objectError.getObjectName(), objectError.getDefaultMessage());
    }
    // # 1
    public void addValidationError(List<ObjectError> globalErrors) { globalErrors.forEach(this::addValidationError); }

    public abstract class ApiSubError {}

    public class ApiValidationError extends ApiSubError {

        private String object; // class
        private String field; // class-field
        private Object rejectedValue; // class-field-value
        private String message; // reject by resone message

        public ApiValidationError() { }

        public ApiValidationError(String object, String message) {
            this.setObject(object);
            this.setMessage(message);
        }

        public ApiValidationError(String object, String field, Object rejectedValue, String message) {
            this.setObject(object);
            this.setField(field);
            this.setRejectedValue(rejectedValue);
            this.setMessage(message);
        }

        public String getObject() { return object; }
        public void setObject(String object) { this.object = object; }

        public String getField() { return field; }
        public void setField(String field) { this.field = field; }

        public Object getRejectedValue() { return rejectedValue; }
        public void setRejectedValue(Object rejectedValue) { this.rejectedValue = rejectedValue; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ApiValidationError that = (ApiValidationError) o;

            if (!object.equals(that.object)) return false;
            if (!field.equals(that.field)) return false;
            if (!rejectedValue.equals(that.rejectedValue)) return false;
            return message.equals(that.message);
        }

        @Override
        public int hashCode() {
            int result = object.hashCode();
            result = 31 * result + field.hashCode();
            result = 31 * result + rejectedValue.hashCode();
            result = 31 * result + message.hashCode();
            return result;
        }
    }
    /**
     * Note:- this check the name of the class is lower or not
     * */
//    public class LowerCaseClassNameResolver extends TypeIdResolverBase {
//
//        @Override
//        public String idFromValue(Object o) { return o.getClass().getSimpleName().toString(); }
//
//        @Override
//        public String idFromValueAndType(Object o, Class<?> aClass) { return idFromValue(o); }
//
//        @Override
//        public JsonTypeInfo.Id getMechanism() { return JsonTypeInfo.Id.CUSTOM; }
//    }

    @Override
    public String toString() {
        return "ApiError{" + "status=" + status + ", timestamp=" + timestamp + ", message='" +
                message + '\'' + ", debugMessage='" + debugMessage + '\'' + ", subErrors=" + subErrors + '}';
    }

}
