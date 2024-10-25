package services;

import java.util.ArrayList;

import entities.No;
import exceptions.EmptyHeapException;

public class PriorityList {
    private ArrayList<No> heap;
    private int tamHeap;

    public PriorityList() {
        this.heap = new ArrayList<>();
        this.tamHeap = 0;
    }

    public void inserir(No novo) {
        heap.add(novo);
        tamHeap++;
        subir(tamHeap - 1);
    }

    public No remover() {
        if (tamHeap == 0) {
            throw new EmptyHeapException("Underflow - Heap vazio!");
        }
        No raiz = heap.get(0);
        heap.set(0, heap.get(tamHeap - 1)); 
        tamHeap--;
        heap.remove(tamHeap); 
        descer(0, tamHeap); 
        return raiz; 
    }

    private void subir(int i) {
        int j = (i - 1) / 2;
        if (j >= 0 && heap.get(i).getFrequency() < heap.get(j).getFrequency()) { 
            trocar(i, j); 
            subir(j); 
        }
    }

    private void descer(int i, int n) {
        int j = 2 * i + 1;
        if (j < n) {
            if (j < n - 1 && heap.get(j + 1).getFrequency() < heap.get(j).getFrequency()) {
                j++; 
            }
            if (heap.get(j).getFrequency() < heap.get(i).getFrequency()) { 
                trocar(i, j); 
                descer(j, n); 
            }
        }
    }

    private void trocar(int i, int j) {
        No temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

    public int tamanho() {
        return tamHeap;
    }
}
