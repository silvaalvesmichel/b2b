package br.com.b2b.application.service;

import br.com.b2b.application.port.in.PedidoUseCase;
import br.com.b2b.application.port.out.NotificacaoPort;
import br.com.b2b.application.port.out.ParceiroCreditoRepositoryPort;
import br.com.b2b.application.port.out.PedidoRepositoryPort;
import br.com.b2b.domain.exception.DomainException;
import br.com.b2b.domain.model.ItemPedido;
import br.com.b2b.domain.model.Pedido;
import br.com.b2b.domain.model.enums.StatusPedido;
import br.com.b2b.infrastructure.commons.Pagination;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoUseCase {

    private final PedidoRepositoryPort pedidoRepository;
    private final ParceiroCreditoRepositoryPort parceiroCreditoRepositoryPort;
    private final NotificacaoPort notificacaoPort;

    @Transactional
    @Override
    public Pedido criarPedido(Long idParceiro, List<ItemPedido> itens) {

        Pedido novoPedido = Pedido.newPedido(idParceiro, itens);

        // vericacao de credito
        boolean itemCredito = parceiroCreditoRepositoryPort.verificarCredito(
                novoPedido.getIdParceiro(),
                novoPedido.getValorTotal().valor()
        );

        if (!itemCredito) {
            throw new DomainException("Parceiro não possui crédito suficiente");
        }

        //persistir o estado inicial
        Pedido pedidoSalvo = pedidoRepository.salvar(novoPedido);

        //debita credito do parceiro
        parceiroCreditoRepositoryPort.debitarCredito(novoPedido.getIdParceiro(), novoPedido.getValorTotal().valor());

        //notificar operacao externa
        try {
            notificacaoPort.notificarMudancaStatus(pedidoSalvo);
        }catch (JsonProcessingException e) {
            log.error("Erro ao publicar notificação: {} ", e.getMessage());
        }

        return pedidoSalvo;
    }

    @Override
    public Pedido atualizarStatus(UUID id, String status) {

        Optional<Pedido> pedido = pedidoRepository.buscarPorId(id);
        if (pedido.isEmpty()) {
            throw new DomainException("Pedido não encontrado.");
        }
        StatusPedido statusPedido;
        try {
            statusPedido = StatusPedido.valueOf(status);
        } catch (Exception e) {
            throw new DomainException("Status inválido.");
        }
        Pedido pedidoParaAtualizar = Pedido.atualizarStatus(pedido.get(), statusPedido);
        Pedido pedidoAtualizado = pedidoRepository.salvar(pedidoParaAtualizar);
        // notificar
        try {
            notificacaoPort.notificarMudancaStatus(pedidoAtualizado);
        }catch (JsonProcessingException e) {
            log.error("Erro ao publicar notificação: {} ", e.getMessage());
        }

        return pedidoAtualizado;
    }

    @Override
    public Pagination<Pedido> listarPedidos(String search, Integer page, Integer perPage, String dir,
                                            String sort, UUID id, String status, OffsetDateTime dateStart,
                                            OffsetDateTime dateEnd) {

        final var searchPage = PageRequest.of(
                page,
                perPage,
                Sort.by(Sort.Direction.fromString(dir), sort)
        );

        if(Objects.nonNull(sort)){
            try {
                StatusPedido.valueOf(status);
            } catch (Exception e) {
                throw new DomainException("Status inválido.");
            }
        }

       return pedidoRepository.listarPedidos(searchPage, id, status, dateStart, dateEnd);

    }
}
