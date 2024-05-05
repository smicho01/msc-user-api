package org.semicorp.msc.userapi.domain.word;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static org.semicorp.msc.userapi.utils.Logger.logInfo;

@RestController
@RequestMapping("/api/v1")
public class WordController {

    @Autowired
    private WordGeneratorService wordGeneratorService;

    @GetMapping("/generatename")
    public ResponseEntity<String> generateRandomUserName(@RequestHeader(HttpHeaders.AUTHORIZATION) String token)
            throws IOException {
        String generatedName = wordGeneratorService.generateRandomUserName();
        logInfo("Generate new username" , token);
        return new ResponseEntity<>(generatedName, HttpStatus.OK);
    }

    @GetMapping("/generatenames")
    public ResponseEntity<List<String>> generateRandomUserNameBulk(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                                         @RequestParam(value="count", required = false) int count)
            throws IOException {
        List<String> names = wordGeneratorService.generateRandomUserNameBulk(count);
        logInfo("Generate usernames list" , token);
        return new ResponseEntity<>(names, HttpStatus.OK);
    }

}