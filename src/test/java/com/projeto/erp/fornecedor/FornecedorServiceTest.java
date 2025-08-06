package com.projeto.erp.fornecedor;

import com.projeto.erp.fornecedor.Fornecedor;
import com.projeto.erp.fornecedor.FornecedorRepository;
import com.projeto.erp.fornecedor.FornecedorService;
import com.projeto.erp.fornecedor.dto.FornecedorRequestDTO;
import com.projeto.erp.fornecedor.dto.FornecedorResponseDTO;
import com.projeto.erp.fornecedor.mapper.FornecedorMapper;
import com.projeto.erp.common.exception.BusinessException;
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

class FornecedorServiceTest {
    @Mock
    private FornecedorRepository fornecedorRepository;

    @Mock
    private FornecedorMapper fornecedorMapper;

    @InjectMocks
    private FornecedorService fornecedorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSalvarFornecedor() {
        // Arrange
        FornecedorRequestDTO requestDTO = new FornecedorRequestDTO();
        Fornecedor fornecedor = new Fornecedor();
        Fornecedor savedFornecedor = new Fornecedor();
        FornecedorResponseDTO responseDTO = new FornecedorResponseDTO();

        when(fornecedorMapper.toEntity(requestDTO)).thenReturn(fornecedor);
        when(fornecedorRepository.save(fornecedor)).thenReturn(savedFornecedor);
        when(fornecedorMapper.toDTO(savedFornecedor)).thenReturn(responseDTO);

        // Act
        FornecedorResponseDTO result = fornecedorService.criarFornecedor(requestDTO);

        // Assert
        assertNotNull(result);
        verify(fornecedorRepository, times(1)).save(fornecedor);
    }

    @Test
    void testBuscarPorId() {
        // Arrange
        Long id = 1L;
        Fornecedor fornecedor = new Fornecedor();
        FornecedorResponseDTO responseDTO = new FornecedorResponseDTO();

        when(fornecedorRepository.findById(id)).thenReturn(Optional.of(fornecedor));
        when(fornecedorMapper.toDTO(fornecedor)).thenReturn(responseDTO);

        // Act
        FornecedorResponseDTO result = fornecedorService.getFornecedor(id);

        // Assert
        assertNotNull(result);
        verify(fornecedorRepository, times(1)).findById(id);
    }

    @Test
    void testListarTodos() {
        // Arrange
        List<Fornecedor> fornecedores = Arrays.asList(new Fornecedor(), new Fornecedor());
        List<FornecedorResponseDTO> responseDTOs = Arrays.asList(new FornecedorResponseDTO(), new FornecedorResponseDTO());

        when(fornecedorRepository.findAll()).thenReturn(fornecedores);
        when(fornecedorMapper.toDTO(fornecedores.get(0))).thenReturn(responseDTOs.get(0));
        when(fornecedorMapper.toDTO(fornecedores.get(1))).thenReturn(responseDTOs.get(1));

        // Act
        List<FornecedorResponseDTO> result = fornecedorService.buscaTodosFornecedors();

        // Assert
        assertEquals(2, result.size());
        verify(fornecedorRepository, times(1)).findAll();
    }

