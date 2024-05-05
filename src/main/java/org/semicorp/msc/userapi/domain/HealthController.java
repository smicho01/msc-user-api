package org.semicorp.msc.userapi.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class HealthController {


    @GetMapping
    public ResponseEntity healtcheck() {
        return new ResponseEntity(HttpStatus.OK);
    }
}
