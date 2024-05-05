package org.semicorp.msc.userapi.domain.user;

import org.semicorp.msc.userapi.domain.user.dto.AddUserDTO;

public class UserMapper {

    public static User addUserDtoToUser(AddUserDTO addUserDTO) {
        return new User();
    }

}
