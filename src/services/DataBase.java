package services;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Random;

import entities.DadosCompressao;
import entities.No;
import entities.NoCache;
import entities.Operacao;
import entities.OrdemDeServico;
import exceptions.ResourceAlreadyExistsException;
import exceptions.ResourceNotFoundException;

public class DataBase {

    int M;
    Cache cache;
    OrdemDeServico tabela[];
    final double FC_CONSTANTE = 0.7;
    final String CAMINHO_ARQUIVO = "data_base_log.txt";
    KMP kmp = new KMP();
    OrdemDeServicoService service;

    public DataBase(int tam, Cache cache, OrdemDeServicoService service) {
        this.M = tam;
        this.cache = cache;
        this.tabela = new OrdemDeServico[this.M];
        this.service = service;
        System.out.println("Base de dados criada com tamanho: " + this.M);
    }

    public int hash(int ch) { 
        double A = 0.6180339887; 
        double temp = ch * A;
        temp = temp - (int) temp;
        return (int) (this.M * temp); 
    }

    public void registrarLogOperacao(String operacao, OrdemDeServico elemento, OrdemDeServico elementoCacheAdicionado, OrdemDeServico elementoCacheRemovido, OrdemDeServico elementoCacheAlterado) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String dataFormatada = LocalDateTime.now().format(formatter);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CAMINHO_ARQUIVO, true))) {
            writer.write("Operação: " + operacao + "\n" + "Data: " + dataFormatada + "\n");

            if (elemento != null) {
                writer.write("Elemento afetado: " + elemento.toString() + "\n");
            } else {
                writer.write("Elemento afetado: -\n");
            }

            int quantidadeElementos = 0;
            for (OrdemDeServico os : this.tabela) {
                if (os != null) {
                    quantidadeElementos++;
                }
            }
            writer.write("Quantidade de Ordens de Serviço na tabela: " + quantidadeElementos + "\n");

            double fatorCarga = (double) quantidadeElementos / this.M;
            writer.write("Fator de carga atual: " + fatorCarga + "\n");

            cache.imprimirCache(writer);

            if (elementoCacheRemovido != null) {
                writer.write("Elemento de código " + elementoCacheRemovido.getCodigo() + " removido da cache.\n");
            }

            if (elementoCacheAdicionado != null) {
                writer.write("Elemento de código " + elementoCacheAdicionado.getCodigo() + " adicionado à cache.\n");
            }

            if (elementoCacheAlterado != null) {
                writer.write("Elemento de código " + elementoCacheAlterado.getCodigo() + " alterado na cache.\n");
            }

            writer.write("----------------------------------------------------\n");
        } catch (IOException e) {
            System.err.println("Erro ao registrar log: " + e.getMessage());
        }
    }
    
    public void doOperacao(Operacao operacao, OrdemDeServico os) {
        switch (operacao) {
            case INSERIR:
                this.inserir(os.getCodigo(), os.getNome(), os.getDescricao(), os.getHoraSolicitacao());
                break;
                
            case REMOVER:
            	this.remover(os.getCodigo());
                break;
                
            case ATUALIZAR:
            	this.atualizar(os.getCodigo(), os.getNome(), os.getDescricao(), os.getHoraSolicitacao());
                break;
                
            case BUSCAR:
            	this.buscar(os.getCodigo());
            	break;
                
            default:
                throw new IllegalArgumentException("Operação inválida: " + operacao);
        }
    }

    public void inserir(int codigo, String nome, String descricao, LocalDateTime horaSolicitacao) {
        if (verificarFatorCarga()) {
            System.out.println("\nFator de carga atingido. Redimensionando tamanho da base de dados...");
            redimensionarTabela();
        }

        int h0 = this.hash(codigo);
        int h = h0;
        int k = 0;

        while (this.tabela[h] != null && this.tabela[h].getCodigo() != codigo) {
            System.out.println("Colisão detectada na posição " + h + " para a Ordem de Serviço com " + codigo + ". Tentando nova posição...");
            k++;
            h = (h0 + k) % this.M;
        }

        if (this.tabela[h] == null) {
            OrdemDeServico novaOrdemDeServico = new OrdemDeServico(codigo, nome, descricao, horaSolicitacao);
            this.tabela[h] = novaOrdemDeServico;
            System.out.println("Inserida na posição " + h + ": " + this.tabela[h]);
            registrarLogOperacao("Inserir OS " + codigo, novaOrdemDeServico, null, null, null);
        } else {
        	registrarLogOperacao("[ERRO] Ordem de Serviço com código " + codigo + " já existe.", null, null, null, null);
            throw new ResourceAlreadyExistsException("Ordem de Serviço com código " + codigo + " já existe.");
        }
    }
    
    public void reinserir(int codigo, String nome, String descricao, LocalDateTime horaSolicitacao) {
    	if (verificarFatorCarga()) {
    		System.out.println("\nFator de carga atingido. Redimensionando tamanho da base de dados...");
    		redimensionarTabela();
    	}
    	
    	int h0 = this.hash(codigo);
    	int h = h0;
    	int k = 0;
    	
    	while (this.tabela[h] != null && this.tabela[h].getCodigo() != codigo) {
    		System.out.println("Colisão detectada na posição " + h + " para a Ordem de Serviço com " + codigo + ". Tentando nova posição...");
    		k++;
    		h = (h0 + k) % this.M;
    	}
    	
    	if (this.tabela[h] == null) {
    		OrdemDeServico novaOrdemDeServico = new OrdemDeServico(codigo, nome, descricao, horaSolicitacao);
    		this.tabela[h] = novaOrdemDeServico;
    		registrarLogOperacao("Reinserir OS " + codigo, novaOrdemDeServico, null, null, null);
    	} else {
    		registrarLogOperacao("[ERRO] Ordem de Serviço com código " + codigo + " já existe.", null, null, null, null);
    		throw new ResourceAlreadyExistsException("Ordem de Serviço com código " + codigo + " já existe.");
    	}
    }

    public OrdemDeServico buscar(int codigo) {
        
        NoCache os = cache.buscarEReorganizar(codigo);
        if(os != null) {
        	System.out.println("HIT!");
        	registrarLogOperacao("Buscar OS " + codigo, os.getOrdemDeServico(), null, null, null);
        	return os.getOrdemDeServico();
        }
        System.out.println("MISS!");
        System.out.println("Buscando Ordem de Serviço com código " + codigo + " na base de dados...");
        
        OrdemDeServico resultado = buscarNaBase(codigo);
        if (resultado != null) {
        	NoCache osRemovida = null;
            if (cache.contar() == cache.getTamanhoMaximo()) {
            	osRemovida = cache.removerUltimo();
            }
            cache.inserir(resultado);
            if (osRemovida != null) {
            	registrarLogOperacao("Buscar OS " + codigo, resultado, resultado, osRemovida.getOrdemDeServico(), null);
            }else {
            	registrarLogOperacao("Buscar OS " + codigo, resultado, resultado, null, null);
            }
            
        }
        return resultado;
    }

    private OrdemDeServico buscarNaBase(int codigo) {
        int h0 = this.hash(codigo);
        int h = h0;
        int k = 0;

        while (this.tabela[h] != null && k < this.M) {
            if (this.tabela[h].getCodigo() == codigo) {
                System.out.println("Ordem de Serviço encontrado na base de dados na posição: " + h);
                return this.tabela[h];
            }
            k++;
            h = (h0 + k) % this.M;
        }
        registrarLogOperacao("[ERRO] Ordem de Serviço com código " + codigo + " não encontrado na base de dados.", null, null, null, null);
        throw new ResourceNotFoundException("Ordem de Serviço com código " + codigo + " não encontrado na base de dados.");
    }

    public void remover(int codigo) {
    	OrdemDeServico osRemovida = cache.remover(codigo);
    	System.out.println("\nRemovendo Ordem de Serviço de código " + codigo + " da base de dados...");
        OrdemDeServico elemento = buscarNaBase(codigo);
        if (elemento != null) {
            int h0 = this.hash(codigo);
            int h = h0;
            int k = 0;

            while (this.tabela[h] != null && k < this.M) {
                if (this.tabela[h].getCodigo() == codigo) {
                    this.tabela[h] = null;
                    System.out.println("Removida da posição " + h + ": " + elemento);
                    if(osRemovida != null) {
                    	registrarLogOperacao("Remover OS " + codigo, elemento, null, osRemovida, null);
                    }else {
                    	registrarLogOperacao("Remover OS " + codigo, elemento, null, null, null);
                    }
                    return;
                }
                k++;
                h = (h0 + k) % this.M;
            }
        }else {
        	registrarLogOperacao("[ERRO] Ordem de Serviço com código " + codigo + " não encontrado para remoção.", null, null, null, null);
        	throw new ResourceNotFoundException("Ordem de Serviço com código " + codigo + " não encontrado para remoção.");
        }
    }

    public void atualizar(int codigo, String nome, String descricao, LocalDateTime horaSolicitacao) {
    	OrdemDeServico osAtualizado = cache.atualizar(codigo, nome, descricao, horaSolicitacao);
    	System.out.println("\nAtualizando Ordem de Serviço de código " + codigo + " da base de dados...");
        OrdemDeServico elemento = buscarNaBase(codigo);
        if (elemento != null) {
            int h0 = this.hash(codigo);
            int h = h0;
            int k = 0;

            while (this.tabela[h] != null && k < this.M) {
                if (this.tabela[h].getCodigo() == codigo) {
                    OrdemDeServico os = this.tabela[h];
                    os.setNome(nome);
                    os.setDescricao(descricao);
                    os.setHoraSolicitacao(horaSolicitacao);
                    System.out.println("Atualizado na posição " + h + ": " + os);
                    if(osAtualizado != null) {
                    	registrarLogOperacao("Atualizar OS " + codigo, os, null, null, osAtualizado);
                    }else {
                    	registrarLogOperacao("Atualizar OS " + codigo, os, null, null, null);
                    }
                    
                    return;
                }
                k++;
                h = (h0 + k) % this.M;
            }
        } else {
        	registrarLogOperacao("[ERRO] Ordem de Serviço com código " + codigo + " não encontrado para atualização.", null, null, null, null);
            throw new ResourceNotFoundException("Ordem de Serviço com código " + codigo + " não encontrado para atualização.");
        }
    }

    public boolean verificarFatorCarga() {
        int elementosOcupados = 0;

        for (OrdemDeServico os : this.tabela) {
            if (os != null) {
                elementosOcupados++;
            }
        }

        double fc = (double) elementosOcupados / this.M;
        return fc > FC_CONSTANTE;
    }

    private void redimensionarTabela() {
        int novoTam = proximoPrimoMaiorQue(this.M * 2);
        OrdemDeServico[] tabelaAntiga = this.tabela;
        this.M = novoTam;
        this.tabela = new OrdemDeServico[this.M];
        System.out.println("\nTabela redimensionada para o novo tamanho: " + novoTam);
        registrarLogOperacao("Redimensionamento da tabela", null, null, null, null);
        reinserirElementos(tabelaAntiga);
    }

    private int proximoPrimoMaiorQue(int n) {
        while (true) {
            if (isPrimo(n))
                return n;
            n++;
        }
    }

    private boolean isPrimo(int num) {
        if (num <= 1)
            return false;
        if (num <= 3)
            return true;
        if (num % 2 == 0 || num % 3 == 0)
            return false;
        for (int i = 5; i * i <= num; i += 6) {
            if (num % i == 0 || num % (i + 2) == 0)
                return false;
        }
        return true;
    }

    private void reinserirElementos(OrdemDeServico[] tabelaAntiga) {
        System.out.println("\nReinserindo Ordens de Serviços da tabela antiga após redimensionamento...");
        for (OrdemDeServico os : tabelaAntiga) {
            if (os != null) {
                reinserir(os.getCodigo(), os.getNome(), os.getDescricao(), os.getHoraSolicitacao());
            }
        }
        System.out.println("Elementos reinseridos com sucesso.");
    }

    public DadosCompressao imprimirBaseDeDados() {
        StringBuilder tabelaString = new StringBuilder();
        tabelaString.append("\nEstado atual da tabela hash:\n");
        
        for (int i = 0; i < this.M; i++) {
            if (this.tabela[i] != null) {
                tabelaString.append(i).append(" --> ").append(this.tabela[i]).append("\n");
            } else {
                tabelaString.append(i).append("\n");
            }
        }
        
        String baseDeDados = tabelaString.toString();
        
		HashMap<Character, Integer> frequencyMap = Huffman.countFrequency(baseDeDados);
		
		PriorityList minHeap = Huffman.populatePriorityList(frequencyMap);

		No root = Huffman.buildHuffmanTree(minHeap);

		HashMap<Character, String> huffmanCodes = Huffman.initBuildHuffmanCodes(root);

		String encodedString = Huffman.encode(baseDeDados, huffmanCodes);
		
		DadosCompressao ac = new DadosCompressao(root, encodedString);
		
		return ac;
    }

    public int acessarQuantidadeRegistros() {
        int quantidadeElementos = 0;
        for (OrdemDeServico os : this.tabela) {
            if (os != null) {
                quantidadeElementos++;
            }
        }
        return quantidadeElementos;
    }
    
	public void enviarMensagem(No root, String encodedString) {
		String decodedString = Huffman.decode(encodedString, root);
		OrdemDeServico osDecodificada = service.toOS(decodedString);
		doOperacao(osDecodificada.getOperacao(), osDecodificada);
	}
	
	public void processarString(String encodedString, No root) {
		String padrao = Huffman.decode(encodedString, root);
        try {
			String conteudoArquivo = new String(Files.readAllBytes(Paths.get(CAMINHO_ARQUIVO)));
			int ocorrencias = kmp.buscar(padrao, conteudoArquivo);
			System.out.println("\nNúmero de ocorrências do padrão " + padrao + ": " + ocorrencias);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void preencherCache() {
	    System.out.println("\nPreenchendo cache com 30 ordens de serviço aleatórias...");
	    Random random = new Random();
	    int count = 0;

	    while (count < 30) {
	        int randomIndex = random.nextInt(this.M);
	        OrdemDeServico os = this.tabela[randomIndex];
	        
	        if (os != null && cache.buscar(os.getCodigo()) == null) {
	        	System.out.println("Não está na cache.");
	            cache.inserir(os);
	            System.out.println("Ordem de Serviço adicionada à cache: " + os);
	            count++;
	        }
	    }
	    System.out.println("Cache preenchida com sucesso.");
	}

}
