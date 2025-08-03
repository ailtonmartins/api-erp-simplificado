package com.projeto.erp.cliente;

import com.projeto.erp.cliente.dto.ClienteRequestDTO;
import com.projeto.erp.cliente.dto.ClienteResponseDTO;
import com.projeto.erp.cliente.mapper.ClienteMapper;
import com.projeto.erp.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    ClienteMapper mapper;

    public List<ClienteResponseDTO> buscaTodosClientes() {
        List<Cliente> clientes = clienteRepository.findAll();
        if (clientes.isEmpty()) {
            throw new RuntimeException("Nenhum cliente encontrado");
        }
        return clientes.stream().map(mapper::toDTO).toList();
     }

    public ClienteResponseDTO getCliente( Long id ) {
        Cliente cliente = buscaClienteByIdOrThrow(id);
        return mapper.toDTO(cliente);
    }

    public ClienteResponseDTO criarCliente(ClienteRequestDTO cliente) {
        Cliente newCliente = mapper.toEntity(cliente);
        validarEmailUnico(newCliente.getEmail());
        Cliente savedCliente = clienteRepository.save(newCliente);
        return mapper.toDTO(savedCliente);
    }

    public ClienteResponseDTO atualizarCliente(Long id, ClienteRequestDTO cliente) {
        Cliente existingCliente = buscaClienteByIdOrThrow(id);
        existingCliente.setNome(cliente.getNome());
        if(cliente.getEmail() != null && !cliente.getEmail().equals(existingCliente.getEmail())) {
            validarEmailUnico(cliente.getEmail());
        }
        existingCliente.setEmail(cliente.getEmail());
        existingCliente.setDocumento(cliente.getDocumento());
        existingCliente.setTelefone(cliente.getTelefone());
        existingCliente.setAtivo(cliente.getAtivo());

        Cliente updatedCliente = clienteRepository.save(existingCliente);
        return mapper.toDTO(updatedCliente);
    }

    public void deleteCliente(Long id) {
        Cliente cliente = buscaClienteByIdOrThrow(id);
        clienteRepository.delete(cliente);
    }

    private void validarEmailUnico(String email) {
        if (clienteRepository.existsByEmail(email)) {
            throw new BusinessException("E-mail já cadastrado", HttpStatus.CONFLICT);
        }
    }

    private Cliente buscaClienteByIdOrThrow(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cliente não encontrado com o ID: " + id, HttpStatus.NOT_FOUND));
    }
}