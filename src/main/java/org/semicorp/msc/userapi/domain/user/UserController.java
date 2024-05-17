package org.semicorp.msc.userapi.domain.user;

import lombok.extern.slf4j.Slf4j;
import org.semicorp.msc.userapi.domain.user.dto.AddUserDTO;
import org.semicorp.msc.userapi.domain.user.dto.BasicUserDataDTO;
import org.semicorp.msc.userapi.domain.user.dto.UserDTO;
import org.semicorp.msc.userapi.domain.wallet.dto.WalletEncryptedDTO;
import org.semicorp.msc.userapi.responses.TextResponse;
import org.semicorp.msc.userapi.responses.ResponseCodes;
import org.semicorp.msc.userapi.security.CryptoUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.semicorp.msc.userapi.utils.Logger.logInfo;

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {

    private final UserService userService;

    @Value("${academi.service.core.url}")
    private String coreServiceUrl;

    private final RestTemplate restTemplate;

    public static String encryptionKey;
    @Value("${academi.encryption.key}")
    public void setEncryptionKey(String value) {
        encryptionKey = value;
    }

    public UserController(UserService studentService, RestTemplate restTemplate) {
        this.userService = studentService;
        this.restTemplate = restTemplate;
    }

    @GetMapping
    public ResponseEntity getAllUsers(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
        @RequestParam(value="field", required = false) String field,
        @RequestParam(value="value", required = false) String value) throws Exception {

        User user = null;
        UserDTO userDTO = null;

        if(field == null) {
            logInfo("Get all users", token);
            List<User> allUsers = userService.getAllUsers();
            List<UserDTO> userDTOS = UserMapper.listUserToListUserDTO(allUsers);
            return new ResponseEntity<>(userDTOS, HttpStatus.OK);
        }

        switch (field) {
            case "username":
                logInfo("Get user by username: " + value , token);
                user = userService.getUserByField("username", value);
                userDTO = UserMapper.userToUserDTO(user);
                return new ResponseEntity<>(userDTO, HttpStatus.OK);
            case "email":
                logInfo("Get user by email: " + value , token);
                user = userService.getUserByField("email", value);
                userDTO = UserMapper.userToUserDTO(user);
                return new ResponseEntity<>(userDTO, HttpStatus.OK);
            case "id":
                logInfo("Get user by id: " + value , token);
                user = userService.getUserByField("id", value);
                userDTO = UserMapper.userToUserDTO(user);
                return new ResponseEntity<>(userDTO, HttpStatus.OK);
            default:
                return new ResponseEntity<>(new TextResponse("Unknown parameter", ResponseCodes.FAIL),
                        HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity getUser(
                                @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                @PathVariable(value="id") String id) {
        logInfo("Get user by id: " + id, token);
        User user = userService.getUser(id);
        if(user == null) {
            return new ResponseEntity<>(new TextResponse("Not Found",
                    ResponseCodes.NOT_FOUND), HttpStatus.NOT_FOUND);
        }
        UserDTO userDTO = UserMapper.userToUserDTO(user);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity addUser(
                            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                            @RequestBody AddUserDTO addUserDTO) throws Exception {
        logInfo(String.format("Register new user: [username: %s]", addUserDTO.getUsername()), token);

        // Get blockchain wallet via Core Service
        // Wallet keys are encrypted and base64 encoded
        String endpointUrl = coreServiceUrl + "/api/v1/wallet/create";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.substring(7));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<WalletEncryptedDTO> response = restTemplate.exchange(endpointUrl,
                HttpMethod.GET, entity, WalletEncryptedDTO.class);
        WalletEncryptedDTO walletDetails = response.getBody();

        User newUser = userService.createUserFromAddUserDto(addUserDTO);
        newUser.setPrivKey(CryptoUtils.decrypt(walletDetails.getPrivateKeyEncrypted(), encryptionKey));
        newUser.setPubKey(CryptoUtils.decrypt(walletDetails.getPublicKeyEncrypted(), encryptionKey));

        TextResponse insertResponse = userService.insert(newUser);
        // Response when insert request didn't go well
        if(insertResponse.getCode() != 200) {
            return new ResponseEntity<>(insertResponse, HttpStatus.BAD_REQUEST);
        }

        logInfo(String.format("User created: [username: %s]", addUserDTO.getUsername()), token);
        // Return encrypted wallet keys
        newUser.setPubKey(walletDetails.getPublicKeyEncrypted());
        newUser.setPrivKey(walletDetails.getPrivateKeyEncrypted());
        newUser.setTokens(walletDetails.getBalance());
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

