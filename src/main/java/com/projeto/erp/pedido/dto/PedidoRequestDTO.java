package com.projeto.erp.pedido.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PedidoRequestDTO {

    @NotNull(message = "ID do cliente não pode ser nulo")
    @Schema(example = "1")
    private Long clienteId;

    @NotEmpty(message = "Lista de itens não pode estar vazia")
    @Valid
    private List<ItemPedidoRequestDTO> itens;
}
