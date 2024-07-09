package org.semicorp.msc.userapi.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;
import org.semicorp.msc.userapi.domain.friend.Friend;
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
        String sql = "SELECT u.id, u.visibleusername, u.college " +
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

}
