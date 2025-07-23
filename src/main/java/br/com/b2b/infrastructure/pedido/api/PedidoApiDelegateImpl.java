package br.com.b2b.infrastructure.pedido.api;

import br.com.b2b.application.port.in.PedidoUseCase;
import br.com.b2b.domain.model.Pedido;
import br.com.b2b.infrastructure.commons.Pagination;
import br.com.b2b.infrastructure.openapi.pedido.api.PedidoApiDelegate;
import br.com.b2b.infrastructure.openapi.pedido.model.PedidoPaginadoResponse;
import br.com.b2b.infrastructure.openapi.pedido.model.PedidoRequest;
import br.com.b2b.infrastructure.openapi.pedido.model.PedidoResponse;
import br.com.b2b.infrastructure.pedido.mapper.PedidoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoApiDelegateImpl implements PedidoApiDelegate {

    private final PedidoUseCase useCase;
    private final PedidoMapper mapper;

    @Override
    public ResponseEntity<PedidoResponse> criar(PedidoRequest pedidoRequest) throws Exception {
        Pedido pedido = useCase.criarPedido(pedidoRequest.getIdParceiro(), mapper.toItensDomain(pedidoRequest.getItens()));
        return ResponseEntity.ok().body(mapper.toResponse(pedido));
    }

    @Override
    public ResponseEntity<PedidoResponse> atualizarStatus(UUID id,
                                                          String status) {
        Pedido pedido = useCase.atualizarStatus(id, status);
        return ResponseEntity.ok().body(mapper.toResponse(pedido));
    }

    @Override
    public ResponseEntity<PedidoPaginadoResponse> listar(String search,
                                                         Integer page,
                                                         Integer perPage,
                                                         String dir,
                                                         String sort,
                                                         UUID id,
                                                         String status,
                                                         OffsetDateTime dateStart,
                                                         OffsetDateTime dateEnd) throws Exception {

        Pagination<Pedido> pedidos = useCase.listarPedidos(search, page, perPage, dir, sort, id, status, dateStart, dateEnd);
        return ResponseEntity.ok().body(mapper.toResponsePagination(pedidos));
    }
}
