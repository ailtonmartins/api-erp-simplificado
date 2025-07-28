package com.projeto.erp.cliente.mapper;

import com.projeto.erp.cliente.Cliente;
import com.projeto.erp.cliente.dto.ClienteRequestDTO;
import com.projeto.erp.cliente.dto.ClienteResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    ClienteMapper INSTANCE = Mappers.getMapper(ClienteMapper.class);
    ClienteResponseDTO toDTO(Cliente cliente);
    Cliente toEntity(ClienteRequestDTO dto);
}
