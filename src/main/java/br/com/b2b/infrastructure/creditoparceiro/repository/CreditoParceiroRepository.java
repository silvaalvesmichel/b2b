package br.com.b2b.infrastructure.creditoparceiro.repository;

import br.com.b2b.infrastructure.creditoparceiro.entity.CreditoParceiroEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CreditoParceiroRepository extends JpaRepository<CreditoParceiroEntity, UUID> {

    Optional<CreditoParceiroEntity> findByIdParceiro(Long idParceiro);
}
