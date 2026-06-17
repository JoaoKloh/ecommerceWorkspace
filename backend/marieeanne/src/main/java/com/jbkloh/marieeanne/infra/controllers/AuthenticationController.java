package com.jbkloh.marieeanne.infra.controllers;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.jbkloh.marieeanne.core.models.Usuario;
import com.jbkloh.marieeanne.infra.dtos.token.OtpTokenRequestDTO;
import com.jbkloh.marieeanne.infra.dtos.token.OtpVerificacaoRequest;
import com.jbkloh.marieeanne.infra.dtos.token.TokenRequestDTO;
import com.jbkloh.marieeanne.infra.dtos.token.TokenResponseDTO;
import com.jbkloh.marieeanne.infra.dtos.user.LoginResponseDTO;
import com.jbkloh.marieeanne.infra.service.AutenticationService;
import com.jbkloh.marieeanne.infra.service.EmailService;
import com.jbkloh.marieeanne.infra.service.OtpService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/auth")
public class AuthenticationController {

    @Autowired
    private AutenticationService authenticationService;

    @Autowired
    private OtpService otpService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;
    
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;
    @Autowired
    private EmailService emailService;

    @PostMapping("/oauthGoogle")
    public ResponseEntity<?> verificarLoginOauth2(@RequestBody Map<String, String> request) throws IOException, GeneralSecurityException {
        String authorizationCode = request.get("code");

        if (authorizationCode == null || authorizationCode.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Código de autorização ausente.");
        }

        GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                new NetHttpTransport(),
                new GsonFactory(),
                "https://oauth2.googleapis.com/token",
                googleClientId,
                googleClientSecret, 
                authorizationCode,
                "https://separate-aquarium-composition-commands.trycloudflare.com/auth/login" 
        ).execute();

        String token = tokenResponse.getIdToken();

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken = verifier.verify(token);

        try {
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                
                Usuario usuario = authenticationService.getUserOrCreate(email);
                
                ResponseCookie accessCookie = authenticationService.gerarCookieToken(usuario);
                ResponseCookie refreshCookie = authenticationService.gerarCookieRefresh(usuario);
                ResponseCookie isAuthenticated = authenticationService.gerarCookieApoioAutenticacao();
                
                List<String> roles = usuario.getAuthorities().stream()
                        .map(auth -> auth.name())
                        .toList();

                String urlDirecionamento = "https://separate-aquarium-composition-commands.trycloudflare.com/";
                if (roles.contains("ROLE_ADMIN")) {
                    urlDirecionamento = "https://separate-aquarium-composition-commands.trycloudflare.com/admin";
                }

                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                        .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                        .header(HttpHeaders.SET_COOKIE, isAuthenticated.toString())
                        .body(new LoginResponseDTO(email, roles, urlDirecionamento));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token do Google inválido.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao validar token: " + e.getMessage());
        }
    }
    @PostMapping("/gerarcodigo")
    public ResponseEntity<?>gerarCodigoLogin(@RequestBody @Valid OtpTokenRequestDTO request) throws MessagingException, Exception{
        String codigo = otpService.gerarCodigoValido(request.email());
        emailService.enviarCodigoOtp(request.email(), codigo);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/verificarcodigo")
    public ResponseEntity<?>verificarCodigoLogin(@RequestBody OtpVerificacaoRequest request){
        otpService.validarCodigoOtp(request.codigo(), request.email());
        Usuario user = authenticationService.getUserOrCreate(request.email());
        ResponseCookie accessCookie = authenticationService.gerarCookieToken(user);
        ResponseCookie refreshCookie = authenticationService.gerarCookieRefresh(user);
        ResponseCookie isAuthenticated = authenticationService.gerarCookieApoioAutenticacao();
        List<String> roles = user.getAuthorities().stream()
            .map(auth -> auth.name())
            .toList();
        System.out.println("Roles do usuário: " + roles);
        String urlDirecionamento = "https://separate-aquarium-composition-commands.trycloudflare.com/";
            
        if (roles.contains("ROLE_ADMIN")) {
            urlDirecionamento = "https://separate-aquarium-composition-commands.trycloudflare.com/admin";
        }
        return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
        .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
        .header(HttpHeaders.SET_COOKIE, isAuthenticated.toString())
        .body(new LoginResponseDTO(
            request.email(),
            roles,
            urlDirecionamento
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDTO> refreshToken(@Valid @RequestBody TokenRequestDTO tokenRequestDTO) {
        // O DTO TokenRequestDTO deve conter apenas o campo 'refreshToken' (String)
        TokenResponseDTO response = authenticationService.refreshToken(tokenRequestDTO.refreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String refreshToken = null;
    
        if (request.getCookies() != null) {
            refreshToken = Arrays.stream(request.getCookies())
            .filter(cookie -> "refreshToken".equals(cookie.getName()))
            .findFirst()
            .map(Cookie::getValue)
            .orElse(null);
        }
        authenticationService.logout(refreshToken);

        List<ResponseCookie> cookiesParaLimpar = authenticationService.limparCookiesLogout();

        var responseBuilder = ResponseEntity.noContent();
    
        for (ResponseCookie cookie : cookiesParaLimpar) {
        responseBuilder.header(HttpHeaders.SET_COOKIE, cookie.toString());
        }

        return responseBuilder.build();
    }
}
