package br.edu.ifpb.pweb2.finangest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpb.pweb2.finangest.Util.PasswordUtil;
import br.edu.ifpb.pweb2.finangest.model.Categoria;
import br.edu.ifpb.pweb2.finangest.model.Conta;
import br.edu.ifpb.pweb2.finangest.model.Correntista;
import br.edu.ifpb.pweb2.finangest.repository.CategoriaRepository;
import br.edu.ifpb.pweb2.finangest.repository.ContaRepository;
import br.edu.ifpb.pweb2.finangest.repository.CorrentistaRepository;
import jakarta.validation.Valid;


@Controller
@RequestMapping("/correntistas")
public class CorrentistaController {

    @Autowired
    private CorrentistaRepository correntistaRepository;
    @Autowired
    private ContaRepository contaRepository;
    @Autowired
    private CategoriaRepository categoriaRepo;

    @RequestMapping("/form")
    public String getForm(Correntista correntista, Model model) {
        model.addAttribute("correntista", correntista);
        return "correntistas/form";
    }

    @RequestMapping("/save")
    public String save(@Valid Correntista correntista, BindingResult result,Model model, RedirectAttributes attr) {
        if(result.hasErrors()){
            return "correntistas/form";
        }
        String senhaHash = PasswordUtil.hashPassword(correntista.getSenha());
        correntista.setSenha(senhaHash);
        
        correntistaRepository.save(correntista);
        attr.addFlashAttribute("mensagem","Correntista inserido com sucesso!"); 
        return "redirect:/login/form";
    }

    @RequestMapping("/list")
    public String listAll(Model model) {
        model.addAttribute("correntistas", correntistaRepository.findAll());

        // --- Adicionado: Lógica para obter e passar o ID do admin para o modelo ---
        // Em um ambiente de produção, esta lógica seria substituída por Spring Security.
        // Por agora, estamos simulando um administrador com ID 1.
        Correntista adminLogado = correntistaRepository.findById(1).orElse(null); // Assumindo ID 1 como admin para teste
        if (adminLogado != null && adminLogado.isAdmin()) {
            model.addAttribute("adminId", adminLogado.getId());
            model.addAttribute("isAdminLoggedIn", true); // Flag para renderização condicional no Thymeleaf
        } else {
            model.addAttribute("isAdminLoggedIn", false);
        }
        // --- Fim da adição ---

        return "correntistas/list";
    }

    @RequestMapping("/{id}")
    public String getCorrentistaById(@PathVariable(value = "id") Integer id, Model model) {
        model.addAttribute("correntista", correntistaRepository.findById(id));
        return "correntistas/form";
    }
    @RequestMapping("/{id}/conta/form")
    public String getContaForm(@PathVariable("id") Integer correntistaId, Model model) {
        Conta conta = new Conta();
        conta.setCorrentista(correntistaRepository.findById(correntistaId).orElse(null));
        model.addAttribute("conta", conta);
        return "contas/form";
    }

    @RequestMapping("/{id}/conta/save")
    public String salvarConta(@PathVariable("id") Integer correntistaId, Conta conta, RedirectAttributes attr) {
        Correntista correntista = correntistaRepository.findById(correntistaId).orElse(null);
        if (correntista == null) {
            attr.addFlashAttribute("mensagem", "Correntista não encontrado.");
            return "redirect:/correntistas/list";
        }

        conta.setCorrentista(correntista);
        contaRepository.save(conta);

        attr.addFlashAttribute("mensagem", "Conta criada com sucesso.");
        return "redirect:/correntistas/" + correntistaId;
    }
    @RequestMapping("/{id}/categoria/form")
    public String getCategoriaForm(@PathVariable("id") Integer correntistaId, Model model, RedirectAttributes attr) {
        Correntista correntista = correntistaRepository.findById(correntistaId).orElse(null);
        if (correntista == null || !correntista.isAdmin()) {
            attr.addFlashAttribute("mensagem", "Acesso negado. Apenas administradores podem criar categorias.");
            return "redirect:/correntistas/list";
        }

        model.addAttribute("categoria", new Categoria());
        model.addAttribute("correntistaId", correntistaId);
        return "categorias/form";
    }

