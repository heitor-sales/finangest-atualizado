package br.edu.ifpb.pweb2.finangest.service;

import java.util.List;

public interface Service<T,ID>{

    public List<T>findall();

    public T findByID(ID id);
    
    public T save(T t);

    public void deleteById(Integer id);

}
