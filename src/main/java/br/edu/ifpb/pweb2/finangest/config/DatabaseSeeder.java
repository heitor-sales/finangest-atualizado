package br.edu.ifpb.pweb2.finangest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import br.edu.ifpb.pweb2.finangest.model.Categoria;
import br.edu.ifpb.pweb2.finangest.model.Correntista;
import br.edu.ifpb.pweb2.finangest.model.NaturezaCategoria;
import br.edu.ifpb.pweb2.finangest.repository.CategoriaRepository;
import br.edu.ifpb.pweb2.finangest.repository.CorrentistaRepository;
import br.edu.ifpb.pweb2.finangest.service.CorrentistaService;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private CorrentistaService correntistaService;

    @Autowired
    private CorrentistaRepository correntistaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Override
    public void run(String... args) throws Exception {
        
        // 1. Criar o Administrador Padrão (se não existir)
        if (correntistaRepository.findByEmail("admin@finangest.com.br") == null) {
            Correntista admin = new Correntista();
            admin.setNome("Administrador do Sistema");
            admin.setEmail("admin@finangest.com.br");
            admin.setSenha("12345678"); // A senha limpa passará pela criptografia do Service!
            admin.setAdmin(true);
            admin.setAtivo(true);
            
            correntistaService.save(admin);
            System.out.println("✅ Administrador padrão criado com sucesso!");
        }

        // 2. Criar as 22 Categorias Obrigatórias (se a tabela estiver vazia)
        if (categoriaRepository.count() == 0) {
            
            // --- Natureza: ENTRADA ---
            categoriaRepository.save(new Categoria(null, "Salário", NaturezaCategoria.ENTRADA, 1, true));
            categoriaRepository.save(new Categoria(null, "Cashback", NaturezaCategoria.ENTRADA, 3, true));
            categoriaRepository.save(new Categoria(null, "Resgate Investimento", NaturezaCategoria.ENTRADA, 2, true));
            categoriaRepository.save(new Categoria(null, "Outras Entradas", NaturezaCategoria.ENTRADA, 3, true));
            
            // --- Natureza: SAIDA ---
            categoriaRepository.save(new Categoria(null, "Saúde e Remédios", NaturezaCategoria.SAIDA, 1, true));
            categoriaRepository.save(new Categoria(null, "Academia e Personal", NaturezaCategoria.SAIDA, 2, true));
            categoriaRepository.save(new Categoria(null, "Carros e Uber", NaturezaCategoria.SAIDA, 2, true));
            categoriaRepository.save(new Categoria(null, "Educação e Cursos", NaturezaCategoria.SAIDA, 1, true));
            categoriaRepository.save(new Categoria(null, "Lazer e Turismo", NaturezaCategoria.SAIDA, 3, true));
            categoriaRepository.save(new Categoria(null, "Condomínio", NaturezaCategoria.SAIDA, 1, true));
            categoriaRepository.save(new Categoria(null, "Energia", NaturezaCategoria.SAIDA, 1, true));
            categoriaRepository.save(new Categoria(null, "Celular", NaturezaCategoria.SAIDA, 2, true));
            categoriaRepository.save(new Categoria(null, "Internet", NaturezaCategoria.SAIDA, 2, true));
            categoriaRepository.save(new Categoria(null, "Itens Pessoais", NaturezaCategoria.SAIDA, 2, true));
            categoriaRepository.save(new Categoria(null, "Feira", NaturezaCategoria.SAIDA, 1, true));
            categoriaRepository.save(new Categoria(null, "Casa", NaturezaCategoria.SAIDA, 1, true));
            categoriaRepository.save(new Categoria(null, "Impostos", NaturezaCategoria.SAIDA, 2, true));
            categoriaRepository.save(new Categoria(null, "Outros gastos", NaturezaCategoria.SAIDA, 3, true));
            
            // --- Natureza: INVESTIMENTO ---
            categoriaRepository.save(new Categoria(null, "Aporte Renda Fixa", NaturezaCategoria.INVESTIMENTO, 1, true));
            categoriaRepository.save(new Categoria(null, "Aporte Renda Variável", NaturezaCategoria.INVESTIMENTO, 2, true));
            categoriaRepository.save(new Categoria(null, "Aporte Reserva Emergencia", NaturezaCategoria.INVESTIMENTO, 1, true));
            categoriaRepository.save(new Categoria(null, "Aporte Previdência", NaturezaCategoria.INVESTIMENTO, 2, true));
            
            System.out.println("✅ Todas as 22 categorias obrigatórias do SpendWise foram inicializadas!");
        }
    }
}