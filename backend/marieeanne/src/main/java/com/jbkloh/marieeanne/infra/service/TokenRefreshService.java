package com.jbkloh.marieeanne.infra.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jbkloh.marieeanne.infra.exceptions.AppException;
import com.jbkloh.marieeanne.infra.models.RefreshTokenEntity;
import com.jbkloh.marieeanne.infra.models.UserEntity;
import com.jbkloh.marieeanne.infra.persistence.RefreshTokenJpaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TokenRefreshService {

    @Value("${api.security.refresh.expiration-days}")
    private Long expirationDays;

    private final RefreshTokenJpaRepository refreshTokenRepository;

    public RefreshTokenEntity criarRefreshToken(UserEntity user) {
        refreshTokenRepository.deleteByUserId(user.getId());

        RefreshTokenEntity refreshToken = new RefreshTokenEntity();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setDataExpiracao(LocalDateTime.now().plusDays(expirationDays));
        refreshToken.setRevogado(false);

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public RefreshTokenEntity validarRefreshToken(String token) {
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new AppException("Refresh Token não encontrado no sistema.", HttpStatus.FORBIDDEN));

        if (refreshToken.getRevogado()) {
            throw new AppException("Este token foi revogado.", HttpStatus.UNAUTHORIZED);
        }

        if (refreshToken.estaExpirado()) {
            refreshTokenRepository.delete(refreshToken);
            throw new AppException("Refresh Token expirado. Faça login novamente.", HttpStatus.UNAUTHORIZED);
        }

        return refreshToken;
    }

    public void deletarPorUsuario(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
    
    public void deletarPeloToken(String tokenStr) {
        refreshTokenRepository.findByToken(tokenStr)
            .ifPresent(refreshTokenRepository::delete);
}
}