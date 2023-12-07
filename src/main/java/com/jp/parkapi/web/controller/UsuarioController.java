package com.jp.parkapi.web.controller;

import com.jp.parkapi.entity.Usuario;
import com.jp.parkapi.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/usuarios") // recurso sempre no plural
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    // ResponseEntity - Resposta para web encapsulando Usuario para corpo da Response
    public ResponseEntity<Usuario> create(@RequestBody Usuario usuario){
        Usuario user = usuarioService.salvar(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
        //return ResponseEntity.created()
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getById (@PathVariable Long id){
        Usuario user = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Usuario> updatePassword(@PathVariable Long id, @RequestBody Usuario usuario){
        Usuario user = usuarioService.editarSenha(id, usuario.getPassword());
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> buscarTodos(){
        List<Usuario> users = usuarioService.buscarTodos();
        return ResponseEntity.ok(users);
    }
}
