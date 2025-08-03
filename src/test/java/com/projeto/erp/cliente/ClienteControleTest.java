package com.projeto.erp.cliente;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.erp.cliente.dto.ClienteRequestDTO;
import com.projeto.erp.cliente.dto.ClienteResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ClienteControleTest {

    private MockMvc mockMvc;

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ClienteControle clienteControle;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clienteControle).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetCliente() throws Exception {
        ClienteResponseDTO responseDTO = new ClienteResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNome("Cliente Teste");
        responseDTO.setEmail("teste@email.com");
        responseDTO.setDocumento("12345678900");
        responseDTO.setTelefone("11999999999");
        responseDTO.setAtivo(true);

        when(clienteService.getCliente(anyLong())).thenReturn(responseDTO);

        mockMvc.perform(get("/clientes/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Cliente Teste"))
                .andExpect(jsonPath("$.email").value("teste@email.com"))
                .andExpect(jsonPath("$.documento").value("12345678900"))
                .andExpect(jsonPath("$.telefone").value("11999999999"))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    void testListCliente() throws Exception {
        ClienteResponseDTO c1 = new ClienteResponseDTO();
        c1.setId(1L);
        c1.setNome("Cliente 1");
        ClienteResponseDTO c2 = new ClienteResponseDTO();
        c2.setId(2L);
        c2.setNome("Cliente 2");
        List<ClienteResponseDTO> lista = Arrays.asList(c1, c2);
        when(clienteService.buscaTodosClientes()).thenReturn(lista);

        mockMvc.perform(get("/clientes/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Cliente 1"));
    }

    @Test
    void testCriarCliente() throws Exception {

        ClienteRequestDTO requestDTO = new ClienteRequestDTO();
        requestDTO.setNome("Novo Cliente");
        requestDTO.setNome("Cliente Teste");
        requestDTO.setEmail("teste@email.com");
        requestDTO.setDocumento("12345678900");
        requestDTO.setTelefone("11999999999");
        requestDTO.setAtivo(true);


        ClienteResponseDTO responseDTO = new ClienteResponseDTO();
        responseDTO.setId(10L);
        responseDTO.setNome("Novo Cliente");
        responseDTO.setNome("Cliente Teste");
        responseDTO.setEmail("teste@email.com");
        responseDTO.setDocumento("12345678900");
        responseDTO.setTelefone("11999999999");
        responseDTO.setAtivo(true);

        when(clienteService.criarCliente(any(ClienteRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/clientes/criar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L));
    }

    @Test
    void testAtualizarCliente() throws Exception {
        ClienteRequestDTO requestDTO = new ClienteRequestDTO();
        requestDTO.setNome("Cliente Atualizado");
        ClienteResponseDTO responseDTO = new ClienteResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNome("Cliente Atualizado");
        when(clienteService.atualizarCliente(eq(1L), any(ClienteRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/clientes/atualizar/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Cliente Atualizado"));
    }
}
