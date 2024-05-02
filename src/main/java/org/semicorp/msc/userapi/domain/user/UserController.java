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

    private final UserService studentService;

    public UserController(UserService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        logInfo("Get all users", token);
        List<User> allUsers = studentService.getAllUsers();
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable(value="id") String id) {
        logInfo("Get user by id: " + id, token);
        User user = studentService.getUser(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}

