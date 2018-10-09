package com.ballistic.fserver.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target( { ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { ImageFileValidator.class })
public @interface StatusValid {

    String message() default  "Status must \"Save\" or \"Delete\"";

    Class<?>[] groups() default  {};

    Class<? extends Payload>[] payload() default {};
}
