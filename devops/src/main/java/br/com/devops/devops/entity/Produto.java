package br.com.devops.devops.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "produto")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProduto;

    @Column(nullable = false, length = 100)
    private String nomeProduto;

    @Column(nullable = false, length = 255)
    private String descricaoProduto;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precoProduto;

    @Column(nullable = false)
    private Integer quantidadeEstoque;

    @Lob
    @Column(name = "foto")
    private byte[] foto;

    @Column(name = "tipo_foto", length = 50)
    private String tipoFoto;
}
