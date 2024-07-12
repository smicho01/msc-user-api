package org.semicorp.msc.userapi.domain.user.dao;

import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.semicorp.msc.userapi.domain.user.User;

import java.util.List;

public interface UserDAO {

    @RegisterRowMapper(UserRowMapper.class)
    @SqlQuery(QueryUser.QUERY_FIND_BY_ID)
    User findById(@Bind("id") String id);

    @RegisterRowMapper(UserRowMapper.class)
    @SqlQuery(QueryUser.QUERY_FIND_BY_USERNAME)
    User findByUsername(@Bind("username") String username);

    @RegisterRowMapper(UserRowMapper.class)
    @SqlQuery(QueryUser.QUERY_FIND_BY_EMAIL)
    User findByEmail(@Bind("email") String email);

    @RegisterRowMapper(UserRowMapper.class)
    @SqlQuery(QueryUser.QUERY_FIND_ALL)
    List<User> findAll();

    @SqlUpdate(QueryUser.QUERY_INSERT_USER)
    boolean insert(@BindBean final UserRow userRow);

    @RegisterRowMapper(UserRowMapper.class)
    @SqlQuery(QueryUser.QUERY_FIND_BY_VISIBLEUSERNAME)
    User findByVisibleUsername(@Bind("visibleusername") String visibleUsername);

    @RegisterRowMapper(UserRowMapper.class)
    @SqlQuery(QueryUser.QUERY_FIND_BY_VISIBLEUSERNAME_LIKE)
    List<User> getUserByVisibleUsernameLIKE(@Bind("username") String username);
}
