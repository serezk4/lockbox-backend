package com.box.user.controller.dto;

import lombok.Value;
import org.keycloak.representations.idm.UserRepresentation;

/**
 * Dto for {@link UserRepresentation}
 *
 * @author serezk4
 * @version 1.0
 * @since 1.0
 */
@Value
public class UserRepresentationDto {
    String id;
    String username;
    String email;
    String firstName;
    String lastName;
    Boolean emailVerified;
    Boolean enabled;

//    String self;
//    Long createdTimestamp;
//    Boolean enabled;
//    String federationLink;
//    String serviceAccountClientId;
//    Set<String> disableableCredentialTypes;
//    List<String> requiredActions;
//    List<String> realmRoles;
//    Map<String, List<String>> clientRoles;
//    Integer notBefore;
//    List<String> groups;
//    Map<String, Boolean> access;
//    String id;
//    String username;
//    String firstName;
//    String lastName;
//    String email;
//    Boolean emailVerified;
//    Map<String, List<String>> attributes;
//    UserProfileMetadata userProfileMetadata;
}
