package org.semicorp.msc.studentapi.domain.student.dao;

import org.semicorp.msc.studentapi.domain.student.Student;

public class StudentRow implements DomainType<Student>, Comparable<Student> {

    private Long id;
    private String studentCodeName;
    private String firstName;
    private String lastName;

    // TODO add correct fields

    @Override
    public int compareTo(Student o) {
        return 0;
    }

    @Override
    public Student asModel() {
        return null;
    }
}
