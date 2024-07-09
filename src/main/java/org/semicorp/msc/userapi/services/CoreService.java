package org.semicorp.msc.userapi.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;
import org.semicorp.msc.userapi.domain.user.User;
import org.semicorp.msc.userapi.domain.user.dao.UserDAO;
import org.semicorp.msc.userapi.domain.user.dao.UserRow;
import org.semicorp.msc.userapi.domain.user.dto.AddUserDTO;
import org.semicorp.msc.userapi.domain.user.exceptions.UserNotFoundException;
import org.semicorp.msc.userapi.domain.wallet.dto.WalletEncryptedDTO;
import org.semicorp.msc.userapi.domain.word.WordGeneratorService;
import org.semicorp.msc.userapi.responses.ResponseCodes;
import org.semicorp.msc.userapi.responses.TextResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.semicorp.msc.userapi.domain.user.UserConstants.USER_NOT_FOUND;

@Service
@Slf4j
public class CoreService {

    private final RestTemplate restTemplate;

    @Value("${academi.service.core.url}")
    private String coreServiceUrl;

    public CoreService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public WalletEncryptedDTO generateBlockchainWalletKeys(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token.substring(7));
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<WalletEncryptedDTO> response = restTemplate.exchange(
                            coreServiceUrl + "/api/v1/wallet/create",
                                HttpMethod.GET, entity, WalletEncryptedDTO.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error while generating wallet keys");
            log.error(e.getMessage());
            log.error(Arrays.toString(e.getStackTrace()));
        }
        return null;
    }

    @Service
    @Slf4j
    @AllArgsConstructor
    public static class UserService {

        private final WordGeneratorService wordGeneratorService;

        private final Jdbi jdbi;

        private final RestTemplate restTemplate;


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
                case "visibleusername" -> jdbi.onDemand(UserDAO.class).findByVisibleUsername(fieldvalue);
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

            boolean insert = false;
            try {
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

    }
}
