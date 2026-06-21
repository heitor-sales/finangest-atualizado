package br.edu.ifpb.pweb2.finangest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.NumberFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "conta")
@Entity
public class Transacao implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String descricao; //ex: “Pagamento do curso de desenho”

    @NumberFormat(pattern = "###,##0.00")
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    private TipoMovimento movimento; //crédito ou débito

    // private String movimento; //crédito ou débito

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comentario_id", unique = true)
    private Comentario comentario; //usado para melhor descrever as transações, mas não é obrigatório
    
    // private String comentario; //usado para melhor descrever as transações, mas não é obrigatório

    private LocalDate data;

    private Categoria categoria; //Salário, Invetimento, Saúde e Remédios e etc.

    // 
    @Transient // Este campo não será persistido no banco de dados
    private BigDecimal saldoParcial;
    // 
    
    @ManyToOne
    @JoinColumn(name = "id_conta")
    private Conta conta;
    
}
