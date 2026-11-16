package com.mycompany.alemdaurna;

/** Lista duplamente ligada com inclusão nas duas pontas e retirada por cronologia. */
final class DequeRegistros {
    /** Cada nó conhece seu anterior e seu próximo: lista duplamente ligada. */
    static class No {
        Registro valor; No anterior, proximo;
        No(Registro valor) { this.valor = valor; }
    }

    No inicio, fim; int tamanho;
    void incluir(Registro valor, boolean no_inicio) {
        No novo = new No(valor);
        if (inicio == null) inicio = fim = novo;
        else if (no_inicio) { novo.proximo = inicio; inicio.anterior = novo; inicio = novo; }
        else { fim.proximo = novo; novo.anterior = fim; fim = novo; }
        tamanho++;
    }
    Registro buscar(long id) {
        for (No n = inicio; n != null; n = n.proximo) if (n.valor.id == id) return n.valor;
        return null;
    }
    void desligar(No n) {
        if (n.anterior == null) inicio = n.proximo; else n.anterior.proximo = n.proximo;
        if (n.proximo == null) fim = n.anterior; else n.proximo.anterior = n.anterior;
        tamanho--;
    }
    // A posição física pode variar; a ordem persistida preserva a cronologia.
    Registro remover_por_ordem(boolean lifo) {
        No escolhido = null;
        for (No n = inicio; n != null; n = n.proximo)
            if (escolhido == null || (lifo ? n.valor.ordem_inclusao > escolhido.valor.ordem_inclusao
                    : n.valor.ordem_inclusao < escolhido.valor.ordem_inclusao)) escolhido = n;
        if (escolhido == null) return null;
        Registro removido = escolhido.valor; desligar(escolhido); return removido;
    }
    Registro[] vetor() {
        Registro[] v = new Registro[tamanho]; int i = 0;
        for (No n = inicio; n != null; n = n.proximo) v[i++] = n.valor;
        return v;
    }
}
