package com.projeto.erp.cliente;

import com.projeto.erp.cliente.dto.ClienteRequestDTO;
import com.projeto.erp.cliente.dto.ClienteResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@Tag(name = "Clientes", description = "Operações relacionadas aos clientes")
public class ClienteControle {

    @Autowired
    ClienteService clienteService;

    @GetMapping(value = "/{id}")
    public ClienteResponseDTO getCliente( Long id ) {
        return clienteService.getCliente( id );
    }

    @GetMapping(value = "/listar")
    @Operation(summary = "Lista todos os clientes", description = "Retorna uma lista de todos os clientes cadastrados")
    public List<ClienteResponseDTO> listCliente() {
        return clienteService.findAll();
    }

    @PostMapping("/criar")
    public ResponseEntity<ClienteResponseDTO> criarCliente(@RequestBody ClienteRequestDTO cliente) {
         ClienteResponseDTO createdCliente = clienteService.createCliente(cliente);
         return ResponseEntity.status(HttpStatus.CREATED).body(createdCliente);
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizarCliente(@PathVariable Long id, @RequestBody ClienteRequestDTO cliente) {
        ClienteResponseDTO updatedCliente = clienteService.updateCliente(id, cliente);
        return ResponseEntity.ok(updatedCliente);
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        clienteService.deleteCliente(id);
        return ResponseEntity.noContent().build();
    }
}
