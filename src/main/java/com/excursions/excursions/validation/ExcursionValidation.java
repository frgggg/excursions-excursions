package com.excursions.excursions.validation;

import com.excursions.excursions.validation.impl.ExcursionValidationImpl;

import javax.validation.Constraint;
import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy= ExcursionValidationImpl.class)
public @interface ExcursionValidation {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends javax.validation.Payload>[] payload() default {};
}
