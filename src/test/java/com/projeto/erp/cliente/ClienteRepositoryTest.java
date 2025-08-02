package com.projeto.erp.cliente;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ClienteRepositoryTest {
    @Autowired
    private ClienteRepository clienteRepository;

    @Test
    @DisplayName("Deve salvar e buscar cliente por ID")
    void testSaveAndFindById() {
        Cliente cliente = new Cliente();
        cliente.setNome("Cliente Teste");
        cliente.setEmail("teste@email.com");
        cliente.setDocumento("12345678900");
        cliente.setTelefone("11999999999");
        cliente.setAtivo(true);
        cliente = clienteRepository.save(cliente);

        Optional<Cliente> found = clienteRepository.findById(cliente.getId());
        assertTrue(found.isPresent());
        assertEquals("Cliente Teste", found.get().getNome());
    }


}

