package org.semicorp.msc.userapi.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;
import org.semicorp.msc.userapi.domain.friend.Friend;
import org.semicorp.msc.userapi.domain.friend.FriendRequestEntity;
import org.semicorp.msc.userapi.domain.friend.Friends;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FriendsService {

    private final Jdbi jdbi;

    public List<Friend> getAllUserFriends(String userId) {
        List<Friend> response = new ArrayList<>();
        String sql = "SELECT u.id, u.visibleusername, u.college, u.rank " +
                "FROM users.friends f " +
                "JOIN users.user u ON (f.friend_id = u.id) " +
                "WHERE f.user_id = :userId AND f.status = 'accepted' " +
                "AND f.friend_id != :userId;";

        response = jdbi.withHandle(handle -> handle.createQuery(sql)
                .bind("userId", userId)
                .mapToBean(Friend.class)
                .stream().toList());

        return response;
    }

    public Boolean sendFriendRequest(String requestingUserId, String requestedUserId) {
        try {
            String sql = "INSERT INTO users.friends (user_id, friend_id, status) " +
                    " VALUES (?, ?, 'pending'), " +
                    " (?, ? , 'requested');";
            jdbi.withHandle(handle ->  handle.execute(sql, requestingUserId, requestedUserId, requestedUserId, requestingUserId));
            log.info("Friend request sent. From {} , to: {}",requestingUserId, requestedUserId);
            return true;
        } catch (Exception e) {
            log.error("Error while sending friend request From {} , to: {}  , ERROR: {}", requestingUserId,
                    requestedUserId, e.getMessage());
        }
        return false;
    }

    /**
     *
     * @param userId
     * @param type 'requested' - for received request ; 'pending' - for sent requests
     * @return
     */
    public List<FriendRequestEntity> getFriendRequestsForUserId(String userId, String type) {

        String sql = "SELECT u.id, u.visibleusername, u.college, u.rank " +
                "from users.friends f " +
                "JOIN users.user u ON (f.friend_id = u.id) " +
                "AND f.status = :type " +
                "AND f.user_id = :userId;";

        List<FriendRequestEntity> friedRequests = jdbi.withHandle(handle -> handle.createQuery(sql)
                .bind("userId", userId)
                .bind("type", type)
                .mapToBean(FriendRequestEntity.class)
                .stream().toList());


        return friedRequests;
    }

    public Boolean acceptFriendRequest(String user1Id, String user2Id) {
        try {
            String sql = "UPDATE users.friends " +
                    "SET status = 'accepted' " +
                    "WHERE (user_id = ? AND friend_id = ?) " +
                    "OR (user_id = ? AND friend_id = ?);";
            jdbi.withHandle(handle ->  handle.execute(sql, user1Id, user2Id, user2Id, user1Id));
            log.info("Friend request accepted between {} and {}", user1Id, user2Id);
            return true;
        } catch (Exception e) {
            log.error("Error while accepting friend request between {} and {}  , ERROR: {}", user1Id,
                            user2Id, e.getMessage());
        }
        return false;
    }
}
