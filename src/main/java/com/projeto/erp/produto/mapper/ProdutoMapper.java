package com.projeto.erp.produto.mapper;

import com.projeto.erp.produto.Produto;
import com.projeto.erp.produto.dto.ProdutoRequestDTO;
import com.projeto.erp.produto.dto.ProdutoResponseDTO;
import com.projeto.erp.produto.dto.ProdutoResponseSemQtdDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {

    @Mapping(source = "fornecedor.id", target = "fornecedorId")
    @Mapping(source = "fornecedor.nome", target = "fornecedorNome")
    @Mapping(source = "estoque.quantidade", target = "quantidadeEstoque")
    ProdutoResponseDTO toDTO(Produto produto);

    @Named("toDTOSemQuantidade")
    @Mapping(source = "fornecedor.id", target = "fornecedorId")
    @Mapping(source = "fornecedor.nome", target = "fornecedorNome")
    ProdutoResponseSemQtdDTO toDTOSemQuantidade(Produto produto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fornecedor", ignore = true)
    @Mapping(target = "estoque", ignore = true)
    Produto toEntity(ProdutoRequestDTO dto);
}
