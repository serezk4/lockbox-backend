package com.box.user.controller.request.user;

import com.box.user.controller.request.validation.PasswordMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;

/**
 * A request object representing user registration data.
 * <p>
 * This class is used to encapsulate the user's input data when registering a new account in the system.
 * The class ensures that all necessary fields, such as username, password, repeated password, and email,
 * are provided and valid.
 * </p>
 * <p>
 * The {@link PasswordMatch} annotation ensures that the {@code password} and {@code password_repeat} fields match.
 * Field-level validation is performed using Jakarta Bean Validation annotations to enforce specific constraints
 * on the fields, such as non-blank, size limits, and format requirements for email and username.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Value
@PasswordMatch
public class UserSignupRequest implements Serializable {

    /**
     * The password of the user.
     * <p>
     * This field must not be blank and must be between 4 and 50 characters in length.
     * </p>
     */
    @NotBlank(message = "password.empty")
    @Size(min = 4, max = 50, message = "password.size")
    String password;

    /**
     * The repeated password, used to verify that the passwords match.
     * <p>
     * This field must not be blank and must be between 4 and 50 characters in length. It should match
     * the {@code password} field, which is enforced by the {@link PasswordMatch} annotation.
     * </p>
     */
    @NotBlank(message = "password_repeat.empty")
    @Size(min = 4, max = 50, message = "password_repeat.size")
    String passwordRepeat;

    /**
     * The email address of the user.
     * <p>
     * This field must not be blank, must be a valid email address, and must not exceed 255 characters in length.
     * </p>
     */
    @NotBlank(message = "mail.empty")
    @Email(message = "mail.invalid")
    @Size(max = 255, message = "mail.size")
    String mail;
}
