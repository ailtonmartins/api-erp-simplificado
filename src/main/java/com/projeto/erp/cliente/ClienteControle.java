package com.projeto.erp.cliente;

import com.projeto.erp.cliente.dto.ClienteResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@Tag(name = "Clientes", description = "Operações relacionadas aos clientes")
public class ClienteControle {

    @Autowired
    ClienteService clienteService;

    @GetMapping(value = "/listar")
    @Operation(summary = "Lista todos os clientes", description = "Retorna uma lista de todos os clientes cadastrados")
    public List<ClienteResponseDTO> listCliente() {
        return clienteService.findAll();
    }



}
