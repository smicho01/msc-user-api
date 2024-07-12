package org.semicorp.msc.userapi.domain.friend;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Friend {
    private String id;
    private String visibleUsername;
    private String college;
    private int rank;
}
