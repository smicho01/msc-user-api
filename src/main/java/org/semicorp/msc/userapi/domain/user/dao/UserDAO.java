package org.semicorp.msc.userapi.domain.user.dao;

import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.semicorp.msc.userapi.domain.user.User;

import java.util.List;

public interface UserDAO {

    @RegisterRowMapper(UserRowMapper.class)
    @SqlQuery(QueryUser.QUERY_FIND_BY_ID)
    User findById(@Bind("id") String id);

    @RegisterRowMapper(UserRowMapper.class)
    @SqlQuery(QueryUser.QUERY_FIND_ALL)
    List<User> findAll();
}
