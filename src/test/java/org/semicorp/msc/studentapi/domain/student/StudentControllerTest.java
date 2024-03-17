package org.semicorp.msc.studentapi.domain.student;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.hamcrest.Matchers.*;
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
        // Given
        List<Student> students =  Utils.createStudentsList();

        given(studentService.getAllStudents()).willReturn(students);

        // When & Then
        mockMvc.perform(get("/api/v1/student")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(5)))
                .andExpect(jsonPath("$[0].firstName", is("Adam")))
                //.andExpect(content().json("[{'id': '1','firstName':'John'}, {'id': '2','firstName':'Adam'}]"))
                .andDo(MockMvcResultHandlers.print());
    }

}