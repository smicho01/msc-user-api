package org.semicorp.msc.userapi.domain.message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.semicorp.msc.userapi.services.MessageService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MessageControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MessageService messageService;

    @InjectMocks
    private MessageController messageController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(messageController).build();
    }

    @Test
    void getAllUserMessages_ShouldReturnOk() throws Exception {
        List<Message> messages = new ArrayList<>();
        when(messageService.getMessagesForUser(anyString())).thenReturn(messages);

        mockMvc.perform(get("/api/v1/message/{userId}", "test-user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap());  // Assuming response is a map

        verify(messageService, times(1)).getMessagesForUser("test-user");
    }

    @Test
    void saveMessage_ShouldReturnCreated() throws Exception {
        Message message = new Message();
        message.setFromId("user1");
        message.setToId("user2");
        message.setContent("Hello");

        when(messageService.saveMessage(any(Message.class))).thenReturn(message);

        mockMvc.perform(post("/api/v1/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromId\": \"user1\", \"toId\": \"user2\", \"content\": \"Hello\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fromId").value("user1"))
                .andExpect(jsonPath("$.toId").value("user2"))
                .andExpect(jsonPath("$.content").value("Hello"));

        verify(messageService, times(1)).saveMessage(any(Message.class));
    }

    @Test
    void deleteMessage_ShouldReturnOk() throws Exception {
        when(messageService.deleteMessage(anyString())).thenReturn(true);

        mockMvc.perform(delete("/api/v1/message/{messageId}", "message-id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(messageService, times(1)).deleteMessage("message-id");
    }

    @Test
    void deleteMessage_ShouldReturnBadRequest() throws Exception {
        when(messageService.deleteMessage(anyString())).thenReturn(false);

        mockMvc.perform(delete("/api/v1/message/{messageId}", "message-id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(false));

        verify(messageService, times(1)).deleteMessage("message-id");
    }

    @Test
    void updateMessage_ShouldReturnOk() throws Exception {
        Message existingMessage = new Message();
        existingMessage.setFromId("user1");
        existingMessage.setToId("user2");
        existingMessage.setContent("Hello");

        when(messageService.getMessage(anyString())).thenReturn(existingMessage);
        when(messageService.updateMessage(any(Message.class))).thenReturn(existingMessage);

        mockMvc.perform(put("/api/v1/message/{messageId}", "message-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromId\": \"user1\", \"toId\": \"user2\", \"content\": \"Updated Hello\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated Hello"));  // This should remain unchanged unless overwritten

        verify(messageService, times(1)).getMessage("message-id");
        verify(messageService, times(1)).updateMessage(any(Message.class));
    }

    @Test
    void updateMessage_ShouldReturnNotFound() throws Exception {
        when(messageService.getMessage(anyString())).thenReturn(null);

        mockMvc.perform(put("/api/v1/message/{messageId}", "message-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromId\": \"user1\", \"toId\": \"user2\", \"content\": \"Updated Hello\"}"))
                .andExpect(status().isNotFound());

        verify(messageService, times(1)).getMessage("message-id");
        verify(messageService, times(0)).updateMessage(any(Message.class));
    }

    @Test
    void updateMessageRead_ShouldReturnOk() throws Exception {
        when(messageService.updateMessageReadAllFromTo(anyString(), anyString(), eq(true))).thenReturn(true);

        mockMvc.perform(put("/api/v1/message/updatereadall/{fromId}/{toId}", "user1", "user2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(messageService, times(1)).updateMessageReadAllFromTo("user1", "user2", true);
    }
}
