package com.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    @Schema(description = "Data e hora em que o erro ocorreu", example = "2024-04-25T14:30:00")
    private final LocalDateTime timestamp;

    @Schema(description = "Código HTTP do erro", example = "404")
    private final int status;

    @Schema(description = "Tipo do erro", example = "Not Found")
    private final String error;

    @Schema(description = "Mensagem detalhada do erro", example = "Recurso com ID 10 não encontrado.")
    private final String message;

    @Schema(description = "Caminho da requisição que gerou o erro", example = "/products/10")
    private final String path;
}