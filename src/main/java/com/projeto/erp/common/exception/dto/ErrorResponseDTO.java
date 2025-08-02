package com.projeto.erp.common.exception.dto;

public class ErrorResponseDTO {
    private String campo;
    private String mensagem;

    public ErrorResponseDTO(String campo, String mensagem) {
        this.campo = campo;
        this.mensagem = mensagem;
    }

    public String getCampo() {
        return campo;
    }

    public String getMensagem() {
        return mensagem;
    }
}