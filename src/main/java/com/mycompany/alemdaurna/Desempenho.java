package com.mycompany.alemdaurna;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;
import static com.mycompany.alemdaurna.ArquivosDados.*;
import static com.mycompany.alemdaurna.Ordenacao.*;

/** Medição das operações, log de tempos e execução do benchmark de ordenação. */
final class Desempenho {
    /** O cronômetro começa depois da digitação e inclui leitura, processo e gravação. */
    interface Operacao { String executar() throws Exception; }
    static String medir(String nome, Operacao op) throws Exception {
        long inicio=System.nanoTime(); String status="OK";
        String resultado;
        try { resultado=op.executar(); }
        catch (Exception e) { status="ERRO "+e.getClass().getSimpleName(); throw e; }
        finally {
            long ns=System.nanoTime()-inicio;
            Files.createDirectories(Path.of("resultados"));
            Path log=Path.of("resultados/desempenho.csv");
            if (!Files.exists(log)) Files.writeString(log,"instante_utc;operacao;tempo_total_ns;status\n",StandardCharsets.UTF_8);
            Files.writeString(log,Instant.now()+";"+nome+";"+ns+";"+status+"\n",StandardCharsets.UTF_8,StandardOpenOption.APPEND);
        }
        return resultado;
    }

    /** Experimento reproduzível: mesmas entradas, semente fixa, aquecimento e mediana. */
    static void benchmark() throws IOException {
        Registro[] base=ler(Path.of("dados/base_original.txt")).vetor();
        List<String> linhas=new ArrayList<>();
        linhas.add("n;cenario;algoritmo;repeticao;tempo_algoritmo_ns;tempo_total_ns");
        for (String algoritmo:algoritmos) for (int k=0;k<5;k++) ordenar(Arrays.copyOf(base,500),algoritmo,"votos");
        for (int n:new int[]{100,1000,3000}) {
            List<Registro> mistura=new ArrayList<>(Arrays.asList(base));
            Collections.shuffle(mistura,new Random(2026));
            Registro[] amostra=mistura.subList(0,n).toArray(new Registro[0]);
            for (String cenario:new String[]{"aleatorio","ordenado","inverso"}) {
                Registro[] origem=amostra.clone();
                if (!cenario.equals("aleatorio")) {
                    ordenar(origem,"mergesort","votos");
                    if (cenario.equals("inverso")) for (int i=0;i<origem.length/2;i++) {
                        Registro aux=origem[i]; origem[i]=origem[origem.length-1-i]; origem[origem.length-1-i]=aux;
                    }
                }
                Path amostra_arquivo=Path.of("resultados/amostra_benchmark.txt");
                Files.createDirectories(amostra_arquivo.getParent()); gravar(amostra_arquivo,origem);
                for (int repeticao=1;repeticao<=7;repeticao++) {
                    // Alterna a ordem para reduzir o favorecimento por cache/aquecimento.
                    for (int a=0;a<algoritmos.length;a++) {
                        String algoritmo=algoritmos[(a+repeticao)%algoritmos.length];
                        long inicio_total=System.nanoTime();
                        Registro[] v=ler(amostra_arquivo).vetor();
                        long inicio_algoritmo=System.nanoTime(); ordenar(v,algoritmo,"votos");
                        long algoritmo_ns=System.nanoTime()-inicio_algoritmo;
                        gravar(Path.of("resultados/temporario_benchmark.txt"),v);
                        long total_ns=System.nanoTime()-inicio_total;
                        for (int i=1;i<v.length;i++) if (comparador("votos").compare(v[i-1],v[i])>0)
                            throw new AssertionError("Ordenação incorreta");
                        linhas.add(n+";"+cenario+";"+algoritmo+";"+repeticao+";"+algoritmo_ns+";"+total_ns);
                    }
                }
            }
        }
        Files.write(Path.of("resultados/benchmark.csv"),linhas,StandardCharsets.UTF_8);
        Files.writeString(Path.of("resultados/ambiente.txt"),"Java: "+System.getProperty("java.version")+"\nVM: "+System.getProperty("java.vm.name")+"\nSO: "+System.getProperty("os.name")+" "+System.getProperty("os.arch")+"\nProcessadores lógicos: "+Runtime.getRuntime().availableProcessors()+"\nInstante UTC: "+Instant.now()+"\n",StandardCharsets.UTF_8);
    }
}
