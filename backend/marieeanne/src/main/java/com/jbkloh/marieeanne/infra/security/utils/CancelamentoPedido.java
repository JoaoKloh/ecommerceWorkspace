package com.jbkloh.marieeanne.infra.security.utils;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jbkloh.marieeanne.core.usecases.PedidoUseCase;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CancelamentoPedido {

    private final PedidoUseCase pedidoUseCase;

    @Scheduled(fixedDelay=60000)
    @Transactional
    public void cancelarPedidoExpirado(){
        LocalDateTime linhaDeCorte = LocalDateTime.now().minusMinutes(5);
        pedidoUseCase.cancelarPedidosExpirados(linhaDeCorte);
    }
    
}
