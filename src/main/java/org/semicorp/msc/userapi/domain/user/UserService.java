package org.semicorp.msc.userapi.domain.user;

import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;
import org.semicorp.msc.userapi.domain.user.dao.UserDAO;
import org.semicorp.msc.userapi.domain.user.dao.UserRow;
import org.semicorp.msc.userapi.domain.user.dto.AddUserDTO;
import org.semicorp.msc.userapi.domain.user.exceptions.UserNotFoundException;
import org.semicorp.msc.userapi.domain.word.WordGeneratorService;
import org.semicorp.msc.userapi.responses.TextResponse;
import org.semicorp.msc.userapi.responses.ResponseCodes;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    public User getUserByField(String fieldName, String fieldvalue) {
        User user = switch (fieldName) {
            case "username" -> jdbi.onDemand(UserDAO.class).findByUsername(fieldvalue);
            case "email" -> jdbi.onDemand(UserDAO.class).findByEmail(fieldvalue);
            case "id" -> jdbi.onDemand(UserDAO.class).findById(fieldvalue);
            default -> null;
        };

        if(user == null) {
            log.info("User not found. {}: {}", fieldName, fieldvalue);
            return null;
        }
        return user;
    }

    public TextResponse insert(User newUser) {
        // Check if username exists
        User usernames = getUserByField("username", newUser.getUsername());
        if(usernames != null) {
            log.warn("Username exists. username: {}", newUser.getUsername());
            return  new TextResponse("Username exists", ResponseCodes.ALREADY_EXISTS);
        }
        // Check if email exists
        User emails = getUserByField("email", newUser.getEmail());
        if(emails != null) {
            log.warn("Email exists. email: {}", newUser.getUsername());
            return  new TextResponse("Email exists", ResponseCodes.ALREADY_EXISTS);
        }
        boolean insert = jdbi.onDemand(UserDAO.class).insert(new UserRow(newUser));
        if(!insert) {
            log.warn("Can't insert user with id {}, and username: {}", newUser.getId(), newUser.getUsername());
            return new TextResponse("Insert error", ResponseCodes.FAIL);
        }
        log.info("User created. id {}, username: {}", newUser.getId(), newUser.getUsername());
        return new TextResponse("User created", ResponseCodes.SUCCESS);
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
