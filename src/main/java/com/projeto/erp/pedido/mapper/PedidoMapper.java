package com.projeto.erp.pedido.mapper;

import com.projeto.erp.pedido.Pedido;
import com.projeto.erp.pedido.dto.PedidoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ItemPedidoMapper.class)
public interface PedidoMapper {

    @Mapping(source = "cliente.id", target = "clienteId")
    @Mapping(source = "cliente.nome", target = "clienteNome")
    PedidoResponseDTO toDTO(Pedido pedido);
}
