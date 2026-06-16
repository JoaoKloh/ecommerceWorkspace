package com.jbkloh.marieeanne.core.usecases.impl;

import java.util.Objects;
import java.util.Optional;

import com.jbkloh.marieeanne.core.models.Usuario;
import com.jbkloh.marieeanne.core.ports.UsuarioRepositoryPort;
import com.jbkloh.marieeanne.core.usecases.UsuarioUseCase;

public class UsuarioUseCaseImpl implements UsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public UsuarioUseCaseImpl(UsuarioRepositoryPort usuarioRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    @Override
    public Usuario save(Usuario usuario) {
        if(usuario == null || usuario.getEmail() == null || usuario.getEmail().isEmpty()) {
            throw new IllegalArgumentException("O email do usuário é obrigatório.");
        }
        return usuarioRepositoryPort.save(usuario);
        
    }

    @Override
    public void delete(Long id) {
        if(id == null) {
            throw new IllegalArgumentException("O ID do usuário é obrigatório.");
        }
        Usuario user = usuarioRepositoryPort.findById(id);
        if(user == null || !Objects.equals(user.getId(), id)||user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Não existe usuário com o ID fornecido.");
        }
        usuarioRepositoryPort.delete(id);
        
    }

    @Override
    public Usuario findById(Long id) {
        return usuarioRepositoryPort.findById(id);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        Optional<Usuario> user =usuarioRepositoryPort.findByUsuarioEmail(email);
        if(user.isEmpty()){
            throw new IllegalArgumentException("Não existe usuário com o email fornecido.");
        }
        return user;
    }   
    
}
