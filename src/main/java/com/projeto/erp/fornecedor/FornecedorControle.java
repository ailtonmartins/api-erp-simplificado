package com.projeto.erp.fornecedor;

import com.projeto.erp.common.dto.PageResponseDTO;
import com.projeto.erp.fornecedor.dto.FornecedorRequestDTO;
import com.projeto.erp.fornecedor.dto.FornecedorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fornecedores")
@Tag(name = "Fornecedores", description = "Operações relacionadas aos Fornecedores")
public class FornecedorControle {

    @Autowired
    FornecedorService fornecedorService;

    @GetMapping(value = "/{id}")
    @Operation(summary = "Buscar fornecedor por ID", description = "Retorna os dados de um fornecedor específico pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Fornecedor encontrado"),
        @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado", content = @Content)
    })
    public FornecedorResponseDTO getFornecedor(@PathVariable Long id) {
        return fornecedorService.getFornecedor(id);
    }

    @GetMapping(value = "/listar")
    @Operation(summary = "Lista todos os fornecedors", description = "Retorna uma lista de todos os fornecedors cadastrados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de fornecedors retornada com sucesso")
    })
    public PageResponseDTO<FornecedorResponseDTO> listFornecedor( @RequestParam(defaultValue = "0") Integer page,
                                                                  @RequestParam(defaultValue = "10") Integer size) {
        return fornecedorService.buscaTodosFornecedores(page , size );
    }

    @PostMapping("/criar")
    @Operation(summary = "Criar novo fornecedor", description = "Cria um novo fornecedor com os dados informados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Fornecedor criado com sucesso"),
        @ApiResponse(responseCode = "422", description = "Dados inválidos", content = @Content),
        @ApiResponse(responseCode = "409", description = "Dados Validados, mas conflito de dados", content = @Content)
    })
    public ResponseEntity<FornecedorResponseDTO> criarFornecedor(@Valid @RequestBody FornecedorRequestDTO fornecedor) {
         FornecedorResponseDTO createdFornecedor = fornecedorService.criarFornecedor(fornecedor);
         return ResponseEntity.status(HttpStatus.CREATED).body(createdFornecedor);
    }

    @PutMapping("/atualizar/{id}")
    @Operation(summary = "Atualizar fornecedor", description = "Atualiza os dados de um fornecedor existente pelo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Fornecedor atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado", content = @Content),
        @ApiResponse(responseCode = "422", description = "Dados inválidos", content = @Content),
        @ApiResponse(responseCode = "409", description = "Dados Validados, mas conflito de dados", content = @Content)
    })
    public ResponseEntity<FornecedorResponseDTO> atualizarFornecedor(@Valid  @PathVariable Long id, @RequestBody FornecedorRequestDTO fornecedor) {
        FornecedorResponseDTO updatedFornecedor = fornecedorService.atualizarFornecedor(id, fornecedor);
        return ResponseEntity.ok(updatedFornecedor);
    }

    @DeleteMapping("/deletar/{id}")
    @Operation(summary = "Deletar fornecedor", description = "Remove um fornecedor existente pelo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Fornecedor deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado", content = @Content)
    })
    public ResponseEntity<Void> deletarFornecedor(@PathVariable Long id) {
        fornecedorService.deleteFornecedor(id);
        return ResponseEntity.noContent().build();
    }
}
