package com.projeto.erp.estoque;

import com.projeto.erp.fornecedor.Fornecedor;
import com.projeto.erp.produto.Produto;
import com.projeto.erp.produto.ProdutoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EstoqueRepositoryTest {

    @Autowired
    EstoqueRepository estoqueRepository;

    @Autowired
    ProdutoRepository produtoRepository;

    @Test
    @DisplayName("Deve salvar e buscar estoque por produto_id e verificar existsByProdutoId")
    void testSaveAndFindByProdutoId() {
        Produto produto = new Produto();
        produto.setNome("Produto Estoque");
        produto.setDescricao("Descrição");
        produto.setPreco(BigDecimal.valueOf(100));
        produto.setFornecedor(Fornecedor.builder().id(1L).build());
        produto.setCodigoBarras("999888777666");

        produto = produtoRepository.save(produto);

        Estoque estoque = Estoque.builder()
                .produto(produto)
                .quantidade(15)
                .build();

        estoqueRepository.save(estoque);

        Optional<Estoque> found = estoqueRepository.findByProdutoId(produto.getId());
        assertTrue(found.isPresent());
        assertEquals(15, found.get().getQuantidade());
        assertTrue(estoqueRepository.existsByProdutoId(produto.getId()));
    }

    @Test
    @DisplayName("Nao deve permitir dois registros de estoque para o mesmo produto (unicidade)")
    void testUniqueConstraintOnProduto() {
        Produto produto = new Produto();
        produto.setNome("Produto Unico");
        produto.setDescricao("Descrição Unica");
        produto.setPreco(BigDecimal.valueOf(50));
        produto.setFornecedor(Fornecedor.builder().id(1L).build());
        produto.setCodigoBarras("111222333444");

        produto = produtoRepository.save(produto);

        Estoque primeiro = Estoque.builder().produto(produto).quantidade(5).build();
        estoqueRepository.saveAndFlush(primeiro);

        Estoque segundo = Estoque.builder().produto(produto).quantidade(2).build();

        assertThrows(DataIntegrityViolationException.class, () -> {
            estoqueRepository.saveAndFlush(segundo);
        });
    }
}
