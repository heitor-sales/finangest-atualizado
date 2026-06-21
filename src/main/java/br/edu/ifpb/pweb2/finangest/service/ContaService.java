package br.edu.ifpb.pweb2.finangest.service;
import br.edu.ifpb.pweb2.finangest.model.Conta;
import br.edu.ifpb.pweb2.finangest.repository.ContaRepository;
import br.edu.ifpb.pweb2.finangest.model.Correntista;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ContaService implements Service<Conta,Integer> {

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private CorrentistaService correntistaRepository;

    @Override
    public List<Conta> findall() {
        return contaRepository.findAll();
}
    public List<Conta> findByCorrentista(Correntista correntista) {
            return contaRepository.findByCorrentista(correntista);
        }

    @Override
    public Conta findByID(Integer id) {
        return contaRepository.findById(id).orElse(null);
    }

    // 
    public Optional<Conta> findById(Integer id) {
        return contaRepository.findById(id);
    }
    // 

    @Override
    public Conta save(Conta conta) {
        Correntista correntista=correntistaRepository.findByID(conta.getCorrentista().getId());
        conta.setCorrentista(correntista);
        return contaRepository.save(conta);
   
    }

    @Override
    public void deleteById(Integer id) {
        contaRepository.deleteById(id);
    }

}
