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
import org.semicorp.msc.userapi.domain.wallet.dto.WalletEncryptedDTO;
import org.semicorp.msc.userapi.domain.word.WordGeneratorService;
import org.semicorp.msc.userapi.responses.ResponseCodes;
import org.semicorp.msc.userapi.responses.TextResponse;
import org.semicorp.msc.userapi.responses.WalletBalanceResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.semicorp.msc.userapi.domain.user.UserConstants.USER_NOT_FOUND;

@Service
@Slf4j
public class UserService {

    private final WordGeneratorService wordGeneratorService;

    @Value("${academi.service.core.url}")
    private String coreServiceUrl;
    private final Jdbi jdbi;
    private final RestTemplate restTemplate;
    @Value("${academi.service.user.numberofuserimagesavailable}")
    private Integer numberOfUserImagesAvailable;

    public UserService(WordGeneratorService wordGeneratorService, Jdbi jdbi, RestTemplate restTemplate) {
        this.wordGeneratorService = wordGeneratorService;
        this.jdbi = jdbi;
        this.restTemplate = restTemplate;
    }

    public List<User> getAllUsers() {
        try {
            return jdbi.onDemand(UserDAO.class).findAll();
        } catch (Exception e) {
            log.error("Error getting all users. Error: {}", e.getMessage());
            return null;
        }
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
        log.info("Get user by field: {} and value: {}", fieldName, fieldvalue);
        try {
            User user = switch (fieldName) {
                case "username" -> jdbi.onDemand(UserDAO.class).findByUsername(fieldvalue);
                case "email" -> jdbi.onDemand(UserDAO.class).findByEmail(fieldvalue);
                case "id" -> jdbi.onDemand(UserDAO.class).findById(fieldvalue);
                case "visibleusername" -> jdbi.onDemand(UserDAO.class).findByVisibleUsername(fieldvalue);
                default -> null;
            };

            if (user == null) {
                log.info("User not found. {}: {}", fieldName, fieldvalue);
                return null;
            }
            return user;
        } catch (Exception e) {
            log.info("Error while getting user by field name: {} and value: {}", fieldName, fieldvalue);
            return null;
        }
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

        boolean insert = false;
        try {
            // Draw user avatar image number
            Random rand = new Random();
            int avatarNumber = rand.nextInt(numberOfUserImagesAvailable) + 1;
            log.info("User {} avatar image number: {}", newUser.getUsername(), avatarNumber);
            newUser.setImageid(avatarNumber);
            // Insert user
            insert = jdbi.onDemand(UserDAO.class).insert(new UserRow(newUser));
        } catch (Exception e) {
            log.error("Can't create new user {}", newUser.getUsername());
            log.error(e.getMessage());
        }

        if(!insert) {
            log.warn("Can't insert user with id {}, and username: {}", newUser.getId(), newUser.getUsername());
            return new TextResponse("Insert error", ResponseCodes.FAIL);
        }
        return new TextResponse("User created", ResponseCodes.SUCCESS);
    }

    public User createUserFromAddUserDto(AddUserDTO addUserDTO) throws IOException {
        log.info("createUserFromAddUserDto: {} ",  addUserDTO.toString());
        String visibleUsername = wordGeneratorService.generateRandomUserName();
        log.info("Visible Username for user will be: {}", visibleUsername);
        return User.builder()
                .id(UUID.randomUUID().toString())
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
    }


    public Boolean updateField(String fieldName, Object value, String userId) {
        log.info("Request to update field name: {} with value: {} for user id: {}", fieldName, value, userId);
        try (Handle handle = jdbi.open()) {
            String sql = "UPDATE users.user SET " + fieldName + " = '" + value + "' WHERE id ='" + userId + "';";
            try (Update update = handle.createUpdate(sql)) {
                update.execute();
                log.info("Field Updated. User id: {}, field: {} updated with value: {}", userId, fieldName, value);
            } catch (Exception e) {
                log.error("Error updating field {} for user id {}. Error: {}", fieldName, userId, e.getMessage());
                return false;
            }
        }
        return true;
    }

    public List<User> getUserByVisibleUsernameLIKE(String username, String collegeId) {
        log.info("Get users by username like: {}", username);
        String collegeWhereClause = "";
        if(collegeId != null) {
            collegeWhereClause = String.format(" AND u.collegeid = '%s'  ", collegeId);
        }

        String sql =  "SELECT * FROM users.user u " +
                " WHERE LOWER(u.visibleUsername) LIKE LOWER(:username) " +
                " AND u.active = true " +
                collegeWhereClause +
                " ORDER BY u.visibleUsername ASC;";

        List<User> users = jdbi.withHandle(handle -> handle.createQuery(sql)
                .bind("username", "%"+username+"%")
                .mapToBean(User.class)
                .list());
        log.info("Results size: {}", users.size());
        return users;
    }

    public Boolean updateUserTokens(String jwtToken, String userId, String userWalletPublicKey) {
        log.info("Call method UserService updateUserTokens");
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(jwtToken.substring(7));
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<WalletBalanceResponse> response = restTemplate.exchange(
                    coreServiceUrl + "/api/v1/wallet/" + userWalletPublicKey,
                    HttpMethod.GET, entity, WalletBalanceResponse.class);

            WalletBalanceResponse walletBalanceResponse = response.getBody();
            log.info("Wallet balance response: {}", walletBalanceResponse);
            assert walletBalanceResponse != null;
            return updateField("tokens", String.valueOf(walletBalanceResponse.getBalance()), userId);
        } catch (Exception e) {
            log.error("Error updating user tokens. Error: {}", e.getMessage());
            return false;
        }
    }
}
