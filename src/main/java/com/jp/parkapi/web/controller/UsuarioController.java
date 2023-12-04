package com.jp.parkapi.web.controller;

import com.jp.parkapi.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/vi/usuarios") // recurso sempre no plural
public class UsuarioController {

    private final UsuarioService usuarioService;

}
