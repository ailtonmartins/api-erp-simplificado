package com.projeto.erp.pedido.mapper;

import com.projeto.erp.pedido.ItemPedido;
import com.projeto.erp.pedido.dto.ItemPedidoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemPedidoMapper {

    @Mapping(source = "produto.id", target = "produtoId")
    @Mapping(source = "produto.nome", target = "produtoNome")
    @Mapping(expression = "java(itemPedido.getSubtotal())", target = "subtotal")
    ItemPedidoResponseDTO toDTO(ItemPedido itemPedido);
}
