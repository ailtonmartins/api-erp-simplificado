package com.projeto.erp.produto;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    boolean existsByCodigoBarras(String codigoBarras);
}

