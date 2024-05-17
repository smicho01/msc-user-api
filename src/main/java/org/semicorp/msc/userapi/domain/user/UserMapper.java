package org.semicorp.msc.userapi.domain.user;

import org.semicorp.msc.userapi.domain.user.dao.UserRowMapper;
import org.semicorp.msc.userapi.domain.user.dto.AddUserDTO;
import org.semicorp.msc.userapi.domain.user.dto.BasicUserDataDTO;
import org.semicorp.msc.userapi.domain.user.dto.UserDTO;
import org.semicorp.msc.userapi.security.CryptoUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public static String encryptionKey;

    /**
     * This method helps to set field `encryptionKey` which is static
     * It is impossible to set static fields with @Value, but this make
     * it possible/
     * @param value
     */
    @Value("${academi.encryption.key}")
    public void setEncryptionKey(String value) {
        encryptionKey = value;
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


    public static UserDTO userToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .visibleUsername(user.getVisibleUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .active(user.getActive())
                .datecreated(user.getDatecreated())
                .dateupdated(user.getDateupdated())
                .tokens(user.getTokens())
                .build();
    }

    public static List<UserDTO> listUserToListUserDTO(List<User> listUsers) {
        List<UserDTO> collect = listUsers.stream()
                .map(UserMapper::userToUserDTO)
                .collect(Collectors.toList());
        return collect;
    }

}
