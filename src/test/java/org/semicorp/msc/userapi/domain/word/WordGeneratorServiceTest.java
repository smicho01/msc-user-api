package org.semicorp.msc.userapi.domain.word;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class WordGeneratorServiceTest {

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private Resource adjectiveResource;

    @Mock
    private Resource nounResource;

    @InjectMocks
    private WordGeneratorService wordGeneratorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateRandomUserName_ShouldReturnCorrectlyFormattedName() throws Exception {
        String adjectivesData = "quick\nlazy\nhappy";
        String nounsData = "dog\ncat\nelephant";

        when(resourceLoader.getResource("classpath:adjectives.txt")).thenReturn(adjectiveResource);
        when(resourceLoader.getResource("classpath:animals.txt")).thenReturn(nounResource);

        when(adjectiveResource.getInputStream()).thenReturn(new ByteArrayInputStream(adjectivesData.getBytes(StandardCharsets.UTF_8)));
        when(nounResource.getInputStream()).thenReturn(new ByteArrayInputStream(nounsData.getBytes(StandardCharsets.UTF_8)));

        String randomUserName = wordGeneratorService.generateRandomUserName();

        assertNotNull(randomUserName);
        assertTrue(randomUserName.matches("(Quick|Lazy|Happy)(dog|cat|elephant)"));
    }



    @Test
    void generateRandomUserNameBulk_ShouldReturnListOfNames() throws Exception {
        String adjectivesData = "quick\nlazy\nhappy";
        String nounsData = "dog\ncat\nelephant";

        when(resourceLoader.getResource("classpath:adjectives.txt")).thenReturn(adjectiveResource);
        when(resourceLoader.getResource("classpath:animals.txt")).thenReturn(nounResource);

        when(adjectiveResource.getInputStream()).thenReturn(new ByteArrayInputStream(adjectivesData.getBytes(StandardCharsets.UTF_8)));
        when(nounResource.getInputStream()).thenReturn(new ByteArrayInputStream(nounsData.getBytes(StandardCharsets.UTF_8)));

        String randomUserName = wordGeneratorService.generateRandomUserName();

        assertNotNull(randomUserName);
        assertTrue(randomUserName.matches("(Quick|Lazy|Happy)(dog|cat|elephant)"));
    }


    @Test
    void capitalizeFirstLetter_ShouldCapitalizeCorrectly() {
        assertEquals("Hello", wordGeneratorService.capitalizeFirstLetter("hello"));
        assertEquals("World", wordGeneratorService.capitalizeFirstLetter("WORLD"));
        assertEquals("", wordGeneratorService.capitalizeFirstLetter(""));
        assertNull(wordGeneratorService.capitalizeFirstLetter(null));
    }
}
