package com.mycompany.alemdaurna;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.awt.Dimension;
import java.awt.Font;
import java.text.Normalizer;
import java.util.concurrent.FutureTask;
import java.util.function.Supplier;
import javax.swing.*;
import com.mycompany.alemdaurna.DequeRegistros.No;
import static com.mycompany.alemdaurna.ArquivosDados.*;
import static com.mycompany.alemdaurna.Ordenacao.*;
import static com.mycompany.alemdaurna.Pesquisa.busca_binaria_id;
import static com.mycompany.alemdaurna.TabelasHash.*;
import static com.mycompany.alemdaurna.Normalizacao.normalizar;

/** Operações do menu e diálogos de entrada, confirmação e resultado. */
final class Menu {
    private final Janelas janelas;
    private final Scanner teclado = new Scanner(System.in, StandardCharsets.UTF_8);

    private static class EntradaCancelada extends RuntimeException {}

    Menu(boolean grafico) {
        janelas = grafico ? new Janelas() : null;
    }

    void medir(String nome, Desempenho.Operacao operacao) throws Exception {
        exibir_linha(Desempenho.medir(nome, operacao));
    }

    void exibir(Object mensagem) { System.out.print(texto_tela(mensagem)); }
    void exibir_linha(Object mensagem) {
        if (janelas == null) System.out.println(texto_tela(mensagem));
        else janelas.mensagens.append(mensagem).append('\n');
    }

    long numero(String mensagem) { return Long.parseLong(texto(mensagem)); }
    String texto(String mensagem) {
        if (janelas != null) return janelas.entrada(mensagem);
        exibir(mensagem);
        System.out.flush();
        return teclado.nextLine().trim();
    }

    /** Só autoriza a operação após uma resposta explícita; respostas inválidas são repetidas. */
    boolean confirmar(String acao) {
        if (janelas != null) return janelas.confirmar(acao);
        while (true) {
            String resposta=normalizar(texto(acao+" [S = sim / N = cancelar]: "));
            if (resposta.equals("S") || resposta.equals("SIM")) return true;
            if (resposta.equals("N") || resposta.equals("NAO")) {
                exibir_linha("Operação cancelada. Nenhum arquivo foi alterado.");
                return false;
            }
            exibir_linha("Digite S para confirmar ou N para cancelar.");
        }
    }

    /** Permite escolher pelo número exibido ou pelo nome, sem exigir decorar comandos. */
    String escolher(String mensagem, String... opcoes) {
        if (janelas != null) return selecionar(mensagem, opcoes, opcoes);
        while (true) {
            String resposta=texto(mensagem);
            for (int i=0;i<opcoes.length;i++)
                if (resposta.equals(String.valueOf(i+1)) || normalizar(resposta).equals(normalizar(opcoes[i])))
                    return opcoes[i];
            exibir_linha("Escolha um número de 1 a "+opcoes.length+" ou um dos nomes exibidos.");
        }
    }

    private String selecionar(String pergunta, String[] rotulos, String[] valores) {
        String escolha = janelas.selecao(pergunta, rotulos);
        if (escolha == null) throw new EntradaCancelada();
        return valores[Arrays.asList(rotulos).indexOf(escolha)];
    }

    private String escolher_opcao() {
        if (janelas == null) {
            mostrar_menu();
            return texto("Opção: ");
        }
        janelas.titulo = "Além da Urna";
        String escolha = janelas.selecao("Presidente 2026 | São Paulo\nEscolha uma operação.",
                "1 - Listar registros", "2 - Incluir resultado", "3 - Alterar votos",
                "4 - Excluir último incluído", "5 - Ordenar registros", "6 - Pesquisar",
                "7 - Consultar fronteiras", "8 - Listar cidades", "9 - Comparar desempenho", "0 - Sair");
        if (escolha == null) return "0";
        janelas.titulo = escolha.substring(4);
        return escolha.substring(0, 1);
    }

    void mostrar_registro(Registro r) {
        exibir_linha("ID: "+r.id+" | Ordem de inclusão: "+r.ordem_inclusao);
        exibir_linha("Eleição: "+r.ano+" | Turno: "+r.turno);
        exibir_linha("Município: "+r.municipio+"/"+r.uf+" | Código: "+r.codigo_municipio);
        exibir_linha("Candidato: "+r.candidato+" | Número: "+r.numero+" | Partido: "+r.partido);
        exibir_linha("Votos: "+r.votos);
    }

