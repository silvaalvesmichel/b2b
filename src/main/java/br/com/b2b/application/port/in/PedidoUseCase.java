package br.com.b2b.application.port.in;

import br.com.b2b.domain.model.ItemPedido;
import br.com.b2b.domain.model.Pedido;
import br.com.b2b.infrastructure.commons.Pagination;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface PedidoUseCase {

    Pedido criarPedido(Long idParceiro, List<ItemPedido> itens);
    Pedido aprovarPedido(UUID id);

    Pedido atualizarStatus(UUID id, String status);

    Pagination<Pedido> listarPedidos(String search, Integer page, Integer perPage, String dir, String sort, UUID id, String status, OffsetDateTime dateStart, OffsetDateTime dateEnd);
}
