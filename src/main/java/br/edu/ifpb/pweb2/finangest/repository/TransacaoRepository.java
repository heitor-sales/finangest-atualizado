package br.edu.ifpb.pweb2.finangest.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpb.pweb2.finangest.model.Conta;
import br.edu.ifpb.pweb2.finangest.model.Transacao;

public interface TransacaoRepository extends JpaRepository<Transacao, Integer> {

    List<Transacao> findByContaAndDataBetween(Conta conta, LocalDate dataInicio, LocalDate dataFim);
    List<Transacao> findByContaAndDataBefore(Conta conta, LocalDate data);
    List<Transacao> findByConta(Conta conta);

}
