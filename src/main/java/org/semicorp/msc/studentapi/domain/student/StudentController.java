package org.semicorp.msc.studentapi.domain.student;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/student")
@Slf4j
public class StudentController {

    @GetMapping
    public String getStudents() {
        log.info("/api/v1/student");
        return "All students will be here";
    }

}
