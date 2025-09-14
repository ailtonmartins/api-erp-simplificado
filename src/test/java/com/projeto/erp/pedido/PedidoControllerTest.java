package com.projeto.erp.pedido;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.erp.common.dto.PageResponseDTO;
import com.projeto.erp.common.exception.BusinessException;
import com.projeto.erp.common.exception.GlobalExceptionHandler;
import com.projeto.erp.pedido.dto.ItemPedidoRequestDTO;
import com.projeto.erp.pedido.dto.ItemPedidoResponseDTO;
import com.projeto.erp.pedido.dto.PedidoRequestDTO;
import com.projeto.erp.pedido.dto.PedidoResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PedidoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private PedidoController pedidoController;

    private ObjectMapper objectMapper;
    private PedidoRequestDTO pedidoRequestDTO;
    private PedidoResponseDTO pedidoResponseDTO;
    private ItemPedidoRequestDTO itemPedidoRequestDTO;
    private ItemPedidoResponseDTO itemPedidoResponseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pedidoController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
        objectMapper = new ObjectMapper();

        // Setup item request
        itemPedidoRequestDTO = new ItemPedidoRequestDTO();
        itemPedidoRequestDTO.setProdutoId(1L);
        itemPedidoRequestDTO.setQuantidade(2);
        itemPedidoRequestDTO.setPrecoUnitario(BigDecimal.valueOf(10.50));

        // Setup pedido request
        pedidoRequestDTO = new PedidoRequestDTO();
        pedidoRequestDTO.setClienteId(1L);
        pedidoRequestDTO.setItens(Arrays.asList(itemPedidoRequestDTO));

        // Setup item response
        itemPedidoResponseDTO = new ItemPedidoResponseDTO();
        itemPedidoResponseDTO.setId(1L);
        itemPedidoResponseDTO.setProdutoId(1L);
        itemPedidoResponseDTO.setProdutoNome("Produto Teste");
        itemPedidoResponseDTO.setQuantidade(2);
        itemPedidoResponseDTO.setPrecoUnitario(BigDecimal.valueOf(10.50));
        itemPedidoResponseDTO.setSubtotal(BigDecimal.valueOf(21.00));

        // Setup pedido response
        pedidoResponseDTO = new PedidoResponseDTO();
        pedidoResponseDTO.setId(1L);
        pedidoResponseDTO.setClienteId(1L);
        pedidoResponseDTO.setClienteNome("João Silva");
        pedidoResponseDTO.setDataPedido(LocalDateTime.now());
        pedidoResponseDTO.setStatus(Pedido.StatusPedido.ABERTO);
        pedidoResponseDTO.setTotal(BigDecimal.valueOf(21.00));
        pedidoResponseDTO.setItens(Arrays.asList(itemPedidoResponseDTO));
    }

    @Test
    void testCriarPedido_Sucesso() throws Exception {
        // Arrange
        when(pedidoService.criarPedido(any(PedidoRequestDTO.class)))
            .thenReturn(pedidoResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/pedidos/criar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.clienteId").value(1L))
                .andExpect(jsonPath("$.clienteNome").value("João Silva"))
                .andExpect(jsonPath("$.status").value("ABERTO"))
                .andExpect(jsonPath("$.total").value(21.00))
                .andExpect(jsonPath("$.itens[0].produtoId").value(1L))
                .andExpect(jsonPath("$.itens[0].quantidade").value(2))
                .andExpect(jsonPath("$.itens[0].subtotal").value(21.00));
    }

    @Test
    void testCriarPedido_ClienteNaoEncontrado() throws Exception {
        // Arrange
        when(pedidoService.criarPedido(any(PedidoRequestDTO.class)))
            .thenThrow(new BusinessException("Cliente não encontrado", HttpStatus.NOT_FOUND));

        // Act & Assert
        mockMvc.perform(post("/pedidos/criar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCriarPedido_DadosInvalidos() throws Exception {
        // Arrange
        PedidoRequestDTO requestInvalido = new PedidoRequestDTO();
        // Não define clienteId nem itens

        // Act & Assert
        mockMvc.perform(post("/pedidos/criar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testProcessarPedido_Sucesso() throws Exception {
        // Arrange
        pedidoResponseDTO.setStatus(Pedido.StatusPedido.PROCESSANDO);
        when(pedidoService.processarPedido(1L)).thenReturn(pedidoResponseDTO);

        // Act & Assert
        mockMvc.perform(put("/pedidos/1/processar"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("PROCESSANDO"));
    }

    @Test
    void testProcessarPedido_PedidoNaoEncontrado() throws Exception {
        // Arrange
        when(pedidoService.processarPedido(1L))
            .thenThrow(new BusinessException("Pedido não encontrado", HttpStatus.NOT_FOUND));

        // Act & Assert
        mockMvc.perform(put("/pedidos/1/processar"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testConcluirPedido_Sucesso() throws Exception {
        // Arrange
        pedidoResponseDTO.setStatus(Pedido.StatusPedido.CONCLUIDO);
        when(pedidoService.concluirPedido(1L)).thenReturn(pedidoResponseDTO);

        // Act & Assert
        mockMvc.perform(put("/pedidos/1/concluir"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("CONCLUIDO"));
    }

    @Test
    void testConcluirPedido_EstoqueInsuficiente() throws Exception {
        // Arrange
        when(pedidoService.concluirPedido(1L))
            .thenThrow(new BusinessException("Estoque insuficiente para o produto", HttpStatus.BAD_REQUEST));

        // Act & Assert
        mockMvc.perform(put("/pedidos/1/concluir"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCancelarPedido_Sucesso() throws Exception {
        // Arrange
        pedidoResponseDTO.setStatus(Pedido.StatusPedido.CANCELADO);
        when(pedidoService.cancelarPedido(1L)).thenReturn(pedidoResponseDTO);

        // Act & Assert
        mockMvc.perform(put("/pedidos/1/cancelar"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("CANCELADO"));
    }

    @Test
    void testBuscarPorId_Sucesso() throws Exception {
        // Arrange
        when(pedidoService.buscarPorId(1L)).thenReturn(pedidoResponseDTO);

        // Act & Assert
        mockMvc.perform(get("/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.clienteId").value(1L))
                .andExpect(jsonPath("$.status").value("ABERTO"))
                .andExpect(jsonPath("$.total").value(21.00));
    }

    @Test
    void testBuscarPorId_NaoEncontrado() throws Exception {
        // Arrange
        when(pedidoService.buscarPorId(1L))
            .thenThrow(new BusinessException("Pedido não encontrado", HttpStatus.NOT_FOUND));

        // Act & Assert
        mockMvc.perform(get("/pedidos/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testListarTodos_Sucesso() throws Exception {
        // Arrange
        PageResponseDTO<PedidoResponseDTO> pageResponse = new PageResponseDTO<>(
            Arrays.asList(pedidoResponseDTO), 0, 10, 1L, 1, true, true);
        when(pedidoService.listarTodos(0, 10)).thenReturn(pageResponse);

        // Act & Assert
        mockMvc.perform(get("/pedidos/listar")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testListarTodos_ParametrosDefault() throws Exception {
        // Arrange
        PageResponseDTO<PedidoResponseDTO> pageResponse = new PageResponseDTO<>(
            Arrays.asList(pedidoResponseDTO), 0, 10, 1L, 1, true, true);
        when(pedidoService.listarTodos(0, 10)).thenReturn(pageResponse);

        // Act & Assert
        mockMvc.perform(get("/pedidos/listar"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void testListarPorCliente_Sucesso() throws Exception {
        // Arrange
        List<PedidoResponseDTO> pedidos = Arrays.asList(pedidoResponseDTO);
        when(pedidoService.listarPorCliente(1L)).thenReturn(pedidos);

        // Act & Assert
        mockMvc.perform(get("/pedidos/cliente/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].clienteId").value(1L));
    }

    @Test
    void testListarPorStatus_Sucesso() throws Exception {
        // Arrange
        List<PedidoResponseDTO> pedidos = Arrays.asList(pedidoResponseDTO);
        when(pedidoService.listarPorStatus(Pedido.StatusPedido.ABERTO)).thenReturn(pedidos);

        // Act & Assert
        mockMvc.perform(get("/pedidos/status/ABERTO"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("ABERTO"));
    }

    @Test
    void testListarPorStatus_StatusInvalido() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/pedidos/status/INVALID_STATUS"))
                .andExpect(status().isBadRequest());
    }
}
