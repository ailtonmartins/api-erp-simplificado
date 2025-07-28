package com.projeto.erp.cliente;

import com.projeto.erp.cliente.dto.ClienteResponseDTO;
import com.projeto.erp.cliente.mapper.ClienteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    ClienteRepository clienteRepository;

    private final ClienteMapper mapper = ClienteMapper.INSTANCE;

    public List<ClienteResponseDTO> findAll() {
        List<Cliente> clientes = clienteRepository.findAll();
        if (clientes.isEmpty()) {
            throw new RuntimeException("Nenhum cliente encontrado");
        }
        List<ClienteResponseDTO> dtos = clientes.stream().map(mapper::toDTO).toList();
        return dtos;
     }
}
