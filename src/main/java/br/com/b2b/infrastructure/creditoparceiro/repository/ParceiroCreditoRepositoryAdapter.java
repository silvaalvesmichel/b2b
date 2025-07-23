package br.com.b2b.infrastructure.creditoparceiro.repository;

import br.com.b2b.application.port.out.ParceiroCreditoRepositoryPort;
import br.com.b2b.domain.model.CreditoParceiro;
import br.com.b2b.infrastructure.commons.exeception.InfrastructureException;
import br.com.b2b.infrastructure.creditoparceiro.entity.CreditoParceiroEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ParceiroCreditoRepositoryAdapter implements ParceiroCreditoRepositoryPort {

    private final CreditoParceiroRepository repository;

    @Override
    public boolean verificarCredito(Long idParceiro, BigDecimal valorPedido) {
        //buscar credito Parceiro
        Optional<CreditoParceiroEntity> creditoParceiro = repository.findByIdParceiro(idParceiro);
        if (!creditoParceiro.isPresent()) {
           throw new InfrastructureException("Parceiro não cadastrado, realize o cadastro do Parceiro");
        }
        BigDecimal creditoDisponivel = creditoParceiro.get().getCredito();
        boolean temCredito = creditoDisponivel.compareTo(valorPedido) >= 0;

        log.info("Verificação de crédito para parceiro {}: Limite={}, Pedido={}, Aprovado={}",
                idParceiro, creditoDisponivel, valorPedido, temCredito);

        return temCredito;
    }

    @Override
    public void debitarCredito(Long idParceiro, BigDecimal valorPedido) {
        //buscar credito Parceiro
        Optional<CreditoParceiroEntity> creditoParceiro = repository.findByIdParceiro(idParceiro);
        CreditoParceiro creditoParceiroOld = creditoParceiro.get().toDomain();
        BigDecimal newCredito = creditoParceiro.get().getCredito().subtract(valorPedido);
        CreditoParceiro creditoParceiroDebitar = CreditoParceiro.atualizarCredito(creditoParceiroOld, newCredito);
        repository.save(CreditoParceiroEntity.from(creditoParceiroDebitar));
    }

    @Override
    public CreditoParceiro criarParceiroCredito(CreditoParceiro creditoParceiro) {
        return repository.save(CreditoParceiroEntity.from(creditoParceiro)).toDomain();
    }

    @Override
    public CreditoParceiro buscarPorIdParceiro(Long idParceiro) {
        Optional<CreditoParceiroEntity> creditoParceiro = repository.findByIdParceiro(idParceiro);
        return creditoParceiro.map(CreditoParceiroEntity::toDomain).orElse(null);
    }
}