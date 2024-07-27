package org.semicorp.msc.userapi.domain.message.dao;

import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.semicorp.msc.userapi.domain.message.Message;

import java.util.List;

public interface MessageDAO {

    @RegisterRowMapper(MessageRowMapper.class)
    @SqlQuery(QueryMessage.QUERY_FIND_ALL_FOR_USER)
    List<Message> findAllForUserId(@Bind("userId") String userId);

    @SqlUpdate(QueryMessage.QUERY_INSERT)
    boolean insert(@BindBean final MessageRow messageRow);

    @RegisterRowMapper(MessageRowMapper.class)
    @SqlQuery(QueryMessage.QUERY_FIND_ONE_BY_ID)
    Message findById(@Bind("messageId") String messageId);

    @SqlUpdate(QueryMessage.QUERY_UPDATE)
    boolean update(@BindBean final MessageRow messageRow);

    @SqlUpdate(QueryMessage.QUERY_DELETE)
    boolean delete(@Bind("messageId") String messageId);

    @SqlUpdate(QueryMessage.QUERY_UPDATE_READ_ALL_BETWEEN_USERS)
    boolean updateReadAllFromTo(@Bind("fromId") String fromId,
                                @Bind("toId")String toId,
                                @Bind("read") Boolean read);
}
