package org.semicorp.msc.userapi.domain.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

        List<User> users = new ArrayList<>();

        if(username != null) {
            logInfo("Get user by username: " + username , token);
            users = userService.getUserByField("username", username);
            return new ResponseEntity<>(users, HttpStatus.OK);
        } else if(email != null) {
            logInfo("Get user by email: " + email , token);
            users = userService.getUserByField("email", email);
            return new ResponseEntity<>(users, HttpStatus.OK);
        } else {
            logInfo("Get all users", token);
            users = userService.getAllUsers();
        }

        return new ResponseEntity<>(users, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity getUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable(value="id") String id) {
        logInfo("Get user by id: " + id, token);
        User user = userService.getUser(id);
        if(user == null) {
            return new ResponseEntity<>(new CustomResponse("Not Found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}

