package com.mycompany.alemdaurna;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

/** Reproduz o recorte a partir do CSV oficial preservado. Não usa bibliotecas CSV externas. */
public class ImportarTse {
    // O arquivo TSE tem uma linha por registro, com campos entre aspas e separados por ;.
    // Aspas duplicadas dentro de um campo representam uma aspa literal.
    static List<String> campos(String linha) {
        List<String> resultado=new ArrayList<>();
        StringBuilder valor=new StringBuilder(); boolean aspas=false;
        for (int i=0;i<linha.length();i++) {
            char c=linha.charAt(i);
            if (c=='"') {
                if (aspas && i+1<linha.length() && linha.charAt(i+1)=='"') { valor.append('"'); i++; }
                else aspas=!aspas;
            } else if (c==';' && !aspas) { resultado.add(valor.toString()); valor.setLength(0); }
            else valor.append(c);
        }
        if (aspas) throw new IllegalArgumentException("Aspas não fechadas no CSV.");
        resultado.add(valor.toString()); return resultado;
    }
    public static void main(String[] args) throws Exception {
        Path fonte=Path.of("dados/tse_presidente_2026_BR.csv");
        Path destino=args.length>0 ? Path.of(args[0]) : Path.of("dados/base_reimportada.txt");
        Map<String,String[]> valores=new TreeMap<>();
        Map<String,Long> votos=new TreeMap<>();
        try (BufferedReader f=Files.newBufferedReader(fonte,StandardCharsets.ISO_8859_1)) {
            List<String> nomes=campos(f.readLine());
            Map<String,Integer> pos=new HashMap<>();
            for (int i=0;i<nomes.size();i++) pos.put(nomes.get(i),i);
            String linha;
            while ((linha=f.readLine())!=null) {
                List<String> c=campos(linha);
                if (c.size()!=nomes.size()) throw new IOException("Quantidade de campos inesperada.");
                if (!c.get(pos.get("ANO_ELEICAO")).equals("2026") || !c.get(pos.get("CD_CARGO")).equals("1") || !c.get(pos.get("NR_TURNO")).equals("1")
                        || !c.get(pos.get("SG_UF")).equals("SP")) continue;
                String codigo=c.get(pos.get("CD_MUNICIPIO")), numero=c.get(pos.get("NR_CANDIDATO"));
                String chave=codigo+"/"+numero;
                String[] registro={"2026","1","SP",codigo,c.get(pos.get("NM_MUNICIPIO")),numero,
                        c.get(pos.get("NM_URNA_CANDIDATO")),c.get(pos.get("SG_PARTIDO"))};
                valores.put(chave,registro);
                votos.merge(chave,Long.parseLong(c.get(pos.get("QT_VOTOS_NOMINAIS_VALIDOS"))),Long::sum);
            }
        }
        List<Registro> registros=new ArrayList<>(); int id=0;
        for (String chave:valores.keySet()) {
            id++;
            registros.add(new Registro(id+";"+id+";"+String.join(";",valores.get(chave))+";"+votos.get(chave)));
        }
        ArquivosDados.gravar(destino,registros.toArray(new Registro[0]));
        System.out.println("Recorte reproduzido: "+registros.size()+" registros em "+destino);
    }
}
