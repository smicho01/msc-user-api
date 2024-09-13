package org.semicorp.msc.userapi.domain.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.semicorp.msc.userapi.domain.user.dto.AddUserDTO;
import org.semicorp.msc.userapi.domain.user.dto.BasicUserDataDTO;
import org.semicorp.msc.userapi.domain.user.dto.UserDTO;
import org.semicorp.msc.userapi.domain.wallet.dto.WalletEncryptedDTO;
import org.semicorp.msc.userapi.responses.ResponseCodes;
import org.semicorp.msc.userapi.responses.TextResponse;
import org.semicorp.msc.userapi.responses.UserFieldUpdateResponse;
import org.semicorp.msc.userapi.security.CryptoUtils;
import org.semicorp.msc.userapi.services.CoreService;
import org.semicorp.msc.userapi.services.ItemService;
import org.semicorp.msc.userapi.services.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Role;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.semicorp.msc.userapi.domain.user.UserMapper.userToBasicUserDTO;
import static org.semicorp.msc.userapi.utils.Logger.logInfo;

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CoreService coreService;
    private final ItemService itemService;

    public static String encryptionKey;

    @Value("${academi.encryption.key}")
    public void setEncryptionKey(String value) {
        encryptionKey = value;
    }


    @GetMapping("{userId}")
    public ResponseEntity<BasicUserDataDTO> getUserById(@PathVariable(value = "userId") String userId) throws Exception {
        User user = userService.getUserByField("id", userId);
        BasicUserDataDTO basicUserDataDTO = userToBasicUserDTO(user);
        return new ResponseEntity<>(basicUserDataDTO, HttpStatus.OK);
    }

    @GetMapping("username/like/{username}")
    public ResponseEntity<List<UserDTO>> getUserByVisibleUsernameLIKE(
            @PathVariable(value = "username") String username,
            @RequestParam(required = false) Map<String, String> urlParams
    ) {
        String userCollegeId = null;
        if (urlParams.containsKey("collegeId") && urlParams.get("collegeId") != null) {
            userCollegeId = urlParams.get("collegeId");
            log.info("Request with parameter collegeId: {}", userCollegeId);
        }

        List<User> users = userService.getUserByVisibleUsernameLIKE(username, userCollegeId);
        if (!users.isEmpty()) {
            List<UserDTO> userDTOS = UserMapper.listUserToListUserDTO(users);
            return new ResponseEntity<>(userDTOS, HttpStatus.OK);
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
    }


    /**
     * Retrieves all users based on the specified field and value.
     *
     * @param token      The authorization token provided in the request header.
     * @param field      The field to search for users (optional). Possible values are "username", "email", "id", "visibleusername".
     * @param value      The value of the field to search for users (optional).
     * @param otherParam Other parameter (optional) . Usage example: "updateTokens" - will update user tokens
     * @return ResponseEntity containing the users found based on the specified field and value.
     * If the field is not specified, returns a list of all users in the system.
     * If the field is specified and a user is found, returns the user details as a UserDTO object.
     * If the field is specified but no user is found, returns HTTP status code 400 (Bad Request).
     * If the user is found but the user id is null, returns HTTP status code 404 (Not Found).
     */
    @GetMapping
    public ResponseEntity getAllUsersByField(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                             @RequestParam(value = "field", required = false) String field,
                                             @RequestParam(value = "value", required = false) String value,
                                             @RequestParam(value = "otherParam", required = false) String otherParam) {
        log.info("Called method UserController.getAllUsersByField");
        if (field == null) {
            logInfo("Get all users", token);
            List<User> allUsers = userService.getAllUsers();
            if (allUsers == null) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            List<UserDTO> userDTOS = UserMapper.listUserToListUserDTO(allUsers);
            return new ResponseEntity<>(userDTOS, HttpStatus.OK);
        }

        if (!Arrays.asList("username", "email", "id", "visibleusername").contains(field)) {
            return new ResponseEntity<>(new TextResponse("Unknown parameter", ResponseCodes.FAIL),
                    HttpStatus.BAD_REQUEST);
        }

        logInfo("Get user by " + field + ": " + value, token);
        User user = userService.getUserByField(field, value);
        if (user == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } else if (user.getId() == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        // It allows to perform some action before returning the user
        // Example can be: update user cyrpto tokens - number of tokens for the user may change, so it will
        // perform an action - get number of tokens for that user wallet from the blockchain and then return the user.
        // This logic will be moved to a separate helper/service class in the next iteration
        // TODO: move below logic to other class and possibly endpoint. Too much is happening here.
        if (otherParam != null && otherParam.equals("updateTokens")) {
            log.info("Request otherParam: {}", otherParam);
            Boolean isUpdated = userService.updateUserTokens(token, user.getId(), user.getPubKey());
            if (isUpdated) {
                log.info("Tokens updated for user id {}", user.getId());
                user = userService.getUserByField("id", user.getId()); // get updated user
            } else {
                log.warn("Unable to update tokens for user id {}", user.getId());
            }
        }
        UserDTO userDTO = UserMapper.userToUserDTO(user);

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity addUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody AddUserDTO addUserDTO) throws Exception {
        logInfo(String.format("Register new user: [username: %s]", addUserDTO.getUsername()), token);

        User newUser = userService.createUserFromAddUserDto(addUserDTO);
        // Get blockchain wallet via Core Service
        WalletEncryptedDTO walletDetails = coreService.generateBlockchainWalletKeys(token);
        if (walletDetails != null) {
            // Decrypt keys. Keys are sent as encrypted strings
            newUser.setPrivKey(CryptoUtils.decrypt(walletDetails.getPrivateKeyEncrypted(), encryptionKey));
            newUser.setPubKey(CryptoUtils.decrypt(walletDetails.getPublicKeyEncrypted(), encryptionKey));
        } else {
            log.warn("Can't generate wallet keys for user {}", addUserDTO.getUsername());
        }

        // Insert users' college name into db
        String collegeId = itemService.insertUsersCollegeIntoDb(addUserDTO, token);
        newUser.setCollegeid(collegeId); // assign college ID from Item Service `college` db table

        try {
            // Insert user
            TextResponse insertResponse = userService.insert(newUser);
            // Response when insert request didn't go well
            if (insertResponse.getCode() != 200) {
                return new ResponseEntity<>(insertResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("Can't insert new user: {}", addUserDTO.getUsername());
            log.error(e.getMessage());
            log.error(Arrays.toString(e.getStackTrace()));
        }

        // Return encrypted wallet keys
        if (walletDetails != null) {
            newUser.setPubKey(walletDetails.getPublicKeyEncrypted());
            newUser.setPrivKey(walletDetails.getPrivateKeyEncrypted());
            newUser.setTokens(walletDetails.getBalance());
        }
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }


    @GetMapping("/getkeys/{id}")
    public ResponseEntity<WalletEncryptedDTO> getUserWalletKeys(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable(value = "id") String id) throws Exception {
        logInfo("Get keys for user id: " + id, token);
        User user = userService.getUser(id);
        // Encrypt use wallet keys
        WalletEncryptedDTO walletDto = WalletEncryptedDTO.builder()
                .publicKeyEncrypted(CryptoUtils.encrypt(user.getPubKey(), encryptionKey))
                .privateKeyEncrypted(CryptoUtils.encrypt(user.getPrivKey(), encryptionKey))
                .balance(user.getTokens())
                .build();

        return new ResponseEntity<>(walletDto, HttpStatus.OK);
    }

    @PutMapping("{userId}")
    public ResponseEntity<Boolean> updateUserField(@PathVariable("userId") String userId,
                                                   @RequestParam("field") String fieldName,
                                                   @RequestParam("value") String value) {
        String[] allowedField = new String[]{"active", "tokens", "rank"};

        if (!Arrays.asList(allowedField).contains(fieldName)) {
            log.warn("Field {} not present in allowed fields list", fieldName);
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }

        Boolean response = false;
        switch (fieldName) {
            case "rank":
                User foundUser = userService.getUserByField("id", userId);
                int userNewRank = foundUser.getRank() + Integer.parseInt(value);
                userService.updateField(fieldName, String.valueOf(userNewRank), userId);
                break;

            default:
                response = userService.updateField(fieldName, value, userId);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Sets status ACTIVE to FALSE
     * Use HARD DELETE to delete user form DB
     * Removing from Keycloak isn't served by this API
     *
     * @param userId the ID of the user to be deleted
     * @return a ResponseEntity object indicating the success or failure of the operation
     */
    //@PreAuthorize("hasRole('ADMIN')")
    @Secured("ROLE_ADMIN")
    @DeleteMapping("{userId}")
    public ResponseEntity<UserFieldUpdateResponse> deleteUser(@PathVariable("userId") String userId) {
        Boolean response = false;
        try {
            String fieldName = "active";
            Object value = false;
            User foundUser = userService.getUserByField("id", userId);
            Boolean result = userService.updateField("active", false, userId);
            return new ResponseEntity<>(new UserFieldUpdateResponse(foundUser.getId(), fieldName, value,  result), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

}
