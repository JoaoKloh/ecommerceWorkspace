package com.jbkloh.marieeanne.infra.security.utils;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.jbkloh.marieeanne.infra.persistence.RefreshTokenJpaRepository;

import lombok.AllArgsConstructor;


@Component
@AllArgsConstructor
public class RefreshTokenCleanup {

    private final RefreshTokenJpaRepository refreshTokenRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void deletarTokensExpirados() {
        LocalDateTime agora = LocalDateTime.now();
        refreshTokenRepository.deleteByDataExpiracaoBefore(agora);
        System.out.println("Logs: Limpeza de Refresh Tokens expirados concluída em " + agora);
    }
}
    

