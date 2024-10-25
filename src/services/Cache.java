package services;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;

import entities.NoCache;
import entities.OrdemDeServico;

public class Cache {
    private NoCache primeiro;
    private NoCache ultimo;
    private final int tamanhoMaximo = 30;

    public Cache() {
        this.primeiro = null;
        this.ultimo = null;
    }

    public int getTamanhoMaximo() {
        return tamanhoMaximo;
    }

    public void inserir(OrdemDeServico ordem) {
        System.out.println("Adicionando Ordem de Serviço de código " + ordem.getCodigo() + " na cache...");
        NoCache novoNoCache = new NoCache(ordem);
        novoNoCache.getOrdemDeServico().setContadorFrequencia(1);
        
        if (primeiro == null) {
            primeiro = novoNoCache;
            ultimo = novoNoCache;
        } else {
            ultimo.setProximo(novoNoCache);
            ultimo = novoNoCache;
        }
        System.out.println("Adicionada com sucesso.");
    }

    public NoCache buscar(int codigo) {
        System.out.println("\nBuscando Ordem de Serviço com código " + codigo + " na cache...");
        NoCache atual = primeiro;

        while (atual != null) {
            if (atual.getOrdemDeServico().getCodigo() == codigo) {
                return atual;
            }
            atual = atual.getProximo();
        }
        return null;
    }
  
    public NoCache buscarEReorganizar(int codigo) {
        NoCache encontrado = buscar(codigo);

        if (encontrado != null) {
            encontrado.getOrdemDeServico().setContadorFrequencia(encontrado.getOrdemDeServico().getContadorFrequencia() + 1);
            reorganizarNo(encontrado); 
        }

        return encontrado; 
    }

    public NoCache removerUltimo() {
        if (primeiro == null) {
            return null; 
        }

        if (primeiro.getProximo() == null) {
            NoCache removido = primeiro; 
            primeiro = null;
            ultimo = null;
            return removido; 
        }

        NoCache atual = primeiro;
        NoCache anterior = null;

        while (atual.getProximo() != null) {
            anterior = atual;
            atual = atual.getProximo();
        }

        if (anterior != null) {
            anterior.setProximo(null);
            ultimo = anterior; 
        }

        return atual;
    }

    public OrdemDeServico remover(int codigo) {
        NoCache encontrado = buscar(codigo); 
        if (encontrado == null) {
            System.out.println("Ordem de Serviço com código " + codigo + " não está na cache.");
            return null; 
        }
        System.out.println("Ordem de Serviço com código " + codigo + " está na cache.");

        if (primeiro.getOrdemDeServico().getCodigo() == codigo) {
            System.out.println("Removendo a Ordem de Serviço com código " + codigo + " da cache...");
            OrdemDeServico removido = primeiro.getOrdemDeServico(); 
            primeiro = primeiro.getProximo(); 
            if (primeiro == null) {
                ultimo = null; 
            }
            System.out.println("Ordem de Serviço com código " + codigo + " removida da cache.");
            return removido;
        }

        NoCache atual = primeiro;
        NoCache anterior = null;

        while (atual != null) {
            if (atual.getOrdemDeServico().getCodigo() == codigo) {
                OrdemDeServico removido = atual.getOrdemDeServico(); 
                anterior.setProximo(atual.getProximo()); 
                if (atual == ultimo) {
                    ultimo = anterior; 
                }
                return removido; 
            }
            anterior = atual;
            atual = atual.getProximo();
        }

        return null; 
    }
    
    public OrdemDeServico atualizar(int codigo, String nome, String descricao, LocalDateTime horaSolicitacao) {
        NoCache encontrado = buscar(codigo); 

        if (encontrado != null) {
            System.out.println("Atualizando a Ordem de Serviço com código " + codigo + " na cache...");
            OrdemDeServico ordemAtualizada = encontrado.getOrdemDeServico(); 
            ordemAtualizada.setNome(nome);
            ordemAtualizada.setDescricao(descricao);
            ordemAtualizada.setHoraSolicitacao(horaSolicitacao);
            System.out.println("Ordem de Serviço com código " + codigo + " foi atualizada com sucesso.");
            return ordemAtualizada; 
        } else {
            System.out.println("Ordem de Serviço com código " + codigo + " não encontrada na cache.");
            return null; 
        }
    }

    private void reorganizarNo(NoCache acessado) {
        // se a lista tá vazia ou o nó acessado é o primeiro, não faz nada
        if (primeiro == null || acessado == primeiro) {
            return; 
        }

        NoCache atual = primeiro;
        NoCache anterior = null;

        // percorre a lista até encontrar o nó a ser movido
        while (atual != null && atual != acessado) {
            anterior = atual;
            atual = atual.getProximo();
        }

        // desconecta o nó acessado da lista
        if (anterior != null) {
            anterior.setProximo(acessado.getProximo());
        }

        // atualiza o último nó se necessário
        if (acessado == ultimo) {
            ultimo = anterior; 
        }

        // encontra a nova posição com base na frequência
        NoCache novoAnterior = null;
        NoCache novoAtual = primeiro;

        // encontra a nova posição onde o nó acessado deve ser inserido na lista com base na frequência
        while (novoAtual != null && novoAtual.getOrdemDeServico().getContadorFrequencia() >= acessado.getOrdemDeServico().getContadorFrequencia()) {
            novoAnterior = novoAtual;
            novoAtual = novoAtual.getProximo();
        }

        // insere o nó acessado na nova posição
        if (novoAnterior == null) {
            // se não tem anterior, o nó acessado vai ser o novo primeiro
            acessado.setProximo(primeiro);
            primeiro = acessado;
        } else {
            // conecta o nó acessado na posição correta
            novoAnterior.setProximo(acessado);
            acessado.setProximo(novoAtual);
        }

        // atualiza o último se necessário
        if (acessado.getProximo() == null) {
            ultimo = acessado;
        }
    }

    public void imprimirCache(){
       System.out.println("\nEstado atual da Cache:\n") ;

        NoCache atual = primeiro;
        int indice = 1;

        if (atual == null) {
        	System.out.println("A cache está vazia.\n");
            return;
        }

        while (atual != null) {
        	System.out.println(indice + " --> " + atual.getOrdemDeServico().toString() + "\n");
            atual = atual.getProximo();
            indice++;
        }

        for (int i = indice; i <= tamanhoMaximo; i++) {
        	System.out.println(i + " --> [Vazio]\n");
        }
    }
    
    public void imprimirCache(BufferedWriter writer) throws IOException {
        writer.write("Estado atual da Cache:\n");

        NoCache atual = primeiro;
        int indice = 1;

        if (atual == null) {
            writer.write("A cache está vazia.\n");
            return;
        }

        while (atual != null) {
            writer.write(indice + " --> " + atual.getOrdemDeServico().toString() + "\n");
            atual = atual.getProximo();
            indice++;
        }

        for (int i = indice; i <= tamanhoMaximo; i++) {
            writer.write(i + " --> [Vazio]\n");
        }
    }

    public int contar() {
        int contador = 0;
        NoCache atual = primeiro;
        while (atual != null) {
            contador++;
            atual = atual.getProximo();
        }
        return contador;
    }

    public boolean isEmpty() {
        return primeiro == null;
    }


}
