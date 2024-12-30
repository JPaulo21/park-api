package com.jp.parkapi.service;

import com.jp.parkapi.entity.Usuario;
import com.jp.parkapi.exception.EntityNotFoundException;
import com.jp.parkapi.exception.PasswordInvalidException;
import com.jp.parkapi.exception.UsernameUniqueViolationException;
import com.jp.parkapi.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor //Cria um método construtor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    @Transactional // Indicia que o Spring tomara conta da parte da transação, sendo o spring q vai abrir e fechar a transação no banco de dados
    public Usuario salvar(Usuario usuario) {
        try {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            return usuarioRepository.save(usuario);
        } catch (DataIntegrityViolationException ex){
            throw new UsernameUniqueViolationException(String.format("Username %s já cadastrado!", usuario.getUsername()));
        }
    }


    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id){
        return usuarioRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Usuário id=%s não encontrado.", id))
        );
    }

    @Transactional
    public Usuario editarSenha(Long id, String senhaAtual, String novaSenha, String confirmaSenha) {
        if(!novaSenha.equals(confirmaSenha)){ // Senha e Confirmação de Senha são diferentes
            throw new PasswordInvalidException("Nova senha não confere com a confirmação de senha.");
        }

        Usuario user = buscarPorId(id);
        if(!passwordEncoder.matches(senhaAtual, user.getPassword())){ // Válida senha atual para realizar a troca
            throw new PasswordInvalidException("Senha atual inválida.");
        }

        user.setPassword(passwordEncoder.encode(novaSenha));
        return user;
    }

    @Transactional(readOnly = true)
    public List<Usuario> buscarTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(String.format("Usuário com '%s' não encontrado.", username))
        );
    }

    @Transactional(readOnly = true)
    public Usuario.Role buscarRolePorUsername(String username) {
        return usuarioRepository.findRoleByUsername(username);
    }
}
