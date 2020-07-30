package br.ufla.felipecb.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import br.ufla.felipecb.bean.ClienteBean;
import br.ufla.felipecb.bean.ProdutoBean;
import br.ufla.felipecb.bean.VendaBean;
import br.ufla.felipecb.entidades.Cliente;
import br.ufla.felipecb.entidades.Funcionario;
import br.ufla.felipecb.entidades.ItemVenda;
import br.ufla.felipecb.entidades.Produto;
import br.ufla.felipecb.entidades.Venda;

@Controller
@SessionScope
@RequestMapping("/venda")
public class VendaController {

	@Autowired
	private VendaBean vendaBean;
	
	@Autowired
	private ClienteBean clienteBean;
	
	@Autowired
	private ProdutoBean produtoBean;
	
	//Mantem na seção
	Venda venda;
	List<Produto> produtos;
	
	@RequestMapping("/criar")
    public String criar(HttpServletRequest request, Model model) {
		
		Funcionario func = (Funcionario) request.getSession().getAttribute("funcionario");
		if(func == null) {
			return "redirect:/autenticacao";
		}
		
		venda = new Venda();
		venda.setFuncionario(func);
		
    	model.addAttribute("venda", venda);
    	model.addAttribute("cliente", new Cliente());

    	return "cadastroVenda";
    }
	
	@PostMapping(value = "/validarCliente")
	public String validarCliente(@ModelAttribute("venda.cliente") Cliente cli, 
			ModelMap model, final RedirectAttributes redirectAttrs) {
		
		Cliente cliente = clienteBean.buscarPorCPF(cli.getCpf());

		if(cliente == null) {
			model.addAttribute("venda", venda);
			model.addAttribute("cliente", cli);
			model.addAttribute("erro", "Cliente não cadastrado");
	    	return "cadastrovenda";
		} else {
			RedirectView rv = new RedirectView();
			rv.setUrl("/venda/carregarVenda");
			
			redirectAttrs.addFlashAttribute("cliente", cliente);
			
			return "redirect:/venda/carregarVenda";
		}
		
		
	}
	
	
	@RequestMapping(value = "/carregarVenda", method = { RequestMethod.POST, RequestMethod.GET })
    public String carregarVenda(ModelMap model) {
		
		if(model.getAttribute("cliente") != null) {
			venda.setCliente((Cliente)model.getAttribute("cliente"));
			model.remove("cliente");
		}
		
		venda.setListaItens(new ArrayList<ItemVenda>());
		produtos = (List<Produto>) produtoBean.buscarProdutos();
    	
		model.addAttribute("venda", venda);	
    	model.addAttribute("produtos", produtos);

    	ItemVenda iv = new ItemVenda();
    	iv.setProduto(new Produto());
    	model.addAttribute("item", iv);
			
		model.addAttribute("venda", venda);
		
		return "cadastroVenda";
	}
	
	@PostMapping("/adicionarItem")
    public String adicionarItem(@ModelAttribute ItemVenda item, Model model) {
		
		if(item.getProduto().getId() == null) {
			model.addAttribute("erro", "Selecione o produto desejado");
			model.addAttribute("item", item);
		
		} else if(item.getQuantidade() > 0) {
			//Preenche o produto no objeto
			for(Produto prod : produtos) {
				if(prod.getId().equals(item.getProduto().getId())) {
					item.setProduto(prod);
					//armazerna o valor do produto na época comprada
					item.setValorUnidade(prod.getValor());
					item.setVenda(venda);
					break;
				}
			}
			
			//Verifica duplicadas
			boolean jaAdicionado = false;
			for(ItemVenda iv: venda.getListaItens()) {
				if(iv.getProduto().getId().equals(item.getProduto().getId())) {
					jaAdicionado = true;
					break;
				}
			}
			//Caso já tenha sido adicionado na sacola lança erro
			if(jaAdicionado) {
				model.addAttribute("erro", "Este produto já está na sacola");	
				model.addAttribute("item", item);
			} else {
				venda.getListaItens().add(item);
				ItemVenda iv = new ItemVenda();
		    	iv.setProduto(new Produto());
		    	model.addAttribute("item", iv);
			}
		} else {
			model.addAttribute("erro", "A quantidade deve ser maior que 0");	
			model.addAttribute("item", item);
		}
		
		model.addAttribute("venda", venda);	
    	model.addAttribute("produtos", produtos);
		
		return "cadastroVenda";
	}
	
	@RequestMapping("/removerItem/{idProduto}")
    public String removerItem(@PathVariable("idProduto") Long idProduto, Model model) {
		
		for(ItemVenda iv : venda.getListaItens()) {
			if(idProduto.equals(iv.getProduto().getId())) {
				venda.getListaItens().remove(iv);
				break;
			}
		}
		
		ItemVenda iv = new ItemVenda();
    	iv.setProduto(new Produto());
    	model.addAttribute("item", iv);
		
		model.addAttribute("venda", venda);	
    	model.addAttribute("produtos", produtos);
    	
    	return "cadastroVenda";
	}
	
	@PostMapping("/salvar")
    public String salvar(Model model) {
		
		if(venda.getListaItens().isEmpty()) {
			model.addAttribute("venda", venda);	
	    	model.addAttribute("produtos", produtos);
	    	ItemVenda iv = new ItemVenda();
	    	iv.setProduto(new Produto());
	    	model.addAttribute("item", iv);
	    	
			model.addAttribute("erro", "É necessário adicionar ao menos um item");
	    	
			return "cadastroVenda";
		}
		
		for(ItemVenda iv : venda.getListaItens()) {
			produtoBean.atualizarEstoque(iv.getProduto().getId(), iv.getQuantidade());
		}
		
		venda = vendaBean.salvar(venda);
		model.addAttribute("mensagem", "Venda realizada");
		
		limparSessao();
		
		return "index";
	}
	
	@RequestMapping(value = "/listar", method = RequestMethod.GET)
	public String listar(Model model) {
    	
    	model.addAttribute("vendas", vendaBean.buscarVendas());
		
		return "vendas";
	}
	
	@RequestMapping("/consultar/{id}")
    public String editar(@PathVariable("id") Long id, Model model) {
    	
		Venda venda = vendaBean.buscarVenda(id);
    	model.addAttribute("venda", venda);

    	return "cadastroVenda";
    }

	@RequestMapping("/excluir/{id}")
    public String excluir(@PathVariable("id") Long id, Model model) {
    	
		vendaBean.excluir(id);

    	return "redirect:/venda/listar";
    }
	
	private void limparSessao() {
		venda = null;
		produtos = null;
	}
	
}
