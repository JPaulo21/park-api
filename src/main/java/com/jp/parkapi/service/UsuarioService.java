package com.jp.parkapi.service;

import com.jp.parkapi.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor //Cria um método construtor
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

}
