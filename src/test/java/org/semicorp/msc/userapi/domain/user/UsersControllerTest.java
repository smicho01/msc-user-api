package org.semicorp.msc.userapi.domain.user;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void getAllUsers_returnsOkWithListOfUsers() throws Exception {
        List<User> users =  Utils.createUsersList();

        given(userService.getAllUsers()).willReturn(users);

        mockMvc.perform(get("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer your_token_here"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(5)))
                .andExpect(jsonPath("$[0].firstName", is("Adam")))
                .andDo(print());
    }

    @Test
    public void shouldReturnUser_whenGivenValidId() throws Exception {
        User expected = User.builder().build();
        given(userService.getUser("1")).willReturn(expected);

        mockMvc.perform(get("/api/v1/user/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        Mockito.verify(userService, Mockito.times(1)).getUser("1");
    }

    @Test
    public void shouldThrowError_whenGivenInvalidId() throws Exception{
        given(userService.getUser("200")).willReturn(null);

        mockMvc.perform(get("/api/v1/user/200")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(userService, Mockito.times(1)).getUser("200");
    }

}