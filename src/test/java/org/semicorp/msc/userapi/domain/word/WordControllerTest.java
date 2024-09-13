package org.semicorp.msc.userapi.domain.word;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

class WordControllerTest {

    @Mock
    private WordGeneratorService wordGeneratorService;

    @InjectMocks
    private WordController wordController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateRandomUserName_shouldReturnGeneratedName() throws IOException {
        String mockGeneratedName = "TestUser";
        String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        when(wordGeneratorService.generateRandomUserName()).thenReturn(mockGeneratedName);

        ResponseEntity<String> response = wordController.generateRandomUserName(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockGeneratedName, response.getBody());
        verify(wordGeneratorService).generateRandomUserName();
    }

    @Test
    void generateRandomUserNameBulk_shouldReturnGeneratedNames() throws IOException {
        int count = 3;
        String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        List<String> mockNames = Arrays.asList("User1", "User2", "User3");
        when(wordGeneratorService.generateRandomUserNameBulk(count)).thenReturn(mockNames);

        ResponseEntity<List<String>> response = wordController.generateRandomUserNameBulk(token, count);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockNames, response.getBody());
        verify(wordGeneratorService).generateRandomUserNameBulk(count);
    }

}
