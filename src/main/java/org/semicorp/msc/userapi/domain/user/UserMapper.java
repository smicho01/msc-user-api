package org.semicorp.msc.userapi.domain.user;

import org.semicorp.msc.userapi.domain.user.dto.AddUserDTO;
import org.semicorp.msc.userapi.domain.user.dto.BasicUserDataDTO;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserMapper() {

    }

    public static User addUserDtoToUser(AddUserDTO addUserDTO) {
        return new User();
    }
    public static BasicUserDataDTO userToBasicUserDTO(User user)  {

        return BasicUserDataDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .visibleUsername(user.getVisibleUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .active(user.getActive())
                .pubKey(user.getPubKey())
                .tokens(user.getTokens())
                .build();
    }

}
