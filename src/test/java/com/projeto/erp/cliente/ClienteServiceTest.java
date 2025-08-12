package com.projeto.erp.cliente;

import com.projeto.erp.cliente.dto.ClienteRequestDTO;
import com.projeto.erp.cliente.dto.ClienteResponseDTO;
import com.projeto.erp.cliente.mapper.ClienteMapper;
import com.projeto.erp.common.dto.PageResponseDTO;
import com.projeto.erp.common.exception.BusinessException;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClienteServiceTest {
    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @InjectMocks
    private ClienteService clienteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSalvarCliente() {
        // Arrange
        ClienteRequestDTO requestDTO = new ClienteRequestDTO();
        Cliente cliente = new Cliente();
        Cliente savedCliente = new Cliente();
        ClienteResponseDTO responseDTO = new ClienteResponseDTO();

        when(clienteMapper.toEntity(requestDTO)).thenReturn(cliente);
        when(clienteRepository.save(cliente)).thenReturn(savedCliente);
        when(clienteMapper.toDTO(savedCliente)).thenReturn(responseDTO);

        // Act
        ClienteResponseDTO result = clienteService.criarCliente(requestDTO);

        // Assert
        assertNotNull(result);
        verify(clienteRepository, times(1)).save(cliente);
    }

    @Test
    void testBuscarPorId() {
        // Arrange
        Long id = 1L;
        Cliente cliente = new Cliente();
        ClienteResponseDTO responseDTO = new ClienteResponseDTO();

        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));
        when(clienteMapper.toDTO(cliente)).thenReturn(responseDTO);

        // Act
        ClienteResponseDTO result = clienteService.getCliente(id);

        // Assert
        assertNotNull(result);
        verify(clienteRepository, times(1)).findById(id);
    }

    @Test
    void testListarTodos() {
        // Arrange
        Page<Cliente> clientes = new PageImpl<>(Arrays.asList(new Cliente(), new Cliente()));
        List<ClienteResponseDTO> responseDTOs = Arrays.asList(new ClienteResponseDTO(), new ClienteResponseDTO());
        Pageable pageable = PageRequest.of(0, 10);

        when(clienteRepository.findAll(pageable)).thenReturn(clientes);
        when(clienteMapper.toDTO(clientes.getContent().get(0))).thenReturn(responseDTOs.get(0));
        when(clienteMapper.toDTO(clientes.getContent().get(1))).thenReturn(responseDTOs.get(1));

        // Act
        PageResponseDTO<ClienteResponseDTO> result = clienteService.buscaTodosClientes(0,10);

        // Assert
        assertEquals(2, result.getTotalElements());
        verify(clienteRepository, times(1)).findAll(pageable);
    }

    @Test
    void testAtualizarCliente_QuandoClienteExiste() {
        // Arrange
        Long clienteId = 1L;
        ClienteRequestDTO requestDTO = new ClienteRequestDTO();
        requestDTO.setNome("Cliente Atualizado");
        requestDTO.setEmail("atualizado@email.com");
        requestDTO.setDocumento("123456789");
        requestDTO.setTelefone("987654321");
        requestDTO.setAtivo(true);

        Cliente clienteExistente = new Cliente();
        clienteExistente.setId(clienteId);
        clienteExistente.setNome("Cliente Original");
        clienteExistente.setEmail("original@email.com");

        Cliente clienteAtualizado = new Cliente();
        clienteAtualizado.setId(clienteId);
        clienteAtualizado.setNome(requestDTO.getNome());
        clienteAtualizado.setEmail(requestDTO.getEmail());
        clienteAtualizado.setDocumento(requestDTO.getDocumento());
        clienteAtualizado.setTelefone(requestDTO.getTelefone());
        clienteAtualizado.setAtivo(requestDTO.getAtivo());

        ClienteResponseDTO responseDTO = new ClienteResponseDTO();
        responseDTO.setId(clienteId);
        responseDTO.setNome(requestDTO.getNome());
        responseDTO.setEmail(requestDTO.getEmail());

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(clienteExistente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteAtualizado);
        when(clienteMapper.toDTO(clienteAtualizado)).thenReturn(responseDTO);
        when(clienteRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);

        // Act
        ClienteResponseDTO resultado = clienteService.atualizarCliente(clienteId, requestDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(clienteId, resultado.getId());
        assertEquals(requestDTO.getNome(), resultado.getNome());
        assertEquals(requestDTO.getEmail(), resultado.getEmail());
        verify(clienteRepository).findById(clienteId);
        verify(clienteRepository).save(any(Cliente.class));
        verify(clienteRepository).existsByEmail(requestDTO.getEmail());
    }

    @Test
    void testAtualizarCliente_QuandoClienteNaoExiste() {
        // Arrange
        Long clienteId = 1L;
        ClienteRequestDTO requestDTO = new ClienteRequestDTO();
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clienteService.atualizarCliente(clienteId, requestDTO);
        });
        assertEquals("Cliente não encontrado com o ID: " + clienteId, exception.getMessage());
        verify(clienteRepository).findById(clienteId);
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void testDeleteCliente_QuandoClienteExiste() {
        // Arrange
        Long clienteId = 1L;
        Cliente cliente = new Cliente();
        cliente.setId(clienteId);

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        doNothing().when(clienteRepository).delete(cliente);

        // Act
        clienteService.deleteCliente(clienteId);

        // Assert
        verify(clienteRepository).findById(clienteId);
        verify(clienteRepository).delete(cliente);
    }

    @Test
    void testDeleteCliente_QuandoClienteNaoExiste() {
        // Arrange
        Long clienteId = 1L;
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clienteService.deleteCliente(clienteId);
        });
        assertEquals("Cliente não encontrado com o ID: " + clienteId, exception.getMessage());
        verify(clienteRepository).findById(clienteId);
        verify(clienteRepository, never()).delete(any(Cliente.class));
    }

    @Test
    void testCriarCliente_QuandoEmailJaExiste() {
        // Arrange
        ClienteRequestDTO requestDTO = new ClienteRequestDTO();
        requestDTO.setEmail("existente@email.com");
        Cliente cliente = new Cliente();
        cliente.setEmail("existente@email.com");

        when(clienteMapper.toEntity(requestDTO)).thenReturn(cliente);
        when(clienteRepository.existsByEmail(requestDTO.getEmail())).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            clienteService.criarCliente(requestDTO);
        });

        assertEquals("E-mail já cadastrado", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        verify(clienteRepository).existsByEmail(requestDTO.getEmail());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void testAtualizarCliente_QuandoNovoEmailJaExiste() {
        // Arrange
        Long clienteId = 1L;
        ClienteRequestDTO requestDTO = new ClienteRequestDTO();
        requestDTO.setEmail("novo@email.com");

        Cliente clienteExistente = new Cliente();
        clienteExistente.setId(clienteId);
        clienteExistente.setEmail("atual@email.com");

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(clienteExistente));
        when(clienteRepository.existsByEmail(requestDTO.getEmail())).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            clienteService.atualizarCliente(clienteId, requestDTO);
        });

        assertEquals("E-mail já cadastrado", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        verify(clienteRepository).findById(clienteId);
        verify(clienteRepository).existsByEmail(requestDTO.getEmail());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void testBuscaTodosClientes_QuandoListaVazia() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        when(clienteRepository.findAll(pageable)).thenReturn(Page.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clienteService.buscaTodosClientes(0,10);
        });

        assertEquals("Nenhum cliente encontrado", exception.getMessage());
        verify(clienteRepository).findAll(pageable);
    }

    @Test
    void testGetCliente_QuandoIdNaoExiste() {
        // Arrange
        Long clienteId = 999L;
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clienteService.getCliente(clienteId);
        });

        assertEquals("Cliente não encontrado com o ID: " + clienteId, exception.getMessage());
        verify(clienteRepository).findById(clienteId);
    }
}
