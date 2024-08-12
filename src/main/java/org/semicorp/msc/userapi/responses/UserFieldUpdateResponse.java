package org.semicorp.msc.userapi.responses;

import lombok.*;

import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserFieldUpdateResponse {
    private String userId;
    private String fieldName;
    private Object value;
    private Boolean updateStatus;
}
