package com.mycompany.alemdaurna;

/** Registro não homogêneo: combina números inteiros, longos e strings. */
final class Registro {
    long id, ordem_inclusao, votos;
    int ano, turno, codigo_municipio, numero;
    String uf, municipio, candidato, partido;
    Registro(String linha) {
        String[] c = linha.split(";", -1);
        if (c.length != 11) throw new IllegalArgumentException("Registro deve ter 11 campos.");
        id = Long.parseLong(c[0]); ordem_inclusao = Long.parseLong(c[1]);
        ano = Integer.parseInt(c[2]); turno = Integer.parseInt(c[3]); uf = c[4];
        codigo_municipio = Integer.parseInt(c[5]); municipio = c[6];
        numero = Integer.parseInt(c[7]); candidato = c[8]; partido = c[9];
        votos = Long.parseLong(c[10]);
        if (id <= 0 || ordem_inclusao <= 0 || votos < 0 || ano < 1900 || turno < 1 || turno > 2
                || uf.length() != 2 || codigo_municipio <= 0 || numero <= 0
                || municipio.isBlank() || candidato.isBlank() || partido.isBlank())
            throw new IllegalArgumentException("Campos inválidos ou votos negativos.");
    }
    String linha() {
        return id+";"+ordem_inclusao+";"+ano+";"+turno+";"+uf+";"+codigo_municipio+";"+municipio
                +";"+numero+";"+candidato+";"+partido+";"+votos;
    }
    String chave() { return ano+"/"+turno+"/"+uf+"/"+codigo_municipio+"/"+numero; }
    public String toString() { return "ID "+id+" | "+municipio+"/"+uf+" | "+candidato+" ("+numero+") | "+votos+" votos"; }
}
