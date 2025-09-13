package com.projeto.erp.estoque.mapper;

import com.projeto.erp.estoque.Estoque;
import com.projeto.erp.estoque.dto.EstoqueResponseDTO;
import com.projeto.erp.produto.mapper.ProdutoMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ProdutoMapper.class)
public interface EstoqueMapper {

    @Mapping(source = "produto", target = "produto", qualifiedByName = "toDTOSemQuantidade")
    EstoqueResponseDTO toDTO(Estoque estoque);

}
