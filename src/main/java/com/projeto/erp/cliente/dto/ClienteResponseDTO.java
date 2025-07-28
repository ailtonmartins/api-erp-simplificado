package com.projeto.erp.cliente.dto;

import lombok.Data;

@Data
public class ClienteResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private String documento;
    private String telefone;
    private Boolean ativo;

}
