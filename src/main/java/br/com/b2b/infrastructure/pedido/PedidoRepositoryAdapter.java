package br.com.b2b.infrastructure.pedido;

import br.com.b2b.application.port.out.PedidoRepositoryPort;
import br.com.b2b.domain.model.Pedido;
import br.com.b2b.domain.model.StatusPedido;
import br.com.b2b.infrastructure.commons.Pagination;
import br.com.b2b.infrastructure.pedido.entity.PedidoEntity;
import br.com.b2b.infrastructure.pedido.repository.PedidoRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PedidoRepositoryAdapter implements PedidoRepositoryPort {

    private final PedidoRepository jpaRepository;


    @Override
    public Pedido salvar(Pedido pedido) {
        PedidoEntity pedidoEntity = PedidoEntity.from(pedido);
        return jpaRepository.save(pedidoEntity).toDomain();
    }

    @Override
    public Optional<Pedido> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(PedidoEntity::toDomain);
    }

    @Override
    public Pagination<Pedido> listarPedidos(PageRequest searchPage, UUID id, String status, OffsetDateTime dateStart, OffsetDateTime dateEnd) {
        Specification<PedidoEntity> combinedSpec = createWhereSpecification(id, status, dateStart, dateEnd);
        final var pageResult = jpaRepository.findAll(combinedSpec, searchPage);
        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.map(PedidoEntity::toDomain).toList()
        );
    }

    private Specification<PedidoEntity> createWhereSpecification(UUID id, String status, OffsetDateTime dateStart, OffsetDateTime dateEnd) {
        return (Root<PedidoEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction();

            if (id != null) {
                predicate = cb.and(predicate, cb.equal(root.get("id"), id));
            }

            if (status != null && !status.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), StatusPedido.valueOf(status.toUpperCase())));
            }

            if (dateStart != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("dataCriacao"), dateStart.toLocalDateTime()));
            }

            if (dateEnd != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("dataCriacao"), dateEnd.toLocalDateTime()));
            }

            return predicate;
        };
    }
}
