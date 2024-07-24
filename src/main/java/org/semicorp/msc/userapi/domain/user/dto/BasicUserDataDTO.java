package org.semicorp.msc.userapi.domain.user.dto;


import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BasicUserDataDTO {

    private String id;
    private String username;
    private String visibleUsername;
    private String firstName;
    private String lastName;
    private String email;
    private String college;
    private String collegeid;
    private Boolean active;
    private String pubKey;
    private int tokens;
    private int rank;
    private int imageid;
}
