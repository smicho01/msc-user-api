package org.semicorp.msc.studentapi.domain.student;

import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;
import org.semicorp.msc.studentapi.domain.student.Student;
import org.semicorp.msc.studentapi.domain.student.dao.StudentDAO;
import org.semicorp.msc.studentapi.domain.student.exceptions.StudentNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.semicorp.msc.studentapi.domain.student.StudentConstants.STUDENT_NOT_FOUND;

@Service
@Slf4j
public class StudentService {

    private final Jdbi jdbi;

    public StudentService(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public List<Student> getAllStudents() {
        return jdbi.onDemand(StudentDAO.class).findAll();
    }

    public Student getStudent(String id) {
        Student student = jdbi.onDemand(StudentDAO.class).findById(id);
        try {
            if (student == null) {
                String errorMessage = STUDENT_NOT_FOUND + " ID: " + id;
                throw new StudentNotFoundException(errorMessage);
            }
        } catch(RuntimeException e) {
            log.warn(e.getMessage());
        }
        return student;
    }
}
