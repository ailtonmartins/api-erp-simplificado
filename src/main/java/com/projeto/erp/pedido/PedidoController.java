package com.projeto.erp.pedido;

import com.projeto.erp.common.dto.PageResponseDTO;
import com.projeto.erp.common.exception.BusinessException;
import com.projeto.erp.pedido.dto.PedidoRequestDTO;
import com.projeto.erp.pedido.dto.PedidoResponseDTO;
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

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
@Tag(name = "Pedidos", description = "Operações relacionadas aos pedidos/vendas")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping("/criar")
    @Operation(summary = "Criar novo pedido", description = "Cria um novo pedido relacionando cliente e itens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cliente ou produto não encontrado", content = @Content)
    })
    public ResponseEntity<PedidoResponseDTO> criarPedido(@Valid @RequestBody PedidoRequestDTO pedidoRequestDTO) {
        PedidoResponseDTO pedido = pedidoService.criarPedido(pedidoRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }

    @PutMapping("/{id}/processar")
    @Operation(summary = "Processar pedido", description = "Muda o status do pedido de ABERTO para PROCESSANDO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido processado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Pedido não pode ser processado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado", content = @Content)
    })
    public ResponseEntity<PedidoResponseDTO> processarPedido(@PathVariable Long id) {
        PedidoResponseDTO pedido = pedidoService.processarPedido(id);
        return ResponseEntity.ok(pedido);
    }

    @PutMapping("/{id}/concluir")
    @Operation(summary = "Concluir pedido", description = "Conclui um pedido e atualiza o estoque dos produtos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido concluído com sucesso"),
            @ApiResponse(responseCode = "400", description = "Pedido não pode ser concluído", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado", content = @Content)
    })
    public ResponseEntity<PedidoResponseDTO> concluirPedido(@PathVariable Long id) {
        PedidoResponseDTO pedido = pedidoService.concluirPedido(id);
        return ResponseEntity.ok(pedido);
    }

    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar pedido", description = "Cancela um pedido em aberto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido cancelado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Pedido não pode ser cancelado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado", content = @Content)
    })
    public ResponseEntity<PedidoResponseDTO> cancelarPedido(@PathVariable Long id) {
        PedidoResponseDTO pedido = pedidoService.cancelarPedido(id);
        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID", description = "Retorna os dados de um pedido específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado", content = @Content)
    })
    public ResponseEntity<PedidoResponseDTO> buscarPorId(@PathVariable Long id) {
        PedidoResponseDTO pedido = pedidoService.buscarPorId(id);
        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/listar")
    @Operation(summary = "Listar todos os pedidos", description = "Retorna uma lista paginada de todos os pedidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pedidos retornada com sucesso")
    })
    public ResponseEntity<PageResponseDTO<PedidoResponseDTO>> listarTodos(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        PageResponseDTO<PedidoResponseDTO> pedidos = pedidoService.listarTodos(page, size);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar pedidos por cliente", description = "Retorna todos os pedidos de um cliente específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pedidos do cliente retornada com sucesso")
    })
    public ResponseEntity<List<PedidoResponseDTO>> listarPorCliente(@PathVariable Long clienteId) {
        List<PedidoResponseDTO> pedidos = pedidoService.listarPorCliente(clienteId);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Listar pedidos por status", description = "Retorna todos os pedidos com um status específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pedidos por status retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status inválido", content = @Content)
    })
    public ResponseEntity<List<PedidoResponseDTO>> listarPorStatus(@PathVariable String status) {
        try {
            Pedido.StatusPedido statusEnum = Pedido.StatusPedido.valueOf(status.toUpperCase());
            List<PedidoResponseDTO> pedidos = pedidoService.listarPorStatus(statusEnum);
            return ResponseEntity.ok(pedidos);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Status inválido: " + status + ". Status válidos: " +
                Arrays.toString(Pedido.StatusPedido.values()), HttpStatus.BAD_REQUEST);
        }
    }
}
