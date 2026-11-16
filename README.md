<<<<<<< HEAD
# Além da Urna

Projeto de Estruturas de Dados, 4º bimestre. Faculdade de Tecnologia da Zona Leste.

**Autores** Letícia Guanaes Moreira e Erick Ferreira Lima.  
**Professora** Eliane Oliveira Santiago.

O programa organiza resultados do primeiro turno presidencial de 2026 em São Paulo, com dados do TSE agregados por município e candidato. Usa um deque implementado com lista duplamente ligada para manipular os registros. Também compara quatro algoritmos de ordenação e aplica tabelas hash às consultas de fronteiras e à listagem alfabética de cidades.

## Executar no NetBeans

1. Use o JDK 17 ou superior e abra a pasta do projeto que contém o `pom.xml`.
2. Selecione **Limpar e Construir** e pressione **F6**.
3. Na janela **Além da Urna**, selecione uma operação e clique em **OK**.

A classe principal é `com.mycompany.alemdaurna.AlemDaUrna`. Na primeira execução, ela cria o arquivo `entrada.txt` a partir de `dados/base_original.txt`. A inclusão, a alteração e a exclusão pedem confirmação antes de gravar.

No Windows, use `compilar.bat` para gerar o JAR e `executar.bat` para abrir o menu. Em um terminal com Maven, rode os comandos na pasta do projeto:

```powershell
mvn clean package
java -jar target/AlemDaUrna-1.0-SNAPSHOT.jar
```

## Onde encontrar cada parte

O código Java fica em [src/main/java/com/mycompany/alemdaurna](src/main/java/com/mycompany/alemdaurna).

| Arquivo | Responsabilidade |
| --- | --- |
| [AlemDaUrna.java](src/main/java/com/mycompany/alemdaurna/AlemDaUrna.java) | Entrada principal, inicialização e modos de execução |
| [Menu.java](src/main/java/com/mycompany/alemdaurna/Menu.java) | Operações, janelas do JOptionPane, confirmações e resultados |
| [Registro.java](src/main/java/com/mycompany/alemdaurna/Registro.java) | Estrutura não homogênea do resultado eleitoral |
| [DequeRegistros.java](src/main/java/com/mycompany/alemdaurna/DequeRegistros.java) | Nós, inserção nas pontas, busca linear e retirada pela ordem de inclusão |
| [ArquivosDados.java](src/main/java/com/mycompany/alemdaurna/ArquivosDados.java) | Leiaute, leitura, validação e gravação em UTF-8 |
| [Ordenacao.java](src/main/java/com/mycompany/alemdaurna/Ordenacao.java) | Bubble Sort, Insertion Sort, Selection Sort e Merge Sort |
| [Pesquisa.java](src/main/java/com/mycompany/alemdaurna/Pesquisa.java) | Busca binária em arquivo ordenado por ID |
| [TabelasHash.java](src/main/java/com/mycompany/alemdaurna/TabelasHash.java) | Fronteiras e cidades, com encadeamento para colisões |
| [Desempenho.java](src/main/java/com/mycompany/alemdaurna/Desempenho.java) | Registro de tempos e comparação dos algoritmos |
| [Normalizacao.java](src/main/java/com/mycompany/alemdaurna/Normalizacao.java) | Comparação de nomes sem distinguir acentos ou maiúsculas |
| [ImportarTse.java](src/main/java/com/mycompany/alemdaurna/ImportarTse.java) | Entrada auxiliar para reproduzir a base a partir do CSV |

## Roteiro de demonstração

- **Opções 2, 3 e 4** permitem incluir, alterar votos e excluir. O deque aceita inclusão no início ou no fim. A exclusão LIFO remove o último registro incluído conforme a ordem persistida.
- **Opção 5** ordena pelo campo selecionado e gera um arquivo com o nome do algoritmo. A **opção 6** pesquisa na entrada ou em uma saída. Para busca binária, primeiro ordene por ID.
- **Opções 7 e 8** consultam fronteiras e listam cidades de A a Z. A função de cidades indexa pela inicial normalizada e cada balde mantém uma lista ordenada.
- **Opção 9** executa a comparação dos quatro algoritmos. O programa grava as medições em `resultados/benchmark.csv` e registra o tempo das operações em `resultados/desempenho.csv`.

## Base e importador

O recorte tem 7.740 registros, correspondentes a 645 municípios e 12 candidaturas. A origem, a data de coleta e os critérios de agregação estão em [dados/proveniencia.json](dados/proveniencia.json). [Portal de Dados Abertos do TSE — Resultados 2026](https://dadosabertos.tse.jus.br/dataset/resultados-2026)

Para reproduzir a base, execute `ImportarTse.java` pela opção **Executar Arquivo** no NetBeans ou rode o comando abaixo a partir da raiz do projeto após compilar. A saída é gravada em `dados/base_reimportada.txt`.

```powershell
java -cp target/AlemDaUrna-1.0-SNAPSHOT.jar com.mycompany.alemdaurna.ImportarTse
```

O executável principal também aceita `--console`, `--gerar-saidas` e `--benchmark`. Por exemplo, `java -jar target/AlemDaUrna-1.0-SNAPSHOT.jar --console` abre o menu no terminal.
=======
# AlemDaUrn
>>>>>>> origin/main