    @RequestMapping("/{id}/categoria/save")
    public String salvarCategoria(@PathVariable("id") Integer correntistaId, Categoria categoria, RedirectAttributes attr) {
        Correntista correntista = correntistaRepository.findById(correntistaId).orElse(null);
        if (correntista == null || !correntista.isAdmin()) {
            attr.addFlashAttribute("mensagem", "Acesso negado. Apenas administradores podem salvar categorias.");
            return "redirect:/correntistas/list";
        }

        categoriaRepo.save(categoria);
        attr.addFlashAttribute("mensagem", "Categoria salva com sucesso.");
        return "redirect:/correntistas/list";
    }
    @RequestMapping("/{id}/categoria/edit/{categoriaId}")
    public String editarCategoria(@PathVariable("id") Integer correntistaId, @PathVariable("categoriaId") Long categoriaId,
                                  Model model, RedirectAttributes attr) {
        Correntista correntista = correntistaRepository.findById(correntistaId).orElse(null);
        if (correntista == null || !correntista.isAdmin()) {
            attr.addFlashAttribute("mensagem", "Acesso negado.");
            return "redirect:/correntistas/list";
        }

        Categoria categoria = categoriaRepo.findById(categoriaId).orElse(null);
        model.addAttribute("categoria", categoria);
        model.addAttribute("correntistaId", correntistaId);
        return "categorias/form";
    }
    @RequestMapping("/{id}/categoria/desativar/{categoriaId}")
    public String desativarCategoria(@PathVariable("id") Integer correntistaId,
                                     @PathVariable("categoriaId") Long categoriaId,
                                     RedirectAttributes attr) {
        Correntista correntista = correntistaRepository.findById(correntistaId).orElse(null);
        if (correntista == null || !correntista.isAdmin()) {
            attr.addFlashAttribute("mensagem", "Acesso negado.");
            return "redirect:/correntistas/list";
        }

        Categoria categoria = categoriaRepo.findById(categoriaId).orElse(null);
        if (categoria != null) {
            categoria.setAtiva(false);
            categoriaRepo.save(categoria);
            attr.addFlashAttribute("mensagem", "Categoria desativada com sucesso.");
        } else { // Adicionado: Mensagem se categoria não for encontrada
            attr.addFlashAttribute("mensagem", "Categoria não encontrada.");
        }

        return "redirect:/correntistas/" + correntistaId + "/categorias";
    }

    @RequestMapping("/{id}/admin/conta/form")
    public String getAdminContaForm(@PathVariable("id") Integer correntistaId, Model model, RedirectAttributes attr) {
        Correntista admin = correntistaRepository.findById(correntistaId).orElse(null);

        if (admin == null || !admin.isAdmin()) {
            attr.addFlashAttribute("mensagem", "Acesso negado.");
            return "redirect:/correntistas/list";
        }

        model.addAttribute("conta", new Conta());
        model.addAttribute("correntistas", correntistaRepository.findAll()); // escolher para quem é a conta
        return "contas/form";
    }

    @RequestMapping("/{id}/admin/conta/save")
    public String salvarAdminConta(@PathVariable("id") Integer correntistaId, Conta conta, RedirectAttributes attr) {
        Correntista admin = correntistaRepository.findById(correntistaId).orElse(null);

        if (admin == null || !admin.isAdmin()) {
            attr.addFlashAttribute("mensagem", "Acesso negado.");
            return "redirect:/correntistas/list";
        }

        contaRepository.save(conta);
        attr.addFlashAttribute("mensagem", "Conta criada com sucesso.");
        return "redirect:/correntistas/list";
    }
    @RequestMapping("/{id}/admin/conta/edit/{contaId}")
    public String editarContaAdmin(@PathVariable("id") Integer correntistaId,
                                     @PathVariable("contaId") Integer contaId,
                                     Model model, RedirectAttributes attr) {
        Correntista admin = correntistaRepository.findById(correntistaId).orElse(null);

        if (admin == null || !admin.isAdmin()) {
            attr.addFlashAttribute("mensagem", "Acesso negado.");
            return "redirect:/correntistas/list";
        }

        Conta conta = contaRepository.findById(contaId).orElse(null);
        if (conta != null) {
            model.addAttribute("conta", conta); // Adiciona a conta encontrada ao modelo
            model.addAttribute("correntistas", correntistaRepository.findAll()); // Para selecionar o correntista no form de edição, se for o caso
            return "contas/form";
        } else {
            attr.addFlashAttribute("mensagem", "Conta não encontrada para edição.");
            return "redirect:/correntistas/list";
        }
    }
    @RequestMapping("/{id}/admin/conta/delete/{contaId}")
    public String deletarContaAdmin(@PathVariable("id") Integer correntistaId,
                                     @PathVariable("contaId") Integer contaId,
                                     RedirectAttributes attr) {
        Correntista admin = correntistaRepository.findById(correntistaId).orElse(null);

        if (admin == null || !admin.isAdmin()) {
            attr.addFlashAttribute("mensagem", "Acesso negado.");
            return "redirect:/correntistas/list";
        }

        Conta conta = contaRepository.findById(contaId).orElse(null);
        if (conta != null) {
            contaRepository.delete(conta);
            attr.addFlashAttribute("mensagem", "Conta e transações removidas.");
        } else {
            attr.addFlashAttribute("mensagem", "Conta não encontrada.");
        }

        return "redirect:/correntistas/list";
    }

