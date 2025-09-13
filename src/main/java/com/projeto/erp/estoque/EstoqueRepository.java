package com.projeto.erp.estoque;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

    Optional<Estoque> findByProdutoId(Long produtoId);
    Boolean existsByProdutoId(Long produtoId);
}