    /** Prepara a inclusão sem modificar a lista: a gravação depende da confirmação. */
    Registro preparar_inclusao(DequeRegistros d, String campos) {
        if (campos.split(";",-1).length!=9)
            throw new IllegalArgumentException("Informe exatamente 9 campos separados por ponto e vírgula (;). Veja o formato e o exemplo da opção 2.");
        long id=0,ordem=0;
        for (No n=d.inicio;n!=null;n=n.proximo) {
            id=Math.max(id,n.valor.id); ordem=Math.max(ordem,n.valor.ordem_inclusao);
        }
        String[] partes=campos.split(";",-1);
        for (int i=0;i<partes.length;i++) partes[i]=partes[i].trim();
        partes[2]=partes[2].toUpperCase(Locale.ROOT);
        Registro novo=new Registro(Math.addExact(id,1)+";"+Math.addExact(ordem,1)+";"+String.join(";",partes));
        for (No n=d.inicio;n!=null;n=n.proximo)
            if (n.valor.chave().equals(novo.chave()))
                throw new IllegalArgumentException("Esse candidato já possui resultado nesse município, ano e turno. Para mudar os votos, use a opção 3.");
        return novo;
    }

    /** A cronologia, e não a posição física, identifica quem será removido por LIFO. */
    Registro ultimo_incluido(DequeRegistros d) {
        Registro ultimo=null;
        for (No n=d.inicio;n!=null;n=n.proximo)
            if (ultimo==null || n.valor.ordem_inclusao>ultimo.ordem_inclusao) ultimo=n.valor;
        return ultimo;
    }

    void mostrar_menu() {
        exibir_linha("\n============================================================");
        exibir_linha("ALÉM DA URNA | Presidente 2026 | São Paulo");
        exibir_linha("Alterações são feitas apenas na base local de estudo.");
        exibir_linha("1 - Listar registros: mostrar os resultados de entrada.txt.");
        exibir_linha("2 - Incluir resultado: cadastrar candidato e votos por município.");
        exibir_linha("3 - Alterar votos: localizar um ID e mudar sua quantidade de votos.");
        exibir_linha("4 - Excluir último incluído: remover um registro pela regra LIFO.");
        exibir_linha("5 - Ordenar: escolher algoritmo e campo e gerar um arquivo de saída.");
        exibir_linha("6 - Pesquisar: procurar um ID ou um nome de candidato/município.");
        exibir_linha("7 - Consultar países: mostrar as fronteiras de um país.");
        exibir_linha("8 - Listar cidades: mostrar a ordem alfabética e gerar um arquivo.");
        exibir_linha("9 - Comparar desempenho: medir os quatro algoritmos de ordenação.");
        exibir_linha("0 - Sair.");
        exibir_linha("Digite o número da opção e pressione Enter.");
    }

    void menu() throws Exception {
        while (true) {
            try {
                String opcao=escolher_opcao();
                switch (opcao) {
                    case "0" -> {
                        if (janelas == null) exibir_linha("Programa encerrado.");
                        return;
                    }
                    case "1" -> listar_registros();
                    case "2" -> incluir_resultado();
                    case "3" -> alterar_votos();
                    case "4" -> excluir_ultimo();
                    case "5" -> ordenar_registros();
                    case "6" -> pesquisar_registros();
                    case "7" -> consultar_pais();
                    case "8" -> listar_cidades();
                    case "9" -> comparar_desempenho();
                    default -> exibir_linha("Opção inválida. Digite um número de 0 a 9, como 2 para incluir ou 6 para pesquisar.");
                }
                if (janelas != null) janelas.resultado(JOptionPane.INFORMATION_MESSAGE);
            } catch (EntradaCancelada e) {
                // Fechar ou cancelar uma entrada volta ao menu sem concluir a operação.
                janelas.mensagens.setLength(0);
            } catch (NoSuchElementException e) {
                if (janelas != null) {
                    mostrar_erro("Não foi possível concluir: " + e.getMessage());
                    continue;
                }
                exibir_linha("\nEntrada encerrada. Programa finalizado; operações não confirmadas foram canceladas.");
                return;
            } catch (NumberFormatException e) {
                mostrar_erro("Número inválido. Use somente inteiros, sem pontos ou vírgulas: por exemplo, 1500.");
            } catch (Exception e) { mostrar_erro("Não foi possível concluir: "+e.getMessage()); }
        }
    }

    private void listar_registros() throws Exception {
        exibir_linha("\nLISTAR REGISTROS | A listagem pode ser longa. Para encontrar um nome ou ID, use a opção 6.");
        medir("READ",()-> {
            DequeRegistros d=ler(entrada);
            for (No n=d.inicio;n!=null;n=n.proximo) exibir_linha(n.valor);
            return "Total: "+d.tamanho+" registros. O número após ID identifica cada cadastro.";
        });
    }

