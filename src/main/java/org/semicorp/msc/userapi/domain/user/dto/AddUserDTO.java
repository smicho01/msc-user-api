package org.semicorp.msc.userapi.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AddUserDTO {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
}
