package br.com.devops.devops.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.devops.devops.entity.Usuario;
import br.com.devops.devops.exception.RegraNegocioException;
import br.com.devops.devops.repository.UsuarioRepository;

@Service
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAllByOrderByNomeUsuario();
    }

    public Optional<Usuario> buscarPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorLogin(String login) {
        return usuarioRepository.findByLoginUsuario(login);
    }

    public boolean loginEmUso(String login, Integer idUsuario) {
        return usuarioRepository.existsByLoginUsuarioIgnoreCaseAndIdUsuarioNot(login, idOuZero(idUsuario));
    }

    public boolean emailEmUso(String email, Integer idUsuario) {
        return usuarioRepository.existsByEmailUsuarioIgnoreCaseAndIdUsuarioNot(email, idOuZero(idUsuario));
    }

    @Transactional
    public Usuario salvar(Usuario usuario) {
        if (usuario.getIdUsuario() == null) {
            usuario.setSenhaUsuario(passwordEncoder.encode(usuario.getSenhaUsuario()));
            return usuarioRepository.save(usuario);
        }

        Usuario usuarioExistente = usuarioRepository.findById(usuario.getIdUsuario())
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado."));
        usuarioExistente.setNomeUsuario(usuario.getNomeUsuario());
        usuarioExistente.setEmailUsuario(usuario.getEmailUsuario());
        usuarioExistente.setLoginUsuario(usuario.getLoginUsuario());
        usuarioExistente.setRoleUsuario(usuario.getRoleUsuario());

        if (usuario.getSenhaUsuario() != null && !usuario.getSenhaUsuario().isBlank()) {
            usuarioExistente.setSenhaUsuario(passwordEncoder.encode(usuario.getSenhaUsuario()));
        }
        return usuarioRepository.save(usuarioExistente);
    }

    @Transactional
    public void deletar(Integer id, String loginUsuarioLogado) {
        usuarioRepository.findById(id).ifPresent(usuario -> {
            if (usuario.getLoginUsuario().equalsIgnoreCase(loginUsuarioLogado)) {
                throw new RegraNegocioException("Você não pode excluir o próprio usuário.");
            }
            usuarioRepository.delete(usuario);
        });
    }

    // Ids começam em 1, então 0 representa "nenhum usuário" ao cadastrar
    private Integer idOuZero(Integer id) {
        return id == null ? 0 : id;
    }
}
