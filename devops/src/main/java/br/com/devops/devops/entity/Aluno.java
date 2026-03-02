package br.com.devops.devops.entity;
import jakarta.persistence.Entity;

@Entity
public class Aluno {
    private int idAluno;
    private String nomeAluno;
    private String emailAluno;
    private String telefoneAluno;
    private String enderecoAluno;
    private String cpfAluno;
    private String raAluno;


    public int getIdAluno() {
        return idAluno;
    }
    public void setIdAluno(int idAluno) { 
        this.idAluno = idAluno;
    }
    public String getNomeAluno() {
        return nomeAluno;
    }
    public void setNomeAluno(String nomeAluno) {
        this.nomeAluno = nomeAluno;
    }
    public String getEmailAluno() {
        return emailAluno;
    }
    public void setEmailAluno(String emailAluno) {
        this.emailAluno = emailAluno;
    }
    public String getTelefoneAluno() {
        return telefoneAluno;
    }
    public void setTelefoneAluno(String telefoneAluno) {
        this.telefoneAluno = telefoneAluno;
    }
    public String getEnderecoAluno() {
        return enderecoAluno;
    }
    public void setEnderecoAluno(String enderecoAluno) {
        this.enderecoAluno = enderecoAluno;
    }
    public String getCpfAluno() {
        return cpfAluno;
    }
    public void setCpfAluno(String cpfAluno) {
        this.cpfAluno = cpfAluno;
    }
    public String getRaAluno() {
        return raAluno;
    }
    public void setRaAluno(String raAluno) {
        this.raAluno = raAluno;
    }

    

}
