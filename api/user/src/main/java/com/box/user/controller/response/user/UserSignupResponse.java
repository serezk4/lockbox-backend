package com.box.user.controller.response.user;

import com.box.user.controller.dto.UserRepresentationDto;
import com.box.user.controller.request.user.UserSignupRequest;
import lombok.Value;

import java.io.Serializable;

/**
 * Registration response for {@link UserSignupRequest}
 *
 * @author serezk4
 * @version 1.0
 * @since 1.0
 */

@Value
public class UserSignupResponse implements Serializable {
    UserRepresentationDto user;
}
