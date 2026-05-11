package br.com.devops.devops.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.devops.devops.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
}