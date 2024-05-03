package org.semicorp.msc.userapi.domain.user;

import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;
import org.semicorp.msc.userapi.domain.user.dao.UserDAO;
import org.semicorp.msc.userapi.domain.user.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.semicorp.msc.userapi.domain.user.UserConstants.USER_NOT_FOUND;

@Service
@Slf4j
public class UserService {

    private final Jdbi jdbi;

    public UserService(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public List<User> getAllUsers() {
        return jdbi.onDemand(UserDAO.class).findAll();
    }

    public User getUser(String id) {
        User user = jdbi.onDemand(UserDAO.class).findById(id);
        try {
            if (user == null) {
                String errorMessage = USER_NOT_FOUND + " ID: " + id;
                throw new UserNotFoundException(errorMessage);
            }
        } catch(RuntimeException e) {
            log.warn(e.getMessage());
        }
        return user;
    }

    public List<User> getUserByField(String fieldName, String fieldvalue) {
        List<User> students = switch (fieldName) {
            case "username" -> jdbi.onDemand(UserDAO.class).findByUsername(fieldvalue);
            case "email" -> jdbi.onDemand(UserDAO.class).findByEmail(fieldvalue);
            default -> new ArrayList<>();
        };

        if(students.isEmpty()) {
            log.info("User not found. {}: {}", fieldName, fieldvalue);
        }

        return students;
    }
}
