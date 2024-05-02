package org.semicorp.msc.userapi.domain.word;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

@Service
public class WordGeneratorService {
    private final ResourceLoader resourceLoader;

    public WordGeneratorService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public String generateRandomPhrase() throws IOException {
        Resource adjectiveResource = resourceLoader.getResource("classpath:adjectives.txt");
        Resource nounResource = resourceLoader.getResource("classpath:animals.txt");

        List<String> adjectives = Files.readAllLines(Paths.get(adjectiveResource.getURI()));
        List<String> nouns = Files.readAllLines(Paths.get(nounResource.getURI()));

        Random random = new Random();
        String randomAdjective = capitalizeFirstLetter(adjectives.get(random.nextInt(adjectives.size())));
        String randomNoun = nouns.get(random.nextInt(nouns.size()));

        return randomAdjective + randomNoun;
    }

    public String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}