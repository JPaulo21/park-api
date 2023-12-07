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

}
