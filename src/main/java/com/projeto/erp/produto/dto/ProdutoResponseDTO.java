package com.projeto.erp.produto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProdutoResponseDTO {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "Notebook Dell Inspiron")
    private String nome;

    @Schema(example = "Notebook com 16GB RAM e SSD 512GB")
    private String descricao;

    @Schema(example = "7891234567890")
    private String codigoBarras;

    @Schema(example = "3500.00")
    private BigDecimal preco;

    @Schema(example = "1")
    private Long fornecedorId;

    @Schema(example = "Dell Computadores")
    private String fornecedorNome;
}

