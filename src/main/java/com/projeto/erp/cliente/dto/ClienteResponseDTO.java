package com.projeto.erp.cliente.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class ClienteResponseDTO {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "Ailton Martins")
    private String nome;

    @Schema(example = "ailton.martins@teste.com")
    private String email;

    @Schema(example = "12345678901")
    private String documento;

    @Schema(example = "11999998888")
    private String telefone;

    @Schema(example = "true")
    private Boolean ativo;

}
