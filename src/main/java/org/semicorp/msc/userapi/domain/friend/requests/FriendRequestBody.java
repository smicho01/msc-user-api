package org.semicorp.msc.userapi.domain.friend.requests;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class FriendRequestBody {
    private String requestingUserId;
    private String requestedUserId;
}
