package com.projeto.erp.produto.mapper;

import com.projeto.erp.produto.Produto;
import com.projeto.erp.produto.dto.ProdutoRequestDTO;
import com.projeto.erp.produto.dto.ProdutoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {

    @Mapping(source = "fornecedor.id", target = "fornecedorId")
    @Mapping(source = "fornecedor.nome", target = "fornecedorNome")
    ProdutoResponseDTO toDTO(Produto produto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fornecedor", ignore = true)
    Produto toEntity(ProdutoRequestDTO dto);
}
