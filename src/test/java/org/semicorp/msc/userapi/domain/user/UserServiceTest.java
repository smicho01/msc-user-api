package org.semicorp.msc.userapi.domain.user;

import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.semicorp.msc.userapi.domain.user.dao.UserDAO;
import org.semicorp.msc.userapi.domain.word.WordGeneratorService;
import org.semicorp.msc.userapi.services.CoreService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private CoreService.UserService userService;
    private UserDAO userDAOMock;

    private WordGeneratorService wordGeneratorService;

    @BeforeEach
    void setupTests() {
        Jdbi jdbiMock = mock(Jdbi.class);
        userDAOMock = mock(UserDAO.class);
        doReturn(userDAOMock).when(jdbiMock).onDemand(UserDAO.class);
        userService = new CoreService.UserService(wordGeneratorService, jdbiMock);
    }

    @Test
    void shouldReturnValidStudent() {
        when(userDAOMock.findById(any())).thenReturn(Utils.createUser());

        User student = userService.getUser("000");

        assertEquals("John", student.getFirstName() );
    }

    @Test
    public void jdbiShouldReturnCorrectStudent(){
        when(userDAOMock.findById(any())).thenReturn(Utils.createUser());

        User student = userService.getUser("000");

        assertEquals("John", student.getFirstName());
    }

    @Test
    void shouldReturnAllStudents() {
        Mockito.when(userService.getAllUsers()).thenReturn(Utils.createUsersList());

        List<User> students = userService.getAllUsers();

        assertEquals(5, students.size());
    }

}