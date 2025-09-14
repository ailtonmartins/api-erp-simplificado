package com.projeto.erp.pedido;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.erp.cliente.Cliente;
import com.projeto.erp.cliente.ClienteRepository;
import com.projeto.erp.estoque.Estoque;
import com.projeto.erp.estoque.EstoqueRepository;
import com.projeto.erp.fornecedor.Fornecedor;
import com.projeto.erp.fornecedor.FornecedorRepository;
import com.projeto.erp.pedido.dto.ItemPedidoRequestDTO;
import com.projeto.erp.pedido.dto.PedidoRequestDTO;
import com.projeto.erp.produto.Produto;
import com.projeto.erp.produto.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PedidoIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    private Cliente cliente;
    private Produto produto1;
    private Produto produto2;
    private Estoque estoque1;
    private Estoque estoque2;

    @BeforeEach
    @Transactional
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Limpar dados
        pedidoRepository.deleteAll();
        estoqueRepository.deleteAll();
        produtoRepository.deleteAll();
        clienteRepository.deleteAll();
        fornecedorRepository.deleteAll();

        // Criar fornecedor
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setNome("Fornecedor Teste");
        fornecedor.setDocumento("12345678000100");
        fornecedor.setTelefone("11999999999");
        fornecedor.setEmail("fornecedor@teste.com");
        fornecedor.setAtivo(true);
        fornecedor = fornecedorRepository.save(fornecedor);

        // Criar cliente
        cliente = new Cliente();
        cliente.setNome("João Silva");
        cliente.setEmail("joao@email.com");
        cliente.setDocumento("12345678901");
        cliente.setTelefone("11999999999");
        cliente.setAtivo(true);
        cliente = clienteRepository.save(cliente);

        // Criar produtos
        produto1 = new Produto();
        produto1.setNome("Produto A");
        produto1.setDescricao("Descrição do Produto A");
        produto1.setCodigoBarras("1234567890123");
        produto1.setPreco(BigDecimal.valueOf(10.50));
        produto1.setFornecedor(fornecedor);
        produto1 = produtoRepository.save(produto1);

        produto2 = new Produto();
        produto2.setNome("Produto B");
        produto2.setDescricao("Descrição do Produto B");
        produto2.setCodigoBarras("1234567890124");
        produto2.setPreco(BigDecimal.valueOf(25.75));
        produto2.setFornecedor(fornecedor);
        produto2 = produtoRepository.save(produto2);

        // Criar estoque
        estoque1 = new Estoque();
        estoque1.setProduto(produto1);
        estoque1.setQuantidade(100);
        estoque1 = estoqueRepository.save(estoque1);

        estoque2 = new Estoque();
        estoque2.setProduto(produto2);
        estoque2.setQuantidade(50);
        estoque2 = estoqueRepository.save(estoque2);
    }

    @Test
    @DisplayName("Deve criar um pedido com sucesso")
    void testCriarPedido_FluxoCompleto() throws Exception {
        // Arrange
        ItemPedidoRequestDTO item1 = new ItemPedidoRequestDTO();
        item1.setProdutoId(produto1.getId());
        item1.setQuantidade(2);
        item1.setPrecoUnitario(BigDecimal.valueOf(10.50));

        ItemPedidoRequestDTO item2 = new ItemPedidoRequestDTO();
        item2.setProdutoId(produto2.getId());
        item2.setQuantidade(1);
        item2.setPrecoUnitario(BigDecimal.valueOf(25.75));

        PedidoRequestDTO pedidoRequest = new PedidoRequestDTO();
        pedidoRequest.setClienteId(cliente.getId());
        pedidoRequest.setItens(Arrays.asList(item1, item2));

        // Act & Assert
        mockMvc.perform(post("/pedidos/criar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteId").value(cliente.getId()))
                .andExpect(jsonPath("$.clienteNome").value("João Silva"))
                .andExpect(jsonPath("$.status").value("ABERTO"))
                .andExpect(jsonPath("$.total").value(46.75))
                .andExpect(jsonPath("$.itens", hasSize(2)))
                .andExpect(jsonPath("$.itens[0].produtoId").value(produto1.getId()))
                .andExpect(jsonPath("$.itens[0].quantidade").value(2))
                .andExpect(jsonPath("$.itens[0].subtotal").value(21.00))
                .andExpect(jsonPath("$.itens[1].produtoId").value(produto2.getId()))
                .andExpect(jsonPath("$.itens[1].quantidade").value(1))
                .andExpect(jsonPath("$.itens[1].subtotal").value(25.75));
    }

    @Test
    @DisplayName("Deve processar e concluir um pedido atualizando o estoque")
    @Transactional
    void testFluxoCompletoPedido_CriarProcessarConcluir() throws Exception {
        // 1. Criar pedido
        ItemPedidoRequestDTO item = new ItemPedidoRequestDTO();
        item.setProdutoId(produto1.getId());
        item.setQuantidade(5);
        item.setPrecoUnitario(BigDecimal.valueOf(10.50));

        PedidoRequestDTO pedidoRequest = new PedidoRequestDTO();
        pedidoRequest.setClienteId(cliente.getId());
        pedidoRequest.setItens(Arrays.asList(item));

        String response = mockMvc.perform(post("/pedidos/criar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ABERTO"))
                .andReturn().getResponse().getContentAsString();

        Long pedidoId = objectMapper.readTree(response).get("id").asLong();

        // 2. Processar pedido
        mockMvc.perform(put("/pedidos/" + pedidoId + "/processar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PROCESSANDO"));

        // 3. Concluir pedido
        mockMvc.perform(put("/pedidos/" + pedidoId + "/concluir"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONCLUIDO"));

        // 4. Verificar se o estoque foi atualizado
        Estoque estoqueAtualizado = estoqueRepository.findByProdutoId(produto1.getId()).orElseThrow();
        assert estoqueAtualizado.getQuantidade() == 95; // 100 - 5 = 95
    }

    @Test
    @DisplayName("Deve falhar ao criar pedido com estoque insuficiente")
    void testCriarPedido_EstoqueInsuficiente() throws Exception {
        // Arrange
        ItemPedidoRequestDTO item = new ItemPedidoRequestDTO();
        item.setProdutoId(produto1.getId());
        item.setQuantidade(150); // Maior que o estoque disponível (100)
        item.setPrecoUnitario(BigDecimal.valueOf(10.50));

        PedidoRequestDTO pedidoRequest = new PedidoRequestDTO();
        pedidoRequest.setClienteId(cliente.getId());
        pedidoRequest.setItens(Arrays.asList(item));

        // Act & Assert
        mockMvc.perform(post("/pedidos/criar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve falhar ao processar pedido que não está em ABERTO")
    @Transactional
    void testProcessarPedido_StatusInvalido() throws Exception {
        // 1. Criar e processar pedido
        ItemPedidoRequestDTO item = new ItemPedidoRequestDTO();
        item.setProdutoId(produto1.getId());
        item.setQuantidade(2);
        item.setPrecoUnitario(BigDecimal.valueOf(10.50));

        PedidoRequestDTO pedidoRequest = new PedidoRequestDTO();
        pedidoRequest.setClienteId(cliente.getId());
        pedidoRequest.setItens(Arrays.asList(item));

        String response = mockMvc.perform(post("/pedidos/criar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long pedidoId = objectMapper.readTree(response).get("id").asLong();

        // Processar uma vez
        mockMvc.perform(put("/pedidos/" + pedidoId + "/processar"))
                .andExpect(status().isOk());

        // 2. Tentar processar novamente (deve falhar)
        mockMvc.perform(put("/pedidos/" + pedidoId + "/processar"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve falhar ao concluir pedido que não está em PROCESSANDO")
    @Transactional
    void testConcluirPedido_StatusInvalido() throws Exception {
        // 1. Criar pedido
        ItemPedidoRequestDTO item = new ItemPedidoRequestDTO();
        item.setProdutoId(produto1.getId());
        item.setQuantidade(2);
        item.setPrecoUnitario(BigDecimal.valueOf(10.50));

        PedidoRequestDTO pedidoRequest = new PedidoRequestDTO();
        pedidoRequest.setClienteId(cliente.getId());
        pedidoRequest.setItens(Arrays.asList(item));

        String response = mockMvc.perform(post("/pedidos/criar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long pedidoId = objectMapper.readTree(response).get("id").asLong();

        // 2. Tentar concluir sem processar (deve falhar)
        mockMvc.perform(put("/pedidos/" + pedidoId + "/concluir"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve listar pedidos paginados")
    @Transactional
    void testListarPedidos() throws Exception {
        // 1. Criar alguns pedidos
        for (int i = 0; i < 3; i++) {
            ItemPedidoRequestDTO item = new ItemPedidoRequestDTO();
            item.setProdutoId(produto1.getId());
            item.setQuantidade(1);
            item.setPrecoUnitario(BigDecimal.valueOf(10.50));

            PedidoRequestDTO pedidoRequest = new PedidoRequestDTO();
            pedidoRequest.setClienteId(cliente.getId());
            pedidoRequest.setItens(Arrays.asList(item));

            mockMvc.perform(post("/pedidos/criar")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(pedidoRequest)));
        }

        // 2. Listar pedidos
        mockMvc.perform(get("/pedidos/listar")
                .param("page", "0")
                .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(2));
    }

    @Test
    @DisplayName("Deve listar pedidos por cliente")
    @Transactional
    void testListarPedidosPorCliente() throws Exception {
        // 1. Criar pedido
        ItemPedidoRequestDTO item = new ItemPedidoRequestDTO();
        item.setProdutoId(produto1.getId());
        item.setQuantidade(1);
        item.setPrecoUnitario(BigDecimal.valueOf(10.50));

        PedidoRequestDTO pedidoRequest = new PedidoRequestDTO();
        pedidoRequest.setClienteId(cliente.getId());
        pedidoRequest.setItens(Arrays.asList(item));

        mockMvc.perform(post("/pedidos/criar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoRequest)));

        // 2. Listar por cliente
        mockMvc.perform(get("/pedidos/cliente/" + cliente.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].clienteId").value(cliente.getId()));
    }

    @Test
    @DisplayName("Deve listar pedidos por status")
    @Transactional
    void testListarPedidosPorStatus() throws Exception {
        // 1. Criar e processar pedido
        ItemPedidoRequestDTO item = new ItemPedidoRequestDTO();
        item.setProdutoId(produto1.getId());
        item.setQuantidade(1);
        item.setPrecoUnitario(BigDecimal.valueOf(10.50));

        PedidoRequestDTO pedidoRequest = new PedidoRequestDTO();
        pedidoRequest.setClienteId(cliente.getId());
        pedidoRequest.setItens(Arrays.asList(item));

        String response = mockMvc.perform(post("/pedidos/criar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoRequest)))
                .andReturn().getResponse().getContentAsString();

        Long pedidoId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(put("/pedidos/" + pedidoId + "/processar"));

        // 2. Listar por status
        mockMvc.perform(get("/pedidos/status/PROCESSANDO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status").value("PROCESSANDO"));
    }

    @Test
    @DisplayName("Deve cancelar pedido com sucesso")
    @Transactional
    void testCancelarPedido() throws Exception {
        // 1. Criar pedido
        ItemPedidoRequestDTO item = new ItemPedidoRequestDTO();
        item.setProdutoId(produto1.getId());
        item.setQuantidade(1);
        item.setPrecoUnitario(BigDecimal.valueOf(10.50));

        PedidoRequestDTO pedidoRequest = new PedidoRequestDTO();
        pedidoRequest.setClienteId(cliente.getId());
        pedidoRequest.setItens(Arrays.asList(item));

        String response = mockMvc.perform(post("/pedidos/criar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoRequest)))
                .andReturn().getResponse().getContentAsString();

        Long pedidoId = objectMapper.readTree(response).get("id").asLong();

        // 2. Cancelar pedido
        mockMvc.perform(put("/pedidos/" + pedidoId + "/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADO"));
    }
}
