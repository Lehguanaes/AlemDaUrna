package com.mycompany.alemdaurna;

import java.text.Normalizer;
import java.util.Locale;

/** Chave de comparação de nomes para pesquisa, ordenação e tabelas hash. */
final class Normalizacao {
    /** Normalização didática: acentos e caixa não alteram a posição alfabética. */
    static String normalizar(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toUpperCase(Locale.ROOT);
    }

}
