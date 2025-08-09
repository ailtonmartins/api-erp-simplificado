package com.projeto.erp.fornecedor;

import com.projeto.erp.common.exception.BusinessException;
import com.projeto.erp.fornecedor.dto.FornecedorRequestDTO;
import com.projeto.erp.fornecedor.dto.FornecedorResponseDTO;
import com.projeto.erp.fornecedor.mapper.FornecedorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FornecedorService {

    @Autowired
    FornecedorRepository fornecedorRepository;

    @Autowired
    FornecedorMapper mapper;

    public List<FornecedorResponseDTO> buscaTodosFornecedors() {
        List<Fornecedor> fornecedores = fornecedorRepository.findAll();
        if (fornecedores.isEmpty()) {
            throw new BusinessException("Nenhum Fornecedor encontrado" , HttpStatus.NOT_FOUND);
        }
        return fornecedores.stream().map(mapper::toDTO).toList();
     }

    public FornecedorResponseDTO getFornecedor( Long id ) {
        Fornecedor fornecedor = buscaFornecedorByIdOrThrow(id);
        return mapper.toDTO(fornecedor);
    }

    public FornecedorResponseDTO criarFornecedor(FornecedorRequestDTO fornecedor) {
        Fornecedor newFornecedor = mapper.toEntity(fornecedor);
        validarEmailUnico(newFornecedor.getEmail());
        Fornecedor savedFornecedor = fornecedorRepository.save(newFornecedor);
        return mapper.toDTO(savedFornecedor);
    }

    public FornecedorResponseDTO atualizarFornecedor(Long id, FornecedorRequestDTO fornecedor) {
        Fornecedor existingFornecedor = buscaFornecedorByIdOrThrow(id);
        existingFornecedor.setNome(fornecedor.getNome());
        if(fornecedor.getEmail() != null && !fornecedor.getEmail().equals(existingFornecedor.getEmail())) {
            validarEmailUnico(fornecedor.getEmail());
        }
        existingFornecedor.setEmail(fornecedor.getEmail());
        existingFornecedor.setDocumento(fornecedor.getDocumento());
        existingFornecedor.setTelefone(fornecedor.getTelefone());
        existingFornecedor.setAtivo(fornecedor.getAtivo());

        Fornecedor updatedFornecedor = fornecedorRepository.save(existingFornecedor);
        return mapper.toDTO(updatedFornecedor);
    }

    public void deleteFornecedor(Long id) {
        Fornecedor fornecedor = buscaFornecedorByIdOrThrow(id);
        fornecedorRepository.delete(fornecedor);
    }

    private void validarEmailUnico(String email) {
        if (fornecedorRepository.existsByEmail(email)) {
            throw new BusinessException("E-mail já cadastrado", HttpStatus.CONFLICT);
        }
    }

    public Fornecedor buscaFornecedorByIdOrThrow(Long id) {
        return fornecedorRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Fornecedor não encontrado com o ID: " + id, HttpStatus.NOT_FOUND));
    }
}