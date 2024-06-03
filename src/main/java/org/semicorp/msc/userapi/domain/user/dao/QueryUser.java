package org.semicorp.msc.userapi.domain.user.dao;

public class QueryUser {

    static final String QUERY_FIND_ALL = "SELECT * FROM users.user;";
    static final String QUERY_FIND_BY_ID = "SELECT * FROM users.user WHERE id = :id;";
    static final String QUERY_FIND_BY_USERNAME = "SELECT * FROM users.user WHERE username =:username;";
    static final String QUERY_FIND_BY_EMAIL = "SELECT * FROM users.user WHERE email =:email;";

    static final String QUERY_INSERT_USER = "INSERT INTO users.user (id, username, visibleusername, firstname, lastname, email, college, active, pubkey, privkey, tokens) " +
            "VALUES(:id, :username, :visibleUsername, :firstName, :lastName, :email, :college, :active, :pubKey, :privKey, :tokens );";
}
