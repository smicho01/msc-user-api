package org.semicorp.msc.userapi.domain.friend;

import lombok.*;

import java.time.LocalDateTime;


/**
 * Stores friend requests
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Friends {
    private String user_id;
    private String friend_id;
    private String status;
    private LocalDateTime created_at;
}
