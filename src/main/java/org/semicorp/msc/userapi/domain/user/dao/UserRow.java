package org.semicorp.msc.userapi.domain.user.dao;

import lombok.NonNull;
import org.semicorp.msc.userapi.domain.user.User;

public class UserRow implements DomainType<User>, Comparable<User> {

    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String sex;

    private Boolean active;

    public UserRow(@NonNull final User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.sex = user.getSex();
        this.active = user.getActive();
    }

    @Override
    public int compareTo(User o) {
        return 0;
    }

    @Override
    public User asModel() {
        return null;
    }
}
