package br.edu.ifpb.pweb2.finangest.model;

public enum TipoMovimento {

    CREDITO("Crédito"),
    DEBITO("Débito");

    private final String descricao;

    TipoMovimento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
