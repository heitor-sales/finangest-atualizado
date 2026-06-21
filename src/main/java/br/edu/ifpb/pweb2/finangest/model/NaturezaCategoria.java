package br.edu.ifpb.pweb2.finangest.model;

public enum NaturezaCategoria {

    ENTRADA("Entrada"),
    SAIDA("Saída"),
    INVESTIMENTO("Investimento");

    private final String descricao;

    NaturezaCategoria(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
