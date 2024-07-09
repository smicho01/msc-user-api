package org.semicorp.msc.userapi.domain.user.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class UserDTO  {
    private String id;
    private String username;
    private String visibleUsername;
    private String firstName;
    private String lastName;
    private String email;
    private String college;
    private String collegeid;
    private Boolean active;
    private LocalDateTime datecreated = LocalDateTime.now();
    private LocalDateTime dateupdated = LocalDateTime.now();
    private int tokens;
    private int rank;

}
