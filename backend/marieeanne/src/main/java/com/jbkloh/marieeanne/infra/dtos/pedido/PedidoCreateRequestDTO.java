package com.jbkloh.marieeanne.infra.dtos.pedido;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

import org.springframework.http.HttpStatus;

import com.jbkloh.marieeanne.infra.exceptions.AppException;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

public record PedidoCreateRequestDTO(
    
    @NotNull(message = "O horário de retirada é obrigatório.")
    LocalTime horaRetirada,
    
    @NotNull(message = "A data de retirada é obrigatório.")
    @FutureOrPresent(message = "A data de retirada não pode ser uma data passada.")
    LocalDate dataRetirada
) {
    public PedidoCreateRequestDTO {

        ZoneId fusoBrasil = ZoneId.of("America/Sao_Paulo");
        LocalDateTime dataHoraRetirada = LocalDateTime.of(dataRetirada, horaRetirada);
        LocalDateTime momentoAgora = LocalDateTime.now(fusoBrasil);
            if (dataHoraRetirada.isBefore(momentoAgora)) {
                throw new AppException("O horário de retirada deve ser futuro.",HttpStatus.BAD_REQUEST);
            }

        LocalTime inicioExpediente = LocalTime.of(12, 30);
        LocalTime fimExpediente = LocalTime.MAX;

            if(horaRetirada.isBefore(inicioExpediente)|| horaRetirada.isAfter(fimExpediente)){
                throw new AppException("O horario de funcionamento do ponto é entre 13:00 ás 00:00", HttpStatus.BAD_REQUEST);
            }
        
    }
}