    @RequestMapping("/{id}/admin/correntista/form")
    public String getCorrentistaAdminForm(@PathVariable("id") Integer adminId, Model model, RedirectAttributes attr) {
        Correntista admin = correntistaRepository.findById(adminId).orElse(null);
        if (admin == null || !admin.isAdmin()) {
            attr.addFlashAttribute("mensagem", "Acesso negado.");
            return "redirect:/correntistas/list";
        }

        model.addAttribute("correntista", new Correntista());
        model.addAttribute("adminId", adminId); // Passa o ID do admin para o formulário
        return "correntistas/form"; // Certifique-se de ter um `form.html`
    }

    // Método para salvar/atualizar correntistas via admin
    @RequestMapping("/{id}/admin/correntista/save")
    public String salvarCorrentistaAdmin(@PathVariable("id") Integer adminId, Correntista correntista, Model model, RedirectAttributes attr) {
        Correntista admin = correntistaRepository.findById(adminId).orElse(null);
        if (admin == null || !admin.isAdmin()) {
            attr.addFlashAttribute("mensagem", "Acesso negado.");
            return "redirect:/correntistas/list";
        }

        // Se a senha não foi preenchida, mantém a senha existente (para edição)
        if (correntista.getId() != null && (correntista.getSenha() == null || correntista.getSenha().trim().isEmpty())) {
            Correntista existingCorrentista = correntistaRepository.findById(correntista.getId()).orElse(null);
            if (existingCorrentista != null) {
                correntista.setSenha(existingCorrentista.getSenha());
            }
        } else if (correntista.getSenha() != null && !correntista.getSenha().trim().isEmpty()) {
            // Se a senha foi preenchida, faz o hash
            String senhaHash = PasswordUtil.hashPassword(correntista.getSenha());
            correntista.setSenha(senhaHash);
        } else {
            // Caso de criação e senha vazia (deve ser validado antes)
            model.addAttribute("mensagem", "A senha é obrigatória para novos correntistas.");
            model.addAttribute("correntista", correntista);
            model.addAttribute("adminId", adminId);
            return "correntistas/form";
        }

        // Validações de nome e email (repetidas aqui ou movidas para um método de serviço)
        if (correntista.getNome() == null || correntista.getNome().trim().isEmpty()) {
            model.addAttribute("mensagem", "O nome do correntista é obrigatório.");
            model.addAttribute("correntista", correntista);
            model.addAttribute("adminId", adminId);
            return "correntistas/form";
        }
        if (correntista.getNome().length() > 50) {
            model.addAttribute("mensagem", "O nome do correntista deve ter no máximo 50 caracteres.");
            model.addAttribute("correntista", correntista);
            model.addAttribute("adminId", adminId);
            return "correntistas/form";
        }
        if (correntista.getEmail() == null || correntista.getEmail().trim().isEmpty()) {
            model.addAttribute("mensagem", "O email é obrigatório.");
            model.addAttribute("correntista", correntista);
            model.addAttribute("adminId", adminId);
            return "correntistas/form";
        }

        correntistaRepository.save(correntista);
        attr.addFlashAttribute("mensagem", "Correntista salvo com sucesso.");
        return "redirect:/correntistas/list";
    }

