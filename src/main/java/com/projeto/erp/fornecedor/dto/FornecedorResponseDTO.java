package com.projeto.erp.fornecedor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FornecedorResponseDTO {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "Empresa A")
    private String nome;

    @Schema(example = "empresa.a@teste.com")
    private String email;

    @Schema(example = "12345678901")
    private String documento;

    @Schema(example = "11999998888")
    private String telefone;

    @Schema(example = "true")
    private Boolean ativo;

}
