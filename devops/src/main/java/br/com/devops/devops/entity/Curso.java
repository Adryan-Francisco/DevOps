package br.com.devops.devops.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idCurso;

    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 40, message = "O nome deve ter no máximo 40 caracteres.")
    @Column(nullable = false, length = 40)
    private String nomeCurso;

    @NotBlank(message = "O período é obrigatório.")
    @Size(max = 40, message = "O período deve ter no máximo 40 caracteres.")
    @Column(nullable = false, length = 40)
    private String periodoCurso;

    @NotNull(message = "A carga horária é obrigatória.")
    @Positive(message = "A carga horária deve ser maior que zero.")
    @Column(nullable = false)
    private Integer cargahorariaCurso;
}
