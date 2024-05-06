package org.semicorp.msc.userapi.domain.user.dao;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.semicorp.msc.userapi.domain.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class UserRow  {

    private String id;
    private String username;
    private String visibleUsername;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean active;
    private LocalDateTime datecreated;
    private LocalDateTime dateupdated;
    private String pubKey;
    private String privKey;
    private int tokens;

    public UserRow(@NonNull final User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.visibleUsername = user.getVisibleUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.active = user.getActive();
        this.datecreated = user.getDatecreated();
        this.dateupdated = user.getDateupdated();
        this.pubKey = user.getPubKey();
        this.privKey = user.getPrivKey();
        this.tokens = user.getTokens();
    }
}
