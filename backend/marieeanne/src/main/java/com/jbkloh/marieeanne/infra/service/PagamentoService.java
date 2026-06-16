package com.jbkloh.marieeanne.infra.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.jbkloh.marieeanne.core.models.Pedido;
import com.jbkloh.marieeanne.core.usecases.PagamentoUseCase;
import com.jbkloh.marieeanne.infra.dtos.pagamento.ProcessamentoPgRequestDTO;
import com.jbkloh.marieeanne.infra.exceptions.AppException;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;

import lombok.RequiredArgsConstructor; 
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PagamentoService {

    @Value("${api.mercadopago.acess.token}")
    private String accessToken;
    private final PagamentoUseCase pagamentoUseCase; 
    

    public String criarPreferenciaPagamento(Pedido pedido) {
        pagamentoUseCase.validarPedidoParaPagamento(pedido.getId());
    
        try {
            MercadoPagoConfig.setAccessToken(accessToken);
            PreferenceClient client = new PreferenceClient();
    
            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .id(pedido.getId().toString())
                    .title("Buffet Marie e Anne - Pedido #" + pedido.getId())
                    .quantity(1)
                    .unitPrice(pedido.getValor()) 
                    .build();
    
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(Collections.singletonList(itemRequest))
                    .externalReference(pedido.getId().toString())   
                    .notificationUrl("https://marie-anne-api.serveousercontent.com/v1/pagamentos/webhook/mercadopago")
                    .build();
    
            Map<String, String> headers = new HashMap<>();
            headers.put("X-Request-ID", "pref-pedido-" + pedido.getId());
            MPRequestOptions requestOptions = MPRequestOptions.builder()
                                .customHeaders(headers)
                                .build();
            log.info("Enviando requisição de preferência para o Mercado Pago do pedido #{}", pedido.getId());
           
            Preference preference = client.create(preferenceRequest, requestOptions);
            
            return preference.getId(); 

        } catch (MPApiException apiEx) {
            log.error("Erro na API Mercado Pago Preference (HTTP {}): {}", 
                apiEx.getStatusCode(), apiEx.getApiResponse().getContent());
            throw new AppException("Erro ao gerar preferência de pagamento: " + apiEx.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (MPException mpEx) {
            log.error("Erro técnico no SDK Mercado Pago ao gerar preferência: ", mpEx);
            throw new AppException("Sistema de pagamentos offline.", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
    public Map<String, Object> enviarProcessamentoAoMp(ProcessamentoPgRequestDTO dto, Pedido p) {
        try {
            MercadoPagoConfig.setAccessToken(accessToken);
            PaymentClient client = new PaymentClient();

            String nomeCompleto = p.getCliente().getNome() != null ? p.getCliente().getNome().trim() : "Cliente Marie Anne";
            String firstName = nomeCompleto;
            String lastName = "";
            int primeiroEspaco = nomeCompleto.indexOf(" ");
            if (primeiroEspaco > 0) {
                firstName = nomeCompleto.substring(0, primeiroEspaco).trim();
                lastName = nomeCompleto.substring(primeiroEspaco).trim();
            }

            String cpfLimpo = p.getCliente().getCpf() != null 
                ? p.getCliente().getCpf().replaceAll("[^0-9]", "") 
                : "00000000000";

            log.info(cpfLimpo);
            log.info(firstName);
            log.info(lastName);

            PaymentPayerRequest payerRequest = PaymentPayerRequest.builder()
                    .email(p.getCliente().getUser().getEmail().trim())
                    .firstName(firstName)
                    .lastName(lastName)
                    .identification(
                        IdentificationRequest.builder()
                            .type("CPF")
                            .number(cpfLimpo)
                            .build()
                    )
                    .build();

            java.math.BigDecimal valorBigDecimal = (p.getValor() instanceof java.math.BigDecimal) 
                ? (java.math.BigDecimal) p.getValor() 
                : new java.math.BigDecimal(p.getValor().toString());

            // 💡 Correção do Hint: O método installments da SDK aceita Integer direto, mantemos limpo
            Integer parcelas = dto.installments() != null ? dto.installments() : 1;

            // Montagem da requisição principal
            PaymentCreateRequest paymentCreateRequest = PaymentCreateRequest.builder()
                    .token(dto.token() != null && !dto.token().isBlank() ? dto.token() : null)
                    .transactionAmount(valorBigDecimal)
                    .description(dto.description() != null ? dto.description() : "Pedido #" + p.getId())
                    .paymentMethodId(dto.paymentMethodId())
                    .externalReference(String.valueOf(p.getId()))
                    .installments(parcelas)
                    .notificationUrl("https://marie-anne-api.serveousercontent.com/api/v1/webhook/mercadopago")
                    .payer(payerRequest)
                    .build();

            Map<String, String> headers = new HashMap<>();
            headers.put("X-Idempotency-Key", UUID.randomUUID().toString());
            MPRequestOptions requestOptions = MPRequestOptions.builder().customHeaders(headers).build();

            log.info("Enviando pagamento do pedido #{} via SDK para o Mercado Pago", p.getId());
            Payment payment = client.create(paymentCreateRequest, requestOptions);

            // Mapeamento manual seguro dos campos essenciais para o Frontend
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("id", payment.getId());
            resultado.put("status", payment.getStatus());
            resultado.put("status_detail", payment.getStatusDetail());
            resultado.put("payment_method_id", payment.getPaymentMethodId());
            
            // 🔥 EM VEZ DE USAR O RETORNO DO MP, RETORNE OS DADOS QUE VOCÊ JÁ TEM
            Map<String, Object> payerResponse = new HashMap<>();
            payerResponse.put("id", payment.getPayer() != null ? payment.getPayer().getId() : null);
            payerResponse.put("email", p.getCliente().getUser().getEmail().trim());
            payerResponse.put("first_name", firstName);
            payerResponse.put("last_name", lastName);
            
            Map<String, Object> identificationResponse = new HashMap<>();
            identificationResponse.put("type", "CPF");
            identificationResponse.put("number", cpfLimpo);
            payerResponse.put("identification", identificationResponse);
            
            resultado.put("payer", payerResponse); // Retorna o payer populado para o front-end
            
            // Se for Pix, extrai os blocos específicos para o QR code
            if (payment.getPointOfInteraction() != null && 
                payment.getPointOfInteraction().getTransactionData() != null) {
                
                var txData = payment.getPointOfInteraction().getTransactionData();
                
                Map<String, Object> pointOfInteraction = new HashMap<>();
                Map<String, Object> transactionData = new HashMap<>();
                
                transactionData.put("qr_code", txData.getQrCode());
                transactionData.put("qr_code_base64", txData.getQrCodeBase64());
                
                pointOfInteraction.put("transaction_data", transactionData);
                resultado.put("point_of_interaction", pointOfInteraction);
            }

            return resultado;

        } catch (MPApiException apiEx) {
            log.error("❌ Erro de validação/API Mercado Pago (HTTP {}): {}", apiEx.getApiResponse().getContent());
            throw new RuntimeException("Campos inválidos enviados ao MP: " + apiEx.getMessage(), apiEx);
        } catch (MPException mpEx) {
            log.error("🔥 Erro técnico na SDK do Mercado Pago: ", mpEx);
            throw new RuntimeException("Erro de comunicação com o Mercado Pago: " + mpEx.getMessage(), mpEx);
        }
    }
}