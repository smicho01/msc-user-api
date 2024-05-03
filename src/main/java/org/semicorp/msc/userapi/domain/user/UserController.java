package org.semicorp.msc.userapi.domain.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.semicorp.msc.userapi.utils.Logger.logInfo;

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService studentService) {
        this.userService = studentService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
        @RequestParam(value="username", required = false) String username,
        @RequestParam(value="email", required = false) String email) {

        if(username != null) {
            logInfo("Get user by username: " + username , token);
            List<User> users = userService.getUserByField("username", username);
            return new ResponseEntity<>(users, HttpStatus.OK);
        }

        if(email != null) {
            logInfo("Get user by email: " + email , token);
            List<User> users = userService.getUserByField("email", email);
            return new ResponseEntity<>(users, HttpStatus.OK);
        }

        logInfo("Get all users", token);
        List<User> allUsers = userService.getAllUsers();
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable(value="id") String id) {
        logInfo("Get user by id: " + id, token);
        User user = userService.getUser(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

//    @GetMapping("/")
//    public ResponseEntity<User> getUserByFields(
//            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
//            @RequestParam(value="username", required = false) String username,
//            @RequestParam(value="email", required = false) String email) {
//        logInfo("Get user by username: " + username + " and email: " + email, token);
//        List<User> user = userService.getUserByFields(username, email);
//        return new ResponseEntity<>(user, HttpStatus.OK);
//    }
}

