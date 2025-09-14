package com.projeto.erp.pedido.dto;

import com.projeto.erp.pedido.Pedido.StatusPedido;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PedidoResponseDTO {

    private Long id;
    private Long clienteId;
    private String clienteNome;
    private LocalDateTime dataPedido;
    private StatusPedido status;
    private BigDecimal total;
    private List<ItemPedidoResponseDTO> itens;
}
