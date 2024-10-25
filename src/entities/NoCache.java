package entities;

public class NoCache {
    OrdemDeServico ordemDeServico;
    NoCache proximo;

    public NoCache (OrdemDeServico ordemDeServico) {
        this.ordemDeServico = ordemDeServico;
        this.proximo = null;
    }

	public OrdemDeServico getOrdemDeServico() {
		return ordemDeServico;
	}

	public void setOrdemDeServico(OrdemDeServico ordemDeServico) {
		this.ordemDeServico = ordemDeServico;
	}

	public NoCache  getProximo() {
		return proximo;
	}

	public void setProximo(NoCache  proximo) {
		this.proximo = proximo;
	}
}
