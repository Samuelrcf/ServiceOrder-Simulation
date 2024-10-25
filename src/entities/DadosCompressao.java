package entities;

public class DadosCompressao {

	No raiz;
	String encodedString;

	public DadosCompressao(No raiz, String encodedString) {
		this.raiz = raiz;
		this.encodedString = encodedString;
	}

	public No getRaiz() {
		return raiz;
	}

	public void setRaiz(No raiz) {
		this.raiz = raiz;
	}

	public String getEncodedString() {
		return encodedString;
	}

	public void setEncodedString(String encodedString) {
		this.encodedString = encodedString;
	}

}
