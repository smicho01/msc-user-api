package org.semicorp.msc.studentapi.domain.student.dao;

public class QueryStudent {

    static final String QUERY_FIND_ALL = "SELECT * FROM studentsschema.student;";
    static final String QUERY_FIND_BY_ID = "SELECT * FROM studentsschema.student WHERE id = :id";
}
