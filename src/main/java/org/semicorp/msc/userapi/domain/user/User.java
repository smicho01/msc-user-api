package org.semicorp.msc.userapi.domain.user;


import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class User implements Serializable {
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
    private String pubKey;
    private String privKey;
    private int tokens;
    private int rank;
    private int imageid;

}