    @RequestMapping("/edit/{id}") // Este é o mapeamento para um correntista comum editar a si mesmo
    public String editarCorrentista(@PathVariable("id") Integer id, Model model, RedirectAttributes attr) {
        Correntista correntista = correntistaRepository.findById(id).orElse(null);

        if (correntista == null) {
            attr.addFlashAttribute("mensagem", "Correntista não encontrado para edição.");
            return "redirect:/correntistas/list";
        }

        model.addAttribute("correntista", correntista);
        return "correntistas/form";
    }

    // Novo mapeamento para edição de correntista por admin
    @RequestMapping("/{adminId}/admin/correntista/edit/{correntistaId}")
    public String editarCorrentistaAdmin(@PathVariable("adminId") Integer adminId,
                                         @PathVariable("correntistaId") Integer correntistaId,
                                         Model model, RedirectAttributes attr) {
        Correntista admin = correntistaRepository.findById(adminId).orElse(null);
        if (admin == null || !admin.isAdmin()) {
            attr.addFlashAttribute("mensagem", "Acesso negado.");
            return "redirect:/correntistas/list";
        }

        Correntista correntista = correntistaRepository.findById(correntistaId).orElse(null);
        if (correntista == null) {
            attr.addFlashAttribute("mensagem", "Correntista não encontrado para edição.");
            return "redirect:/correntistas/list";
        }
        model.addAttribute("correntista", correntista);
        model.addAttribute("adminId", adminId); // Passa o ID do admin para o formulário
        return "correntistas/form"; // Redireciona para o formulário específico de admin
    }


    @RequestMapping("/{id}/admin/correntista/delete/{deleteId}")
    public String excluirCorrentistaAdmin(@PathVariable("id") Integer adminId,
                                          @PathVariable("deleteId") Integer deleteId,
                                          RedirectAttributes attr) {
        Correntista admin = correntistaRepository.findById(adminId).orElse(null);
        if (admin == null || !admin.isAdmin()) {
            attr.addFlashAttribute("mensagem", "Acesso negado.");
            return "redirect:/correntistas/list";
        }

        Correntista correntista = correntistaRepository.findById(deleteId).orElse(null);
        if (correntista != null) {
            correntistaRepository.delete(correntista); // Apaga as contas e transações em cascata (se configurado no JPA)
            attr.addFlashAttribute("mensagem", "Correntista e suas contas foram removidos.");
        } else {
            attr.addFlashAttribute("mensagem", "Correntista não encontrado.");
        }

        return "redirect:/correntistas/list";
    }

    @RequestMapping("/{adminId}/admin/correntista/bloquear/{targetId}")
    public String bloquearCorrentista(@PathVariable("adminId") Integer adminId,
                                      @PathVariable("targetId") Integer targetId,
                                      RedirectAttributes attr) {
        Correntista admin = correntistaRepository.findById(adminId).orElse(null);
        if (admin == null || !admin.isAdmin()) {
            attr.addFlashAttribute("mensagem", "Acesso negado.");
            return "redirect:/correntistas/list";
        }
        Correntista alvo = correntistaRepository.findById(targetId).orElse(null);
        if (alvo != null) {
            alvo.setAtivo(false); // Define o correntista como inativo
            correntistaRepository.save(alvo);
            attr.addFlashAttribute("mensagem", "Correntista bloqueado com sucesso.");
        } else {
            attr.addFlashAttribute("mensagem", "Correntista não encontrado.");
        }
        return "redirect:/correntistas/list";
    }

    // Adicionado: Método para ativar um correntista
    @RequestMapping("/{adminId}/admin/correntista/ativar/{targetId}")
    public String ativarCorrentista(@PathVariable("adminId") Integer adminId,
                                    @PathVariable("targetId") Integer targetId,
                                    RedirectAttributes attr) {
        Correntista admin = correntistaRepository.findById(adminId).orElse(null);
        if (admin == null || !admin.isAdmin()) {
            attr.addFlashAttribute("mensagem", "Acesso negado.");
            return "redirect:/correntistas/list";
        }
        Correntista alvo = correntistaRepository.findById(targetId).orElse(null);
        if (alvo != null) {
            alvo.setAtivo(true); // Define o correntista como ativo
            correntistaRepository.save(alvo);
            attr.addFlashAttribute("mensagem", "Correntista ativado com sucesso.");
        } else {
            attr.addFlashAttribute("mensagem", "Correntista não encontrado.");
        }
        return "redirect:/correntistas/list";
    }
}