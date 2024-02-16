package com.jp.parkapi.jwt;

import com.jp.parkapi.entity.Role;
import com.jp.parkapi.entity.Usuario;
import com.jp.parkapi.repository.UsuarioRepository;
import com.jp.parkapi.service.UsuarioService;
import com.jp.parkapi.web.controller.UsuarioController;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JwtUserDetailsService implements UserDetailsService {//Interface para localizar o usuário no banco de dados

    private final UsuarioService usuarioService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioService.buscarPorUsername(username);
        return new JwtUserDetails(usuario);
    }

    public JwtToken getTokenAuthenticated(String username){
        Role role = usuarioService.buscarRolePorUsername(username);
        return JwtUtils.createToken(username, role.name().substring("ROLE_".length()));
    }

}
