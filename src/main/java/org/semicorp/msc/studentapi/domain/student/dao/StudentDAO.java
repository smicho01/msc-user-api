package org.semicorp.msc.studentapi.domain.student.dao;

import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.semicorp.msc.studentapi.domain.student.Student;

public interface StudentDAO {

    @RegisterRowMapper(StudentRowMapper.class)
    Student findById(@Bind("id") Long id);
}
