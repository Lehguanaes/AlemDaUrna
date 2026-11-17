<div align="center"> 
<img width="3000" alt="Além da Urna - slide 1" src="https://github.com/user-attachments/assets/5c6416f5-f17a-4c94-b73a-d282b5969b1b" />
</div>

# Além da Urna

**Uma aplicação em Java para explorar os resultados do primeiro turno presidencial de 2026 em São Paulo.**

Projeto de Estruturas de Dados do 4º bimestre de Análise e Desenvolvimento de Sistemas, na Fatec Zona Leste, com orientação da professora Eliane Oliveira Santiago.

## Sobre o projeto

Quantos votos uma candidatura recebeu em cada município? O **Além da Urna** organiza os dados do TSE para facilitar essa consulta e demonstrar como estruturas de dados afetam a gravação, a busca e o tempo de processamento.

A base reúne **7.740 registros**, de **645 municípios** e **12 candidaturas**. Cada linha representa o total de votos de uma candidatura em um município. O programa usa uma cópia local para os testes de inclusão, alteração e exclusão.

## Como executar

### NetBeans

1. Use o **JDK 17 ou superior** e abra a pasta que contém o [pom.xml](pom.xml).
2. Clique em **Limpar e Construir** e pressione **F6**.
3. Na janela **Além da Urna**, selecione uma opção e clique em **OK**.

Na primeira execução, a classe principal `com.mycompany.alemdaurna.AlemDaUrna` cria `entrada.txt` a partir da [base original](dados/base_original.txt).

### Windows ou terminal

No Windows, abra [compilar.bat](compilar.bat) e, após a compilação, [executar.bat](executar.bat). Se preferir usar Maven pelo terminal, execute na raiz do projeto:

~~~powershell
mvn clean package
java -jar target/AlemDaUrna-1.0-SNAPSHOT.jar
~~~

Acrescente `--console` ao comando `java -jar` para abrir o menu em modo texto.

## O que experimentar

| Opções | O que o programa faz |
| --- | --- |
| **1 a 4 · Leitura e cadastro** | Lê `entrada.txt`, insere em qualquer ponta do deque e permite alterar os votos pelo ID. A exclusão segue LIFO e remove o registro mais recente pela ordem de inclusão. |
| **5 · Ordenação** | Aplica Bubble Sort, Insertion Sort, Selection Sort ou Merge Sort e grava a saída com o nome do algoritmo. |
| **6 · Pesquisa** | Busca por nome ou ID na entrada ou em uma saída. Para usar a busca binária, ordene um arquivo por ID na opção 5. |
| **7 e 8 · Tabelas hash** | Consulta fronteiras terrestres por país e lista cidades em ordem alfabética, salvando `cidades_ordenadas.txt`. |
| **9 · Desempenho** | Compara os quatro algoritmos em diferentes tamanhos e ordens iniciais. |

As operações que alteram a base pedem confirmação. Para demonstrar a relação entre ordenação e pesquisa, use **Merge Sort por ID** na opção 5 e busque o **ID 1** em `mergesort.txt` pela opção 6.

## Organização do código

| Arquivos | Responsabilidade |
| --- | --- |
| [AlemDaUrna](src/main/java/com/mycompany/alemdaurna/AlemDaUrna.java) e [Menu](src/main/java/com/mycompany/alemdaurna/Menu.java) | Inicialização, menu Swing e operações. |
| [Registro](src/main/java/com/mycompany/alemdaurna/Registro.java), [DequeRegistros](src/main/java/com/mycompany/alemdaurna/DequeRegistros.java) e [ArquivosDados](src/main/java/com/mycompany/alemdaurna/ArquivosDados.java) | Modelo dos dados, lista duplamente ligada e leitura e gravação em UTF-8. |
| [Ordenacao](src/main/java/com/mycompany/alemdaurna/Ordenacao.java) e [Pesquisa](src/main/java/com/mycompany/alemdaurna/Pesquisa.java) | Quatro algoritmos de ordenação e busca binária. |
| [TabelasHash](src/main/java/com/mycompany/alemdaurna/TabelasHash.java) e [Normalizacao](src/main/java/com/mycompany/alemdaurna/Normalizacao.java) | Índices geográficos, encadeamento e comparação de nomes. |
| [Desempenho](src/main/java/com/mycompany/alemdaurna/Desempenho.java) e [ImportarTse](src/main/java/com/mycompany/alemdaurna/ImportarTse.java) | Medição dos tempos e reprodução da base eleitoral. |

## Dados e resultados

A [proveniência da base](dados/proveniencia.json) registra a origem, a data da coleta e o recorte aplicado ao CSV. As referências para fronteiras e cidades estão em [fontes geográficas](dados/fontes_geograficas.txt). [Portal de Dados Abertos do TSE](https://dadosabertos.tse.jus.br/dataset/resultados-2026)

Para reproduzir o recorte, execute `ImportarTse.java` com **Executar Arquivo** no NetBeans. Após compilar o JAR, o mesmo importador pode ser executado pelo terminal:

~~~powershell
java -cp target/AlemDaUrna-1.0-SNAPSHOT.jar com.mycompany.alemdaurna.ImportarTse
~~~

O importador gera `dados/base_reimportada.txt`. A versão arquivada do experimento está nas [medições do benchmark](resultados/benchmark.csv), no [resumo das medianas](resultados/benchmark_resumo.csv) e na [descrição do ambiente](resultados/ambiente.txt). Ao executar a opção 9, o programa regrava `benchmark.csv` e `ambiente.txt`. As operações cronometradas do menu são registradas localmente em `resultados/desempenho.csv`.

## ✨ Desenvolvedores do Projeto

<table align="center">
  <tr>
    <td align="center">
      <a href="https://github.com/ErickFLM">
        <img src="https://avatars.githubusercontent.com/u/131476329?v=4" width="135" alt="Erick Ferreira no GitHub"><br>
        <sub><b>Erick Ferreira</b></sub>
      </a><br>
      <sub><a href="mailto:erickferreiralima001@gmail.com">erickferreiralima001@gmail.com</a></sub>
    </td>
    <td align="center">
      <a href="https://github.com/Lehguanaes">
        <img src="https://avatars.githubusercontent.com/u/125403978?v=4" width="135" alt="Letícia Guanaes no GitHub"><br>
        <sub><b>Letícia Guanaes</b></sub>
      </a><br>
      <sub><a href="mailto:lehguanaes@gmail.com">lehguanaes@gmail.com</a></sub>
    </td>
  </tr>
</table>

<p align="center">
  <sub>Projeto desenvolvido em ambiente acadêmico.<br>
  ✨ Fatec Zona Leste ✨</sub>
</p>
