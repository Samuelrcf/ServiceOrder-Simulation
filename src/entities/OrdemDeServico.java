package entities;

import java.io.Serializable;
import java.time.LocalDateTime;

public class OrdemDeServico implements Serializable {
	private static final long serialVersionUID = 1L;

	private Operacao operacao;
    private int codigo;          
    private String nome;        
    private String descricao;    
    private LocalDateTime horaSolicitacao; 
    private int contadorFrequencia;
    
    public OrdemDeServico() {
    }

    public OrdemDeServico(Operacao operacao, int codigo, String nome, String descricao, LocalDateTime horaSolicitacao) {
    	this.operacao = operacao;
		this.codigo = codigo;
		this.nome = nome;
		this.descricao = descricao;
		this.horaSolicitacao = horaSolicitacao;
		this.contadorFrequencia = 0;
	}
    
    public OrdemDeServico(Operacao operacao, int codigo) {
    	this.operacao = operacao;
    	this.codigo = codigo;
    }
    
    public OrdemDeServico(int codigo, String nome, String descricao, LocalDateTime horaSolicitacao) {
    	this.codigo = codigo;
    	this.nome = nome;
    	this.descricao = descricao;
    	this.horaSolicitacao = horaSolicitacao;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getHoraSolicitacao() {
        return horaSolicitacao;
    }

    public void setHoraSolicitacao(LocalDateTime horaSolicitacao) {
        this.horaSolicitacao = horaSolicitacao;
    }
    
	public Operacao getOperacao() {
		return operacao;
	}

	public void setOperacao(Operacao operacao) {
		this.operacao = operacao;
	}
	
	public int getContadorFrequencia() {
		return contadorFrequencia;
	}

	public void setContadorFrequencia(int contadorFrequencia) {
		this.contadorFrequencia = contadorFrequencia;
	}

	public String toStringWithOperacao() {
	    return "Ordem De Serviço [operacao=" + operacao + ", codigo=" + codigo + ", nome=" + nome + 
	           ", descricao=" + descricao + ", horaSolicitacao=" + horaSolicitacao + 
	           ", contadorFrequencia=" + contadorFrequencia + "]";
	}

	public String toString() {
	    return "Ordem De Serviço [codigo=" + codigo + ", nome=" + nome + ", descricao=" + descricao + 
	           ", horaSolicitacao=" + horaSolicitacao + ", contadorFrequencia=" + contadorFrequencia + "]";
	}

}
