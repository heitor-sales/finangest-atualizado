package br.edu.ifpb.pweb2.finangest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpb.pweb2.finangest.Util.PasswordUtil;
import br.edu.ifpb.pweb2.finangest.model.Correntista;
import br.edu.ifpb.pweb2.finangest.repository.CorrentistaRepository;
import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired CorrentistaRepository correntistaRepository;

    @GetMapping("/form")
    public ModelAndView showLoginForm(ModelAndView model) {
        model.setViewName("form-login");
        model.addObject("usuario", new Correntista());
        return model;
    }
    
    @PostMapping
    public ModelAndView valideLogin(Correntista c, HttpSession session, ModelAndView model, RedirectAttributes ratt) {
        if((c = this.isValido(c)) != null){
            session.setAttribute("usuario", c);
            model.setViewName("index");
            return model;
        } else {
            ratt.addFlashAttribute("mensagem", "Login e/ou senha inválidos!");
            model.setViewName("form-login");
            return model;
        }
      
    }

    @GetMapping("/logout")
    public ModelAndView logout(ModelAndView model, HttpSession session){
        session.invalidate();
        model.setViewName("form-login");
        return model;
    }

    private Correntista isValido(Correntista c){
        Correntista correntistaBD = correntistaRepository.findByEmail(c.getEmail());
        boolean valido = false;
        if (correntistaBD != null){
            if (PasswordUtil.checkPass(c.getSenha(), correntistaBD.getSenha())){
                valido = true;
            }
        }
        return valido ? correntistaBD : null;
    }
    
}
