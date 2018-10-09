package com.ballistic.fserver.validation;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StatusValidator implements ConstraintValidator<StatusValid, String> {

    private static final Logger logger = LogManager.getLogger(StatusValidator.class);

    @Override
    public void initialize(StatusValid constraintAnnotation) { }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        Boolean result = true;
        logger.debug("validation-image-repository");
        if(StringUtils.isNotEmpty(value)) {
            if(!isSupportedStatusType(value)) {
                logger.error("status not valid " + value);
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Only \"Save\" or \"Delete\" Status Use").
                        addConstraintViolation();
                result = false;
            }
        }else {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Only \"Save\" or \"Delete\" Status Use").
                    addConstraintViolation();
            result = false;
        }
        logger.debug("Status Type Va");
        return result;

    }

    public static Boolean isSupportedStatusType(String statusType) {
        return statusType.equalsIgnoreCase("Delete") || statusType.equalsIgnoreCase("SAVE");
    }
}