    private void incluir_resultado() throws Exception {
        exibir_linha("\nINCLUIR RESULTADO | Adiciona um resultado de candidato por município em entrada.txt.");
        exibir_linha("Digite os 9 campos em UMA linha, separados por ponto e vírgula (;):");
        exibir_linha("ano;turno;UF;codigo_municipio;municipio;numero;candidato;partido;votos");
        exibir_linha("Exemplo FICTÍCIO, apenas para testar o cadastro:");
        exibir_linha("2026;1;SP;99999;MUNICIPIO DE TESTE;13;CANDIDATO DE TESTE;TESTE;10");
        exibir_linha("O ID e a ordem de inclusão são automáticos. Consulte códigos existentes nas opções 1 ou 6.");
        String campos=texto("Dados do novo resultado: ");
        Registro previa=preparar_inclusao(ler(entrada),campos);
        String ponta;
        if (janelas != null) {
            ponta=selecionar("Onde inserir na lista?",
                    new String[]{"Início da lista", "Fim da lista"}, new String[]{"I","F"});
        } else {
            while (true) {
                ponta=normalizar(texto("Onde inserir na lista? I = início / F = fim: "));
                if (ponta.equals("I") || ponta.equals("F")) break;
                exibir_linha("Digite I ou F. A escolha muda a posição na lista, não a quantidade de votos.");
            }
        }
        final boolean no_inicio=ponta.equals("I");
        exibir_linha("\nConfira o registro que será incluído:"); mostrar_registro(previa);
        exibir_linha("Posição escolhida: "+(no_inicio ? "início" : "fim")+" da lista.");
        if (!confirmar("Confirma incluir este resultado e salvar em entrada.txt?")) return;
        medir("CREATE",()-> {
            DequeRegistros d=ler(entrada); Registro novo=preparar_inclusao(d,campos);
            if (!novo.linha().equals(previa.linha()))
                throw new IllegalStateException("A base mudou durante a confirmação. Inicie a inclusão novamente.");
            d.incluir(novo,no_inicio); gravar(entrada,d.vetor()); return "Resultado incluído e salvo: "+novo;
        });
    }

    private void alterar_votos() throws Exception {
        exibir_linha("\nALTERAR VOTOS | Muda somente os votos de um registro já cadastrado.");
        exibir_linha("Encontre seu ID nas opções 1 ou 6. Exemplo de ID: 1.");
        long id=numero("ID do registro que deseja alterar: ");
        Registro previa=ler(entrada).buscar(id);
        if (previa==null) throw new IllegalArgumentException("ID não encontrado. Consulte as opções 1 ou 6.");
        exibir_linha("\nRegistro encontrado:"); mostrar_registro(previa);
        long votos=numero("Novo total de votos (inteiro sem pontos; exemplo: 1500): ");
        if (votos<0) throw new IllegalArgumentException("Votos não podem ser negativos.");
        exibir_linha("Alteração proposta: "+previa.votos+" -> "+votos+" votos.");
        if (!confirmar("Confirma alterar os votos do ID "+id+" e salvar em entrada.txt?")) return;
        medir("UPDATE",()-> {
            DequeRegistros d=ler(entrada); Registro r=d.buscar(id);
            if (r==null || !r.linha().equals(previa.linha()))
                throw new IllegalStateException("O registro mudou durante a confirmação. Consulte-o novamente.");
            r.votos=votos; gravar(entrada,d.vetor()); return "Alterado na cópia de estudo: "+r;
        });
    }

    private void excluir_ultimo() throws Exception {
        exibir_linha("\nEXCLUIR ÚLTIMO INCLUÍDO | LIFO: o último a entrar é o primeiro a sair.");
        exibir_linha("Remove UM registro, mesmo que ele tenha sido inserido no início da lista.");
        Registro previa=ultimo_incluido(ler(entrada));
        if (previa==null) { exibir_linha("A base está vazia. Não há registro para excluir."); return; }
        exibir_linha("\nEste é o registro que será excluído:"); mostrar_registro(previa);
        if (!confirmar("Confirma excluir este registro de entrada.txt?")) return;
        medir("DELETE_LIFO",()-> {
            DequeRegistros d=ler(entrada); Registro atual=ultimo_incluido(d);
            if (atual==null || !atual.linha().equals(previa.linha()))
                throw new IllegalStateException("O último incluído mudou durante a confirmação. Consulte-o novamente.");
            Registro r=d.remover_por_ordem(true); gravar(entrada,d.vetor());
            return "Registro excluído: "+r+". O arquivo entrada.txt foi preservado.";
        });
    }

