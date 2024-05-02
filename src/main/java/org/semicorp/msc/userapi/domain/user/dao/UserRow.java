package org.semicorp.msc.userapi.domain.user.dao;

import lombok.NonNull;
import org.semicorp.msc.userapi.domain.user.User;

public class UserRow  {

    private String id;
    private String username;
    private String visibleUsername;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean active;

    public UserRow(@NonNull final User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.visibleUsername = user.getVisibleUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.active = user.getActive();
    }
}
