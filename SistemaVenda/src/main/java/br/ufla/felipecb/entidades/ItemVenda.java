package br.ufla.felipecb.entidades;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="item_venda")
public class ItemVenda {
	
	@Id
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "sequence_id_item_venda"
		)
		@SequenceGenerator(
			name =  "sequence_id_item_venda",
			sequenceName = "sequence_item_venda"
		)
	private Long id;
	
	@Column(name="valor_unidade")
	private float valorUnidade;

	@Column(name="quantidade")
	private int quantidade;
	
	@ManyToOne
	@JoinColumn(name="id_produto")
	private Produto produto;
	
	@ManyToOne
	@JoinColumn(name="id_venda")
	private Venda venda;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public float getValorUnidade() {
		return valorUnidade;
	}
	public void setValorUnidade(float valorUnidade) {
		this.valorUnidade = valorUnidade;
	}

	public int getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}

	public Venda getVenda() {
		return venda;
	}
	public void setVenda(Venda venda) {
		this.venda = venda;
	}
	
	public Produto getProduto() {
		return produto;
	}
	public void setProduto(Produto produto) {
		this.produto = produto;
	}
	
	
}
