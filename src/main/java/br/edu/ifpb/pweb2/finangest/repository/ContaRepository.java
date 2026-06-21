package br.edu.ifpb.pweb2.finangest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import br.edu.ifpb.pweb2.finangest.model.Conta;
import br.edu.ifpb.pweb2.finangest.model.Correntista;

@Component
public interface ContaRepository  extends JpaRepository<Conta, Integer>{

   List<Conta> findByCorrentista(Correntista correntista);


   
}