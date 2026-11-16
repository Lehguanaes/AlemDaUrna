package com.mycompany.alemdaurna;

/** Busca binária em registros ordenados por ID. A busca linear fica em DequeRegistros.buscar. */
final class Pesquisa {
    /** Busca binária só é válida quando o arquivo está ordenado pela chave usada. */
    static Registro busca_binaria_id(Registro[] v, long id) {
        for (int i = 1; i < v.length; i++) if (v[i-1].id > v[i].id)
            throw new IllegalArgumentException("Para busca binária, ordene este arquivo por id.");
        int inicio = 0, fim = v.length-1;
        while (inicio <= fim) {
            int meio = (inicio+fim) >>> 1;
            if (v[meio].id == id) return v[meio];
            if (v[meio].id < id) inicio = meio+1; else fim = meio-1;
        }
        return null;
    }
}
