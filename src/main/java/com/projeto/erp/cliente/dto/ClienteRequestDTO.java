package com.projeto.erp.cliente.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClienteRequestDTO {

    @NotNull(message = "Nome não pode ser nulo")
    @Size(min = 1, max = 100, message = "Nome deve ter entre 1 e 100 caracteres")
    private String nome;

    @NotNull(message = "Email não pode ser nulo")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Email inválido")
    private String email;

    @NotNull(message = "Documento não pode ser nulo")
    @Size(min = 11, max = 14, message = "Documento deve ter entre 11 e 14 caracteres")
    @Pattern(regexp = "^[0-9]+$", message = "Documento deve conter apenas números")
    private String documento;

    @NotNull(message = "Telefone não pode ser nulo")
    @Size(min = 10, max = 15, message = "Telefone deve ter entre 10 e 15 caracteres e apenas números")
    @Pattern(regexp = "^[0-9]+$", message = "Telefone deve conter apenas números")
    private String telefone;

    @NotNull(message = "Ativo não pode ser nulo")
    private Boolean ativo;
}
