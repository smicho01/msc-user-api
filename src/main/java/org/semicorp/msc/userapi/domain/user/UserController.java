package org.semicorp.msc.userapi.domain.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {

    private final UserService studentService;

    public UserController(UserService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Get all users");
        List<User> allUsers = studentService.getAllUsers();
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable(value="id") String id) {
        User user = studentService.getUser(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}
