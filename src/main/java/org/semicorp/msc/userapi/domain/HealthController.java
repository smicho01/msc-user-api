package org.semicorp.msc.userapi.domain;

import lombok.extern.slf4j.Slf4j;
import org.semicorp.msc.userapi.responses.BasicResponse;
import org.semicorp.msc.userapi.responses.ResponseCodes;
import org.semicorp.msc.userapi.responses.TextResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class HealthController {


    @GetMapping("/healthcheck")
    public ResponseEntity<BasicResponse> healtcheck() {
        return new ResponseEntity<>(new TextResponse("OK", ResponseCodes.SUCCESS), HttpStatus.OK);
    }
}
