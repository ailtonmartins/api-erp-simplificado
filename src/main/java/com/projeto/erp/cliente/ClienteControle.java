package com.projeto.erp.cliente;

import com.projeto.erp.cliente.dto.ClienteRequestDTO;
import com.projeto.erp.cliente.dto.ClienteResponseDTO;
import com.projeto.erp.common.dto.PageResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
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
    @Operation(summary = "Buscar cliente por ID", description = "Retorna os dados de um cliente específico pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content)
    })
    public ClienteResponseDTO getCliente(@PathVariable Long id) {
        return clienteService.getCliente(id);
    }

    @GetMapping(value = "/listar")
    @Operation(summary = "Lista todos os clientes", description = "Retorna uma lista paginada de todos os clientes cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso")
    })
    public PageResponseDTO<ClienteResponseDTO> listCliente(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return clienteService.buscaTodosClientes(page, size);
    }

    @PostMapping("/criar")
    @Operation(summary = "Criar novo cliente", description = "Cria um novo cliente com os dados informados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
        @ApiResponse(responseCode = "422", description = "Dados inválidos", content = @Content),
        @ApiResponse(responseCode = "409", description = "Dados Validados, mas conflito de dados", content = @Content)
    })
    public ResponseEntity<ClienteResponseDTO> criarCliente(@Valid @RequestBody ClienteRequestDTO cliente) {
         ClienteResponseDTO createdCliente = clienteService.criarCliente(cliente);
         return ResponseEntity.status(HttpStatus.CREATED).body(createdCliente);
    }

    @PutMapping("/atualizar/{id}")
    @Operation(summary = "Atualizar cliente", description = "Atualiza os dados de um cliente existente pelo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content),
        @ApiResponse(responseCode = "422", description = "Dados inválidos", content = @Content),
        @ApiResponse(responseCode = "409", description = "Dados Validados, mas conflito de dados", content = @Content)
    })
    public ResponseEntity<ClienteResponseDTO> atualizarCliente(@Valid  @PathVariable Long id, @RequestBody ClienteRequestDTO cliente) {
        ClienteResponseDTO updatedCliente = clienteService.atualizarCliente(id, cliente);
        return ResponseEntity.ok(updatedCliente);
    }

    @DeleteMapping("/deletar/{id}")
    @Operation(summary = "Deletar cliente", description = "Remove um cliente existente pelo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cliente deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content)
    })
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        clienteService.deleteCliente(id);
        return ResponseEntity.noContent().build();
    }
}
