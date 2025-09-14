package com.projeto.erp.estoque;


import com.projeto.erp.produto.Produto;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estoque")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Estoque {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "produto_id", unique = true, nullable = false)
    private Produto produto;

    @Builder.Default
    @Min(0)
    private Integer quantidade = 0;

    public void reduzirQuantidade(Integer quantidade) {
        if (this.quantidade < quantidade) {
            throw new IllegalArgumentException("Quantidade insuficiente em estoque");
        }
        this.quantidade -= quantidade;
    }

    public void aumentarQuantidade(Integer quantidade) {
        this.quantidade += quantidade;
    }
}