package com.projeto.erp.estoque.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EstoqueRequestDTO {

    @NotNull
    @Min(1)
    private Integer quantidade;

    @NotNull
    private Long idProduto;
}
