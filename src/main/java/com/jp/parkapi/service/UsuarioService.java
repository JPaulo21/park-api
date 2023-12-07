package com.jp.parkapi.service;

import com.jp.parkapi.entity.Usuario;
import com.jp.parkapi.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor //Cria um método construtor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    @Transactional // Indicia que o Spring tomara conta da parte da transação, sendo o spring q vai abrir e fechar a transação no banco de dados
    public Usuario salvar(Usuario usuairo) {
        return usuarioRepository.save(usuairo);
    }


    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id){
        return usuarioRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Usuário não encontrado.")
        );
    }

    @Transactional
    public Usuario editarSenha(Long id, String password) {
        Usuario user = buscarPorId(id);
        user.setPassword(password);
        return user;
    }

    @Transactional(readOnly = true)
    public List<Usuario> buscarTodos() {
        return usuarioRepository.findAll();
    }

}
