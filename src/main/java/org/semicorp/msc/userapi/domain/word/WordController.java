package org.semicorp.msc.userapi.domain.word;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class WordController {

    @Autowired
    private WordGeneratorService wordGeneratorService;

    @GetMapping("/generatename")
    public ResponseEntity<String> generateRandomUserName(@RequestHeader(HttpHeaders.AUTHORIZATION) String token)  {
        try {
            String generatedName = wordGeneratorService.generateRandomUserName();
            log.info("Generate new username");
            return new ResponseEntity<>(generatedName, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error while generating new username. Error: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/generatenames")
    public ResponseEntity<List<String>> generateRandomUserNameBulk(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                                         @RequestParam(value="count", required = false) int count) {
        try {
            List<String> names = wordGeneratorService.generateRandomUserNameBulk(count);
            log.info("Generate usernames list");
            return new ResponseEntity<>(names, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error while generating bulk usernames. Error: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

}