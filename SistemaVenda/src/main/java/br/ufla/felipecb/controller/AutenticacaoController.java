package br.ufla.felipecb.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import br.ufla.felipecb.bean.FuncionarioBean;
import br.ufla.felipecb.entidades.Funcionario;
import br.ufla.felipecb.vo.AutenticacaoVO;
import br.ufla.util.Util;

@Controller
@RequestMapping("/autenticacao")
public class AutenticacaoController {

	@Autowired
	FuncionarioBean funcionarioBean;
	
	@RequestMapping({ "", "/", "/index" })
    public String inicio(Model model) {
		model.addAttribute("autenticacaoVO", new AutenticacaoVO());
    	return "login";
    }
	
	@RequestMapping(value = "/conectar", method = RequestMethod.POST)
	 public String conectar(HttpServletRequest request, @ModelAttribute AutenticacaoVO autenticacao, Model model) {
		
		Funcionario funcionario = funcionarioBean.logar(autenticacao.getLogin(), Util.get_SHA_256(autenticacao.getSenha()));
		
		if(funcionario == null) {
			model.addAttribute("mensagem", "Login ou senha inválido");
			return "login";
		}
		
		model.addAttribute("mensagem", "Funcionario "+ funcionario.getNome()+ " autenticado");
		
		request.getSession().setAttribute("funcionario", funcionario);
		
		return "index";
    		
	}
	
	@PostMapping("/desconectar")
    public String desconectar(HttpServletRequest request, Model model) {
    	
		request.getSession().removeAttribute("funcionario");
		
		
		return "login";
    }
	
}
