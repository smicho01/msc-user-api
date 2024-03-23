package org.semicorp.msc.userapi.domain.user.dao;

public class QueryUser {

    static final String QUERY_FIND_ALL = "SELECT * FROM users.user;";
    static final String QUERY_FIND_BY_ID = "SELECT * FROM users.user WHERE id = :id";
}
