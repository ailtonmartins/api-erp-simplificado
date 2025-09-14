package com.projeto.erp.pedido;

import com.projeto.erp.cliente.Cliente;
import com.projeto.erp.cliente.ClienteRepository;
import com.projeto.erp.common.dto.PageResponseDTO;
import com.projeto.erp.common.exception.BusinessException;
import com.projeto.erp.estoque.Estoque;
import com.projeto.erp.estoque.EstoqueRepository;
import com.projeto.erp.pedido.dto.ItemPedidoRequestDTO;
import com.projeto.erp.pedido.dto.PedidoRequestDTO;
import com.projeto.erp.pedido.dto.PedidoResponseDTO;
import com.projeto.erp.pedido.mapper.PedidoMapper;
import com.projeto.erp.produto.Produto;
import com.projeto.erp.produto.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Autowired
    private PedidoMapper pedidoMapper;

    @Transactional
    public PedidoResponseDTO criarPedido(PedidoRequestDTO pedidoRequestDTO) {
        // Validar cliente
        Cliente cliente = clienteRepository.findById(pedidoRequestDTO.getClienteId())
                .orElseThrow(() -> new BusinessException("Cliente não encontrado", HttpStatus.NOT_FOUND));

        // Validar produtos e estoque
        validarItens(pedidoRequestDTO.getItens());

        // Criar pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);

        // Criar itens do pedido
        List<ItemPedido> itens = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (ItemPedidoRequestDTO itemDto : pedidoRequestDTO.getItens()) {
            Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                    .orElseThrow(() -> new BusinessException("Produto não encontrado", HttpStatus.NOT_FOUND));

            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(itemDto.getQuantidade());
            item.setPrecoUnitario(itemDto.getPrecoUnitario());

            itens.add(item);
            total = total.add(item.getSubtotal());
        }

        pedido.setItens(itens);
        pedido.setTotal(total);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        return pedidoMapper.toDTO(pedidoSalvo);
    }

    @Transactional
    public PedidoResponseDTO processarPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new BusinessException("Pedido não encontrado", HttpStatus.NOT_FOUND));

        if (pedido.getStatus() != Pedido.StatusPedido.ABERTO) {
            throw new BusinessException("Apenas pedidos em aberto podem ser processados", HttpStatus.BAD_REQUEST);
        }

        pedido.setStatus(Pedido.StatusPedido.PROCESSANDO);
        Pedido pedidoAtualizado = pedidoRepository.save(pedido);
        return pedidoMapper.toDTO(pedidoAtualizado);
    }

    @Transactional
    public PedidoResponseDTO concluirPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new BusinessException("Pedido não encontrado", HttpStatus.NOT_FOUND));

        if (pedido.getStatus() != Pedido.StatusPedido.PROCESSANDO) {
            throw new BusinessException("Apenas pedidos em processamento podem ser concluídos", HttpStatus.BAD_REQUEST);
        }

        // Atualizar estoque
        for (ItemPedido item : pedido.getItens()) {
            Estoque estoque = estoqueRepository.findByProdutoId(item.getProduto().getId())
                    .orElseThrow(() -> new BusinessException("Estoque não encontrado para o produto: " + item.getProduto().getNome(), HttpStatus.NOT_FOUND));

            try {
                estoque.reduzirQuantidade(item.getQuantidade());
                estoqueRepository.save(estoque);
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Estoque insuficiente para o produto: " + item.getProduto().getNome(), HttpStatus.BAD_REQUEST);
            }
        }

        pedido.setStatus(Pedido.StatusPedido.CONCLUIDO);
        Pedido pedidoAtualizado = pedidoRepository.save(pedido);
        return pedidoMapper.toDTO(pedidoAtualizado);
    }

    public PedidoResponseDTO buscarPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Pedido não encontrado", HttpStatus.NOT_FOUND));
        return pedidoMapper.toDTO(pedido);
    }

    public PageResponseDTO<PedidoResponseDTO> listarTodos(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dataPedido").descending());
        Page<Pedido> pedidosPage = pedidoRepository.findAll(pageable);

        List<PedidoResponseDTO> pedidos = pedidosPage.getContent().stream()
                .map(pedidoMapper::toDTO)
                .toList();

        return new PageResponseDTO<>(
                pedidos,
                pedidosPage.getNumber(),
                pedidosPage.getSize(),
                pedidosPage.getTotalElements(),
                pedidosPage.getTotalPages(),
                pedidosPage.isFirst(),
                pedidosPage.isLast()
        );
    }

    public List<PedidoResponseDTO> listarPorCliente(Long clienteId) {
        List<Pedido> pedidos = pedidoRepository.findByClienteIdOrderByDataPedidoDesc(clienteId);
        return pedidos.stream()
                .map(pedidoMapper::toDTO)
                .toList();
    }

    public List<PedidoResponseDTO> listarPorStatus(Pedido.StatusPedido status) {
        List<Pedido> pedidos = pedidoRepository.findByStatus(status);
        return pedidos.stream()
                .map(pedidoMapper::toDTO)
                .toList();
    }

    @Transactional
    public PedidoResponseDTO cancelarPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new BusinessException("Pedido não encontrado", HttpStatus.NOT_FOUND));

        if (pedido.getStatus() == Pedido.StatusPedido.CONCLUIDO) {
            throw new BusinessException("Pedidos concluídos não podem ser cancelados", HttpStatus.BAD_REQUEST);
        }

        pedido.setStatus(Pedido.StatusPedido.CANCELADO);
        Pedido pedidoAtualizado = pedidoRepository.save(pedido);
        return pedidoMapper.toDTO(pedidoAtualizado);
    }

    private void validarItens(List<ItemPedidoRequestDTO> itens) {
        for (ItemPedidoRequestDTO item : itens) {
            // Verificar se produto existe
            if (!produtoRepository.existsById(item.getProdutoId())) {
                throw new BusinessException("Produto com ID " + item.getProdutoId() + " não encontrado", HttpStatus.NOT_FOUND);
            }

            // Verificar se existe estoque para o produto
            Estoque estoque = estoqueRepository.findByProdutoId(item.getProdutoId())
                    .orElseThrow(() -> new BusinessException("Estoque não encontrado para o produto", HttpStatus.NOT_FOUND));

            if (estoque.getQuantidade() < item.getQuantidade()) {
                throw new BusinessException("Quantidade insuficiente em estoque para o produto", HttpStatus.BAD_REQUEST);
            }
        }
    }
}
