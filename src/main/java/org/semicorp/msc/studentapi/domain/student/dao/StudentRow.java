package org.semicorp.msc.studentapi.domain.student.dao;

import lombok.NonNull;
import org.semicorp.msc.studentapi.domain.student.Student;

import java.util.UUID;

public class StudentRow implements DomainType<Student>, Comparable<Student> {

    private String id;
    private String studentId;
    private String firstName;
    private String lastName;
    private String email;
    private String sex;

    public StudentRow(@NonNull final Student student) {
        this.id = student.getId();
        this.studentId = student.getStudentId();
        this.firstName = student.getFirstName();
        this.lastName = student.getLastName();
        this.email = student.getEmail();
        this.sex = student.getSex();
    }

    @Override
    public int compareTo(Student o) {
        return 0;
    }

    @Override
    public Student asModel() {
        return null;
    }
}
