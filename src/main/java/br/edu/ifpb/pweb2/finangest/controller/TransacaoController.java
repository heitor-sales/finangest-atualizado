package br.edu.ifpb.pweb2.finangest.controller;

import br.edu.ifpb.pweb2.finangest.model.Conta;
import br.edu.ifpb.pweb2.finangest.model.Transacao;
import br.edu.ifpb.pweb2.finangest.model.Comentario;
import br.edu.ifpb.pweb2.finangest.repository.CategoriaRepository;
import br.edu.ifpb.pweb2.finangest.repository.ContaRepository;
import br.edu.ifpb.pweb2.finangest.repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Optional;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.ui.Model;
@Controller
@RequestMapping("/transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    
    @GetMapping("/form")
    public ModelAndView getForm(@RequestParam(name = "contaId", required = false) Integer contaId, ModelAndView modelAndView) {
        modelAndView.setViewName("transacoes/form");
        Transacao transacao = new Transacao(); 

        // Se um contaId for fornecido (ao clicar em "Registrar Nova Transação" no extrato)
        if (contaId != null) {
            Optional<Conta> contaOpt = contaRepository.findById(contaId);
            if (contaOpt.isPresent()) {
                Conta conta = contaOpt.get();
                transacao.setConta(conta);
                transacao.setData(LocalDate.now()); // Pré-preenche a data com a data atual
                transacao.setComentario(new Comentario()); // Garante que o objeto Comentario não seja nulo para o formulário
            } else {
                // Se contaId foi fornecido mas a conta não foi encontrada, redireciona com erro
                modelAndView.setViewName("redirect:/contas/list");
                modelAndView.addObject("errorMessage", "Conta não encontrada com o ID fornecido.");
                return modelAndView;
            }
        } else {
            // Caso acesse /transacoes/form diretamente sem contaId,
            // o campo de seleção de conta no futuro formulário seria necessário.
            // Para este cenário, como o link vem do extrato, é esperado que `contaId` esteja presente.
            // Se quiser permitir adicionar transações sem um contaId inicial,
            // precisaria adicionar um campo de seleção de conta no formulário.
            // Por enquanto, inicializa uma conta vazia para evitar NullPointerException no Thymeleaf.
            transacao.setConta(new Conta());
            transacao.setComentario(new Comentario()); // Garante o objeto Comentario
        }

        modelAndView.addObject("transacao", transacao);
        modelAndView.addObject("categoriaItems", categoriaRepository.findAll()); // Carrega todas as categorias
        return modelAndView;
    }

    // Método para salvar uma transação (POST do formulário)
    @PostMapping("/save")
    public String saveTransacao(@Valid @ModelAttribute("transacao") Transacao transacao,
                                BindingResult result, // Adicione BindingResult aqui
                                Model model, // Adicione Model aqui para usar addAttribute ao retornar a view
                                RedirectAttributes redirectAttributes) {

        // --- INÍCIO DA VALIDAÇÃO DO SPRING VALIDATION ---
        if (result.hasErrors()) {
            // Se houver erros de validação (incluindo o @Size do Comentario),
            // retorne para o formulário para exibir os erros ao usuário.
            // É importante adicionar novamente os dados que o formulário precisa.
            model.addAttribute("categoriaItems", categoriaRepository.findAll());
            // Se a transação já tem uma conta, passamos ela para que o form se preencha
            // Caso contrário, passamos uma nova Conta vazia para evitar NPE.
            if (transacao.getConta() == null) {
                transacao.setConta(new Conta());
            }
            // Garante que o objeto Comentario não seja nulo se o erro for dele
            if (transacao.getComentario() == null) {
                transacao.setComentario(new Comentario());
            }
            return "transacoes/form"; // Retorna para o template do formulário
        }
        // --- FIM DA VALIDAÇÃO DO SPRING VALIDATION ---

        

        // Validação da Conta (mantida, mas pode ser complementada com @NotNull ou @Valid na Transacao)
        if (transacao.getConta() == null || transacao.getConta().getId() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "A transação deve ser associada a uma conta válida.");
            return "redirect:/transacoes/form";
        }

        // Busca a entidade Conta completa do banco de dados
        Optional<Conta> contaOpt = contaRepository.findById(transacao.getConta().getId());
        if (contaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Conta não encontrada para a transação.");
            return "redirect:/contas/list";
        }
        transacao.setConta(contaOpt.get()); // Associa a entidade Conta completa à transação

        // Validação da Categoria (mantida, mas pode ser complementada com @NotNull ou @Valid na Transacao)
        if (transacao.getCategoria() == null || transacao.getCategoria().getId() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Selecione uma categoria para a transação.");
            return "redirect:/transacoes/form?contaId=" + transacao.getConta().getId();
        }

        // Busca a entidade Categoria completa do banco de dados
        categoriaRepository.findById(transacao.getCategoria().getId())
            .ifPresentOrElse(
                transacao::setCategoria,
                () -> {
                    // Categoria não encontrada, isso não deveria acontecer se o select é preenchido do banco
                    redirectAttributes.addFlashAttribute("errorMessage", "Categoria não encontrada para a transação.");
                    // Usamos model.addAttribute e retornamos para a view diretamente
                    // para manter o estado do formulário se o erro não for de redirect
                    model.addAttribute("categoriaItems", categoriaRepository.findAll());
                    return; // Retorna para sair do lambda e continuar para o save, que pode lançar exceção ou ser evitado
                }
            );

       
        if (transacao.getComentario() != null && (transacao.getComentario().getTexto() == null || transacao.getComentario().getTexto().trim().isEmpty())) {
            transacao.setComentario(null);
        } else if (transacao.getComentario() != null && transacao.getComentario().getId() != null) {
            // Se for uma edição e o comentário já existe, o Hibernate/JPA deve lidar com a atualização.
            // Não precisamos buscar o Comentario aqui se o CascadeType.ALL está em Transacao.
        }

        transacaoRepository.save(transacao); // Salva a transação (e o comentário, se houver, devido ao CascadeType.ALL)
        redirectAttributes.addFlashAttribute("successMessage", "Transação salva com sucesso!");

        // Redireciona de volta para o extrato da conta à qual a transação foi registrada
        return "redirect:/contas/" + transacao.getConta().getId() + "/extrato";
    }

    // Método para exibir o formulário de edição de transação
    @GetMapping("/edit/{id}")
    public ModelAndView getFormForEdit(@PathVariable("id") Integer id, ModelAndView modelAndView) {
        modelAndView.setViewName("transacoes/form");
        Optional<Transacao> transacaoOpt = transacaoRepository.findById(id);

        if (transacaoOpt.isPresent()) {
            Transacao transacao = transacaoOpt.get();
            // Garante que o objeto Comentario não seja nulo no formulário para evitar NPE no th:field="*{comentario.texto}"
            if (transacao.getComentario() == null) {
                transacao.setComentario(new Comentario());
            }
            modelAndView.addObject("transacao", transacao);
            modelAndView.addObject("categoriaItems", categoriaRepository.findAll());
        } else {
            // Lidar com transação não encontrada
            modelAndView.setViewName("redirect:/contas/list");
            modelAndView.addObject("errorMessage", "Transação não encontrada para edição.");
        }
        return modelAndView;
    }

    // Método para deletar uma transação
    @GetMapping("/delete/{id}")
    public String deleteTransacao(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        Optional<Transacao> transacaoOpt = transacaoRepository.findById(id);
        if (transacaoOpt.isPresent()) {
            Integer contaId = transacaoOpt.get().getConta().getId(); // Pega o ID da conta antes de deletar a transação
            transacaoRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Transação excluída com sucesso!");
            return "redirect:/contas/" + contaId + "/extrato"; // Redireciona de volta para o extrato da conta
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Transação não encontrada para exclusão.");
            return "redirect:/contas/list"; // Redireciona para lista de contas se a transação não for encontrada
        }
    }
}