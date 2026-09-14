package br.com.devops.devops.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "disciplina")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Disciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_disciplina")
    private Integer idDisciplina;

    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
    @Column(name = "nome_disciplina", nullable = false, length = 100)
    private String nomeDisciplina;

    @NotBlank(message = "A sigla é obrigatória.")
    @Size(max = 10, message = "A sigla deve ter no máximo 10 caracteres.")
    @Column(name = "sigla_disciplina", nullable = false, length = 10)
    private String siglaDisciplina;

    @NotNull(message = "A carga horária é obrigatória.")
    @Positive(message = "A carga horária deve ser maior que zero.")
    @Column(name = "carga_horaria_disciplina", nullable = false)
    private Integer cargaHorariaDisciplina;

    @ManyToOne
    @JoinColumn(name = "id_professor", nullable = false, foreignKey = @ForeignKey(name = "fk_disciplina_professor"))
    private Professor professor;

    // Usa a mesma coluna id_curso de antes, agora como relacionamento com Curso
    @ManyToOne
    @JoinColumn(name = "id_curso", nullable = false)
    private Curso curso;
}
