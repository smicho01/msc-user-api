package org.semicorp.msc.userapi.services;

import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.Update;
import org.semicorp.msc.userapi.domain.user.User;
import org.semicorp.msc.userapi.domain.user.dao.UserDAO;
import org.semicorp.msc.userapi.domain.user.dao.UserRow;
import org.semicorp.msc.userapi.domain.user.dto.AddUserDTO;
import org.semicorp.msc.userapi.domain.user.exceptions.UserNotFoundException;
import org.semicorp.msc.userapi.domain.word.WordGeneratorService;
import org.semicorp.msc.userapi.responses.TextResponse;
import org.semicorp.msc.userapi.responses.ResponseCodes;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
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
        } catch (RuntimeException e) {
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

        if (user == null) {
            log.info("User not found. {}: {}", fieldName, fieldvalue);
            return null;
        }
        return user;
    }

    public TextResponse insert(User newUser) {
        // Check if username exists
        User usernames = getUserByField("username", newUser.getUsername());
        if (usernames != null) {
            log.warn("Username exists. username: {}", newUser.getUsername());
            return new TextResponse("Username exists", ResponseCodes.ALREADY_EXISTS);
        }
        // Check if email exists
        User emails = getUserByField("email", newUser.getEmail());
        if (emails != null) {
            log.warn("Email exists. email: {}", newUser.getUsername());
            return new TextResponse("Email exists", ResponseCodes.ALREADY_EXISTS);
        }

        try {
            boolean insert = jdbi.onDemand(UserDAO.class).insert(new UserRow(newUser));
        } catch (Exception e) {
            log.error("Can't create new user {}", newUser.getUsername());
            log.error(e.getMessage());
            return new TextResponse("Insert error", ResponseCodes.FAIL);
        }
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
                .college(addUserDTO.getCollege())
                .datecreated(LocalDateTime.now())
                .dateupdated(LocalDateTime.now())
                .active(true)
                .build();
        return user;
    }


    public Boolean updateField(String fieldName, String value, String userId) {
        try (Handle handle = jdbi.open()) {
            String sql = "UPDATE users.user SET " + fieldName + " = '" + value + "' WHERE id ='" + userId + "';";
            try (Update update = handle.createUpdate(sql)) {
                update.execute();
                log.info("User id: {}, field: {} updated with value: {}", userId, fieldName, value);
            } catch (Exception e) {
                log.error("Error updating field {} for user id {}. Error: {}", fieldName, userId, e.getMessage());
                return false;
            }
        }
        return true;
    }

    public List<User> getUserByVisibleUsernameLIKE(String username) {
        try {
            List<User> userByVisibleUsernameLIKE = jdbi.onDemand(UserDAO.class).getUserByVisibleUsernameLIKE("%"+username+"%");
            return userByVisibleUsernameLIKE;
        } catch (Exception e) {
            log.error("Can't get users by visible username LIKE. ERROR: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
}
