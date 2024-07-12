package org.semicorp.msc.userapi.domain.user.dao;

public class QueryUser {


    static final String QUERY_FIND_ALL = "SELECT * FROM users.user;";
    static final String QUERY_FIND_BY_ID = "SELECT * FROM users.user WHERE id = :id;";
    static final String QUERY_FIND_BY_USERNAME = "SELECT * FROM users.user WHERE username =:username;";
    static final String QUERY_FIND_BY_EMAIL = "SELECT * FROM users.user WHERE email =:email;";

    public static final String QUERY_FIND_BY_VISIBLEUSERNAME = "SELECT * FROM users.user WHERE visibleusername =:visibleusername;";
    public static final String QUERY_FIND_BY_VISIBLEUSERNAME_LIKE = "SELECT * FROM users.\"user\" u " +
            "WHERE LOWER(u.visibleUsername) LIKE LOWER(:username) " +
            "ORDER BY u.visibleUsername ASC;";

    static final String QUERY_INSERT_USER = "INSERT INTO users.user (id, username, visibleusername, firstname, lastname, email, college, collegeid, active, pubkey, privkey, tokens) " +
            "VALUES(:id, :username, :visibleUsername, :firstName, :lastName, :email, :college, :collegeid, :active, :pubKey, :privKey, :tokens );";
}
