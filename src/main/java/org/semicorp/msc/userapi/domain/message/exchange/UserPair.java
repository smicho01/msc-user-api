package org.semicorp.msc.userapi.domain.message.exchange;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * User Pair is used to establish a pair of users having a conversation (exchanging Message[s])
 */
@Getter
@Setter
public class UserPair {
    private String user1;
    private String user2;

    public UserPair(String user1, String user2) {
        if (user1.compareTo(user2) < 0) {
            this.user1 = user1;
            this.user2 = user2;
        } else {
            this.user1 = user2;
            this.user2 = user1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPair userPair = (UserPair) o;
        return Objects.equals(user1, userPair.user1) && Objects.equals(user2, userPair.user2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user1, user2);
    }
}
