package br.edu.ifpb.pweb2.finangest.model;

public enum TipoConta {
    
    CORRENTE("Corrente"),
    CARTAO_CREDITO("Cartão de Crédito");

    private final String descricao;

    TipoConta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
