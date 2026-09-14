package br.com.devops.devops.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "professor")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_professor")
    private Integer idProfessor;

    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
    @Column(name = "nome_professor", nullable = false, length = 100)
    private String nomeProfessor;

    @NotBlank(message = "O telefone é obrigatório.")
    @Pattern(regexp = "\\(\\d{2}\\) \\d{4,5}-\\d{4}", message = "Informe o telefone no formato (00) 00000-0000.")
    @Column(name = "telefone_professor", nullable = false, length = 15)
    private String telefoneProfessor;

    @NotBlank(message = "A graduação é obrigatória.")
    @Size(max = 100, message = "A graduação deve ter no máximo 100 caracteres.")
    @Column(name = "graduacao_professor", nullable = false, length = 100)
    private String graduacaoProfessor;

    @NotBlank(message = "O RM é obrigatório.")
    @Size(max = 20, message = "O RM deve ter no máximo 20 caracteres.")
    @Column(name = "rm_professor", nullable = false, length = 20)
    private String rmProfessor;
}
