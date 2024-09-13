package org.semicorp.msc.userapi.domain.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.semicorp.msc.userapi.domain.user.dto.BasicUserDataDTO;
import org.semicorp.msc.userapi.domain.user.dto.UserDTO;
import org.semicorp.msc.userapi.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        User mockUser = new User();
        mockUser.setId("user123");
        mockUser.setUsername("JohnDoe");

        when(userService.getUserByField(anyString(), anyString())).thenReturn(mockUser);

        ResponseEntity<BasicUserDataDTO> response = userController.getUserById("user123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("user123", response.getBody().getId());
        verify(userService, times(1)).getUserByField("id", "user123");
    }

    @Test
    void getUserByVisibleUsernameLIKE_ShouldReturnUsers() {
        User mockUser1 = new User();
        mockUser1.setUsername("JohnDoe1");
        User mockUser2 = new User();
        mockUser2.setUsername("JohnDoe2");

        List<User> users = new ArrayList<>();
        users.add(mockUser1);
        users.add(mockUser2);

        when(userService.getUserByVisibleUsernameLIKE(anyString(), any())).thenReturn(users);

        ResponseEntity<List<UserDTO>> response = userController.getUserByVisibleUsernameLIKE("John", null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(userService, times(1)).getUserByVisibleUsernameLIKE("John", null);
    }

    @Test
    void deleteUser_ShouldReturnSuccess() {
        User mockUser = new User();
        mockUser.setId("user123");
        mockUser.setUsername("JohnDoe");

        when(userService.getUserByField(anyString(), anyString())).thenReturn(mockUser);
        when(userService.updateField(anyString(), any(), anyString())).thenReturn(true);

        ResponseEntity response = userController.deleteUser("user123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService, times(1)).updateField("active", false, "user123");
    }
}
