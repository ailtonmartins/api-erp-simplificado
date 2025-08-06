package com.projeto.erp.fornecedor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.erp.fornecedor.dto.FornecedorRequestDTO;
import com.projeto.erp.fornecedor.dto.FornecedorResponseDTO;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FornecedorControleTest {

    private MockMvc mockMvc;

    @Mock
    private FornecedorService fornecedorService;

    @InjectMocks
    private FornecedorControle fornecedorControle;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(fornecedorControle).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetFornecedor() throws Exception {
        FornecedorResponseDTO responseDTO = new FornecedorResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNome("Fornecedor Teste");
        responseDTO.setEmail("teste@email.com");
        responseDTO.setDocumento("12345678900");
        responseDTO.setTelefone("11999999999");
        responseDTO.setAtivo(true);

        when(fornecedorService.getFornecedor(anyLong())).thenReturn(responseDTO);

        mockMvc.perform(get("/fornecedores/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Fornecedor Teste"))
                .andExpect(jsonPath("$.email").value("teste@email.com"))
                .andExpect(jsonPath("$.documento").value("12345678900"))
                .andExpect(jsonPath("$.telefone").value("11999999999"))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    void testListFornecedor() throws Exception {
        FornecedorResponseDTO c1 = new FornecedorResponseDTO();
        c1.setId(1L);
        c1.setNome("Fornecedor 1");
        FornecedorResponseDTO c2 = new FornecedorResponseDTO();
        c2.setId(2L);
        c2.setNome("Fornecedor 2");
        List<FornecedorResponseDTO> lista = Arrays.asList(c1, c2);
        when(fornecedorService.buscaTodosFornecedors()).thenReturn(lista);

        mockMvc.perform(get("/fornecedores/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Fornecedor 1"));
    }

    @Test
    void testCriarFornecedor() throws Exception {

        FornecedorRequestDTO requestDTO = new FornecedorRequestDTO();
        requestDTO.setNome("Novo Fornecedor");
        requestDTO.setNome("Fornecedor Teste");
        requestDTO.setEmail("teste@email.com");
        requestDTO.setDocumento("12345678900");
        requestDTO.setTelefone("11999999999");
        requestDTO.setAtivo(true);


        FornecedorResponseDTO responseDTO = new FornecedorResponseDTO();
        responseDTO.setId(10L);
        responseDTO.setNome("Novo Fornecedor");
        responseDTO.setNome("Fornecedor Teste");
        responseDTO.setEmail("teste@email.com");
        responseDTO.setDocumento("12345678900");
        responseDTO.setTelefone("11999999999");
        responseDTO.setAtivo(true);

        when(fornecedorService.criarFornecedor(any(FornecedorRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/fornecedores/criar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L));
    }

    @Test
    void testAtualizarFornecedor() throws Exception {
        FornecedorRequestDTO requestDTO = new FornecedorRequestDTO();
        requestDTO.setNome("Fornecedor Atualizado");
        FornecedorResponseDTO responseDTO = new FornecedorResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNome("Fornecedor Atualizado");
        when(fornecedorService.atualizarFornecedor(eq(1L), any(FornecedorRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/fornecedores/atualizar/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Fornecedor Atualizado"));
    }

    @Test
    void testDeletarFornecedor() throws Exception {
        mockMvc.perform(delete("/fornecedores/deletar/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}
