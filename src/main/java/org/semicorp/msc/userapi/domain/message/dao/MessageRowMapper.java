package org.semicorp.msc.userapi.domain.message.dao;

import org.jdbi.v3.core.statement.StatementContext;
import org.semicorp.msc.userapi.domain.message.Message;
import org.jdbi.v3.core.mapper.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class MessageRowMapper implements RowMapper<Message> {

    @Override
    public Message map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new Message(
                rs.getString("id"),
                rs.getString("fromId"),
                rs.getString("toId"),
                rs.getString("content"),
                rs.getTimestamp("dateCreated").toLocalDateTime(),
                rs.getBoolean("read")
        );
    }
}