    @Test
    void testAtualizarFornecedor_QuandoFornecedorExiste() {
        // Arrange
        Long fornecedorId = 1L;
        FornecedorRequestDTO requestDTO = new FornecedorRequestDTO();
        requestDTO.setNome("Fornecedor Atualizado");
        requestDTO.setEmail("atualizado@email.com");
        requestDTO.setDocumento("123456789");
        requestDTO.setTelefone("987654321");
        requestDTO.setAtivo(true);

        Fornecedor fornecedorExistente = new Fornecedor();
        fornecedorExistente.setId(fornecedorId);
        fornecedorExistente.setNome("Fornecedor Original");
        fornecedorExistente.setEmail("original@email.com");

        Fornecedor fornecedorAtualizado = new Fornecedor();
        fornecedorAtualizado.setId(fornecedorId);
        fornecedorAtualizado.setNome(requestDTO.getNome());
        fornecedorAtualizado.setEmail(requestDTO.getEmail());
        fornecedorAtualizado.setDocumento(requestDTO.getDocumento());
        fornecedorAtualizado.setTelefone(requestDTO.getTelefone());
        fornecedorAtualizado.setAtivo(requestDTO.getAtivo());

        FornecedorResponseDTO responseDTO = new FornecedorResponseDTO();
        responseDTO.setId(fornecedorId);
        responseDTO.setNome(requestDTO.getNome());
        responseDTO.setEmail(requestDTO.getEmail());

        when(fornecedorRepository.findById(fornecedorId)).thenReturn(Optional.of(fornecedorExistente));
        when(fornecedorRepository.save(any(Fornecedor.class))).thenReturn(fornecedorAtualizado);
        when(fornecedorMapper.toDTO(fornecedorAtualizado)).thenReturn(responseDTO);
        when(fornecedorRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);

        // Act
        FornecedorResponseDTO resultado = fornecedorService.atualizarFornecedor(fornecedorId, requestDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(fornecedorId, resultado.getId());
        assertEquals(requestDTO.getNome(), resultado.getNome());
        assertEquals(requestDTO.getEmail(), resultado.getEmail());
        verify(fornecedorRepository).findById(fornecedorId);
        verify(fornecedorRepository).save(any(Fornecedor.class));
        verify(fornecedorRepository).existsByEmail(requestDTO.getEmail());
    }

    @Test
    void testAtualizarFornecedor_QuandoFornecedorNaoExiste() {
        // Arrange
        Long fornecedorId = 1L;
        FornecedorRequestDTO requestDTO = new FornecedorRequestDTO();
        when(fornecedorRepository.findById(fornecedorId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fornecedorService.atualizarFornecedor(fornecedorId, requestDTO);
        });
        assertEquals("Fornecedor não encontrado com o ID: " + fornecedorId, exception.getMessage());
        verify(fornecedorRepository).findById(fornecedorId);
        verify(fornecedorRepository, never()).save(any(Fornecedor.class));
    }

    @Test
    void testDeleteFornecedor_QuandoFornecedorExiste() {
        // Arrange
        Long fornecedorId = 1L;
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setId(fornecedorId);

        when(fornecedorRepository.findById(fornecedorId)).thenReturn(Optional.of(fornecedor));
        doNothing().when(fornecedorRepository).delete(fornecedor);

        // Act
        fornecedorService.deleteFornecedor(fornecedorId);

        // Assert
        verify(fornecedorRepository).findById(fornecedorId);
        verify(fornecedorRepository).delete(fornecedor);
    }

    @Test
    void testDeleteFornecedor_QuandoFornecedorNaoExiste() {
        // Arrange
        Long fornecedorId = 1L;
        when(fornecedorRepository.findById(fornecedorId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fornecedorService.deleteFornecedor(fornecedorId);
        });
        assertEquals("Fornecedor não encontrado com o ID: " + fornecedorId, exception.getMessage());
        verify(fornecedorRepository).findById(fornecedorId);
        verify(fornecedorRepository, never()).delete(any(Fornecedor.class));
    }

    @Test
    void testCriarFornecedor_QuandoEmailJaExiste() {
        // Arrange
        FornecedorRequestDTO requestDTO = new FornecedorRequestDTO();
        requestDTO.setEmail("existente@email.com");
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setEmail("existente@email.com");

        when(fornecedorMapper.toEntity(requestDTO)).thenReturn(fornecedor);
        when(fornecedorRepository.existsByEmail(requestDTO.getEmail())).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            fornecedorService.criarFornecedor(requestDTO);
        });

        assertEquals("E-mail já cadastrado", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        verify(fornecedorRepository).existsByEmail(requestDTO.getEmail());
        verify(fornecedorRepository, never()).save(any(Fornecedor.class));
    }

    @Test
    void testAtualizarFornecedor_QuandoNovoEmailJaExiste() {
        // Arrange
        Long fornecedorId = 1L;
        FornecedorRequestDTO requestDTO = new FornecedorRequestDTO();
        requestDTO.setEmail("novo@email.com");

        Fornecedor fornecedorExistente = new Fornecedor();
        fornecedorExistente.setId(fornecedorId);
        fornecedorExistente.setEmail("atual@email.com");

        when(fornecedorRepository.findById(fornecedorId)).thenReturn(Optional.of(fornecedorExistente));
        when(fornecedorRepository.existsByEmail(requestDTO.getEmail())).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            fornecedorService.atualizarFornecedor(fornecedorId, requestDTO);
        });

        assertEquals("E-mail já cadastrado", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        verify(fornecedorRepository).findById(fornecedorId);
        verify(fornecedorRepository).existsByEmail(requestDTO.getEmail());
        verify(fornecedorRepository, never()).save(any(Fornecedor.class));
    }

    @Test
    void testBuscaTodosFornecedors_QuandoListaVazia() {
        // Arrange
        when(fornecedorRepository.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fornecedorService.buscaTodosFornecedors();
        });

        assertEquals("Nenhum Fornecedor encontrado", exception.getMessage());
        verify(fornecedorRepository).findAll();
    }

    @Test
    void testGetFornecedor_QuandoIdNaoExiste() {
        // Arrange
        Long fornecedorId = 999L;
        when(fornecedorRepository.findById(fornecedorId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(BusinessException.class, () -> {
            fornecedorService.getFornecedor(fornecedorId);
        });

        assertEquals("Fornecedor não encontrado com o ID: " + fornecedorId, exception.getMessage());
        verify(fornecedorRepository).findById(fornecedorId);
    }
}
