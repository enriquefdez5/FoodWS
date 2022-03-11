package es.uniovi.miw.foodws.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.uniovi.miw.foodws.models.Token;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;


public class TokenManager {

    private static Token token;

    public TokenManager() {
    }

    public static String getAccessToken() {
        if (token == null) {
            token = requestNewToken();
        } else {
            System.out.println("Expires in: " + token.getExpires_in());
            if (new Date(token.getExpires_in()).toInstant().isBefore(new Date().toInstant())) {
                token = requestNewToken();
            }
        }
        return token.getAccess_token();
    }

    public static Token requestNewToken() {
        String CLIENT_SECRET = "51ca4d89329e4b64890023d1c88fe003";
        String CLIENT_ID = "1bdea3a471974265bc15440817c2bd2a";

        String authHeader = CLIENT_ID + ":" + CLIENT_SECRET;
        byte[] b = authHeader.getBytes(StandardCharsets.US_ASCII);

        String OAUTH_FATSECRET_TOKEN_URL = "https://oauth.fatsecret.com/connect/token";

        Response postResponse = ClientBuilder.newClient().
                target(OAUTH_FATSECRET_TOKEN_URL).
                request(MediaType.APPLICATION_JSON).
                header(HttpHeaders.AUTHORIZATION, "Basic " + new String(Base64.getEncoder().encode(b))).
                post(Entity.entity("grant_type=client_credentials&scope=basic", MediaType.APPLICATION_FORM_URLENCODED));

        // If status != 200
        if (!postResponse.getStatusInfo().toEnum().equals(Response.Status.OK)) {
            return null;
        }

        // Status == 200
        String response = postResponse.readEntity(String.class);
        try {
            return new ObjectMapper().readValue(response, Token.class);
        } catch (Exception e) {
            return null;
        }
    }

}
