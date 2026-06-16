package com.jbkloh.marieeanne.core.usecases;

import java.util.Optional;

import com.jbkloh.marieeanne.core.models.Usuario;

public interface UsuarioUseCase {

    Usuario save (Usuario usuario);
    void delete (Long id);
    Usuario findById (Long id);
    Optional<Usuario> findByEmail (String email);
    
}
