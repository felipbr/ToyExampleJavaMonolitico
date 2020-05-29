package br.ufla.felipecb.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.annotation.SessionScope;

import br.ufla.felipecb.bean.FuncionarioBean;
import br.ufla.felipecb.entidades.Funcionario;
import br.ufla.felipecb.vo.AutenticacaoVO;
import br.ufla.util.Util;

@SessionScope
@Controller
@RequestMapping("/autenticacao")
public class AutenticacaoController {

	private Funcionario funcionario;
	
	@Autowired
	FuncionarioBean funcionarioBean;
	
	@RequestMapping({ "", "/", "/index" })
    public String inicio(Model model) {
		model.addAttribute("autenticacaoVO", new AutenticacaoVO());
    	return "login";
    }
	
	@RequestMapping(value = "/conectar", method = RequestMethod.POST)
	 public String conectar(@ModelAttribute AutenticacaoVO autenticacao, Model model) {
		
		funcionario = funcionarioBean.logar(autenticacao.getLogin(), Util.get_SHA_256(autenticacao.getSenha()));
		
		if(funcionario == null) {
//			model.addAttribute("au", senha);
			model.addAttribute("mensagem", "Login ou senha inválido");
			return "login";
		}
		
		model.addAttribute("mensagem", "Funcionario "+ funcionario.getNome()+ " autenticado");
		
		return "index";
    		
	}
	
	@PostMapping("/desconectar")
    public String desconectar(@ModelAttribute Funcionario funcionario, Model model) {
    	
		funcionario = null;
		
		return "login";
    }
	
	@ResponseBody
	@RequestMapping("/autenticarFunc/{login}/{senha}")
	public Funcionario autenticarFunc(@PathVariable("login") String login, @PathVariable("senha") String senha) {
		Funcionario funcionario = funcionarioBean.logar(login, senha);
		
		if(funcionario != null) {
			return funcionario;
		}
		return null;
	}
	
	@ResponseBody
	@RequestMapping(value = "/autenticar", method = RequestMethod.POST)
	public Funcionario autenticarFuncionario(@RequestBody Map<String, String> json) {
		Funcionario funcionario = funcionarioBean.logar(json.get("login"), json.get("senha"));
		
		if(funcionario != null) {
			return funcionario;
		}
		return null;
	}
	
	@ResponseBody
	@RequestMapping(value = "/verificarAutenticacao", method = RequestMethod.POST)
	public Funcionario verificarAutenticacao() {
		if(funcionario != null) {
			return funcionario;
		}
		return null;
	}
	
	
}
