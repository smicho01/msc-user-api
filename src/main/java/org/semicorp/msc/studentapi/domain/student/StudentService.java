package org.semicorp.msc.studentapi.domain.student;

import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;
import org.semicorp.msc.studentapi.domain.student.Student;
import org.semicorp.msc.studentapi.domain.student.dao.StudentDAO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class StudentService {

    private final Jdbi jdbi;

    public StudentService(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public List<Student> getAllStudents() {
        log.info("Get all students");
        return jdbi.onDemand(StudentDAO.class).findAll();
    }
}
