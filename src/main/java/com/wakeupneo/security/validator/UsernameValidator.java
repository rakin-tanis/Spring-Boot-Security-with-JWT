package com.wakeupneo.security.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<Username, String> {

    @Override
    public void initialize(Username contactNumber) {
    }

    @Override
    public boolean isValid(String usernameField, ConstraintValidatorContext cxt) {
        if (usernameField == null) return true;
        return usernameField.matches("^[a-zA-Z]+([a-zA-Z0-9_.]+)*$");
    }

}