    private void ordenar_registros() throws Exception {
        exibir_linha("\nORDENAR | Gera uma cópia em ordem crescente, sem reordenar entrada.txt.");
        exibir_linha("Algoritmos: 1 = bubblesort (troca); 2 = insertionsort (inserção);");
        exibir_linha("3 = selectionsort (seleção); 4 = mergesort (intercalação).");
        String algoritmo=escolher(janelas == null ? "Número ou nome do algoritmo: " : "Selecione o algoritmo.",algoritmos);
        exibir_linha("Campos: 1 = votos; 2 = municipio; 3 = candidato; 4 = id.");
        String campo=escolher(janelas == null ? "Número ou nome do campo: " : "Selecione o campo.","votos","municipio","candidato","id");
        exibir_linha("Será gerado "+algoritmo+".txt em ordem crescente por "+campo+", com desempate por ID.");
        if (Files.exists(Path.of(algoritmo+".txt"))) exibir_linha("O arquivo de saída existente será substituído.");
        if (!confirmar("Confirma ordenar e gravar esse arquivo?")) return;
        medir("SORT_"+algoritmo+"_"+campo,()-> {
            Registro[] v=ler(entrada).vetor(); ordenar(v,algoritmo,campo);
            gravar(Path.of(algoritmo+".txt"),v); return "Gerado "+algoritmo+".txt com "+v.length+" registros em ordem crescente por "+campo;
        });
    }

    private void pesquisar_registros() throws Exception {
        exibir_linha("\nPESQUISAR | Consulta registros sem alterar os arquivos.");
        exibir_linha("Arquivos: 1 = entrada.txt; 2 = bubblesort.txt; 3 = insertionsort.txt;");
        exibir_linha("4 = selectionsort.txt; 5 = mergesort.txt.");
        String nome=escolher(janelas == null ? "Número ou nome do arquivo: " : "Selecione o arquivo.","entrada.txt","bubblesort.txt","insertionsort.txt","selectionsort.txt","mergesort.txt");
        exibir_linha("L = procurar ID por busca linear (funciona em qualquer ordem).");
        exibir_linha("B = procurar ID por busca binária (exige arquivo ordenado por id na opção 5).");
        exibir_linha("T = procurar parte do nome do candidato ou município, como Lula ou São Paulo.");
        String modo=janelas == null ? normalizar(texto("Tipo de pesquisa (L / B / T): "))
                : selecionar("Selecione o tipo de pesquisa.",
                        new String[]{"ID (busca linear)", "ID (busca binária)", "Nome (candidato ou município)"},
                        new String[]{"L","B","T"});
        if (!List.of("L","B","T").contains(modo)) throw new IllegalArgumentException("Escolha L, B ou T.");
        String termo=texto(modo.equals("T") ? "Nome ou trecho a pesquisar: " : "ID numérico a pesquisar (exemplo: 1): ");
        if (termo.isBlank()) throw new IllegalArgumentException("Informe um ID ou texto para pesquisar.");
        medir("SEARCH_"+modo,()-> {
            DequeRegistros d=ler(Path.of(nome));
            if (!modo.equals("T")) {
                Registro encontrado=modo.equals("L") ? d.buscar(Long.parseLong(termo)) : busca_binaria_id(d.vetor(),Long.parseLong(termo));
                return encontrado==null ? "Nenhum registro encontrado para esse ID." : encontrado.toString();
            }
            int total=0;
            for (Registro r:d.vetor()) if (normalizar(r.candidato+" "+r.municipio).contains(normalizar(termo))) {
                exibir_linha(r); total++;
            }
            return total+" resultados.";
        });
    }

    private void consultar_pais() throws Exception {
        exibir_linha("\nCONSULTAR PAÍSES | Mostra os vizinhos terrestres cadastrados para um país.");
        exibir_linha("Digite apenas o nome, como Brasil, Peru ou Argentina. A consulta não altera os dados.");
        String pais=texto("País: ");
        if (pais.isBlank()) throw new IllegalArgumentException("Informe o nome do país.");
        medir("HASH_PAISES",()->"Consulta de "+pais+": "+carregar_paises().buscar(pais));
    }

    private void listar_cidades() throws Exception {
        exibir_linha("\nLISTAR CIDADES | Organiza as cidades cadastradas de A a Z usando a tabela hash.");
        exibir_linha("Mostra a lista e grava cidades_ordenadas.txt, substituindo a saída anterior, se existir.");
        if (!confirmar("Confirma gerar a lista de cidades?")) return;
        medir("HASH_CIDADES",()-> {
            List<String> cidades=carregar_cidades().listar();
            Files.write(Path.of("cidades_ordenadas.txt"),cidades,StandardCharsets.UTF_8);
            return String.join("\n",cidades)+"\nTotal: "+cidades.size()+" cidades. Arquivo cidades_ordenadas.txt salvo.";
        });
    }

