package com.projeto.erp.pedido;

import com.projeto.erp.cliente.Cliente;
import com.projeto.erp.fornecedor.Fornecedor;
import com.projeto.erp.produto.Produto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PedidoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PedidoRepository pedidoRepository;

    private Cliente cliente;
    private Produto produto;
    private Pedido pedido;

    @BeforeEach
    void setUp() {
        // Limpar dados antes de cada teste
        entityManager.getEntityManager().createQuery("DELETE FROM ItemPedido").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Pedido").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Estoque").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Produto").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Cliente").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Fornecedor").executeUpdate();
        entityManager.flush();

        // Criar e persistir fornecedor
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setNome("Fornecedor Teste");
        fornecedor.setDocumento("12345678000100");
        fornecedor.setAtivo(true);
        entityManager.persistAndFlush(fornecedor);

        // Criar e persistir cliente
        cliente = new Cliente();
        cliente.setNome("João Silva");
        cliente.setEmail("joao@email.com");
        cliente.setDocumento("12345678901");
        cliente.setTelefone("11999999999");
        cliente.setAtivo(true);
        entityManager.persistAndFlush(cliente);

        // Criar e persistir produto
        produto = new Produto();
        produto.setNome("Produto Teste");
        produto.setDescricao("Descrição do produto teste");
        produto.setCodigoBarras("1234567890123");
        produto.setPreco(BigDecimal.valueOf(10.50));
        produto.setFornecedor(fornecedor);
        entityManager.persistAndFlush(produto);

        // Preparar pedido (não persistir ainda)
        pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(Pedido.StatusPedido.ABERTO);
        pedido.setTotal(BigDecimal.valueOf(21.00));
    }

    @Test
    @DisplayName("Deve salvar e buscar pedido por ID")
    void testSaveAndFindById() {
        // Act
        Pedido savedPedido = pedidoRepository.save(pedido);
        Optional<Pedido> found = pedidoRepository.findById(savedPedido.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals(pedido.getCliente().getId(), found.get().getCliente().getId());
        assertEquals(pedido.getStatus(), found.get().getStatus());
        assertEquals(pedido.getTotal(), found.get().getTotal());
    }

    @Test
    @DisplayName("Deve encontrar pedidos por cliente ID")
    void testFindByClienteId() {
        // Arrange
        Pedido savedPedido = pedidoRepository.save(pedido);

        // Act
        List<Pedido> pedidos = pedidoRepository.findByClienteId(cliente.getId());

        // Assert
        assertFalse(pedidos.isEmpty());
        assertEquals(1, pedidos.size());
        assertEquals(savedPedido.getId(), pedidos.get(0).getId());
        assertEquals(cliente.getId(), pedidos.get(0).getCliente().getId());
    }

    @Test
    @DisplayName("Deve encontrar pedidos por cliente ID ordenados por data decrescente")
    void testFindByClienteIdOrderByDataPedidoDesc() {
        // Arrange
        Pedido pedido1 = new Pedido();
        pedido1.setCliente(cliente);
        pedido1.setDataPedido(LocalDateTime.now().minusDays(1));
        pedido1.setStatus(Pedido.StatusPedido.ABERTO);
        pedido1.setTotal(BigDecimal.valueOf(10.00));
        pedido1 = pedidoRepository.save(pedido1);

        Pedido pedido2 = new Pedido();
        pedido2.setCliente(cliente);
        pedido2.setDataPedido(LocalDateTime.now());
        pedido2.setStatus(Pedido.StatusPedido.PROCESSANDO);
        pedido2.setTotal(BigDecimal.valueOf(20.00));
        pedido2 = pedidoRepository.save(pedido2);

        // Act
        List<Pedido> pedidos = pedidoRepository.findByClienteIdOrderByDataPedidoDesc(cliente.getId());

        // Assert
        assertEquals(2, pedidos.size());
        // O pedido mais recente deve vir primeiro
        assertEquals(pedido2.getId(), pedidos.get(0).getId());
        assertEquals(pedido1.getId(), pedidos.get(1).getId());
        assertTrue(pedidos.get(0).getDataPedido().isAfter(pedidos.get(1).getDataPedido()));
    }

    @Test
    @DisplayName("Deve encontrar pedidos por status")
    void testFindByStatus() {
        // Arrange
        Pedido pedidoAberto = new Pedido();
        pedidoAberto.setCliente(cliente);
        pedidoAberto.setDataPedido(LocalDateTime.now());
        pedidoAberto.setStatus(Pedido.StatusPedido.ABERTO);
        pedidoAberto.setTotal(BigDecimal.valueOf(15.00));
        pedidoAberto = pedidoRepository.save(pedidoAberto);

        Pedido pedidoProcessando = new Pedido();
        pedidoProcessando.setCliente(cliente);
        pedidoProcessando.setDataPedido(LocalDateTime.now());
        pedidoProcessando.setStatus(Pedido.StatusPedido.PROCESSANDO);
        pedidoProcessando.setTotal(BigDecimal.valueOf(25.00));
        pedidoProcessando = pedidoRepository.save(pedidoProcessando);

        // Act
        List<Pedido> pedidosAbertos = pedidoRepository.findByStatus(Pedido.StatusPedido.ABERTO);
        List<Pedido> pedidosProcessando = pedidoRepository.findByStatus(Pedido.StatusPedido.PROCESSANDO);

        // Assert
        assertEquals(1, pedidosAbertos.size());
        assertEquals(pedidoAberto.getId(), pedidosAbertos.get(0).getId());
        assertEquals(Pedido.StatusPedido.ABERTO, pedidosAbertos.get(0).getStatus());

        assertEquals(1, pedidosProcessando.size());
        assertEquals(pedidoProcessando.getId(), pedidosProcessando.get(0).getId());
        assertEquals(Pedido.StatusPedido.PROCESSANDO, pedidosProcessando.get(0).getStatus());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não encontrar pedidos por cliente")
    void testFindByClienteId_ClienteInexistente() {
        // Act
        List<Pedido> pedidos = pedidoRepository.findByClienteId(999L);

        // Assert
        assertTrue(pedidos.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não encontrar pedidos por status")
    void testFindByStatus_StatusSemPedidos() {
        // Act
        List<Pedido> pedidos = pedidoRepository.findByStatus(Pedido.StatusPedido.CONCLUIDO);

        // Assert
        assertTrue(pedidos.isEmpty());
    }

    @Test
    @DisplayName("Deve criar pedido com itens em cascata")
    void testSavePedidoComItens() {
        // Arrange
        ItemPedido item = new ItemPedido();
        item.setPedido(pedido);
        item.setProduto(produto);
        item.setQuantidade(2);
        item.setPrecoUnitario(BigDecimal.valueOf(10.50));

        pedido.setItens(List.of(item));

        // Act
        Pedido savedPedido = pedidoRepository.save(pedido);
        entityManager.flush();

        // Verificar se o pedido foi salvo com sucesso
        assertNotNull(savedPedido.getId());

        // Buscar o pedido com JOIN FETCH para carregar os itens
        List<Pedido> foundList = entityManager.getEntityManager()
                .createQuery("SELECT p FROM Pedido p LEFT JOIN FETCH p.itens WHERE p.id = :id", Pedido.class)
                .setParameter("id", savedPedido.getId())
                .getResultList();

        // Assert
        assertFalse(foundList.isEmpty());
        Pedido found = foundList.get(0);
        assertNotNull(found);
        assertNotNull(found.getItens());
        assertEquals(1, found.getItens().size());
        assertEquals(2, found.getItens().get(0).getQuantidade());
        assertEquals(BigDecimal.valueOf(10.50), found.getItens().get(0).getPrecoUnitario());
    }
}
