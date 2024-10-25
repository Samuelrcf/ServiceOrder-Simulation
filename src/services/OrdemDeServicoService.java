package services;

import java.time.LocalDateTime;

import entities.Operacao;
import entities.OrdemDeServico;

public class OrdemDeServicoService {

    public OrdemDeServico toOS(String string) {
        String content = string.substring(string.indexOf("[") + 1, string.lastIndexOf("]"));
        String[] atributos = content.split(", ");

        Operacao operacao = Operacao.valueOf(atributos[0].split("=")[1]);  // extrai a operação
        int codigo = Integer.parseInt(atributos[1].split("=")[1]);  // extrai o código

        if (operacao == Operacao.BUSCAR || operacao == Operacao.REMOVER) { // se for remover ou buscar
            return new OrdemDeServico(operacao, codigo);
        }

        String nome = atributos[2].split("=")[1];  // extrai o nome
        String descricao = atributos[3].split("=")[1];  // extrai a descrição
        LocalDateTime horaSolicitacao = LocalDateTime.parse(atributos[4].split("=")[1]);  // extrai a hora
        int contadorFrequencia = Integer.parseInt(atributos[5].split("=")[1]);  // extrai o contador de frequência

        OrdemDeServico os = new OrdemDeServico(operacao, codigo, nome, descricao, horaSolicitacao);
        os.setContadorFrequencia(contadorFrequencia);  // seta o contador de frequência

        return os;
    }
}

