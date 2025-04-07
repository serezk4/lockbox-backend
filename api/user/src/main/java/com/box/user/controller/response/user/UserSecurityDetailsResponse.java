package com.box.user.controller.response.user;

import lombok.Value;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserSessionRepresentation;

import java.util.List;

@Value
public class UserSecurityDetailsResponse {
    List<UserSessionRepresentation> sessions;
    List<CredentialsDto> credentials;

    @Value
    public static class CredentialsDto {
        long createdDate;
        String type;
        String userLabel;

        public CredentialsDto(CredentialRepresentation credentialRepresentation) {
            this.createdDate = credentialRepresentation.getCreatedDate();
            this.type = credentialRepresentation.getType();
            this.userLabel = credentialRepresentation.getUserLabel();
        }
    }
}
