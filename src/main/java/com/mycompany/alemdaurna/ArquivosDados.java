package com.mycompany.alemdaurna;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/** Leitura, validação e gravação dos registros em arquivos UTF-8. */
final class ArquivosDados {
    static final Path entrada = Path.of("entrada.txt");
    static final String cabecalho = "id;ordem_inclusao;ano;turno;uf;codigo_municipio;municipio;numero;candidato;partido;votos";

    /** UTF-8, validação de duplicatas e manutenção da sequência de inclusão. */
    static DequeRegistros ler(Path arquivo) throws IOException {
        DequeRegistros d = new DequeRegistros();
        Set<Long> ids = new HashSet<>(), ordens = new HashSet<>();
        Set<String> chaves = new HashSet<>();
        try (BufferedReader f = Files.newBufferedReader(arquivo, StandardCharsets.UTF_8)) {
            if (!cabecalho.equals(f.readLine())) throw new IOException("Cabeçalho inválido: "+arquivo);
            String linha; int numero_linha = 1;
            while ((linha = f.readLine()) != null) {
                numero_linha++;
                try {
                    Registro r = new Registro(linha);
                    if (!ids.add(r.id) || !ordens.add(r.ordem_inclusao) || !chaves.add(r.chave()))
                        throw new IllegalArgumentException("ID, ordem ou chave eleitoral duplicados");
                    d.incluir(r, false);
                } catch (RuntimeException e) { throw new IOException("Linha "+numero_linha+": "+e.getMessage(),e); }
            }
        }
        return d;
    }
    // Grava em temporário antes da substituição para reduzir risco de arquivo incompleto.
    static void gravar(Path arquivo, Registro[] registros) throws IOException {
        Path temp = arquivo.resolveSibling(arquivo.getFileName()+".tmp");
        try (BufferedWriter f = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
            f.write(cabecalho); f.write("\n");
            for (Registro r : registros) { f.write(r.linha()); f.write("\n"); }
        }
        try { Files.move(temp, arquivo, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE); }
        catch (AtomicMoveNotSupportedException e) { Files.move(temp, arquivo, StandardCopyOption.REPLACE_EXISTING); }
    }
}
