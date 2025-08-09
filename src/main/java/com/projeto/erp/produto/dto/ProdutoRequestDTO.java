package com.projeto.erp.produto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProdutoRequestDTO {

    @NotBlank
    @Schema(example = "Notebook Dell Inspiron")
    private String nome;

    @Schema(example = "Notebook com 16GB RAM e SSD 512GB")
    private String descricao;

    @NotBlank
    @Schema(example = "7891234567890")
    private String codigoBarras;

    @NotNull
    @DecimalMin("0.01")
    @Schema(example = "3500.00")
    private BigDecimal preco;

    @NotNull
    @Schema(example = "1")
    private Long fornecedorId;
}

