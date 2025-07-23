package br.com.b2b.infrastructure.pedido.repository;

import br.com.b2b.infrastructure.pedido.entity.PedidoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PedidoRepository extends JpaRepository<PedidoEntity, UUID> {

     Page<PedidoEntity> findAll(Specification<PedidoEntity> where, Pageable page);
}
