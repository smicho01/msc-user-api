package org.semicorp.msc.userapi.domain.user;

import lombok.extern.slf4j.Slf4j;
import org.semicorp.msc.userapi.domain.user.dto.AddUserDTO;
import org.semicorp.msc.userapi.domain.user.dto.UserDTO;
import org.semicorp.msc.userapi.domain.wallet.dto.WalletEncryptedDTO;
import org.semicorp.msc.userapi.responses.ResponseCodes;
import org.semicorp.msc.userapi.responses.TextResponse;
import org.semicorp.msc.userapi.security.CryptoUtils;
import org.semicorp.msc.userapi.services.CoreService;
import org.semicorp.msc.userapi.services.ItemService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

import static org.semicorp.msc.userapi.utils.Logger.logInfo;

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {

    private final CoreService.UserService userService;
    private final CoreService coreService;

    private final ItemService itemService;

    public static String encryptionKey;

    @Value("${academi.encryption.key}")
    public void setEncryptionKey(String value) {
        encryptionKey = value;
    }

    public UserController(CoreService.UserService studentService, CoreService coreService, ItemService itemService) {
        this.userService = studentService;
        this.coreService = coreService;
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity getAllUsersByField(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                             @RequestParam(value="field", required = false) String field,
                                             @RequestParam(value="value", required = false) String value) throws Exception {

        if (field == null) {
            logInfo("Get all users", token);
            List<User> allUsers = userService.getAllUsers();
            List<UserDTO> userDTOS = UserMapper.listUserToListUserDTO(allUsers);
            return new ResponseEntity<>(userDTOS, HttpStatus.OK);
        }

        if (!Arrays.asList("username", "email", "id").contains(field)) {
            return new ResponseEntity<>(new TextResponse("Unknown parameter", ResponseCodes.FAIL),
                    HttpStatus.BAD_REQUEST);
        }

        logInfo("Get user by " + field + ": " + value , token);
        User user = userService.getUserByField(field, value);
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

        User newUser = userService.createUserFromAddUserDto(addUserDTO);
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
            TextResponse insertResponse = userService.insert(newUser);
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
        User user = userService.getUser(id);
        // Encrypt use wallet keys
        WalletEncryptedDTO walletDto = WalletEncryptedDTO.builder()
                .publicKeyEncrypted(CryptoUtils.encrypt(user.getPubKey(), encryptionKey))
                .privateKeyEncrypted(CryptoUtils.encrypt(user.getPrivKey(), encryptionKey))
                .balance(user.getTokens())
                .build();

        return new ResponseEntity<>(walletDto, HttpStatus.OK);
    }

}
