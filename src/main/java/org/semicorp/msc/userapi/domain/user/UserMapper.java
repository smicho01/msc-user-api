package org.semicorp.msc.userapi.domain.user;

import org.semicorp.msc.userapi.domain.user.dto.AddUserDTO;
import org.semicorp.msc.userapi.domain.user.dto.BasicUserDataDTO;
import org.semicorp.msc.userapi.security.CryptoUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public static String encryptionKey;
    @Value("${academi.encryption.key}")
    public void setEncryptionKey(String value) {
        encryptionKey = value;
    }

    public static User addUserDtoToUser(AddUserDTO addUserDTO) {
        return new User();
    }
    public static BasicUserDataDTO userToBasicUserDTO(User user) throws Exception {
        if(user !=null ) {


            String encryptedPubKey = "";
            if (user.getPubKey() != null) {
                encryptedPubKey = CryptoUtils.encrypt(user.getPubKey(), encryptionKey);
            }

            return BasicUserDataDTO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .visibleUsername(user.getVisibleUsername())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .active(user.getActive())
                    .pubKey(encryptedPubKey)
                    .tokens(user.getTokens())
                    .build();
        }
        return null;
    }

}
