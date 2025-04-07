package com.box.user.controller.request.validation;

import com.box.user.controller.request.user.UserSignupRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, UserSignupRequest> {
    @Override
    public boolean isValid(UserSignupRequest request, ConstraintValidatorContext context) {
        return request.getPassword().equals(request.getPasswordRepeat());
    }
}