    private void comparar_desempenho() throws Exception {
        exibir_linha("\nCOMPARAR DESEMPENHO | Mede os quatro algoritmos com 100, 1.000 e 3.000 registros.");
        exibir_linha("Usa a base preservada, com entradas aleatórias, ordenadas e inversas. Pode levar alguns instantes.");
        exibir_linha("Substitui resultados/benchmark.csv e resultados/ambiente.txt e recria os arquivos temporários do experimento.");
        if (!confirmar("Confirma iniciar a comparação de desempenho?")) return;
        medir("BENCHMARK",()-> { Desempenho.benchmark(); return "Comparação concluída. Consulte resultados/benchmark.csv"; });
    }

    void mostrar_erro(String mensagem) {
        if (janelas != null) janelas.mensagens.setLength(0);
        exibir_linha(mensagem);
        if (janelas != null) janelas.resultado(JOptionPane.ERROR_MESSAGE);
    }

    /** Converte somente a apresentação para ASCII; os registros e arquivos continuam em UTF-8. */
    private static String texto_tela(Object valor) {
        return Normalizer.normalize(String.valueOf(valor),Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replace('\u2013','-').replace('\u2014','-')
                .replace('\u2018','\'').replace('\u2019','\'')
                .replace('\u201c','"').replace('\u201d','"')
                .replace("\u2026","...")
                .replaceAll("[^\\x00-\\x7F]","?");
    }

    /** Mostra as mensagens da operação na próxima janela de entrada, confirmação ou resultado. */
    private static final class Janelas {
        final StringBuilder mensagens = new StringBuilder();
        String titulo = "Além da Urna";

        // O programa executa as operações e acessa os arquivos fora da thread do Swing.
        private static <T> T dialogo(Supplier<T> abrir) {
            FutureTask<T> tarefa = new FutureTask<>(abrir::get);
            try {
                SwingUtilities.invokeAndWait(tarefa);
                return tarefa.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("A interface foi interrompida.", e);
            } catch (java.lang.reflect.InvocationTargetException | java.util.concurrent.ExecutionException e) {
                throw new IllegalStateException("Não foi possível abrir a janela.", e);
            }
        }

        private String recolher(String pergunta) {
            String texto = (mensagens.toString() + pergunta).strip();
            mensagens.setLength(0);
            return texto;
        }

        private static Object conteudo(String texto) {
            if (texto.length() < 85 && !texto.contains("\n")) return texto;
            JTextArea area = new JTextArea(texto);
            area.setEditable(false);
            area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
            area.setCaretPosition(0);
            JScrollPane painel = new JScrollPane(area);
            int linhas = Math.max(texto.split("\n", -1).length, (texto.length() + 65) / 66);
            painel.setPreferredSize(new Dimension(560, Math.min(400, 30 + 19 * Math.min(linhas, 20))));
            return painel;
        }

        String entrada(String pergunta) {
            String texto = recolher(pergunta);
            String resposta = dialogo(() -> JOptionPane.showInputDialog(
                    null, conteudo(texto), titulo, JOptionPane.QUESTION_MESSAGE));
            if (resposta == null) throw new EntradaCancelada();
            return resposta.trim();
        }

        String selecao(String pergunta, String... opcoes) {
            String texto = recolher(pergunta);
            return dialogo(() -> {
                String convite = "Escolha uma opção";
                String[] itens = new String[opcoes.length + 1];
                itens[0] = convite;
                System.arraycopy(opcoes, 0, itens, 1, opcoes.length);
                String escolha;
                do {
                    escolha = (String) JOptionPane.showInputDialog(
                            null, conteudo(texto), titulo, JOptionPane.QUESTION_MESSAGE,
                            null, itens, convite);
                } while (convite.equals(escolha));
                return escolha;
            });
        }

        boolean confirmar(String pergunta) {
            String texto = recolher(pergunta);
            String[] botoes = {"Confirmar", "Cancelar"};
            return dialogo(() -> JOptionPane.showOptionDialog(null, conteudo(texto), titulo,
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, botoes, botoes[1])) == 0;
        }

        void resultado(int tipo) {
            String texto = recolher("");
            if (texto.isEmpty()) return;
            dialogo(() -> {
                JOptionPane.showMessageDialog(null, conteudo(texto), titulo, tipo);
                return null;
            });
        }
    }
}
