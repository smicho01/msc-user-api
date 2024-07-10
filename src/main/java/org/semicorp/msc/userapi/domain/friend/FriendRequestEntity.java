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
public class FriendRequestEntity {
    String id;
    String visibleUsername;
    String college;
    int rank;
}
