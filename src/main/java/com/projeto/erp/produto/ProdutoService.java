package com.projeto.erp.produto;

import com.projeto.erp.common.exception.BusinessException;
import com.projeto.erp.fornecedor.Fornecedor;
import com.projeto.erp.fornecedor.FornecedorService;
import com.projeto.erp.produto.dto.ProdutoRequestDTO;
import com.projeto.erp.produto.dto.ProdutoResponseDTO;
import com.projeto.erp.produto.mapper.ProdutoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private FornecedorService fornecedorService;

    @Autowired
    ProdutoMapper mapper;


    public ProdutoResponseDTO criarProduto(ProdutoRequestDTO dto) {
        validarCodigoBarraUnico(dto.getCodigoBarras());

        Fornecedor fornecedor = fornecedorService.buscaFornecedorByIdOrThrow(dto.getFornecedorId());

        Produto produto = mapper.toEntity(dto);
        produto.setFornecedor(fornecedor);

        produto = produtoRepository.save(produto);
        return  mapper.toDTO(produto);
    }

    public ProdutoResponseDTO buscarPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
        return mapper.toDTO(produto);
    }

    public List<ProdutoResponseDTO> listarTodos() {
        return produtoRepository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    public ProdutoResponseDTO atualizarProduto(Long id, ProdutoRequestDTO dto) {
        Produto existingProduto = buscaProdutoByIdOrThrow(id);

        if (!existingProduto.getCodigoBarras().equals(dto.getCodigoBarras())) {
            validarCodigoBarraUnico(dto.getCodigoBarras());
        }

        Fornecedor fornecedor = fornecedorService.buscaFornecedorByIdOrThrow(dto.getFornecedorId());

        existingProduto.setNome(dto.getNome());
        existingProduto.setDescricao(dto.getDescricao());
        existingProduto.setCodigoBarras(dto.getCodigoBarras());
        existingProduto.setPreco(dto.getPreco());
        existingProduto.setFornecedor(fornecedor);

        existingProduto = produtoRepository.save(existingProduto);
        return mapper.toDTO(existingProduto);
    }

    public void deletarProduto(Long id) {
        Produto produto = buscaProdutoByIdOrThrow(id);
        produtoRepository.delete(produto);
    }

    private void validarCodigoBarraUnico(String codigoBarras) {
        if (produtoRepository.existsByCodigoBarras(codigoBarras)) {
            throw new BusinessException("Código de barras já cadastrado", HttpStatus.CONFLICT);
        }
    }

    private Produto buscaProdutoByIdOrThrow(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Produto não encontrado com o ID: " + id, HttpStatus.NOT_FOUND));
    }
}

