package com.projeto.erp.estoque;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.erp.estoque.dto.EstoqueRequestDTO;
import com.projeto.erp.estoque.dto.EstoqueResponseDTO;
import com.projeto.erp.produto.dto.ProdutoResponseSemQtdDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EstoqueControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EstoqueService estoqueService;

    @InjectMocks
    private EstoqueController estoqueController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(estoqueController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testEntradaEstoque() throws Exception {
        EstoqueRequestDTO request = new EstoqueRequestDTO();
        request.setIdProduto(1L);
        request.setQuantidade(5);

        EstoqueResponseDTO response = new EstoqueResponseDTO();
        ProdutoResponseSemQtdDTO produto = new ProdutoResponseSemQtdDTO();
        produto.setId(1L);
        response.setProduto(produto);
        response.setQuantidade(10);

        when(estoqueService.entradaEstoque(any(EstoqueRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/estoque/entrada")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.produto.id").value(1))
                .andExpect(jsonPath("$.quantidade").value(10));
    }

    @Test
    void testSaidaEstoque() throws Exception {
        EstoqueRequestDTO request = new EstoqueRequestDTO();
        request.setIdProduto(1L);
        request.setQuantidade(2);

        EstoqueResponseDTO response = new EstoqueResponseDTO();
        ProdutoResponseSemQtdDTO produto = new ProdutoResponseSemQtdDTO();
        produto.setId(1L);
        response.setProduto(produto);
        response.setQuantidade(3);

        when(estoqueService.saidaEstoque(any(EstoqueRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/estoque/saida")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.produto.id").value(1))
                .andExpect(jsonPath("$.quantidade").value(3));
    }

    @Test
    void testConsultarSaldo() throws Exception {
        EstoqueResponseDTO response = new EstoqueResponseDTO();
        ProdutoResponseSemQtdDTO produto = new ProdutoResponseSemQtdDTO();
        produto.setId(2L);
        response.setProduto(produto);
        response.setQuantidade(7);

        when(estoqueService.consultarSaldo(2L)).thenReturn(response);

        mockMvc.perform(get("/estoque/saldo/{produtoId}", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.produto.id").value(2))
                .andExpect(jsonPath("$.quantidade").value(7));
    }
}
