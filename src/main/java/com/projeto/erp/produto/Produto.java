package com.projeto.erp.produto;

import com.projeto.erp.fornecedor.Fornecedor;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "produtos")
@Data
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "codigo_barras", length = 50, unique = true)
    private String codigoBarras;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fornecedor_id")
    private Fornecedor fornecedor;
}

