package org.semicorp.msc.studentapi.domain.student;

import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.semicorp.msc.studentapi.domain.student.dao.StudentDAO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    private StudentService studentService;
    private StudentDAO studentDAOMock;

    @BeforeEach
    void setupTests() {
        Jdbi jdbiMock = mock(Jdbi.class);
        studentDAOMock = mock(StudentDAO.class);
        doReturn(studentDAOMock).when(jdbiMock).onDemand(StudentDAO.class);
        studentService = new StudentService(jdbiMock);
    }

    @Test
    void shouldReturnValidStudent() {
        when(studentDAOMock.findById(any())).thenReturn(Utils.createStudent());

        Student student = studentService.getStudent("000");

        assertEquals("John", student.getFirstName() );
    }

    @Test
    public void jdbiShouldReturnCorrectStudent(){
        when(studentDAOMock.findById(any())).thenReturn(Utils.createStudent());

        Student student = studentService.getStudent("000");

        assertEquals("John", student.getFirstName());
    }

    @Test
    void shouldReturnAllStudents() {
        Mockito.when(studentService.getAllStudents()).thenReturn(Utils.createStudentsList());

        List<Student> students = studentService.getAllStudents();

        assertEquals(5, students.size());
    }

}