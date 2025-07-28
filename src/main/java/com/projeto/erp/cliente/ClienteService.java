package com.projeto.erp.cliente;

import com.projeto.erp.cliente.dto.ClienteRequestDTO;
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

    public ClienteResponseDTO getCliente( Long id ) {

        var cliente = clienteRepository.findById(id).orElseThrow( () -> new RuntimeException("Cliente não encontrado com o ID:" + id)  );
        ClienteResponseDTO dto = mapper.toDTO(cliente);
        return dto;
    }

    public ClienteResponseDTO createCliente(ClienteRequestDTO cliente) {
        Cliente newCliente = mapper.toEntity(cliente);
        Cliente savedCliente = clienteRepository.save(newCliente);
        return mapper.toDTO(savedCliente);
    }

    public ClienteResponseDTO updateCliente(Long id, ClienteRequestDTO cliente) {
        Cliente existingCliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com o ID: " + id));

        existingCliente.setNome(cliente.getNome());
        existingCliente.setEmail(cliente.getEmail());
        existingCliente.setDocumento(cliente.getDocumento());
        existingCliente.setTelefone(cliente.getTelefone());
        existingCliente.setAtivo(cliente.getAtivo());

        Cliente updatedCliente = clienteRepository.save(existingCliente);
        return mapper.toDTO(updatedCliente);
    }

    public void deleteCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com o ID: " + id));
        clienteRepository.delete(cliente);
    }
}
