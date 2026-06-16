package com.jbkloh.marieeanne.infra.service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jbkloh.marieeanne.core.models.Usuario;
import com.jbkloh.marieeanne.core.models.enums.AutoridadesUsuario;
import com.jbkloh.marieeanne.core.usecases.UsuarioUseCase;
import com.jbkloh.marieeanne.infra.dtos.token.TokenResponseDTO;
import com.jbkloh.marieeanne.infra.dtos.user.UserLoginResponseDTO;
import com.jbkloh.marieeanne.infra.exceptions.AppException;
import com.jbkloh.marieeanne.infra.mappers.UsuarioMapper;
import com.jbkloh.marieeanne.infra.models.RefreshTokenEntity;
import com.jbkloh.marieeanne.infra.models.UserEntity;
import com.jbkloh.marieeanne.infra.persistence.UserRoleJpaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutenticationService {

    private final UsuarioUseCase usuarioUseCase;
    private final TokenService tokenService;
    private final TokenRefreshService tokenRefreshService;
    private final UserRoleJpaRepository userRoleJpaRepository;

    @Transactional
    public Usuario getUserOrCreate(String email){
        return usuarioUseCase.findByEmail(email)
            .orElseGet(() -> {
                Usuario newUser = new Usuario();
                newUser.setEmail(email);
                newUser.addAuthority(AutoridadesUsuario.ROLE_USER);
                return usuarioUseCase.save(newUser);
            });
        }

    @Transactional 
    public UserLoginResponseDTO registerAdmin(String email) {

        Optional<Usuario> existingUser = usuarioUseCase.findByEmail(email);
        if (existingUser.isPresent()) {
            return GerarRespostaCompleta(existingUser.get());
        }

        Usuario userAdmin = new Usuario(email);
        userAdmin.addAuthority(AutoridadesUsuario.ROLE_ADMIN);
        return GerarRespostaCompleta(usuarioUseCase.save(userAdmin));
    }
    @Transactional
    public TokenResponseDTO refreshToken(String refreshToken) {
        RefreshTokenEntity refreshTokenEntity = tokenRefreshService.validarRefreshToken(refreshToken);
        UserEntity user = refreshTokenEntity.getUser();

        String novoAccessToken = tokenService.gerarToken(user);
        var novoRefreshToken = tokenRefreshService.criarRefreshToken(user);

        return new TokenResponseDTO(novoAccessToken, novoRefreshToken.getToken(), "Bearer");
    }

    @Transactional
    public void logout(String refreshTokenStr) {
        if (refreshTokenStr == null || refreshTokenStr.isEmpty()) {
            throw new AppException("Refresh Token inválido", HttpStatus.BAD_REQUEST);
        }
        tokenRefreshService.deletarPeloToken(refreshTokenStr);
    }

    public Usuario getUser(String email){
        return usuarioUseCase.findByEmail(email)
            .orElseThrow(() -> new AppException("Usuário não encontrado", HttpStatus.NOT_FOUND));
    }

    public ResponseCookie gerarCookieToken(Usuario usuario) {
        UserEntity userEntity = UsuarioMapper.toEntity(usuario,userRoleJpaRepository);
        String token = tokenService.gerarToken(userEntity);
        return ResponseCookie.from("accessToken", token)
            .httpOnly(true)                     // Impede acesso via JavaScript (XSS)
            .secure(true)                      // Mudar para true em PRODUÇÃO (HTTPS)
            .path("/")          
            .sameSite("None")        
            .domain("serveousercontent.com")      // Disponível para toda a aplicação
            .maxAge(Duration.ofHours(1))            // Expiração do cookie       
            .build();
    }
    public ResponseCookie gerarCookieRefresh(Usuario usuario) {
        UserEntity userEntity = UsuarioMapper.toEntity(usuario,userRoleJpaRepository);
        var refreshToken = tokenRefreshService.criarRefreshToken(userEntity);
        return ResponseCookie.from("refreshToken", refreshToken.getToken())
            .httpOnly(true)
            .secure(true)            // Mudar para true em PRODUÇÃO
            .path("/")
            .sameSite("None")
            .domain("serveousercontent.com")
            .maxAge(Duration.ofDays(7))  // Refresh token dura mais
            .build();
    }
    public ResponseCookie gerarCookieApoioAutenticacao() {
    return ResponseCookie.from("is-authenticated", "true")
        .httpOnly(false)     
        .secure(true)       // Mudar para true em produção
        .path("/")
        .sameSite("None")
        .domain("serveousercontent.com")
        .maxAge(Duration.ofHours(1))
        .build();
    }
    public List<ResponseCookie> limparCookiesLogout() {
        return List.of(
            criarCookieLimpo("accessToken", true),
            criarCookieLimpo("refreshToken", true),
            criarCookieLimpo("is-authenticated", false)
        );
    }

    private ResponseCookie criarCookieLimpo(String nome, boolean httpOnly) {
    return ResponseCookie.from(nome, "")
        .path("/")
        .domain("serveousercontent.com") // <--- ESSENCIAL PARA O NAVEGADOR ACHAR O COOKIE
        .httpOnly(httpOnly)
        .secure(true)
        .sameSite("None")
        .maxAge(0) 
        .build();
    }
    private UserLoginResponseDTO GerarRespostaCompleta(Usuario user) {
        UserEntity userEntity = UsuarioMapper.toEntity(user,userRoleJpaRepository);
        String accessToken = tokenService.gerarToken(userEntity);
        var refreshTokenEntity = tokenRefreshService.criarRefreshToken(userEntity);

        return new UserLoginResponseDTO(
            accessToken,
            refreshTokenEntity.getToken(),
            userEntity.getEmail()
        );
    }
}