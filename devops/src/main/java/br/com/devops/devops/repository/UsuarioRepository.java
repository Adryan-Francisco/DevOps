package br.com.devops.devops.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.devops.devops.entity.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByLoginUsuario(String loginUsuario);
    Optional<Usuario> findByEmailUsuario(String emailUsuario);
    Optional<Usuario> findByTokenResetSenha(String tokenResetSenha);
    List<Usuario> findAllByOrderByNomeUsuario();
    boolean existsByLoginUsuarioIgnoreCaseAndIdUsuarioNot(String loginUsuario, Integer idUsuario);
    boolean existsByEmailUsuarioIgnoreCaseAndIdUsuarioNot(String emailUsuario, Integer idUsuario);
}
