package org.semicorp.msc.userapi.domain.user;

import lombok.extern.slf4j.Slf4j;
import org.semicorp.msc.userapi.domain.user.dto.AddUserDTO;
import org.semicorp.msc.userapi.domain.user.dto.BasicUserDataDTO;
import org.semicorp.msc.userapi.responses.TextResponse;
import org.semicorp.msc.userapi.responses.ResponseCodes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
    public ResponseEntity getAllUsers(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
        @RequestParam(value="field", required = false) String field,
        @RequestParam(value="value", required = false) String value) throws Exception {

        User user = null;

        if(field == null) {
            logInfo("Get all users", token);
            return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
        }

        switch (field) {
            case "username":
                logInfo("Get user by username: " + value , token);
                user = userService.getUserByField("username", value);
                BasicUserDataDTO basicUserDataDTO = UserMapper.userToBasicUserDTO(user);
                return new ResponseEntity<>(basicUserDataDTO, HttpStatus.OK);
            case "email":
                logInfo("Get user by email: " + value , token);
                user = userService.getUserByField("email", value);
                return new ResponseEntity<>(user, HttpStatus.OK);
            case "id":
                logInfo("Get user by id: " + value , token);
                user = userService.getUserByField("id", value);
                return new ResponseEntity<>(user, HttpStatus.OK);
            default:
                logInfo("Get all users", token);
                return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity getUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable(value="id") String id) {
        logInfo("Get user by id: " + id, token);
        User user = userService.getUser(id);
        if(user == null) {
            return new ResponseEntity<>(new TextResponse("Not Found", ResponseCodes.NOT_FOUND), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity addUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody AddUserDTO addUserDTO) throws IOException {
        logInfo(String.format("Register new user: [username: %s]", addUserDTO.getUsername()), token);
        User newUser = userService.createUserFromAddUserDto(addUserDTO);
        TextResponse insertResponse = userService.insert(newUser);
        if(insertResponse.getCode() != 200) {
            return new ResponseEntity<>(insertResponse, HttpStatus.BAD_REQUEST);
        }
        logInfo(String.format("User created: [username: %s]", addUserDTO.getUsername()), token);
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

}

