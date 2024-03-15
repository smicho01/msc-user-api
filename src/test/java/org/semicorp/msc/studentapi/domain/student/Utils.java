package org.semicorp.msc.studentapi.domain.student;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static Student createStudent() {
        return Student.builder()
                .id("00000000-0000-0000-0000-000000000000")
                .studentId("johdoe12")
                .firstName("John")
                .lastName("Doe")
                .email("jd@gmail.com")
                .sex("M")
                .build();
    }

    public static List<Student> createStudentsList() {
        List<Student> students = new ArrayList<>();
        students.add(new Student());
        students.add(new Student());
        students.add(new Student());
        students.add(new Student());
        students.add(new Student());
        return students;
    }
}
