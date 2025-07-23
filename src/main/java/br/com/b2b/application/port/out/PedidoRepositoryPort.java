package br.com.b2b.application.port.out;

import br.com.b2b.domain.model.Pedido;
import br.com.b2b.infrastructure.commons.Pagination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface PedidoRepositoryPort {

    Pedido salvar(Pedido pedido);
    Optional<Pedido> buscarPorId(UUID id);

    Pagination<Pedido> listarPedidos(PageRequest searchPage, UUID id, String status, OffsetDateTime dateStart, OffsetDateTime dateEnd);
}
