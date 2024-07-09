package org.semicorp.msc.userapi.domain.friend;

import lombok.RequiredArgsConstructor;
import org.semicorp.msc.userapi.services.FriendsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
public class FriendsController {

    private final FriendsService friendsService;

    @GetMapping("{userId}")
    public ResponseEntity<List<Friend>> getAllUserFriends(@PathVariable("userId") String userId) {
        List<Friend> allUserFriends = friendsService.getAllUserFriends(userId);
        return new ResponseEntity<>(allUserFriends, HttpStatus.OK);
    }
}
