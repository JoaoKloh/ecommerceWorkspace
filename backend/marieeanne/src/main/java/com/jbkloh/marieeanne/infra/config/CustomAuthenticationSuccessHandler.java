package com.jbkloh.marieeanne.infra.config;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.jbkloh.marieeanne.core.models.Usuario;
import com.jbkloh.marieeanne.infra.service.AutenticationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AutenticationService autenticationService;

   @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        Usuario user=autenticationService.getUserOrCreate(email);
        String accessCookie=autenticationService.gerarCookieToken(user).toString();
        String refreshCookie=autenticationService.gerarCookieRefresh(user).toString();
        String isAuthenticated=autenticationService.gerarCookieApoioAutenticacao().toString();

        List<String> roles = user.getAuthorities().stream()
                .map(auth -> auth.name())
                .toList();
        
        System.out.println("Roles do usuário: " + roles);
        
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie);
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie);
        response.addHeader(HttpHeaders.SET_COOKIE, isAuthenticated);
        
        String urlDirecionamento = "https://projected-organic-interventions-humanity.trycloudflare.com/";
            
        if (roles.contains("ROLE_ADMIN")) {
            urlDirecionamento = "https://projected-organic-interventions-humanity.trycloudflare.com/admin";
        }        
        getRedirectStrategy().sendRedirect(request, response, urlDirecionamento);
    }
}
