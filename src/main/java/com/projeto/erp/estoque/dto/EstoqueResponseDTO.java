package com.projeto.erp.estoque.dto;

import com.projeto.erp.produto.dto.ProdutoResponseSemQtdDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class EstoqueResponseDTO {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "100")
    private Integer quantidade;

    private ProdutoResponseSemQtdDTO produto;
}
