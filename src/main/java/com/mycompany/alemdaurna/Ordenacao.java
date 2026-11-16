package com.mycompany.alemdaurna;

import java.util.Comparator;
import static com.mycompany.alemdaurna.Normalizacao.normalizar;

/** Bubble Sort, Insertion Sort, Selection Sort e Merge Sort. */
final class Ordenacao {
    static Comparator<Registro> comparador(String campo) {
        Comparator<Registro> c;
        switch (campo) {
            case "votos": c = Comparator.comparingLong((Registro r) -> r.votos); break;
            case "municipio": c = Comparator.comparing((Registro r) -> normalizar(r.municipio)); break;
            case "candidato": c = Comparator.comparing((Registro r) -> normalizar(r.candidato)); break;
            case "id": c = Comparator.comparingLong((Registro r) -> r.id); break;
            default: throw new IllegalArgumentException("Campo: votos, municipio, candidato ou id.");
        }
        // Desempate por ID torna a saída determinística em todos os algoritmos.
        return c.thenComparingLong(r -> r.id);
    }
    /** Troca: após cada passagem o maior elemento restante chega ao final. */
    static void bubble_sort(Registro[] v, Comparator<Registro> c) {
        for (int fim = v.length-1; fim > 0; fim--) {
            boolean trocou = false;
            for (int j = 0; j < fim; j++) if (c.compare(v[j],v[j+1]) > 0) {
                Registro aux = v[j]; v[j] = v[j+1]; v[j+1] = aux; trocou = true;
            }
            if (!trocou) break;
        }
    }
    /** Inserção: mantém o prefixo ordenado e abre espaço para o próximo valor. */
    static void insertion_sort(Registro[] v, Comparator<Registro> c) {
        for (int i = 1; i < v.length; i++) {
            Registro atual = v[i]; int j = i-1;
            while (j >= 0 && c.compare(v[j],atual) > 0) { v[j+1] = v[j]; j--; }
            v[j+1] = atual;
        }
    }
    /** Seleção: procura o menor valor do sufixo e o coloca na próxima posição. */
    static void selection_sort(Registro[] v, Comparator<Registro> c) {
        for (int i = 0; i < v.length-1; i++) {
            int menor = i;
            for (int j = i+1; j < v.length; j++) if (c.compare(v[j],v[menor]) < 0) menor = j;
            Registro aux = v[i]; v[i] = v[menor]; v[menor] = aux;
        }
    }
    /** Intercalação: divide em metades e reúne duas sequências já ordenadas. */
    static void merge_sort(Registro[] v, Comparator<Registro> c) {
        merge_rec(v,new Registro[v.length],0,v.length,c);
    }
    static void merge_rec(Registro[] v, Registro[] aux, int inicio, int fim, Comparator<Registro> c) {
        if (fim-inicio < 2) return;
        int meio = (inicio+fim)/2;
        merge_rec(v,aux,inicio,meio,c); merge_rec(v,aux,meio,fim,c);
        int i = inicio, j = meio, k = inicio;
        while (i < meio && j < fim) aux[k++] = c.compare(v[i],v[j]) <= 0 ? v[i++] : v[j++];
        while (i < meio) aux[k++] = v[i++];
        while (j < fim) aux[k++] = v[j++];
        System.arraycopy(aux,inicio,v,inicio,fim-inicio);
    }
    static void ordenar(Registro[] v, String algoritmo, String campo) {
        Comparator<Registro> c = comparador(campo);
        switch (algoritmo) {
            case "bubblesort": bubble_sort(v,c); break;
            case "insertionsort": insertion_sort(v,c); break;
            case "selectionsort": selection_sort(v,c); break;
            case "mergesort": merge_sort(v,c); break;
            default: throw new IllegalArgumentException("Algoritmo desconhecido.");
        }
    }
    static final String[] algoritmos = {"bubblesort","insertionsort","selectionsort","mergesort"};
}
