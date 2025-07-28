package com.projeto.erp.cliente.mapper;

import com.projeto.erp.cliente.Cliente;
import com.projeto.erp.cliente.dto.ClienteResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ClienteMapper {

    ClienteMapper INSTANCE = Mappers.getMapper(ClienteMapper.class);

    ClienteResponseDTO toDTO(Cliente cliente);

}
