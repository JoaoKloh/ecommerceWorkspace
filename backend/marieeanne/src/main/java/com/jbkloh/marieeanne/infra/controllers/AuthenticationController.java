package com.jbkloh.marieeanne.infra.controllers;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @Autowired
    private EmailService emailService;

    @PostMapping("/gerarcodigo")
    public ResponseEntity<?>gerarCodigoLogin(@RequestBody OtpTokenRequestDTO request) throws MessagingException, Exception{
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
        return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
        .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
        .header(HttpHeaders.SET_COOKIE, isAuthenticated.toString())
        .body(new LoginResponseDTO(
            request.email(),
            roles
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
