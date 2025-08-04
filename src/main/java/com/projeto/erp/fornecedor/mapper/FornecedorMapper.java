package com.projeto.erp.fornecedor.mapper;

import com.projeto.erp.fornecedor.Fornecedor;
import com.projeto.erp.fornecedor.dto.FornecedorRequestDTO;
import com.projeto.erp.fornecedor.dto.FornecedorResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FornecedorMapper {

    FornecedorResponseDTO toDTO(Fornecedor fornecedor);

    @Mapping(target = "id", ignore = true)
    Fornecedor toEntity(FornecedorRequestDTO dto);
}
