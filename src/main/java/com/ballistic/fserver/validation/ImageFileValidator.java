package com.ballistic.fserver.validation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ImageFileValidator implements ConstraintValidator<ValidImage, MultipartFile> {

    private static final Logger logger = LogManager.getLogger(ImageFileValidator.class);

    @Override
    public void initialize(ValidImage constraintAnnotation) { }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
        Boolean result = true;
        logger.info("validation-image-repository");
        if(multipartFile != null) {
            String contentType = multipartFile.getContentType();
            if(!isSupportedContentType(contentType)) {
                logger.error("file not a image" + multipartFile.getContentType());
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Only PNG or JPG image are allowed.").
                        addConstraintViolation();
                result = false;
            }
        }else {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("File object null pls enter PNG or JPG image.").
                    addConstraintViolation();
            result = false;
        }
        logger.info("file valid image");
        return result;
    }

    public static Boolean isSupportedContentType(String contentType) {
        return contentType.equals("image/png") || contentType.equals("image/jpg") || contentType.equals("image/jpeg");
    }

}
