package com.mycompany.alemdaurna;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import static com.mycompany.alemdaurna.Normalizacao.normalizar;

/** Tabelas hash de países e cidades com encadeamento para colisões. */
final class TabelasHash {
    /** Tabela hash genérica com colisões resolvidas por encadeamento próprio. */
    static class ItemHash {
        String chave, valor; ItemHash proximo;
        ItemHash(String c, String v, ItemHash p) { chave=c; valor=v; proximo=p; }
    }
    static class HashPaises {
        ItemHash[] baldes = new ItemHash[17];
        int hash(String s) {
            int h = 0;
            for (char ch : normalizar(s).toCharArray()) h = (31*h+ch)%baldes.length;
            return h;
        }
        void inserir(String chave, String valor) {
            int h = hash(chave);
            for (ItemHash n=baldes[h]; n!=null; n=n.proximo)
                if (normalizar(n.chave).equals(normalizar(chave))) { n.valor=valor; return; }
            baldes[h] = new ItemHash(chave,valor,baldes[h]);
        }
        String buscar(String chave) {
            for (ItemHash n=baldes[hash(chave)]; n!=null; n=n.proximo)
                if (normalizar(n.chave).equals(normalizar(chave))) return n.valor;
            return "País não cadastrado.";
        }
    }
    /**
     * Hash de cidades que preserva a ordem entre baldes A..Z.
     * Colisões (mesma inicial) são inseridas em lista ligada ordenada.
     * A garantia global vem da combinação: hash monotônica + listas ordenadas.
     * Critério: ordem lexicográfica sem acentos, com desempate pelo texto original.
     */
    static class HashCidades {
        ItemHash[] baldes = new ItemHash[26];
        int hash(String cidade) {
            String s=normalizar(cidade);
            if (s.isEmpty() || s.charAt(0)<'A' || s.charAt(0)>'Z')
                throw new IllegalArgumentException("Cidade deve começar com letra A-Z após normalização.");
            return s.charAt(0)-'A';
        }
        int comparar(String a,String b) {
            int c=normalizar(a).compareTo(normalizar(b)); return c!=0 ? c : a.compareTo(b);
        }
        void inserir(String cidade) {
            int h=hash(cidade); ItemHash anterior=null, atual=baldes[h];
            while (atual!=null && comparar(atual.chave,cidade)<0) { anterior=atual; atual=atual.proximo; }
            if (atual!=null && atual.chave.equals(cidade)) return;
            ItemHash novo=new ItemHash(cidade,"",atual);
            if (anterior==null) baldes[h]=novo; else anterior.proximo=novo;
        }
        boolean buscar(String cidade) {
            for (ItemHash n=baldes[hash(cidade)]; n!=null; n=n.proximo)
                if (normalizar(n.chave).equals(normalizar(cidade))) return true;
            return false;
        }
        List<String> listar() {
            List<String> resultado=new ArrayList<>();
            for (ItemHash inicio:baldes) for (ItemHash n=inicio;n!=null;n=n.proximo) resultado.add(n.chave);
            return resultado; // Nenhuma chamada a sort é necessária.
        }
    }
    static HashPaises carregar_paises() throws IOException {
        HashPaises h=new HashPaises();
        for (String linha:Files.readAllLines(Path.of("dados/paises.txt"),StandardCharsets.UTF_8)) {
            String[] c=linha.split(";",2); h.inserir(c[0],c[1]);
        }
        return h;
    }
    static HashCidades carregar_cidades() throws IOException {
        HashCidades h=new HashCidades();
        for (String cidade:Files.readAllLines(Path.of("dados/cidades.txt"),StandardCharsets.UTF_8)) h.inserir(cidade);
        return h;
    }
}
