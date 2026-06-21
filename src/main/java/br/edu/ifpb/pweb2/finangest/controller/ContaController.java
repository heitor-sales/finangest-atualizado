package br.edu.ifpb.pweb2.finangest.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javax.naming.Binding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpb.pweb2.finangest.model.Conta;
import br.edu.ifpb.pweb2.finangest.model.Correntista;
import br.edu.ifpb.pweb2.finangest.model.TipoMovimento;
import br.edu.ifpb.pweb2.finangest.model.Transacao;
import br.edu.ifpb.pweb2.finangest.repository.CorrentistaRepository;
import br.edu.ifpb.pweb2.finangest.repository.TransacaoRepository;
import br.edu.ifpb.pweb2.finangest.service.ContaService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid; 

@Controller
@RequestMapping("/contas")
public class ContaController {

    @Autowired
    private ContaService contaService;

    @Autowired
    private CorrentistaRepository correntistaRepository; 

    @Autowired
    private TransacaoRepository transacaoRepository;

    // Modificado para usar Model e HttpSession para verificar o admin
    @GetMapping("/form")
public String getForm(Model model, HttpSession session) {
    model.addAttribute("conta", new Conta());

    Correntista usuarioLogado = (Correntista) session.getAttribute("usuario");
    if (usuarioLogado != null && usuarioLogado.isAdmin()) {
        model.addAttribute("correntistaItems", correntistaRepository.findAll());
    }
    return "contas/form"; 
}

    // Este @ModelAttribute não é mais necessário aqui, pois a lista de correntistas
    // será adicionada condicionalmente no método getForm().
    // @ModelAttribute("correntistaItems")
    // public List<Correntista> getCorrentistas(){
    //     try {
    //         return correntistaRepository.findAll();
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return Collections.emptyList();
    //     }
    // }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String adicioneOuAtualizeConta(@Valid Conta conta,BindingResult result,HttpSession session,RedirectAttributes ra) {
        
        if(result.hasErrors()){
            return "contas/form";
            
        }


        Correntista usuarioLogado = (Correntista) session.getAttribute("usuario");

        if (usuarioLogado == null) {
            ra.addFlashAttribute("mensagem", "Sessão expirada ou usuário não logado. Por favor, faça login.");
            return "redirect:/login/form"; 
        }

        
        // 1. Se o usuário NÃO for admin
        // 2. OU se o usuário for admin mas NÃO SELECIONOU um correntista no formulário
        if (!usuarioLogado.isAdmin() || conta.getCorrentista() == null || conta.getCorrentista().getId() == null) {
            // Associa a conta ao correntista logado
            // busca o correntista completo do banco de dados para evitar
            // TransientObjectException ao salvar a Conta no JPA.
            Correntista correntistaCompleto = correntistaRepository.findById(usuarioLogado.getId())
                .orElseThrow(() -> new IllegalArgumentException("Correntista logado não encontrado!"));
            conta.setCorrentista(correntistaCompleto);
        } else {
            // Se for admin e um correntista foi selecionado no formulário,
            // associa a conta ao correntista selecionado.
            // Também busca o correntista completo para evitar TransientObjectException.
            Correntista correntistaSelecionado = correntistaRepository.findById(conta.getCorrentista().getId())
                .orElseThrow(() -> new IllegalArgumentException("Correntista selecionado não encontrado!"));
            conta.setCorrentista(correntistaSelecionado);
        }

