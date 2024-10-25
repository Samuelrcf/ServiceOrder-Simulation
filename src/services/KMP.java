package services;

public class KMP {
    private int[] tabelaPrefixo; 

    public int buscar(String padrao, String texto) {
        int M = padrao.length(); 
        int N = texto.length();  
        tabelaPrefixo = new int[M];
        criarTabelaPrefixo(padrao, M);

        int j = 0; 
        int i = 0; 
        int contador = 0; 

        while (i < N) {
            if (padrao.charAt(j) == texto.charAt(i)) {
                j++;
                i++;
            }
            if (j == M) {
                contador++;
                j = tabelaPrefixo[j - 1]; 
            } else if (i < N && padrao.charAt(j) != texto.charAt(i)) {
                if (j != 0) {
                    j = tabelaPrefixo[j - 1]; 
                } else {
                    i++; 
                }
            }
        }
        return contador; 
    }

    private void criarTabelaPrefixo(String padrao, int M) {
        int tamanhoPrefixo = 0; 
        int i = 1; 
        tabelaPrefixo[0] = 0;

        while (i < M) {
            if (padrao.charAt(i) == padrao.charAt(tamanhoPrefixo)) {
                tamanhoPrefixo++;
                tabelaPrefixo[i] = tamanhoPrefixo;
                i++;
            } else {
                if (tamanhoPrefixo != 0) {
                    tamanhoPrefixo = tabelaPrefixo[tamanhoPrefixo - 1]; // Reduz o tamanho do prefixo
                } else {
                    tabelaPrefixo[i] = tamanhoPrefixo;
                    i++;
                }
            }
        }
    }
}
