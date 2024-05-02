package org.semicorp.msc.userapi.domain.word;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class WordController {

    @Autowired
    private WordGeneratorService wordGeneratorService;

    @GetMapping("/generatename")
    public ResponseEntity<String> generateRandomUserName() throws IOException {
        String generatedName = wordGeneratorService.generateRandomPhrase();
        return new ResponseEntity<>(generatedName, HttpStatus.OK);
    }

}