package com.projeto.erp.pedido;

import com.projeto.erp.cliente.Cliente;
import com.projeto.erp.cliente.ClienteRepository;
import com.projeto.erp.common.dto.PageResponseDTO;
import com.projeto.erp.common.exception.BusinessException;
import com.projeto.erp.estoque.Estoque;
import com.projeto.erp.estoque.EstoqueRepository;
import com.projeto.erp.pedido.dto.ItemPedidoRequestDTO;
import com.projeto.erp.pedido.dto.PedidoRequestDTO;
import com.projeto.erp.pedido.dto.PedidoResponseDTO;
import com.projeto.erp.pedido.mapper.PedidoMapper;
import com.projeto.erp.produto.Produto;
import com.projeto.erp.produto.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private EstoqueRepository estoqueRepository;

    @Mock
    private PedidoMapper pedidoMapper;

    @InjectMocks
    private PedidoService pedidoService;

    private Cliente cliente;
    private Produto produto;
    private Estoque estoque;
    private Pedido pedido;
    private ItemPedido itemPedido;
    private PedidoRequestDTO pedidoRequestDTO;
    private ItemPedidoRequestDTO itemPedidoRequestDTO;
    private PedidoResponseDTO pedidoResponseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Setup cliente
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setEmail("joao@email.com");

        // Setup produto
        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto Teste");
        produto.setPreco(BigDecimal.valueOf(10.50));

        // Setup estoque
        estoque = new Estoque();
        estoque.setId(1L);
        estoque.setProduto(produto);
        estoque.setQuantidade(100);

        // Setup item pedido request
        itemPedidoRequestDTO = new ItemPedidoRequestDTO();
        itemPedidoRequestDTO.setProdutoId(1L);
        itemPedidoRequestDTO.setQuantidade(2);
        itemPedidoRequestDTO.setPrecoUnitario(BigDecimal.valueOf(10.50));

        // Setup pedido request
        pedidoRequestDTO = new PedidoRequestDTO();
        pedidoRequestDTO.setClienteId(1L);
        pedidoRequestDTO.setItens(Arrays.asList(itemPedidoRequestDTO));

        // Setup item pedido
        itemPedido = new ItemPedido();
        itemPedido.setId(1L);
        itemPedido.setProduto(produto);
        itemPedido.setQuantidade(2);
        itemPedido.setPrecoUnitario(BigDecimal.valueOf(10.50));

        // Setup pedido
        pedido = new Pedido();
        pedido.setId(1L);
        pedido.setCliente(cliente);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(Pedido.StatusPedido.ABERTO);
        pedido.setTotal(BigDecimal.valueOf(21.00));
        pedido.setItens(Arrays.asList(itemPedido));

        // Setup response
        pedidoResponseDTO = new PedidoResponseDTO();
        pedidoResponseDTO.setId(1L);
        pedidoResponseDTO.setClienteId(1L);
        pedidoResponseDTO.setClienteNome("João Silva");
        pedidoResponseDTO.setStatus(Pedido.StatusPedido.ABERTO);
        pedidoResponseDTO.setTotal(BigDecimal.valueOf(21.00));
    }

    @Test
    void testCriarPedido_Sucesso() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(produtoRepository.existsById(1L)).thenReturn(true);
        when(estoqueRepository.findByProdutoId(1L)).thenReturn(Optional.of(estoque));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(pedidoMapper.toDTO(any(Pedido.class))).thenReturn(pedidoResponseDTO);

        // Act
        PedidoResponseDTO resultado = pedidoService.criarPedido(pedidoRequestDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(BigDecimal.valueOf(21.00), resultado.getTotal());
        assertEquals(Pedido.StatusPedido.ABERTO, resultado.getStatus());
        
        verify(clienteRepository).findById(1L);
        verify(produtoRepository).existsById(1L);
        verify(estoqueRepository).findByProdutoId(1L);
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void testCriarPedido_ClienteNaoEncontrado() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> pedidoService.criarPedido(pedidoRequestDTO));
        
        assertEquals("Cliente não encontrado", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        
        verify(clienteRepository).findById(1L);
        verifyNoInteractions(pedidoRepository);
    }

    @Test
    void testCriarPedido_ProdutoNaoEncontrado() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(produtoRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> pedidoService.criarPedido(pedidoRequestDTO));
        
        assertEquals("Produto com ID 1 não encontrado", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testCriarPedido_EstoqueInsuficiente() {
        // Arrange
        estoque.setQuantidade(1); // Quantidade menor que a solicitada (2)
        
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(produtoRepository.existsById(1L)).thenReturn(true);
        when(estoqueRepository.findByProdutoId(1L)).thenReturn(Optional.of(estoque));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> pedidoService.criarPedido(pedidoRequestDTO));
        
        assertEquals("Quantidade insuficiente em estoque para o produto", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testProcessarPedido_Sucesso() {
        // Arrange
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        
        Pedido pedidoProcessando = new Pedido();
        pedidoProcessando.setId(1L);
        pedidoProcessando.setStatus(Pedido.StatusPedido.PROCESSANDO);
        
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoProcessando);
        when(pedidoMapper.toDTO(any(Pedido.class))).thenReturn(pedidoResponseDTO);

        // Act
        PedidoResponseDTO resultado = pedidoService.processarPedido(1L);

        // Assert
        assertNotNull(resultado);
        verify(pedidoRepository).findById(1L);
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void testProcessarPedido_PedidoNaoAberto() {
        // Arrange
        pedido.setStatus(Pedido.StatusPedido.PROCESSANDO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> pedidoService.processarPedido(1L));
        
        assertEquals("Apenas pedidos em aberto podem ser processados", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testConcluirPedido_Sucesso() {
        // Arrange
        pedido.setStatus(Pedido.StatusPedido.PROCESSANDO);
        itemPedido.setPedido(pedido);
        
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(estoqueRepository.findByProdutoId(1L)).thenReturn(Optional.of(estoque));
        when(estoqueRepository.save(any(Estoque.class))).thenReturn(estoque);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(pedidoMapper.toDTO(any(Pedido.class))).thenReturn(pedidoResponseDTO);

        // Act
        PedidoResponseDTO resultado = pedidoService.concluirPedido(1L);

        // Assert
        assertNotNull(resultado);
        verify(estoqueRepository).save(any(Estoque.class));
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void testConcluirPedido_NaoProcessando() {
        // Arrange
        pedido.setStatus(Pedido.StatusPedido.ABERTO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> pedidoService.concluirPedido(1L));
        
        assertEquals("Apenas pedidos em processamento podem ser concluídos", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testBuscarPorId_Sucesso() {
        // Arrange
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoMapper.toDTO(pedido)).thenReturn(pedidoResponseDTO);

        // Act
        PedidoResponseDTO resultado = pedidoService.buscarPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(pedidoRepository).findById(1L);
    }

    @Test
    void testBuscarPorId_NaoEncontrado() {
        // Arrange
        when(pedidoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> pedidoService.buscarPorId(1L));
        
        assertEquals("Pedido não encontrado", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testListarTodos_Sucesso() {
        // Arrange
        Page<Pedido> pagePedidos = new PageImpl<>(Arrays.asList(pedido));
        Pageable pageable = PageRequest.of(0, 10);
        
        when(pedidoRepository.findAll(any(Pageable.class))).thenReturn(pagePedidos);
        when(pedidoMapper.toDTO(pedido)).thenReturn(pedidoResponseDTO);

        // Act
        PageResponseDTO<PedidoResponseDTO> resultado = pedidoService.listarTodos(0, 10);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        assertEquals(0, resultado.getPageNumber());
        verify(pedidoRepository).findAll(any(Pageable.class));
    }

    @Test
    void testListarPorCliente_Sucesso() {
        // Arrange
        when(pedidoRepository.findByClienteIdOrderByDataPedidoDesc(1L))
            .thenReturn(Arrays.asList(pedido));
        when(pedidoMapper.toDTO(pedido)).thenReturn(pedidoResponseDTO);

        // Act
        List<PedidoResponseDTO> resultado = pedidoService.listarPorCliente(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(pedidoRepository).findByClienteIdOrderByDataPedidoDesc(1L);
    }

    @Test
    void testListarPorStatus_Sucesso() {
        // Arrange
        when(pedidoRepository.findByStatus(Pedido.StatusPedido.ABERTO))
            .thenReturn(Arrays.asList(pedido));
        when(pedidoMapper.toDTO(pedido)).thenReturn(pedidoResponseDTO);

        // Act
        List<PedidoResponseDTO> resultado = pedidoService.listarPorStatus(Pedido.StatusPedido.ABERTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(pedidoRepository).findByStatus(Pedido.StatusPedido.ABERTO);
    }

    @Test
    void testCancelarPedido_Sucesso() {
        // Arrange
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(pedidoMapper.toDTO(any(Pedido.class))).thenReturn(pedidoResponseDTO);

        // Act
        PedidoResponseDTO resultado = pedidoService.cancelarPedido(1L);

        // Assert
        assertNotNull(resultado);
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void testCancelarPedido_PedidoConcluido() {
        // Arrange
        pedido.setStatus(Pedido.StatusPedido.CONCLUIDO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> pedidoService.cancelarPedido(1L));
        
        assertEquals("Pedidos concluídos não podem ser cancelados", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }
}
