package application;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import entities.Operacao;
import entities.OrdemDeServico;
import services.Cache;
import services.Cliente;
import services.DataBase;
import services.OrdemDeServicoService;

public class Simulacao {

    public static void main(String[] args) {
    	
    	Cache cache = new Cache();
    	OrdemDeServicoService service = new OrdemDeServicoService();
        DataBase db = new DataBase(16, cache, service);
        Cliente cliente = new Cliente(db);
        
        for(int i = 1; i<=100; i++) {
        	cliente.doOperacao(new OrdemDeServico(Operacao.INSERIR, i, "Ordem de Serviço " + i, "Descrição " + i, LocalDateTime.now()));
        }
        
        db.preencherCache();
        
        cache.imprimirCache();
        
        Random random = new Random();
        List<Integer> codigosAleatorios = new ArrayList<>();
        
        for (int i = 0; i < 3; i++) {
        	codigosAleatorios.add(random.nextInt(db.acessarQuantidadeRegistros()));
        }
        
        cliente.doOperacao(new OrdemDeServico(Operacao.BUSCAR, codigosAleatorios.get(0)));
        cliente.doOperacao(new OrdemDeServico(Operacao.BUSCAR, codigosAleatorios.get(0)));
        cliente.doOperacao(new OrdemDeServico(Operacao.BUSCAR, codigosAleatorios.get(0)));
        cliente.doOperacao(new OrdemDeServico(Operacao.BUSCAR, codigosAleatorios.get(0)));
        cache.imprimirCache();
        cliente.doOperacao(new OrdemDeServico(Operacao.BUSCAR, codigosAleatorios.get(1)));
        cliente.doOperacao(new OrdemDeServico(Operacao.BUSCAR, codigosAleatorios.get(1)));
        cliente.doOperacao(new OrdemDeServico(Operacao.BUSCAR, codigosAleatorios.get(1)));
        cache.imprimirCache();
        cliente.doOperacao(new OrdemDeServico(Operacao.BUSCAR, codigosAleatorios.get(2)));
        cliente.doOperacao(new OrdemDeServico(Operacao.BUSCAR, codigosAleatorios.get(2)));
        cache.imprimirCache();
        
        cliente.listarOS();
        
        cliente.doOperacao(new OrdemDeServico(Operacao.INSERIR, 101, "Ordem de Serviço " + 101, "Descrição " + 101, LocalDateTime.now()));
        cliente.doOperacao(new OrdemDeServico(Operacao.INSERIR, 102, "Ordem de Serviço " + 102, "Descrição " + 102, LocalDateTime.now()));
        
        cliente.listarOS();
        
        cliente.doOperacao(new OrdemDeServico(Operacao.ATUALIZAR, codigosAleatorios.get(0), "Ordem de Serviço Atualizada " + codigosAleatorios.get(0), "Descrição Atualizada " + codigosAleatorios.get(0), LocalDateTime.now()));
        cliente.doOperacao(new OrdemDeServico(Operacao.ATUALIZAR, codigosAleatorios.get(1), "Ordem de Serviço Atualizada " + codigosAleatorios.get(1), "Descrição Atualizada " + codigosAleatorios.get(1), LocalDateTime.now()));
        
        cliente.listarOS();
        cache.imprimirCache();
        
        cliente.doOperacao(new OrdemDeServico(Operacao.REMOVER, codigosAleatorios.get(0)));
        cliente.doOperacao(new OrdemDeServico(Operacao.REMOVER, codigosAleatorios.get(1)));
        cliente.doOperacao(new OrdemDeServico(Operacao.REMOVER, 102));
        
        cliente.listarOS();
        cache.imprimirCache();
        
        cliente.acessarQuantidadeOcorrencias("Inserir");
        cliente.acessarQuantidadeOcorrencias("Atualizar");
        cliente.acessarQuantidadeOcorrencias("Remover");
        
        cliente.acessarQuantidadeRegistros();
        
        db.buscar(200);
    }
}
