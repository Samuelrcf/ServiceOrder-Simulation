package services;
import java.util.HashMap;
import entities.No;

public class Huffman {

	 // conta a frequência de cada caractere na string de entrada e armazena no HashMap
    public static HashMap<Character, Integer> countFrequency(String input) {
        HashMap<Character, Integer> frequencyMap = new HashMap<>();

        for (char c : input.toCharArray()) {
            if (frequencyMap.containsKey(c)) {
                frequencyMap.put(c, frequencyMap.get(c) + 1);
            } else {
                frequencyMap.put(c, 1); 
            }
        }
        return frequencyMap;
    }
    
    // insere os nós no minHeap (PriorityList), baseados nas frequências dos caracteres
    public static PriorityList populatePriorityList(HashMap<Character, Integer> frequencyMap) {
	    PriorityList minHeap = new PriorityList();
	    for(char c: frequencyMap.keySet()) {
	    	minHeap.inserir(new No(c, frequencyMap.get(c)));
	    }
	    return minHeap;
    }

    // constrói a árvore de Huffman combinando os dois menores nós até restar só a raiz
	public static No buildHuffmanTree(PriorityList minHeap) {

	    // constrói a árvore de Huffman combinando os Nos com as menores frequências
	    while (minHeap.tamanho() > 1) {
	        // obtém os dois Nos com as menores frequências
	        No left = minHeap.remover();
	        No right = minHeap.remover();
	        // cria um novo No com a soma das frequências
	        No parent = new No('\0', left.getFrequency() + right.getFrequency());
	        parent.setLeft(left);
	        parent.setRight(right);

	        minHeap.inserir(parent);
	    }

	    // retorna a raiz da árvore de Huffman
	    return minHeap.remover();
	}

	public static HashMap<Character, String> initBuildHuffmanCodes(No raiz) {
		HashMap<Character, String> huffmanCodes = new HashMap<>(); // serve para armazenar o código de cada caractere como uma string
		buildHuffmanCodes(raiz, "", huffmanCodes); // inicializar a recursão
		return huffmanCodes;
	}

	// percorre a árvore e gera os códigos de Huffman para cada caractere
	private static void buildHuffmanCodes(No node, String code, HashMap<Character, String> huffmanCodes) {
		if (node == null) { 
			return;
		}
		
		// o huffmanCodes serve para armazenar o código cada vez que ele encontrar um terminal
		if (node.getLeft() == null && node.getRight() == null) { // verifica se é terminal
			huffmanCodes.put(node.getCharacter(), code); // se for terminal, cria a ligação do caractere com o código dele no hashmap
		} 

		//toda vez que mover para a esquerda, adiciona um 0 e chama recursivamente o método até achar um nó null na subárvore esquerda
		buildHuffmanCodes(node.getLeft(), code + "0", huffmanCodes); 
		//toda vez que mover para a direita, adiciona um 1 e chama recursivamente o método até achar um nó null na subárvore direita
		buildHuffmanCodes(node.getRight(), code + "1", huffmanCodes); 
	}

	// codifica a string de entrada usando os códigos de Huffman gerados
	public static String encode(String input, HashMap<Character, String> huffmanCodes) {
		StringBuilder encodedString = new StringBuilder();

		// obtém cada caractere do arquivo e codifica de acordo com a tabela de códigos de Huffman
		for (char c : input.toCharArray()) {
			encodedString.append(huffmanCodes.get(c)); // cocatena os codigos de cada caractere de acordo com a chave c
		}

		return encodedString.toString();
	}

	// decodifica a string codificada utilizando a árvore de Huffman
	public static String decode(String encodedString, No raiz) {
		StringBuilder decodedString = new StringBuilder();
		No current = raiz;

		// decodifica a string codificada de acordo com a árvore de Huffman
		for (char bit : encodedString.toCharArray()) {
			if (bit == '0') {
				current = current.getLeft();
			} else {
				current = current.getRight();
			}

			// se o nó atual for uma folha, adiciona o caractere decodificado à string resultante
			if (current.getLeft() == null && current.getRight() == null) {
				decodedString.append(current.getCharacter()); // obtém o caractere associado ao nó
				current = raiz; // retorna para a raiz
			}
		}

		return decodedString.toString();
	}

}