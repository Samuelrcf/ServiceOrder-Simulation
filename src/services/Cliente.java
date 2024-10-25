package services;

import java.util.HashMap;

import entities.DadosCompressao;
import entities.No;
import entities.OrdemDeServico;

public class Cliente {
	
	private DataBase dataBase;
	
	public Cliente(DataBase dataBase) {
		this.dataBase = dataBase;
	}

	private DadosCompressao comprimirMensagem(String mensagem) {

		// conta a frequência de cada caractere na entrada
		HashMap<Character, Integer> frequencyMap = Huffman.countFrequency(mensagem);
		
		PriorityList minHeap = Huffman.populatePriorityList(frequencyMap);

		// constrói a árvore de Huffman
		No root = Huffman.buildHuffmanTree(minHeap);

		// constrói a tabela de códigos de Huffman
		HashMap<Character, String> huffmanCodes = Huffman.initBuildHuffmanCodes(root);

		// concatena os códigos binários
		String encodedString = Huffman.encode(mensagem, huffmanCodes);
		
		DadosCompressao ac = new DadosCompressao(root, encodedString);
		
		return ac;
	}
	
	public void doOperacao(OrdemDeServico os) {
		DadosCompressao dc = comprimirMensagem(os.toStringWithOperacao());
		dataBase.enviarMensagem(dc.getRaiz(), dc.getEncodedString());
	}
	
	public void listarOS() {
		DadosCompressao ac = dataBase.imprimirBaseDeDados(); 
		String decodedString = Huffman.decode(ac.getEncodedString(), ac.getRaiz());
		System.out.println(decodedString);
	}
	
	public void acessarQuantidadeRegistros() {
		System.out.println("\nQuantidade de ordens de serviço atual: " + dataBase.acessarQuantidadeRegistros()); 
	}
	
	public void acessarQuantidadeOcorrencias(String padrao) {
		DadosCompressao dc = comprimirMensagem(padrao);
		dataBase.processarString(dc.getEncodedString(), dc.getRaiz());
	}
}
