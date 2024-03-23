package org.semicorp.msc.userapi.domain.user;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static User createUser() {
        return User.builder()
                .id("00000000-0000-0000-0000-000000000000")
                .username("johdoe12")
                .firstName("John")
                .lastName("Doe")
                .email("jd@gmail.com")
                .sex("M")
                .build();
    }

    public static List<User> createUsersList() {
        List<User> users = new ArrayList<>();
        users.add(User.builder().firstName("Adam").lastName("Smith").build());
        users.add(new User());
        users.add(new User());
        users.add(new User());
        users.add(new User());
        return users;
    }
}
