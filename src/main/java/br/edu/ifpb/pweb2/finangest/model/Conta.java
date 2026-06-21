package br.edu.ifpb.pweb2.finangest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
public class Conta implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(min = 4, message = "Número da conta deve ter no mínimo 4 dígitos.")
    private String numero;

    private String descricao; // ex: “Minha conta no BB”

    @Enumerated(EnumType.STRING) // Armazena o nome do enum (CORRENTE, CARTAO_CREDITO) no BD
    @NotNull(message = "Tipo obrigatório")
    private TipoConta tipo; //ex: “CORRENTE”

   

    private Integer diaFechamento; //apenas para tipo == cartão

    @OneToMany(mappedBy = "conta", fetch = FetchType.EAGER)
    private Set<Transacao> transacoes = new HashSet<Transacao>();

    @NotNull(message = "Correntista não foi escolhido.")
    private Correntista correntista;

    public Conta(Correntista correntista){
        this.correntista=correntista;
    }

    // public BigDecimal getSaldo() {
    //     BigDecimal total = BigDecimal.ZERO;
    //     for (Transacao t : this.transacoes) {
    //         total = total.add(t.getValor());
    //     }
    //     return total;
    // }

    public BigDecimal getSaldo() {
        BigDecimal total = BigDecimal.ZERO;
        if (this.transacoes != null) {
            for (Transacao t : this.transacoes) {
                // Se a transação for de CRÉDITO, adiciona ao total
                if (t.getMovimento() == TipoMovimento.CREDITO) {
                    total = total.add(t.getValor());
                } 
                // Se a transação for de DÉBITO, subtrai do total
                else if (t.getMovimento() == TipoMovimento.DEBITO) {
                    total = total.subtract(t.getValor());
                }
                // Adicione outras condições se houver outros tipos de movimento
            }
        }
        return total;
    }

    // public void addTransacao(Transacao transacao) {
    //     this.transacoes.add(transacao);
    //     transacao.setConta(this);
    // }
}