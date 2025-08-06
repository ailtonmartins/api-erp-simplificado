package com.projeto.erp.fornecedor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class FornecedorRepositoryTest {
    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Test
    @DisplayName("Deve salvar e buscar fornecedor por ID")
    void testSaveAndFindById() {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setNome("Fornecedor Teste");
        fornecedor.setEmail("teste@email.com");
        fornecedor.setDocumento("12345678900");
        fornecedor.setTelefone("11999999999");
        fornecedor.setAtivo(true);
        fornecedor = fornecedorRepository.save(fornecedor);

        Optional<Fornecedor> found = fornecedorRepository.findById(fornecedor.getId());
        assertTrue(found.isPresent());
        assertEquals("Fornecedor Teste", found.get().getNome());
    }


}

