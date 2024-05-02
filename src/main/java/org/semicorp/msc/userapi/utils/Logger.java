package org.semicorp.msc.userapi.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Logger {

    public static void logInfo(String message, String token) {
        JwtCredentials.extractTokenData(token);
        log.info(message + " | Keycloak User ID: {} , username: {}", JwtCredentials.getKeycloakUserId(),
                JwtCredentials.getKeycloakUsername());
    }

}
