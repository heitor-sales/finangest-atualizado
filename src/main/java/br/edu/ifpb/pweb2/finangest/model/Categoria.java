package br.edu.ifpb.pweb2.finangest.model;

import java.io.Serializable;

import jakarta.persistence.Id;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Categoria implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    
    @Enumerated(EnumType.STRING)
    private NaturezaCategoria natureza; //ENTRADA, SAÍDA e INVESTIMENTO

    // private String natureza;  //ENTRADA, SAÍDA e INVESTIMENTO

    private Integer ordem;  //1, 2 ou 3

    private boolean ativa = true; //se a categoria está ativa ou não
   
    // private List<Transacao> transacoes;

}
