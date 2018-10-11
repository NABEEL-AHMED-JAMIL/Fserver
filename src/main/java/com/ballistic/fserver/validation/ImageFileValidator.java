package com.ballistic.fserver.validation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.ObjectUtils;
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
        logger.debug("validation-image-repository");
        // handle the null if the part-file present not null vlaue
        if(!ObjectUtils.isEmpty(multipartFile)) {
            if(!isSupportedContentType(multipartFile.getContentType())) {
                logger.error("file not a image" + multipartFile.getContentType());
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Only PNG or JPG image are allowed.").
                        addConstraintViolation();
                result = false;
            }
        }else {
            // possible error accrue due to get-Content-Type null
            logger.error("file object {}" + multipartFile.getContentType());
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("File object null pls enter PNG or JPG image.").
                    addConstraintViolation();
            result = false;
        }
        return result;
    }

    public static Boolean isSupportedContentType(String contentType) {
        return contentType.equals("image/png") || contentType.equals("image/jpg") || contentType.equals("image/jpeg");
    }

}
