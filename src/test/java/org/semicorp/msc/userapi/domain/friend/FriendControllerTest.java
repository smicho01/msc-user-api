package org.semicorp.msc.userapi.domain.friend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.semicorp.msc.userapi.domain.friend.requests.FriendRequestBody;
import org.semicorp.msc.userapi.services.FriendsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class FriendControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FriendsService friendsService;

    @InjectMocks
    private FriendsController friendsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(friendsController).build();
    }

    @Test
    void getAllUserFriends_ShouldReturnOk() throws Exception {
        List<Friend> friendsList = Collections.emptyList();
        when(friendsService.getAllUserFriends(anyString())).thenReturn(friendsList);

        mockMvc.perform(get("/api/v1/friends/{userId}", "test-user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(friendsService, times(1)).getAllUserFriends("test-user");
    }

    @Test
    void sendFriendRequest_ShouldReturnOk() throws Exception {
        FriendRequestBody friendRequestBody = new FriendRequestBody("requestingUser", "requestedUser");
        when(friendsService.sendFriendRequest(anyString(), anyString())).thenReturn(true);

        mockMvc.perform(post("/api/v1/friends/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"requestingUserId\": \"requestingUser\", \"requestedUserId\": \"requestedUser\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(friendsService, times(1)).sendFriendRequest("requestingUser", "requestedUser");
    }

    @Test
    void sendFriendRequest_ShouldReturnBadRequest_OnError() throws Exception {
        FriendRequestBody friendRequestBody = new FriendRequestBody("requestingUser", "requestedUser");
        when(friendsService.sendFriendRequest(anyString(), anyString())).thenThrow(new RuntimeException("Error"));


        mockMvc.perform(post("/api/v1/friends/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"requestingUserId\": \"requestingUser\", \"requestedUserId\": \"requestedUser\"}"))
                .andExpect(status().isBadRequest());

        verify(friendsService, times(1)).sendFriendRequest("requestingUser", "requestedUser");
    }

    @Test
    void deleteFriendRequest_ShouldReturnOk() throws Exception {
        when(friendsService.deleteFriendRequest(anyString(), anyString())).thenReturn(true);

        mockMvc.perform(delete("/api/v1/friends/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"requestingUserId\": \"requestingUser\", \"requestedUserId\": \"requestedUser\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(friendsService, times(1)).deleteFriendRequest("requestingUser", "requestedUser");
    }

    @Test
    void getFriendRequestsForUserId_ShouldReturnOk() throws Exception {
        List<FriendRequestEntity> requests = Collections.emptyList(); // Expecting an empty list
        when(friendsService.getFriendRequestsForUserId(anyString(), eq("requested"))).thenReturn(requests);

        mockMvc.perform(get("/api/v1/friends/requests/received/{userId}", "test-user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray()) // Verifying it's an array
                .andExpect(jsonPath("$.length()").value(0)); // Verifying the array is empty

        verify(friendsService, times(1)).getFriendRequestsForUserId("test-user", "requested");
    }

    @Test
    void acceptFriendRequest_ShouldReturnOk() throws Exception {
        when(friendsService.acceptFriendRequest(anyString(), anyString())).thenReturn(true);

        mockMvc.perform(put("/api/v1/friends/request/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"requestingUserId\": \"requestingUser\", \"requestedUserId\": \"requestedUser\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(friendsService, times(1)).acceptFriendRequest("requestingUser", "requestedUser");
    }
}
