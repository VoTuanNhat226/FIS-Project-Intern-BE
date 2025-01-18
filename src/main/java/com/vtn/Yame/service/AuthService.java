package com.vtn.Yame.service;

import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    private final Keycloak keycloak;

    public AuthService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public AccessTokenResponse login(String username, String password) {
        try {
            Keycloak keycloak = Keycloak.getInstance(
                    keycloakServerUrl,
                    realm,
                    clientId,
                    clientSecret,
                    username,
                    password
            );
            return keycloak.tokenManager().getAccessToken();
        } catch (Exception e) {
            throw new RuntimeException("Invalid username or password", e);
        }
    }

    public Map<String, Object> getUserInfo(String username) {
        UsersResource usersResource = keycloak.realm(realm).users();
        return usersResource.search(username)
                .stream()
                .findFirst()
                .map(userRepresentation -> Map.of(
                        "id", (Object) userRepresentation.getId(),
                        "username", (Object) userRepresentation.getUsername(),
                        "email", (Object) userRepresentation.getEmail(),
                        "firstName", (Object) userRepresentation.getFirstName(),
                        "lastName", (Object) userRepresentation.getLastName()
                ))
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public String registerUser(Map<String, String> userDetails) {
        var user = new org.keycloak.representations.idm.UserRepresentation();
        user.setUsername(userDetails.get("username"));
        user.setEmail(userDetails.get("email"));
        user.setFirstName(userDetails.get("firstName"));
        user.setLastName(userDetails.get("lastName"));
        user.setEnabled(true);

        var usersResource = keycloak.realm(realm).users();
        Response response = usersResource.create(user);
        if (response.getStatus() != 201) {
            throw new RuntimeException("Failed to create user: " + response.getStatusInfo());
        }

        String userId = response.getLocation().getPath().replaceAll(".*/(.*)", "$1");
        usersResource.get(userId).resetPassword(new org.keycloak.representations.idm.CredentialRepresentation() {{
            setTemporary(false);
            setType("password");
            setValue(userDetails.get("password"));
        }});

        return userId;
    }
}
