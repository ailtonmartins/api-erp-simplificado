package com.projeto.erp.estoque;

import com.projeto.erp.estoque.dto.EstoqueRequestDTO;
import com.projeto.erp.estoque.dto.EstoqueResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/estoque")
@Tag(name = "Estoque", description = "Operações relacionadas ao estoque")
public class EstoqueController {

    @Autowired
    private EstoqueService estoqueService;

    @PostMapping("/entrada")
    @Operation(summary = "Registrar entrada de estoque", description = "Adiciona quantidade ao estoque de um produto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Entrada registrada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content)
    })
    public ResponseEntity<EstoqueResponseDTO> entrada(@RequestBody @Valid EstoqueRequestDTO dto) {
        return ResponseEntity.ok(estoqueService.entradaEstoque(dto));
    }

    @PostMapping("/saida")
    @Operation(summary = "Registrar saída de estoque", description = "Remove quantidade do estoque de um produto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Saída registrada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado ou estoque insuficiente", content = @Content)
    })
    public ResponseEntity<EstoqueResponseDTO> saida(@RequestBody @Valid EstoqueRequestDTO dto) {
        return ResponseEntity.ok(estoqueService.saidaEstoque(dto));
    }

    @GetMapping("/saldo/{produtoId}")
    @Operation(summary = "Consultar saldo de estoque", description = "Retorna o saldo atual do estoque para um produto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Saldo retornado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content)
    })
    public ResponseEntity<EstoqueResponseDTO> saldo(@PathVariable Long produtoId) {
        return  ResponseEntity.ok(estoqueService.consultarSaldo(produtoId));
    }
}