        try {
            contaService.save(conta);
            ra.addFlashAttribute("mensagem", "Conta salva com sucesso!");
            return "redirect:/contas/list"; 
        } catch (Exception e) {
            ra.addFlashAttribute("mensagem", "Erro ao salvar conta: " + e.getMessage());
            return "redirect:/contas/form";
        }
    }

    @RequestMapping("/list")
    public String listContas(Model model, HttpSession session, RedirectAttributes ra) {
        Correntista usuarioLogado = (Correntista) session.getAttribute("usuario");

        if (usuarioLogado == null) {
            ra.addFlashAttribute("mensagem", "Sessão expirada ou usuário não logado. Por favor, faça login.");
            return "redirect:/login/form";
        }

        List<Conta> contas;
        if (usuarioLogado.isAdmin()) {
            contas = contaService.findall(); // Administrador vê todas as contas
        } else {
            // Usuário comum vê apenas suas próprias contas
            
            contas = contaService.findByCorrentista(usuarioLogado); 
        }

        model.addAttribute("contas", contas);
        return "contas/list";
    }

    @RequestMapping
    public String redirectToContasList() {
        return "redirect:/contas/list";
    }

    @GetMapping("/edit/{id}")
    public String getFormForEdit(@PathVariable(name = "id") Integer id, Model model, HttpSession session, RedirectAttributes ra) {
        Correntista usuarioLogado = (Correntista) session.getAttribute("usuario");

        if (usuarioLogado == null) {
            ra.addFlashAttribute("mensagem", "Sessão expirada ou usuário não logado. Por favor, faça login.");
            return "redirect:/login/form";
        }

        Conta conta = contaService.findByID(id);

        if (conta == null) {
            ra.addFlashAttribute("mensagem", "Conta não encontrada para edição.");
            return "redirect:/contas/list";
        }

        // Verifica se o usuário logado tem permissão para editar esta conta
        // Admins podem editar qualquer conta. Não-admins só podem editar suas próprias contas.
        if (!usuarioLogado.isAdmin() && !conta.getCorrentista().getId().equals(usuarioLogado.getId())) {
            ra.addFlashAttribute("mensagem", "Você não tem permissão para editar esta conta.");
            return "redirect:/contas/list"; 
        }
        
        model.addAttribute("conta", conta);

        if (usuarioLogado.isAdmin()) {
            model.addAttribute("correntistaItems", correntistaRepository.findAll());
        }
        return "contas/form"; 
    }


    @GetMapping("/delete/{id}")
    public String deleteConta(@PathVariable(name = "id") Integer id, HttpSession session, RedirectAttributes ra) {
        Correntista usuarioLogado = (Correntista) session.getAttribute("usuario");

        if (usuarioLogado == null) {
            ra.addFlashAttribute("mensagem", "Sessão expirada ou usuário não logado. Por favor, faça login.");
            return "redirect:/login/form";
        }

        Conta conta = contaService.findByID(id);

        if (conta == null) {
            ra.addFlashAttribute("mensagem", "Conta não encontrada para exclusão.");
            return "redirect:/contas/list";
        }

        // Verifica permissão para exclusão
        if (!usuarioLogado.isAdmin() && !conta.getCorrentista().getId().equals(usuarioLogado.getId())) {
            ra.addFlashAttribute("mensagem", "Você não tem permissão para excluir esta conta.");
            return "redirect:/contas/list";
        }

        contaService.deleteById(id);
        ra.addFlashAttribute("mensagem", "Conta excluída com sucesso!");
        return "redirect:/contas/list";
    }

    @GetMapping("/{id}/extrato")
    public String exibirExtrato(
            @PathVariable("id") Integer id,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            Model model,
            HttpSession session, 
            RedirectAttributes ra) { 

        Correntista usuarioLogado = (Correntista) session.getAttribute("usuario");
        if (usuarioLogado == null) {
            ra.addFlashAttribute("mensagem", "Sessão expirada ou usuário não logado. Por favor, faça login.");
            return "redirect:/login/form";
        }

        Optional<Conta> contaOpt = contaService.findById(id);

        if (contaOpt.isEmpty()) {
            ra.addFlashAttribute("mensagem", "Conta não encontrada.");
            return "redirect:/contas/list";
        }
        Conta conta = contaOpt.get();

        // Verificar se o usuário logado tem permissão para ver o extrato desta conta
        if (!usuarioLogado.isAdmin() && !conta.getCorrentista().getId().equals(usuarioLogado.getId())) {
            ra.addFlashAttribute("mensagem", "Você não tem permissão para visualizar o extrato desta conta.");
            return "redirect:/contas/list";
        }

        LocalDate dataInicialFiltro = null;
        LocalDate dataFinalFiltro = null;

        try {
            if (dataInicio != null && !dataInicio.trim().isEmpty()) {
                dataInicialFiltro = LocalDate.parse(dataInicio);
            }
            if (dataFim != null && !dataFim.trim().isEmpty()) {
                dataFinalFiltro = LocalDate.parse(dataFim);
            }
        } catch (Exception e) {
            ra.addFlashAttribute("mensagem", "Formato de data inválido. Use AAAA-MM-DD.");
            // Redireciona de volta para o extrato da mesma conta com a mensagem de erro
            return "redirect:/contas/" + id + "/extrato";
        }

        if (mes != null) {
            YearMonth yearMonth = YearMonth.of(LocalDate.now().getYear(), mes);
            dataInicialFiltro = yearMonth.atDay(1);
            dataFinalFiltro = yearMonth.atEndOfMonth();
        }

        BigDecimal saldoInicial = BigDecimal.ZERO;
        List<Transacao> transacoesAnteriores;

        if (dataInicialFiltro != null) {
            transacoesAnteriores = transacaoRepository.findByContaAndDataBefore(conta, dataInicialFiltro);
        } else {
            transacoesAnteriores = Collections.emptyList();
        }

        for (Transacao t : transacoesAnteriores) {
            if (t.getMovimento() == TipoMovimento.CREDITO) {
                saldoInicial = saldoInicial.add(t.getValor());
            } else {
                saldoInicial = saldoInicial.subtract(t.getValor());
            }
        }

        List<Transacao> transacoesPeriodo;
        if (dataInicialFiltro != null && dataFinalFiltro != null) {
            transacoesPeriodo = transacaoRepository.findByContaAndDataBetween(conta, dataInicialFiltro, dataFinalFiltro);
        } else {
            transacoesPeriodo = transacaoRepository.findByConta(conta);
        }

        transacoesPeriodo.sort(Comparator.comparing(Transacao::getData));

        BigDecimal saldoCorrente = saldoInicial;
        for (Transacao t : transacoesPeriodo) {
            if (t.getMovimento() == TipoMovimento.CREDITO) {
                saldoCorrente = saldoCorrente.add(t.getValor());
            } else {
                saldoCorrente = saldoCorrente.subtract(t.getValor());
            }
            t.setSaldoParcial(saldoCorrente);
        }

        BigDecimal saldoFinal = saldoCorrente;

        model.addAttribute("conta", conta);
        model.addAttribute("transacoes", transacoesPeriodo);
        model.addAttribute("saldoInicial", saldoInicial);
        model.addAttribute("saldoFinal", saldoFinal);
        model.addAttribute("mes", mes);
        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);

        return "contas/extrato";
    }
}