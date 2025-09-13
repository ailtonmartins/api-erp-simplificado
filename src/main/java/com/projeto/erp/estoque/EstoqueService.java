package com.projeto.erp.estoque;

import com.projeto.erp.common.exception.BusinessException;
import com.projeto.erp.estoque.dto.EstoqueRequestDTO;
import com.projeto.erp.estoque.dto.EstoqueResponseDTO;
import com.projeto.erp.estoque.mapper.EstoqueMapper;
import com.projeto.erp.produto.Produto;
import com.projeto.erp.produto.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EstoqueService {

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    EstoqueMapper estoqueMapper;

    @Transactional
    public EstoqueResponseDTO entradaEstoque(EstoqueRequestDTO requestDTO) {

        Produto produto = produtoService.buscaProdutoByIdOrThrow(requestDTO.getIdProduto());

        Estoque estoque = estoqueRepository.findByProdutoId(produto.getId())
                .orElseGet(() ->  estoqueRepository.save(
                                                          Estoque.builder().
                                                                  produto(produto).
                                                                  quantidade(0).build()));
        estoque.setQuantidade(estoque.getQuantidade() + requestDTO.getQuantidade());
        return estoqueMapper.toDTO(estoqueRepository.save(estoque));
    }

    @Transactional
    public EstoqueResponseDTO saidaEstoque(EstoqueRequestDTO requestDTO) {
        Estoque estoque = estoqueRepository.findByProdutoId(requestDTO.getIdProduto())
                .orElseThrow(() -> new BusinessException("Estoque não encontrado", HttpStatus.NOT_FOUND));

        if (estoque.getQuantidade() < requestDTO.getQuantidade()) {
            throw new BusinessException("Saldo insuficiente no estoque", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        estoque.setQuantidade(estoque.getQuantidade() - requestDTO.getQuantidade());
        return estoqueMapper.toDTO(estoqueRepository.save(estoque));
    }

    public EstoqueResponseDTO consultarSaldo(Long produtoId) {
        Estoque estoque = estoqueRepository.findByProdutoId(produtoId)
                .orElseThrow(() -> new BusinessException("Estoque não encontrado", HttpStatus.NOT_FOUND));
        return estoqueMapper.toDTO(estoque);
    }
}