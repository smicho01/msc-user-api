package org.semicorp.msc.studentapi.domain.student;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.List;

@WebMvcTest(StudentController.class)
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Test
    public void getAllStudents_returnsOkWithListOfStudents() throws Exception {
        List<Student> students =  Utils.createStudentsList();

        given(studentService.getAllStudents()).willReturn(students);

        mockMvc.perform(get("/api/v1/student")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(5)))
                .andExpect(jsonPath("$[0].firstName", is("Adam")))
                .andDo(print());
    }

    @Test
    public void shouldReturnStudent_whenGivenValidId() throws Exception {
        Student expectedStudent = Student.builder().build();
        given(studentService.getStudent("1")).willReturn(expectedStudent);

        mockMvc.perform(get("/api/v1/student/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        Mockito.verify(studentService, Mockito.times(1)).getStudent("1");
    }

    @Test
    public void shouldThrowError_whenGivenInvalidId() throws Exception{
        given(studentService.getStudent("200")).willReturn(null);

        mockMvc.perform(get("/api/v1/student/200")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(studentService, Mockito.times(1)).getStudent("200");
    }

}