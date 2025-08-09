package com.projeto.erp.produto;

import com.projeto.erp.fornecedor.Fornecedor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ProidutoRepositoryTest {
    @Autowired
    private ProdutoRepository produtoRepository;

    @Test
    @DisplayName("Deve salvar e buscar fornecedor por ID")
    void testSaveAndFindById() {
        Produto produto = new Produto();
        produto.setNome("Produto Teste");
        produto.setDescricao("Descrição do produto teste");
        produto.setPreco(BigDecimal.TEN);
        produto.setFornecedor(Fornecedor.builder().id(1L).build());
        produto.setCodigoBarras("012345678901");


        produto = produtoRepository.save(produto);

        Optional<Produto> found = produtoRepository.findById(produto.getId());
        assertTrue(found.isPresent());
        assertEquals("Produto Teste", found.get().getNome());
    }


}

