package org.semicorp.msc.userapi.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
@Getter
public class JwtCredentials {

    @Getter
    private static String keycloakUserId;
    @Getter
    private static String keycloakGivenName;
    @Getter
    private static String keycloakFamilyName;
    @Getter
    private static String keycloakEmail;
    @Getter
    private static String keycloakUsername;

    public static void extractTokenData(String token) {
        if(token.startsWith("Bearer")) {
            String tokenString = token.substring(7);

            DecodedJWT jwt = JWT.decode(tokenString);

            //String headerJson = jwt.getHeader();
            String payloadJson = jwt.getPayload();

            byte[] decodedBytes = Base64.getDecoder().decode(payloadJson);
            String decodedString = new String(decodedBytes);

            JSONParser parser = new JSONParser();
            try {
                JSONObject json = (JSONObject) parser.parse(decodedString);

                keycloakUserId = (String) json.get("sub");
                keycloakGivenName = (String) json.get("given_name");
                keycloakFamilyName = (String) json.get("family_name");
                keycloakEmail = (String) json.get("email");
                keycloakUsername = (String) json.get("preferred_username");

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

}
