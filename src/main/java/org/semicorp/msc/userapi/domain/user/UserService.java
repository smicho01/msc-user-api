package org.semicorp.msc.userapi.domain.user;

import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;
import org.semicorp.msc.userapi.domain.user.dao.UserDAO;
import org.semicorp.msc.userapi.domain.user.dao.UserRow;
import org.semicorp.msc.userapi.domain.user.dto.AddUserDTO;
import org.semicorp.msc.userapi.domain.user.exceptions.UserNotFoundException;
import org.semicorp.msc.userapi.domain.word.WordGeneratorService;
import org.semicorp.msc.userapi.utils.CustomResponse;
import org.semicorp.msc.userapi.utils.ResponseCodes;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.semicorp.msc.userapi.domain.user.UserConstants.USER_NOT_FOUND;

@Service
@Slf4j
public class UserService {

    private final WordGeneratorService wordGeneratorService;

    private final Jdbi jdbi;

    public UserService(WordGeneratorService wordGeneratorService, Jdbi jdbi) {
        this.wordGeneratorService = wordGeneratorService;
        this.jdbi = jdbi;
    }

    public List<User> getAllUsers() {
        return jdbi.onDemand(UserDAO.class).findAll();
    }

    public User getUser(String id) {
        User user = jdbi.onDemand(UserDAO.class).findById(id);
        try {
            if (user == null) {
                String errorMessage = USER_NOT_FOUND + " ID: " + id;
                throw new UserNotFoundException(errorMessage);
            }
        } catch(RuntimeException e) {
            log.warn(e.getMessage());
        }
        return user;
    }

    public List<User> getUserByField(String fieldName, String fieldvalue) {
        List<User> users = switch (fieldName) {
            case "username" -> jdbi.onDemand(UserDAO.class).findByUsername(fieldvalue);
            case "email" -> jdbi.onDemand(UserDAO.class).findByEmail(fieldvalue);
            default -> new ArrayList<>();
        };

        if(users.isEmpty()) {
            log.info("User not found. {}: {}", fieldName, fieldvalue);
        }
        return users;
    }

    public CustomResponse insert(User newUser) {
        // Check if username exists
        List<User> usernames = getUserByField("username", newUser.getUsername());
        if(usernames.size() > 0) {
            log.warn("Username exists. username: {}", newUser.getUsername());
            return  new CustomResponse("Username exists", ResponseCodes.ALREADY_EXISTS);
        }
        // Check if email exists
        List<User> emails = getUserByField("email", newUser.getEmail());
        if(emails.size() > 0) {
            log.warn("Email exists. email: {}", newUser.getUsername());
            return  new CustomResponse("Email exists", ResponseCodes.ALREADY_EXISTS);
        }
        boolean insert = jdbi.onDemand(UserDAO.class).insert(new UserRow(newUser));
        if(!insert) {
            log.warn("Can't insert user with id {}, and username: {}", newUser.getId(), newUser.getUsername());
            return new CustomResponse("Insert error", ResponseCodes.FAIL);
        }
        log.info("User created. id {}, username: {}", newUser.getId(), newUser.getUsername());
        return new CustomResponse("User created", ResponseCodes.SUCCESS);
    }

    public User createUserFromAddUserDto(AddUserDTO addUserDTO) throws IOException {
        UUID userId = UUID.randomUUID();
        String visibleUsername = wordGeneratorService.generateRandomUserName();

        User user = User.builder()
                .id(userId.toString())
                .visibleUsername(visibleUsername)
                .username(addUserDTO.getUsername())
                .firstName(addUserDTO.getFirstName())
                .lastName(addUserDTO.getLastName())
                .email(addUserDTO.getEmail())
                .active(true)
                .build();
        return user;
    }


}
