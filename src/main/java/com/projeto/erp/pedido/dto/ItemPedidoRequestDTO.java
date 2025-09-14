package com.projeto.erp.pedido.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemPedidoRequestDTO {

    @NotNull(message = "ID do produto não pode ser nulo")
    @Schema(example = "1")
    private Long produtoId;

    @NotNull(message = "Quantidade não pode ser nula")
    @Positive(message = "Quantidade deve ser positiva")
    @Schema(example = "2")
    private Integer quantidade;

    @NotNull(message = "Preço unitário não pode ser nulo")
    @Positive(message = "Preço unitário deve ser positivo")
    @Schema(example = "10.50")
    private BigDecimal precoUnitario;
}
