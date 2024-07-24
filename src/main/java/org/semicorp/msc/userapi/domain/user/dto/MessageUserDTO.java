package org.semicorp.msc.userapi.domain.user.dto;


import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class MessageUserDTO {

    private String id;
    private String username;
    private String visibleUsername;
    private String college;
    private int imageid;

}
