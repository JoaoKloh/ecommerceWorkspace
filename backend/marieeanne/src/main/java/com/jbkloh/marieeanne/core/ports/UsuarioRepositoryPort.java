package com.jbkloh.marieeanne.core.ports;

import java.util.Optional;

import com.jbkloh.marieeanne.core.models.Usuario;

public interface UsuarioRepositoryPort {
    Optional<Usuario> findByUsuarioEmail(String email);
    Usuario findById(Long id);
    Usuario save(Usuario usuario);
    void delete(Long id);
}
