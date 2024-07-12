package org.semicorp.msc.userapi.domain.friend;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.semicorp.msc.userapi.domain.friend.requests.FriendRequestBody;
import org.semicorp.msc.userapi.domain.friend.response.FriendRequestResponse;
import org.semicorp.msc.userapi.services.FriendsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
@Slf4j
public class FriendsController {

    private final FriendsService friendsService;

    @GetMapping("{userId}")
    public ResponseEntity<List<Friend>> getAllUserFriends(@PathVariable("userId") String userId) {
        List<Friend> allUserFriends = friendsService.getAllUserFriends(userId);
        return new ResponseEntity<>(allUserFriends, HttpStatus.OK);
    }

    @PostMapping("request")
    public ResponseEntity<Boolean> sendFriendRequest(@RequestBody FriendRequestBody friendRequest) {
        log.info("Friend request endpoint");
        Boolean result = friendsService.sendFriendRequest(friendRequest.getRequestingUserId(),
                friendRequest.getRequestedUserId());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("request")
    public ResponseEntity<Boolean> deleteFriendRequest(@RequestBody FriendRequestBody friendRequest) {
        log.info("Delete friend request endpoint");
        Boolean result = friendsService.deleteFriendRequest(friendRequest.getRequestingUserId(),
                friendRequest.getRequestedUserId());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Friends requests user get
     * @param userId
     * @return
     */
    @GetMapping("requests/received/{userId}")
    public ResponseEntity<List<FriendRequestEntity>> getFriendRequestsForUserId(
                            @PathVariable("userId") String userId) {
        log.info("Get received friend requests for user id: {}", userId);
        List<FriendRequestEntity> requests = friendsService.getFriendRequestsForUserId(userId, "requested");
        log.info("Received friend requests for user id: {} = {}", userId, requests.size());
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    /**
     * Friends requests user sent (Invites)
     * @param userId
     * @return
     */
    @GetMapping("requests/sent/{userId}")
    public ResponseEntity<List<FriendRequestEntity>> getFriendInvitationsForUserId(@PathVariable("userId") String userId) {
        log.info("Get sent friend requests for user id: {}", userId);
        List<FriendRequestEntity> requests = friendsService.getFriendRequestsForUserId(userId, "pending");
        log.info("Sent friend requests for user id: {} = {}", userId, requests.size());
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    @PutMapping("request/accept")
    public ResponseEntity<Boolean> acceptFriendRequest(@RequestBody FriendRequestBody friendRequest) {
        log.info("Accept friend request between {} and {}", friendRequest.getRequestedUserId(), friendRequest.getRequestingUserId());
        Boolean result = friendsService.acceptFriendRequest(friendRequest.getRequestingUserId(),
                friendRequest.getRequestedUserId());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
