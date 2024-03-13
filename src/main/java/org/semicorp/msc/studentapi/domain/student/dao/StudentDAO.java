package org.semicorp.msc.studentapi.domain.student.dao;

import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.semicorp.msc.studentapi.domain.student.Student;

import java.util.List;

public interface StudentDAO {

//    @RegisterRowMapper(StudentRowMapper.class)
//    Student findById(@Bind("id") Long id);

    @RegisterRowMapper(StudentRowMapper.class)
    @SqlQuery(QueryStudent.QUERY_FIND_ALL)
    List<Student> findAll();
}
