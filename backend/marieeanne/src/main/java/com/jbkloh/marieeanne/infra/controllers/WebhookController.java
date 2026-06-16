package com.jbkloh.marieeanne.infra.controllers;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jbkloh.marieeanne.infra.service.WebhookService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/webhook")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {
    
    private final WebhookService webhookService;

    @PostMapping("/mercadopago")
    public ResponseEntity<Void> receberWebhook(
            @RequestHeader("X-Signature") String signature,
            @RequestHeader("X-Request-Id") String requestId,
            @RequestBody Map<String, Object> payload) {
        log.info("A requisição chegou com as informações no body1:" + signature);
        log.info("A requisição chegou com as informações no body2:" + requestId);

        log.info("A requisição chegou com as informações no body:" + payload);
        String resourceId = null;
        if (payload.containsKey("data") && payload.get("data") instanceof Map) {
            Map<?, ?> dataMap = (Map<?, ?>) payload.get("data");
            if (dataMap.containsKey("id")) {
                resourceId = String.valueOf(dataMap.get("id"));
            }
        }
        
        // Fallback caso o ID principal da raiz mude por algum motivo
        if (resourceId == null && payload.containsKey("id")) {
            resourceId = String.valueOf(payload.get("id"));
        }

        String action = payload.containsKey("action") ? String.valueOf(payload.get("action")) : null;
        String type = payload.containsKey("type") ? String.valueOf(payload.get("type")) : null;
        String transacaoId = payload.containsKey("id") ? String.valueOf(payload.get("id")) : null;

        LocalDateTime dataCreated = null;
            if (payload.containsKey("date_created") && payload.get("date_created") != null) {
                try {
                    String dateStr = String.valueOf(payload.get("date_created"));
                    // Suporta fusos horários (-03:00) nativos do Mercado Pago e converte para LocalDateTime
                    dataCreated = java.time.OffsetDateTime.parse(dateStr).toLocalDateTime();
                } catch (Exception e) {
                    log.error("Falha ao converter data do Webhook, usando data atual do sistema.", e);
                    dataCreated = LocalDateTime.now(); 
                }
            }
        log.info("controllor verificou se os dados chegaram");


        if (resourceId != null) {
        try {
            log.info("action: "+action);
            log.info("type: "+type);
            log.info("transacaoId: "+transacaoId);
            log.info("data de criacao: "+dataCreated);
            log.info("resource id: +"+requestId);
            log.info("siagnature: "+signature);
            log.info("request id: "+requestId);
            
            webhookService.processMercadoPagoWebhook(
                action,
                type,
                transacaoId,
                dataCreated,
                resourceId, 
                signature, 
                requestId
            );
            
            log.info("Webhook processado com sucesso!");
            return ResponseEntity.ok().build(); 
            
        } catch (com.mercadopago.exceptions.MPException | com.mercadopago.exceptions.MPApiException e) {
            log.error("Erro de comunicação com a API do Mercado Pago ao processar o webhook", e.getMessage());
            log.error(e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); 
            
        } catch (SecurityException e) {
            log.warn("Tentativa de invasão ou assinatura inválida bloqueada: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 400 para assinaturas falsas
        }
    }
    
    return ResponseEntity.ok().build();
}
}
