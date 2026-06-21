package br.edu.ifpb.pweb2.finangest.api;

import br.edu.ifpb.pweb2.finangest.model.Categoria;
import br.edu.ifpb.pweb2.finangest.repository.CategoriaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaRestController {

    private final CategoriaRepository categoriaRepository;

    // Melhor prática: Injeção pelo construtor em vez de @Autowired no field
    public CategoriaRestController(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping
    public ResponseEntity<List<Categoria>> listarTodas() {
        return ResponseEntity.ok(categoriaRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Categoria> criar(@RequestBody Categoria categoria) {
        Categoria novaCategoria = categoriaRepository.save(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaCategoria); // Retorna 201 Created
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> atualizar(@PathVariable Long id, @RequestBody Categoria categoria) {
        if (!categoriaRepository.existsById(id)) {
            return ResponseEntity.notFound().build(); // Retorna 404 se a categoria não existir
        }
        categoria.setId(id);
        Categoria categoriaAtualizada = categoriaRepository.save(categoria);
        return ResponseEntity.ok(categoriaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!categoriaRepository.existsById(id)) {
            return ResponseEntity.notFound().build(); // Retorna 404 se não existir
        }
        categoriaRepository.deleteById(id);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content para deleção com sucesso
    }
}