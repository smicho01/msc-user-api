package org.semicorp.msc.userapi.domain.user;

import lombok.extern.slf4j.Slf4j;
import org.semicorp.msc.userapi.domain.user.dto.AddUserDTO;
import org.semicorp.msc.userapi.domain.user.dto.UserDTO;
import org.semicorp.msc.userapi.domain.wallet.dto.WalletEncryptedDTO;
import org.semicorp.msc.userapi.responses.TextResponse;
import org.semicorp.msc.userapi.responses.ResponseCodes;
import org.semicorp.msc.userapi.security.CryptoUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.semicorp.msc.userapi.utils.Logger.logInfo;

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {

    private final UserService userService;

    @Value("${academi.service.core.url}")
    private String coreServiceUrl;

    @Value("${academi.service.item.url}")
    private String itemServiceUrl;

    private String token;

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
    public ResponseEntity getAllUsersByField(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
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
                if(user!=null) {
                    userDTO = UserMapper.userToUserDTO(user);
                    return new ResponseEntity<>(userDTO, HttpStatus.OK);
                }
                return new ResponseEntity<>(null, HttpStatus.OK);
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

        this.token = token;
        User newUser = userService.createUserFromAddUserDto(addUserDTO);
        // Get blockchain wallet via Core Service
        WalletEncryptedDTO walletDetails = generateBlockchainWalletKeys();
        if(walletDetails != null) {
            // Decrypt keys. Keys are sent as encrypted strings
            newUser.setPrivKey(CryptoUtils.decrypt(walletDetails.getPrivateKeyEncrypted(), encryptionKey));
            newUser.setPubKey(CryptoUtils.decrypt(walletDetails.getPublicKeyEncrypted(), encryptionKey));
        } else {
            log.warn("Can't generate wallet keys for user {}", addUserDTO.getUsername());
        }
        // Insert users' college name into db
        String collegeId = insertUsersCollegeIntoDb(addUserDTO);
        newUser.setCollegeId(collegeId); // assign college ID from Item Service `college` db table
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

    /**
     * Inserts users' college name into `items.college` table in Item Service
     * It does not duplicate college names but returns `HttpStatus.CONFLICT` to indicate
     * that the college entry is already present in the table.
     * @param addUserDTO
     */
    private String insertUsersCollegeIntoDb(AddUserDTO addUserDTO) {
        String collegeId = null;
        // Add college to item schema table
        String itemsEndpointUrl = itemServiceUrl + "/api/v1/college";
        HttpHeaders headers2 = new HttpHeaders();
        headers2.setContentType(MediaType.APPLICATION_JSON);
        headers2.setBearerAuth(this.token.substring(7));
        String payload = "{ \"name\": \"" + addUserDTO.getCollege()  +"\"}";
        HttpEntity<String> request = new HttpEntity<>(payload, headers2);
        try {
            ResponseEntity<Map> exchange = restTemplate.exchange(itemsEndpointUrl,
                    HttpMethod.POST, request, Map.class);
            // Intercept collegeId from response
            Map body = exchange.getBody();
            if(body != null) {
                collegeId = (String) body.get("id");
            }
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.CONFLICT) {
                log.info("College {} already exists in Item Service database", addUserDTO.getCollege());
                log.error(e.getMessage());
            }
        }
        return collegeId;
    }

    private WalletEncryptedDTO generateBlockchainWalletKeys() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(this.token.substring(7));
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

