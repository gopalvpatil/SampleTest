package com.westernalliancebancorp.positivepay.web.validator;

import com.westernalliancebancorp.positivepay.dto.UserDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * UserValidator is
 *
 * @author Giridhar Duggirala
 */

@Component
public class UserValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return UserDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "userName", "username.empty");
        ValidationUtils.rejectIfEmpty(errors, "email", "emailAddress.empty");
        ValidationUtils.rejectIfEmpty(errors, "firstName", "firstName.empty");
        ValidationUtils.rejectIfEmpty(errors, "lastName", "lastName.empty");
        ValidationUtils.rejectIfEmpty(errors, "accountNumber", "accountNumber.empty");
    }
}
