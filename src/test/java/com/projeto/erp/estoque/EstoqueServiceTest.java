package com.projeto.erp.estoque;

import com.projeto.erp.common.exception.BusinessException;
import com.projeto.erp.estoque.dto.EstoqueRequestDTO;
import com.projeto.erp.estoque.dto.EstoqueResponseDTO;
import com.projeto.erp.estoque.mapper.EstoqueMapper;
import com.projeto.erp.produto.Produto;
import com.projeto.erp.produto.ProdutoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EstoqueServiceTest {

    @Mock
    private EstoqueRepository estoqueRepository;

    @Mock
    private EstoqueMapper estoqueMapper;

    @Mock
    private ProdutoService produtoService;

    @InjectMocks
    private EstoqueService estoqueService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testEntradaEstoque_CriaNovoEstoqueQuandoNaoExiste() {
        EstoqueRequestDTO request = new EstoqueRequestDTO();
        request.setIdProduto(1L);
        request.setQuantidade(5);

        Produto produto = new Produto();
        produto.setId(1L);

        Estoque saved = Estoque.builder().id(1L).produto(produto).quantidade(5).build();
        EstoqueResponseDTO responseDTO = new EstoqueResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setQuantidade(5);

        when(produtoService.buscaProdutoByIdOrThrow(1L)).thenReturn(produto);
        when(estoqueRepository.findByProdutoId(1L)).thenReturn(Optional.empty());
        when(estoqueRepository.save(any(Estoque.class))).thenReturn(saved);
        when(estoqueMapper.toDTO(saved)).thenReturn(responseDTO);

        EstoqueResponseDTO result = estoqueService.entradaEstoque(request);

        assertNotNull(result);
        assertEquals(5, result.getQuantidade());
        verify(estoqueRepository).findByProdutoId(1L);
        verify(estoqueRepository, times(2)).save(any(Estoque.class));
    }

    @Test
    void testEntradaEstoque_AtualizaEstoqueExistente() {
        EstoqueRequestDTO request = new EstoqueRequestDTO();
        request.setIdProduto(1L);
        request.setQuantidade(3);

        Produto produto = new Produto();
        produto.setId(1L);

        Estoque existente = Estoque.builder().id(2L).produto(produto).quantidade(4).build();
        Estoque updated = Estoque.builder().id(2L).produto(produto).quantidade(7).build();

        EstoqueResponseDTO responseDTO = new EstoqueResponseDTO();
        responseDTO.setId(2L);
        responseDTO.setQuantidade(7);

        when(produtoService.buscaProdutoByIdOrThrow(1L)).thenReturn(produto);
        when(estoqueRepository.findByProdutoId(1L)).thenReturn(Optional.of(existente));
        when(estoqueRepository.save(any(Estoque.class))).thenReturn(updated);
        when(estoqueMapper.toDTO(updated)).thenReturn(responseDTO);

        EstoqueResponseDTO result = estoqueService.entradaEstoque(request);

        assertNotNull(result);
        assertEquals(7, result.getQuantidade());
        verify(estoqueRepository).findByProdutoId(1L);
        verify(estoqueRepository).save(any(Estoque.class));
    }

    @Test
    void testSaidaEstoque_Sucesso() {
        EstoqueRequestDTO request = new EstoqueRequestDTO();
        request.setIdProduto(1L);
        request.setQuantidade(2);

        Produto produto = new Produto();
        produto.setId(1L);

        Estoque existente = Estoque.builder().id(3L).produto(produto).quantidade(5).build();
        Estoque updated = Estoque.builder().id(3L).produto(produto).quantidade(3).build();

        EstoqueResponseDTO responseDTO = new EstoqueResponseDTO();
        responseDTO.setId(3L);
        responseDTO.setQuantidade(3);

        when(estoqueRepository.findByProdutoId(1L)).thenReturn(Optional.of(existente));
        when(estoqueRepository.save(any(Estoque.class))).thenReturn(updated);
        when(estoqueMapper.toDTO(updated)).thenReturn(responseDTO);

        EstoqueResponseDTO result = estoqueService.saidaEstoque(request);

        assertNotNull(result);
        assertEquals(3, result.getQuantidade());
        verify(estoqueRepository).findByProdutoId(1L);
        verify(estoqueRepository).save(any(Estoque.class));
    }

    @Test
    void testSaidaEstoque_SaldoInsuficiente() {
        EstoqueRequestDTO request = new EstoqueRequestDTO();
        request.setIdProduto(1L);
        request.setQuantidade(10);

        Estoque existente = Estoque.builder().id(4L).quantidade(5).build();

        when(estoqueRepository.findByProdutoId(1L)).thenReturn(Optional.of(existente));

        BusinessException ex = assertThrows(BusinessException.class, () -> estoqueService.saidaEstoque(request));
        assertEquals("Saldo insuficiente no estoque", ex.getMessage());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ex.getStatus());
        verify(estoqueRepository).findByProdutoId(1L);
        verify(estoqueRepository, never()).save(any(Estoque.class));
    }

    @Test
    void testSaidaEstoque_EstoqueNaoEncontrado() {
        EstoqueRequestDTO request = new EstoqueRequestDTO();
        request.setIdProduto(99L);
        request.setQuantidade(1);

        when(estoqueRepository.findByProdutoId(99L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> estoqueService.saidaEstoque(request));
        assertEquals("Estoque não encontrado", ex.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        verify(estoqueRepository).findByProdutoId(99L);
    }

    @Test
    void testConsultarSaldo_Sucesso() {
        Produto produto = new Produto();
        produto.setId(5L);

        Estoque existente = Estoque.builder().id(6L).produto(produto).quantidade(8).build();
        EstoqueResponseDTO responseDTO = new EstoqueResponseDTO();
        responseDTO.setId(6L);
        responseDTO.setQuantidade(8);

        when(estoqueRepository.findByProdutoId(5L)).thenReturn(Optional.of(existente));
        when(estoqueMapper.toDTO(existente)).thenReturn(responseDTO);

        EstoqueResponseDTO result = estoqueService.consultarSaldo(5L);

        assertNotNull(result);
        assertEquals(8, result.getQuantidade());
        verify(estoqueRepository).findByProdutoId(5L);
    }

    @Test
    void testConsultarSaldo_NaoEncontrado() {
        when(estoqueRepository.findByProdutoId(7L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> estoqueService.consultarSaldo(7L));
        assertEquals("Estoque não encontrado", ex.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        verify(estoqueRepository).findByProdutoId(7L);
    }
}
