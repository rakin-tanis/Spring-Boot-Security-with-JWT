package com.wakeupneo.security.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Documented
@Constraint(validatedBy = UsernameValidator.class)
@Target({METHOD, FIELD, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Username {
    String message() default "Invalid username. A username has to start with a letter, and can only include alphanumeric chars, underscore and dot.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
