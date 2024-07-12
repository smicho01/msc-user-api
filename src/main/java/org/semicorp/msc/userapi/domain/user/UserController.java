package org.semicorp.msc.userapi.domain.user;

import com.google.common.base.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.semicorp.msc.userapi.domain.user.dto.AddUserDTO;
import org.semicorp.msc.userapi.domain.user.dto.BasicUserDataDTO;
import org.semicorp.msc.userapi.domain.user.dto.UserDTO;
import org.semicorp.msc.userapi.domain.wallet.dto.WalletEncryptedDTO;
import org.semicorp.msc.userapi.responses.ResponseCodes;
import org.semicorp.msc.userapi.responses.TextResponse;
import org.semicorp.msc.userapi.security.CryptoUtils;
import org.semicorp.msc.userapi.services.CoreService;
import org.semicorp.msc.userapi.services.ItemService;
import org.semicorp.msc.userapi.services.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

import static org.semicorp.msc.userapi.domain.user.UserMapper.userToBasicUserDTO;
import static org.semicorp.msc.userapi.utils.Logger.logInfo;

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CoreService.UserService userCoreService;
    private final CoreService coreService;

    private final ItemService itemService;

    public static String encryptionKey;

    @Value("${academi.encryption.key}")
    public void setEncryptionKey(String value) {
        encryptionKey = value;
    }



    @GetMapping("{userId}")
    public ResponseEntity<BasicUserDataDTO> getUserById( @PathVariable(value="userId") String userId) throws Exception {
        User user = userService.getUserByField("id", userId);
        BasicUserDataDTO basicUserDataDTO = userToBasicUserDTO(user);
        return new ResponseEntity<>(basicUserDataDTO, HttpStatus.OK);
    }

    @GetMapping("username/like/{username}")
    public ResponseEntity<List<UserDTO>> getUserByVisibleUsernameLIKE( @PathVariable(value="username") String username) throws Exception {
        List<User> users = userService.getUserByVisibleUsernameLIKE(username);
        System.out.println(users.size());
        List<UserDTO> userDTOS = UserMapper.listUserToListUserDTO(users);
        return new ResponseEntity<>(userDTOS, HttpStatus.OK);
    }


    @GetMapping
    public ResponseEntity getAllUsersByField(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                             @RequestParam(value="field", required = false) String field,
                                             @RequestParam(value="value", required = false) String value) throws Exception {

        if (field == null) {
            logInfo("Get all users", token);
            List<User> allUsers = userCoreService.getAllUsers();
            List<UserDTO> userDTOS = UserMapper.listUserToListUserDTO(allUsers);
            return new ResponseEntity<>(userDTOS, HttpStatus.OK);
        }

        if (!Arrays.asList("username", "email", "id", "visibleusername").contains(field)) {
            return new ResponseEntity<>(new TextResponse("Unknown parameter", ResponseCodes.FAIL),
                    HttpStatus.BAD_REQUEST);
        }

        logInfo("Get user by " + field + ": " + value , token);
        User user = userCoreService.getUserByField(field, value);
        UserDTO userDTO = null;

        if (user != null) {
            userDTO = UserMapper.userToUserDTO(user);
        }

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity addUser(
                                @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                @RequestBody AddUserDTO addUserDTO) throws Exception {
        logInfo(String.format("Register new user: [username: %s]", addUserDTO.getUsername()), token);

        User newUser = userCoreService.createUserFromAddUserDto(addUserDTO);
        // Get blockchain wallet via Core Service
        WalletEncryptedDTO walletDetails = coreService.generateBlockchainWalletKeys(token);
        if(walletDetails != null) {
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
            TextResponse insertResponse = userCoreService.insert(newUser);
            // Response when insert request didn't go well
            if(insertResponse.getCode() != 200) {
                return new ResponseEntity<>(insertResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("Can't insert new user: {}", addUserDTO.getUsername());
            log.error(e.getMessage());
            log.error(Arrays.toString(e.getStackTrace()));
        }

        // Return encrypted wallet keys
        if(walletDetails != null) {
            newUser.setPubKey(walletDetails.getPublicKeyEncrypted());
            newUser.setPrivKey(walletDetails.getPrivateKeyEncrypted());
            newUser.setTokens(walletDetails.getBalance());
        }
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }


    @GetMapping("/getkeys/{id}")
    public ResponseEntity<WalletEncryptedDTO> getUserWalletKeys(
                                            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                            @PathVariable(value="id") String id) throws Exception {
        logInfo("Get keys for user id: " + id, token);
        User user = userCoreService.getUser(id);
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
        String[] allowedField = new String[] {"active", "tokens", "rank"};

        if(!Arrays.asList(allowedField).contains(fieldName)) {
            log.warn("Field {} not present in allowed list", fieldName);
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }

        Boolean response = false;
        switch (fieldName) {
            case "rank":
                User foundUser = userService.getUserByField("id", userId);
                int userNewRank = foundUser.getRank() + Integer.parseInt(value);
                userService.updateField(fieldName, String.valueOf(userNewRank), userId);
                break;

            default: response = userService.updateField(fieldName, value, userId);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
