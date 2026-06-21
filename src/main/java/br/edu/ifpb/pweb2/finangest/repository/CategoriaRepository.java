package br.edu.ifpb.pweb2.finangest.repository;

import br.edu.ifpb.pweb2.finangest.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
   
}