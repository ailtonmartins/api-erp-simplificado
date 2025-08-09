package com.projeto.erp.produto;

import com.projeto.erp.common.exception.BusinessException;
import com.projeto.erp.fornecedor.FornecedorService;
import com.projeto.erp.produto.dto.ProdutoRequestDTO;
import com.projeto.erp.produto.dto.ProdutoResponseDTO;
import com.projeto.erp.produto.mapper.ProdutoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private ProdutoMapper produtoMapper;

    @Mock
    private FornecedorService fornecedorService;

    @InjectMocks
    private ProdutoService produtoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSalvarProduto() {
        // Arrange
        ProdutoRequestDTO requestDTO = new ProdutoRequestDTO();
        Produto produto = new Produto();
        Produto savedProduto = new Produto();
        ProdutoResponseDTO responseDTO = new ProdutoResponseDTO();

        when(produtoMapper.toEntity(requestDTO)).thenReturn(produto);
        when(produtoRepository.save(produto)).thenReturn(savedProduto);
        when(produtoMapper.toDTO(savedProduto)).thenReturn(responseDTO);

        // Act
        ProdutoResponseDTO result = produtoService.criarProduto(requestDTO);

        // Assert
        assertNotNull(result);
        verify(produtoRepository, times(1)).save(produto);
    }

    @Test
    void testBuscarPorId() {
        // Arrange
        Long id = 1L;
        Produto produto = new Produto();
        ProdutoResponseDTO responseDTO = new ProdutoResponseDTO();

        when(produtoRepository.findById(id)).thenReturn(Optional.of(produto));
        when(produtoMapper.toDTO(produto)).thenReturn(responseDTO);

        // Act
        ProdutoResponseDTO result = produtoService.buscarPorId(id);

        // Assert
        assertNotNull(result);
        verify(produtoRepository, times(1)).findById(id);
    }

    @Test
    void testListarTodos() {
        // Arrange
        List<Produto> produtoes = Arrays.asList(new Produto(), new Produto());
        List<ProdutoResponseDTO> responseDTOs = Arrays.asList(new ProdutoResponseDTO(), new ProdutoResponseDTO());

        when(produtoRepository.findAll()).thenReturn(produtoes);
        when(produtoMapper.toDTO(produtoes.get(0))).thenReturn(responseDTOs.get(0));
        when(produtoMapper.toDTO(produtoes.get(1))).thenReturn(responseDTOs.get(1));

        // Act
        List<ProdutoResponseDTO> result = produtoService.listarTodos();

        // Assert
        assertEquals(2, result.size());
        verify(produtoRepository, times(1)).findAll();
    }

    @Test
    void testAtualizarProduto_QuandoProdutoExiste() {
        // Arrange
        Long produtoId = 1L;
        ProdutoRequestDTO requestDTO = new ProdutoRequestDTO();
        requestDTO.setNome("Produto Atualizado");

        Produto produtoExistente = new Produto();
        produtoExistente.setId(produtoId);
        produtoExistente.setNome("Produto Original");
        produtoExistente.setCodigoBarras("123456789");

        Produto produtoAtualizado = new Produto();
        produtoAtualizado.setId(produtoId);
        produtoAtualizado.setNome(requestDTO.getNome());

        ProdutoResponseDTO responseDTO = new ProdutoResponseDTO();
        responseDTO.setId(produtoId);
        responseDTO.setNome(requestDTO.getNome());

        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produtoExistente));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produtoAtualizado);
        when(produtoMapper.toDTO(produtoAtualizado)).thenReturn(responseDTO);
        when(produtoRepository.existsByCodigoBarras(requestDTO.getCodigoBarras())).thenReturn(false);

        // Act
        ProdutoResponseDTO resultado = produtoService.atualizarProduto(produtoId, requestDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(produtoId, resultado.getId());
        assertEquals(requestDTO.getNome(), resultado.getNome());
        assertEquals(requestDTO.getCodigoBarras(), resultado.getCodigoBarras());
        verify(produtoRepository).findById(produtoId);
        verify(produtoRepository).save(any(Produto.class));
        verify(produtoRepository).existsByCodigoBarras(requestDTO.getCodigoBarras());
    }

    @Test
    void testAtualizarProduto_QuandoProdutoNaoExiste() {
        // Arrange
        Long produtoId = 1L;
        ProdutoRequestDTO requestDTO = new ProdutoRequestDTO();
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            produtoService.atualizarProduto(produtoId, requestDTO);
        });
        assertEquals("Produto não encontrado com o ID: " + produtoId, exception.getMessage());
        verify(produtoRepository).findById(produtoId);
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    void testDeleteProduto_QuandoProdutoExiste() {
        // Arrange
        Long produtoId = 1L;
        Produto produto = new Produto();
        produto.setId(produtoId);

        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produto));
        doNothing().when(produtoRepository).delete(produto);

        // Act
        produtoService.deletarProduto(produtoId);

        // Assert
        verify(produtoRepository).findById(produtoId);
        verify(produtoRepository).delete(produto);
    }

    @Test
    void testDeleteProduto_QuandoProdutoNaoExiste() {
        // Arrange
        Long produtoId = 1L;
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            produtoService.deletarProduto(produtoId);
        });
        assertEquals("Produto não encontrado com o ID: " + produtoId, exception.getMessage());
        verify(produtoRepository).findById(produtoId);
        verify(produtoRepository, never()).delete(any(Produto.class));
    }

    @Test
    void testCriarProduto_QuandoCodigoBarraJaExiste() {
        // Arrange
        ProdutoRequestDTO requestDTO = new ProdutoRequestDTO();
        requestDTO.setCodigoBarras("123456789");
        Produto produto = new Produto();
        produto.setCodigoBarras("123456789");

        when(produtoMapper.toEntity(requestDTO)).thenReturn(produto);
        when(produtoRepository.existsByCodigoBarras(requestDTO.getCodigoBarras())).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            produtoService.criarProduto(requestDTO);
        });

        assertEquals("Código de barras já cadastrado", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        verify(produtoRepository).existsByCodigoBarras(requestDTO.getCodigoBarras());
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    void testAtualizarProduto_QuandoNovoCodigoBarraJaExiste() {
        // Arrange
        Long produtoId = 1L;
        ProdutoRequestDTO requestDTO = new ProdutoRequestDTO();
        requestDTO.setCodigoBarras("123456789");

        Produto produtoExistente = new Produto();
        produtoExistente.setId(produtoId);
        produtoExistente.setCodigoBarras("123456789novo");

        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produtoExistente));
        when(produtoRepository.existsByCodigoBarras(requestDTO.getCodigoBarras())).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            produtoService.atualizarProduto(produtoId, requestDTO);
        });

        assertEquals("Código de barras já cadastrado", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        verify(produtoRepository).findById(produtoId);
        verify(produtoRepository).existsByCodigoBarras(requestDTO.getCodigoBarras());
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    void testGetProduto_QuandoIdNaoExiste() {
        // Arrange
        Long produtoId = 999L;
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(BusinessException.class, () -> {
            produtoService.buscarPorId(produtoId);
        });

        assertEquals("Produto não encontrado com o ID: " + produtoId, exception.getMessage());
        verify(produtoRepository).findById(produtoId);
    }
}
