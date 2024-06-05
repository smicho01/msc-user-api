package org.semicorp.msc.userapi.domain.user.dao;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.semicorp.msc.userapi.domain.user.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class UserRowMapper implements RowMapper<User> {

    @Override
    public User map(ResultSet rs, StatementContext ctx) throws SQLException {
        Timestamp timestamp = rs.getTimestamp("datecreated");
        LocalDateTime dateCreated = timestamp != null ? timestamp.toLocalDateTime() : null;

        timestamp = rs.getTimestamp("dateupdated");
        LocalDateTime dateUpdated = timestamp != null ? timestamp.toLocalDateTime() : null;

        return new User(
                rs.getString("id"),
                rs.getString("username"),
                rs.getString("visibleusername"),
                rs.getString("firstname"),
                rs.getString("lastname"),
                rs.getString("email"),
                rs.getString("college"),
                rs.getString("collegeId"),
                rs.getBoolean("active"),
                dateCreated,
                dateUpdated,
                rs.getString("pubKey"),
                rs.getString("privKey"),
                rs.getInt("tokens")
        );
    }
}
