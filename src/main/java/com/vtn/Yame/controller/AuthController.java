package com.vtn.Yame.controller;

import com.vtn.Yame.dto.ForgotPasswordRequest;
import com.vtn.Yame.service.EmailService;
import com.vtn.Yame.service.AuthService;
import com.vtn.Yame.service.UserService;
import jakarta.annotation.PostConstruct;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;  // Để tìm người dùng trong DB

    @Autowired
    private EmailService emailService;

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    private Keycloak keycloak;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    public void init() {
        this.keycloak = Keycloak.getInstance(
                keycloakServerUrl,
                realm,
                clientId,
                clientSecret,
                "tuannhat-rest-api"
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        RestTemplate restTemplate = new RestTemplate();

        String url = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("username", username);
        body.add("password", password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        System.out.println("Request body: " + body);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            System.out.println("Response body: " + response.getBody());

            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            // 1. Lấy token admin từ Keycloak
            String adminToken = getAdminAccessToken();

            // 2. Tìm User trong Keycloak
            String userId = findUserIdByEmailOrUsername(request.getUsernameOrEmail(), adminToken);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            // 3. Gửi email đặt lại mật khẩu
            sendResetPasswordEmail(userId, adminToken);

            return ResponseEntity.ok("Password reset email sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    //Lấy được token
    private String getAdminAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
                keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token",
                request,
                Map.class
        );

        return (String) response.getBody().get("access_token");
    }

    //http://localhost:8080/admin/realms/TuanNhat/users?username=thitruc
    private String findUserIdByEmailOrUsername(String emailOrUsername, String adminToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        String url = keycloakServerUrl + "/admin/realms/" + realm + "/users?username=" + emailOrUsername;

        ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), List.class);

        List<?> users = response.getBody();
        if (users != null && !users.isEmpty()) {
            Map<String, Object> user = (Map<String, Object>) users.get(0);
            System.out.println("Found user: " + user.get("id"));
            return (String) user.get("id");
        }

        return null;
    }

    private void sendResetPasswordEmail(String userId, String adminToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Body raw JSON ["UPDATE_PASSWORD"]
        String body = "[\"UPDATE_PASSWORD\"]";

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        String url = keycloakServerUrl + "/admin/realms/" + realm + "/users/" + userId + "/execute-actions-email";

        restTemplate.exchange(url, HttpMethod.PUT, request, Void.class);

    }
}

