package com.projeto.erp.produto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.erp.common.dto.PageResponseDTO;
import com.projeto.erp.fornecedor.dto.FornecedorRequestDTO;
import com.projeto.erp.fornecedor.dto.FornecedorResponseDTO;
import com.projeto.erp.produto.dto.ProdutoRequestDTO;
import com.projeto.erp.produto.dto.ProdutoResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProdutoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProdutoService produtoService;

    @InjectMocks
    private ProdutoController produtoController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(produtoController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testBuscarPorId() throws Exception {
        ProdutoResponseDTO responseDTO = new ProdutoResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNome("Produto Teste");
        responseDTO.setCodigoBarras("1234567890123");
        responseDTO.setPreco( BigDecimal.TEN );
        responseDTO.setFornecedorId(1L);
        responseDTO.setFornecedorNome("Fornecedor Teste");

        when(produtoService.buscarPorId(anyLong())).thenReturn(responseDTO);

        mockMvc.perform(get("/produtos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Produto Teste"))
                .andExpect(jsonPath("$.codigoBarras").value("1234567890123"))
                .andExpect(jsonPath("$.preco").value(BigDecimal.TEN))
                .andExpect(jsonPath("$.fornecedorId").value(1L));
    }

    @Test
    void testListarProdutos() throws Exception {
        // Arrange
        ProdutoResponseDTO p1 = new ProdutoResponseDTO();
        p1.setId(1L);
        p1.setNome("Produto 1");
        ProdutoResponseDTO p2 = new ProdutoResponseDTO();
        p2.setId(2L);
        p2.setNome("Produto 2");

        PageResponseDTO<ProdutoResponseDTO> lista = new PageResponseDTO<>(Arrays.asList(p1, p2), 0, 10, 2, 1, true, false);

        // Act
        when(produtoService.listarTodos(0,10)).thenReturn(lista);


        // Assert
        mockMvc.perform(get("/produtos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("Produto 1"));
    }

    @Test
    void testCriarProduto() throws Exception {
        ProdutoRequestDTO requestDTO = new ProdutoRequestDTO();
        requestDTO.setNome("Novo Produto");
        requestDTO.setCodigoBarras("1234567890123");
        requestDTO.setPreco(BigDecimal.TEN);
        requestDTO.setFornecedorId(1L);

        ProdutoResponseDTO responseDTO = new ProdutoResponseDTO();
        responseDTO.setId(10L);
        responseDTO.setNome("Novo Produto");
        responseDTO.setCodigoBarras("1234567890123");
        responseDTO.setPreco(BigDecimal.TEN);

        when(produtoService.criarProduto(any(ProdutoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L));
    }

    @Test
    void testAtualizarProduto() throws Exception {

        ProdutoRequestDTO requestDTO = new ProdutoRequestDTO();
        requestDTO.setNome("Produto Atualizado");
        requestDTO.setCodigoBarras("1234567890123");
        requestDTO.setPreco(BigDecimal.TEN);
        requestDTO.setFornecedorId(1L);

        ProdutoResponseDTO responseDTO = new ProdutoResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNome("Produto Atualizado");
        responseDTO.setCodigoBarras("1234567890123");
        responseDTO.setFornecedorId(1L);
        responseDTO.setFornecedorNome("Fornecedor Teste");
        responseDTO.setPreco(BigDecimal.TEN);

        when(produtoService.atualizarProduto(eq(1L), any(ProdutoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/produtos/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Produto Atualizado"));
    }

    @Test
    void testDeletarProduto() throws Exception {
        mockMvc.perform(delete("/produtos/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}

