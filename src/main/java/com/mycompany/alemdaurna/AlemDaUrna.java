package com.mycompany.alemdaurna;

import java.awt.GraphicsEnvironment;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import static com.mycompany.alemdaurna.ArquivosDados.*;

/**
 * Além da Urna — Estruturas de Dados, 4º bimestre.
 * Autores: Letícia Guanaes Moreira e Erick Ferreira Lima.
 * Java 17+, sem dependências externas. Execute a partir da pasta do projeto.
 * Os arquivos alterados são uma cópia de estudo; a fonte TSE é preservada.
 */
public class AlemDaUrna {
    public static void main(String[] argumentos) throws Exception {
        boolean grafico = argumentos.length == 0 && !GraphicsEnvironment.isHeadless();
        Menu menu = new Menu(grafico);
        try {
            executar(menu, argumentos);
        } catch (Exception erro) {
            if (!grafico) throw erro;
            menu.mostrar_erro("Não foi possível iniciar a aplicação: " + erro.getMessage());
        }
    }

    private static void executar(Menu menu, String[] argumentos) throws Exception {
        if (Files.notExists(entrada)) {
            menu.medir("CREATE_ARQUIVO", () -> {
                Registro[] base = ler(Path.of("dados/base_original.txt")).vetor();
                gravar(entrada, base);
                return "entrada.txt criado com a base TSE de estudo.";
            });
        }

        String modo = argumentos.length == 0 ? "--console" : argumentos[0];
        switch (modo) {
            case "--benchmark" -> menu.medir("BENCHMARK", () -> {
                Desempenho.benchmark();
                return "Benchmark concluído.";
            });
            case "--gerar-saidas" -> gerar_saidas(menu);
            default -> menu.menu();
        }
    }

    private static void gerar_saidas(Menu menu) throws Exception {
        for (String algoritmo : Ordenacao.algoritmos) {
            menu.medir("SORT_" + algoritmo, () -> {
                Registro[] registros = ler(entrada).vetor();
                Ordenacao.ordenar(registros, algoritmo, "votos");
                gravar(Path.of(algoritmo + ".txt"), registros);
                return algoritmo + ": " + registros.length + " registros.";
            });
        }
        menu.medir("HASH_CIDADES", () -> {
            Files.write(Path.of("cidades_ordenadas.txt"), TabelasHash.carregar_cidades().listar(), StandardCharsets.UTF_8);
            return "Cidades geradas.";
        });
    }
}
