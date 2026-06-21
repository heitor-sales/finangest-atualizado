package br.edu.ifpb.pweb2.finangest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.edu.ifpb.pweb2.finangest.Util.PasswordUtil;
import br.edu.ifpb.pweb2.finangest.model.Correntista;
import br.edu.ifpb.pweb2.finangest.repository.CorrentistaRepository;

@Component
public class CorrentistaService implements Service<Correntista, Integer>{

    @Autowired
    private CorrentistaRepository correntistaRepository;

    @Override
    public List<Correntista> findall() {
        return correntistaRepository.findAll();
    }

    @Override
    public Correntista findByID(Integer id) {
        return correntistaRepository.findById(id).orElse(null);
    }

    @Override
    public Correntista save(Correntista c) {
        c.setSenha(PasswordUtil.hashPassword(c.getSenha()));
       return correntistaRepository.save(c);
    }

    @Override
    public void deleteById(Integer id) {
        correntistaRepository.deleteById(id);
    }
    
}
