package org.semicorp.msc.userapi.domain.message.dao;

public class QueryMessage {

    static final String QUERY_FIND_ALL_FOR_USER = "SELECT * FROM  users.message " +
            "WHERE fromid = :userId OR toid = :userId " +
            "ORDER BY datecreated ASC;";
    public static final String QUERY_INSERT = "INSERT INTO users.message (id, fromid, toid, content) \n" +
            "VALUES (:id, :fromId, :toId, :content);";
    public static final String QUERY_FIND_ONE_BY_ID = "SELECT * FROM users.message WHERE id = :messageId;";
    public static final String QUERY_UPDATE = "UPDATE users.message SET " +
            "fromid = :fromId, toid = :toId, content = :content, datecreated = :dateCreated, read = :read " +
            " WHERE id = :id;";
    public static final String QUERY_DELETE = "DELETE FROM users.message WHERE id = :messageId;";

    public static final String QUERY_UPDATE_READ_ALL_BETWEEN_USERS = "UPDATE users.message SET " +
            " read = :read WHERE toid = :toId AND fromid = :fromId;";
}
