package org.semicorp.msc.userapi.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddUserDTO {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String college;
    private String collegeid;
    private int rank;
}
