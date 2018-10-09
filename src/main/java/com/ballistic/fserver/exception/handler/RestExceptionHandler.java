package com.ballistic.fserver.exception.handler;

import com.ballistic.fserver.exception.EntityNotFoundException;
import com.ballistic.fserver.exception.FileStorageException;
import com.ballistic.fserver.exception.IllegalBeanFieldException;
import com.ballistic.fserver.exception.IllegalFileFormatException;
import com.ballistic.fserver.exception.bean.ApiError;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.*;


@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LogManager.getLogger(RestExceptionHandler.class);

    @Autowired
    private ModelMapper modelMapper;

    /**
     * @RequestParam(value = "name", required = true, defaultValue = "defaultName") == work
     * @RequestParam(value = "name", defaultValue = "defaultName") == work (default => required = true)
     * Handle MissingServletRequestParameterException. Triggered when a 'required' request parameter is missing.
     *
     * @param ex  MissingServletRequestParameterException
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
              HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = ex.getParameterName() + " parameter is missing";
        logger.error("{}", error);
        return this.buildResponseEntity(new ApiError(status.INTERNAL_SERVER_ERROR, error, ex));
    }

    /**
     * Handle HttpMediaTypeNotSupportedException. This one triggers when JSON is invalid as well.
     *
     * @param ex      HttpMediaTypeNotSupportedException
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
              HttpHeaders headers, HttpStatus status, WebRequest request) {
        StringBuilder error = new StringBuilder();
        error.append(ex.getContentType());
        error.append(" media type is not supported. Supported media types are \"image/jpeg\", \"image/png\", \"image/jpg\"");
        logger.error("{}", error);
        return this.buildResponseEntity(new ApiError(status.UNSUPPORTED_MEDIA_TYPE, error.toString() , ex));
    }


    /**
     * @RequestParam(value = "name", required = true, defaultValue = "defaultName") == work
     * @RequestParam(value = "name", defaultValue = "defaultName") == work (default => required = true)
     * Handle MissingServletRequestPartException. Triggered when a 'required' request parameter is missing.
     *
     * @param ex  MissingServletRequestPartException
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex,
              HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = ex.getRequestPartName() + " part is missing";
        logger.error("{}", error);
        return this.buildResponseEntity(new ApiError(status.BAD_REQUEST, error, ex));
     }


    /**
     * Handle MethodArgumentNotValidException. Triggered when an object fails @Valid validation.
     *
     * @param ex      the MethodArgumentNotValidException that is thrown when @Valid validation fails
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequesthandlerExceptionResolver
     * @return the ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
              HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiError apiError = new ApiError(status.BAD_REQUEST);
        apiError.setMessage("Validation error");
        apiError.addValidationErrors(ex.getBindingResult().getFieldErrors());
        apiError.addValidationError(ex.getBindingResult().getGlobalErrors());
        logger.error("{}", apiError);
        return buildResponseEntity(apiError);
    }


    /**
     * Handles EntityNotFoundException.
     * Created to encapsulate errors with more detail than javax.persistence.EntityNotFoundException.
     *
     * @param ex the EntityNotFoundException
     * @return the ApiError object
     */
    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
        apiError.setMessage(ex.getMessage());
        logger.error("{}", apiError);
        return buildResponseEntity(apiError);
    }


    /**
     * Handles NullPointerException.
     * Created to encapsulate errors with more detail than NullPointerException.
     * @param ex the NullPointerException
     * @return the ApiError object
     */
    @ExceptionHandler(NullPointerException.class)
    protected ResponseEntity<Object> handleEntityNotFound(NullPointerException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage(ex.getMessage());
        logger.error("{}" + apiError);
        return buildResponseEntity(apiError);
    }



    /**
     * Handles IllegalFileFormatException.
     * Help to show error related to repository format error
     *
     * @param ex the EntityNotFoundException
     * @return the ApiError object
     */
    @ExceptionHandler(IllegalFileFormatException.class)
    protected ResponseEntity<Object> illegalFileFormatException(IllegalFileFormatException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage("Format error");
        apiError.setDebugMessage(ex.getMessage());
        logger.error("{}", apiError);
        return buildResponseEntity(apiError);
    }

    /**
     * Handles IllegalBeanFieldException.
     * Help to show error related to IllegalBean Field format error
     * use the costume parse method for parse the error..
     * to understand this method plz check the method call in AppRestController
     *
     * @param ex the EntityNotFoundException
     * @return the ApiError object
     */
    @ExceptionHandler(IllegalBeanFieldException.class)
    protected ResponseEntity<Object> illegalBeanFieldException(IllegalBeanFieldException ex) throws IOException {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage("Validation error");
        String error = ex.getMessage();
        Integer i = 0;
        while (true) {
            String key = "key-"+i;
            if(error.contains(key)) {
                String value = StringUtils.substring(error, error.indexOf(key)); // key-i...to whole string
                value = value.substring(key.length()+1, value.indexOf("}")+1); // key-i={value}
                if(value.charAt(0) == '{' && value.charAt(value.length()-1) == '}') {
                    value = value.substring(1, value.length() - 1); //remove curly brackets {value} => value
                    String[] keyValuePairs = value.split(","); //split the string to creat key-value pairs
                    String objectName = null;
                    String field = null;
                    String reject = null;
                    String message = null;
                    for(String pair : keyValuePairs) {     //iterate over the pairs
                        String[] subPair = pair.split("=");      //split the pairs to get key and value
                        if(subPair[0].trim().equals("objectName")) {
                            objectName = subPair[1].trim();
                        } else if (subPair[0].trim().equals("field")) {
                            field = subPair[1].trim();
                        } else if (subPair[0].trim().equals("reject")) {
                            // reject may be reject=, so check need here also
                            reject = subPair.length > 1 ? subPair[1].trim(): "";
                        } else if (subPair[0].trim().equals("message")) {
                            message = subPair[1].trim();
                        }
                    }
                    apiError.addValidationError(objectName, field, reject, message);
                }
            }else {
                break;
            }
            i++;
        }
        logger.error("{}", apiError);
        return buildResponseEntity(apiError);
    }


    /**
     * Handles FileStorageException.
     * Help to show error related to repository format error
     *
     * @param ex the EntityNotFoundException
     * @return the ApiError object
     */
    @ExceptionHandler(FileStorageException.class)
    protected ResponseEntity<Object> handleEntityNotFound(FileStorageException ex) {
        // convert ex-message into map
        String error = ex.getMessage();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if(error.charAt(0) == '{' && error.charAt(error.length()-1) == '}') {
            error = error.substring(1, error.length()-1); //remove curly brackets
            String[] keyValuePairs = error.split(","); //split the string to creat key-value pairs

            Map<String,Object> map = new HashMap<>();
            for(String pair : keyValuePairs) {     //iterate over the pairs
                String[] entry = pair.split("=");      //split the pairs to get key and value
                map.put(entry[0].trim(), entry[1].trim());   //add them to the hashmap and trim whitespaces
            }
            error = String.valueOf(map.get("message"));
            if(map.get("status").equals("500")) { status = HttpStatus.INTERNAL_SERVER_ERROR; }
        }
        ApiError apiError = new ApiError(status);
        apiError.setMessage(error);
        if(StringUtils.isNotEmpty(ex.getLocalizedMessage())) { apiError.setDebugMessage(ex.getLocalizedMessage()); }
        logger.error("{}", apiError);
        return buildResponseEntity(apiError);
    }


    /**
     * Handle HttpMessageNotReadableException. Happens when request JSON is malformed.
     *
     * @param ex      HttpMessageNotReadableException
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
              HttpHeaders headers, HttpStatus status, WebRequest request) {
        ServletWebRequest servletWebRequest = (ServletWebRequest) request;
        String error = "Malformed JSON request";
        logger.error("{} to {} error {}", servletWebRequest.getHttpMethod(),
                servletWebRequest.getRequest().getServletPath(), error);
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
    }

    /**
     * Handle HttpMessageNotWritableException.
     *
     * @param ex      HttpMessageNotWritableException
     * @l8param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex,
              HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = "Error writing JSON output";
        logger.error("{}", error);
        return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, error, ex));
    }

    /**
     * Handle NoHandlerFoundException.
     *
     * @param ex
     * @param headers
     * @param status
     * @param request
     * @return
     */
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiError apiError = new ApiError(status.BAD_REQUEST);
        apiError.setMessage(String.format("Could not find the %s method for URL %s", ex.getHttpMethod(), ex.getRequestURL()));
        apiError.setDebugMessage(ex.getMessage());
        logger.error("{}", apiError);
        return buildResponseEntity(apiError);
    }


    /**
     * Handle DataIntegrityViolationException, inspects the cause for different DB causes.
     *
     * @param ex the DataIntegrityViolationException
     * @return the ApiError object
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        if (ex.getCause() instanceof ConstraintViolationException) {
            return buildResponseEntity(new ApiError(HttpStatus.CONFLICT, "Database error", ex.getCause()));
        }
        return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex));
    }

    /**
     * Handle Exception, handle generic Exception.class
     *
     * @param ex the Exception
     * @return the ApiError object
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage(String.format("The parameter '%s' of value '%s' could not be converted to type '%s'",
                ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName()));
        apiError.setDebugMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    /**
     * Common resposne builder for Entity
     *
     * @param apiError ApiError
     * @return the ApiError object
     * */
    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    /**
    public static void main(String args[]) {

        String error = "{key-1={field=password, reject=, objectName=accountBean, message=Password contain blank}" +
                "key-0={field=email, reject=, objectName=accountBean, message=Email Contain blank}," +
                "key-3={field=file, reject=null, objectName=accountBean, message=File object null pls enter PNG or JPG image.}," +
                "key-2={field=file, reject=null, objectName=accountBean, message=File contain null}" +
                "}";

        Integer i = 0;
        while (true) {
            String key = "key-"+i;
            if(error.contains(key)) {
                String value = StringUtils.substring(error, error.indexOf(key)); // key-i...to whole string
                value = value.substring(key.length()+1, value.indexOf("}")+1); // key-i={value}
                if(value.charAt(0) == '{' && value.charAt(value.length()-1) == '}') {
                    value = value.substring(1, value.length() - 1); //remove curly brackets {value} => value
                    System.out.println(value);
                    String[] keyValuePairs = value.split(","); //split the string to creat key-value pairs
                    String objectName = null;
                    String field = null;
                    String reject = null;
                    String message = null;
                    for(String pair : keyValuePairs) {     //iterate over the pairs
                        String[] subPair = pair.split("=");      //split the pairs to get key and value
                        if(subPair[0].trim().equals("objectName")) {
                            objectName = subPair[1].trim();
                            System.out.println(objectName);
                        } else if (subPair[0].trim().equals("field")) {
                            field = subPair[1].trim();
                            System.out.println(field);
                        } else if (subPair[0].trim().equals("reject")) {
                            // reject may be reject=, so check need here also
                            reject = subPair.length > 1 ? subPair[1].trim(): "";
                            System.out.println(reject);
                        } else if (subPair[0].trim().equals("message")) {
                            message = subPair[1].trim();
                            System.out.println(message);
                        }
                    }
                   // apiError.addValidationError(objectName, field, reject, message);
                }
            }else {
                break;
            }
            i++;
        }
    } */
}
