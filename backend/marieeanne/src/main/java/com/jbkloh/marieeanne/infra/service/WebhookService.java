package com.jbkloh.marieeanne.infra.service;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.jbkloh.marieeanne.infra.exceptions.AppException;
import com.mercadopago.MercadoPagoConfig; // 👈 Importação necessária
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookService {
    @Value("${mercadopago.webhook.secret}")
    private String webhookSecret;

    @Value("${api.mercadopago.acess.token}") 
    private String accessToken;

    private final PedidoService pedidoService;

    public void processMercadoPagoWebhook(
        String action,
        String type,
        String transacaoId,
        LocalDateTime dataCreated,
        String resourceId, 
        String signatureHeader, 
        String requestId) throws MPException, MPApiException {
        try {
            String ts = "";
            String v1 = "";
            String[] parts = signatureHeader.split(",");
            for (String part : parts) {
                String[] keyValue = part.trim().split("=");
                if (keyValue.length == 2) {
                    if ("ts".equals(keyValue[0])) ts = keyValue[1];
                    if ("v1".equals(keyValue[0])) v1 = keyValue[1];
                }
            }
            log.info("O processamento das info foi feito");

            // 2. Montar a String de manifesto exatamente como o Mercado Pago exige
            String manifest = String.format("id:%s;request-id:%s;ts:%s;", resourceId, requestId, ts);

            // 3. Gerar o hash HMAC-SHA256
            Mac sha256HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256HMAC.init(secretKey);
            
            byte[] hashBytes = sha256HMAC.doFinal(manifest.getBytes(StandardCharsets.UTF_8));
            
            // Converte os bytes gerados para uma String Hexadecimal
            String generatedSignature = HexFormat.of().formatHex(hashBytes);

            // 4. Validação final da assinatura
            if (!generatedSignature.equalsIgnoreCase(v1)) {
                throw new SecurityException("Assinatura do webhook inválida. Verifique se o Secret está correto.");
            }

            log.info("Avaliando Type de fato recebido: \"{}\"", type);

            // Verifica se não é nulo e processa o pagamento
            if (type != null && "payment".equalsIgnoreCase(type.trim())) {
                
                // 🔥 CORREÇÃO 1: Configura o token na SDK antes de inicializar o client
                MercadoPagoConfig.setAccessToken(this.accessToken);
                
                PaymentClient client = new PaymentClient();
                
                String limpandoId = resourceId.replaceAll("[^0-9]", "");
                
                if (limpandoId.isEmpty()) {
                    throw new IllegalArgumentException("O resourceId recebido não possui caracteres numéricos válidos: " + resourceId);
                }

                Payment payment = client.get(Long.valueOf(limpandoId));
                String externalReference = payment.getExternalReference();
                
                if (externalReference != null) {
                    Long meuPedidoId = Long.valueOf(externalReference); 
                    log.info("Sucesso! Entrando no pedido service para o pedido ID: {}", meuPedidoId);
                    pedidoService.processarPedidoWebhook(meuPedidoId, action, type, limpandoId, LocalDateTime.now());
                } else {
                    log.warn("Aviso: O pagamento do Mercado Pago não possui un 'external_reference' (ID do pedido) associado.");
                }
                
            } else {
                log.error("Erro de Fluxo: O Webhook foi rejeitado porque o type recebido foi '{}' e esperávamos 'payment'.", type);
                throw new AppException("Houve uma falha na comunicação com o pagamento. Tente novamente mais tarde.", HttpStatus.BAD_GATEWAY);
            }

        // 🔥 CORREÇÃO 2: Captura inteligente do erro da API para revelar o JSON real do Mercado Pago
        } catch (MPApiException e) {
            log.error("❌ Erro detalhado retornado pela API do Mercado Pago:");
            log.error("Status HTTP retornado: {}", e.getStatusCode());
            if (e.getApiResponse() != null) {
                log.error("JSON de resposta do erro: {}", e.getApiResponse().getContent());
            }
            throw e; 
        } catch (SecurityException e) {
            throw e;
        } catch (IllegalStateException | InvalidKeyException | NoSuchAlgorithmException e) {
            throw new SecurityException("Falha crítica no cálculo da assinatura criptográfica", e);
        }
    }
}