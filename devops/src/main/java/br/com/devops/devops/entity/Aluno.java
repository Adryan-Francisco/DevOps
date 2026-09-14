package br.com.devops.devops.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "aluno")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aluno")
    private Integer idAluno;

    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 40, message = "O nome deve ter no máximo 40 caracteres.")
    @Column(name = "nome_aluno", nullable = false, length = 40)
    private String nomeAluno;

    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "Informe um email válido.")
    @Size(max = 100, message = "O email deve ter no máximo 100 caracteres.")
    @Column(name = "email_aluno", length = 100)
    private String emailAluno;

    @NotBlank(message = "O telefone é obrigatório.")
    @Pattern(regexp = "\\(\\d{2}\\) \\d{4,5}-\\d{4}", message = "Informe o telefone no formato (00) 00000-0000.")
    @Column(name = "telefone_aluno", nullable = false, length = 15)
    private String telefoneAluno;

    @NotBlank(message = "O endereço é obrigatório.")
    @Size(max = 50, message = "O endereço deve ter no máximo 50 caracteres.")
    @Column(name = "endereco_aluno", nullable = false, length = 50)
    private String enderecoAluno;

    @NotBlank(message = "O CPF é obrigatório.")
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "Informe o CPF no formato 000.000.000-00.")
    @Column(name = "cpf_aluno", nullable = false, length = 14)
    private String cpfAluno;

    @NotBlank(message = "O RA é obrigatório.")
    @Size(max = 20, message = "O RA deve ter no máximo 20 caracteres.")
    @Column(name = "ra_aluno", nullable = false, length = 20)
    private String raAluno;

    @ManyToOne
    @JoinColumn(name = "id_curso_fk")
    private Curso curso;
}
