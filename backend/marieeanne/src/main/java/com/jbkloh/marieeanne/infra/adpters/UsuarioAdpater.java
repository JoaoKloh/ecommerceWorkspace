package com.jbkloh.marieeanne.infra.adpters;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.jbkloh.marieeanne.core.models.Usuario;
import com.jbkloh.marieeanne.core.ports.UsuarioRepositoryPort;
import com.jbkloh.marieeanne.infra.exceptions.AppException;
import com.jbkloh.marieeanne.infra.mappers.UsuarioMapper;
import com.jbkloh.marieeanne.infra.models.UserEntity;
import com.jbkloh.marieeanne.infra.persistence.UserJpaRepository;
import com.jbkloh.marieeanne.infra.persistence.UserRoleJpaRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class UsuarioAdpater implements UsuarioRepositoryPort {

    private final UserJpaRepository userEntityJpaRepository;
    private final UserRoleJpaRepository userRoleJpaRepository;

    @Override
    public Optional<Usuario> findByUsuarioEmail(String email) {
        return userEntityJpaRepository.findByEmail(email).map(UsuarioMapper::toDomain);
    }

    @Override
    public Usuario findById(Long id) {
        return userEntityJpaRepository.findById(id)
                .map(UsuarioMapper::toDomain)
                .orElseThrow(() -> new AppException("Não existe usuário com o ID cadastrado", HttpStatus.NOT_FOUND));
    }

    @Override
    public Usuario save(Usuario usuario) {
        UserEntity userEntity = UsuarioMapper.toEntity(usuario,userRoleJpaRepository);
        userEntityJpaRepository.saveAndFlush(userEntity);
        return UsuarioMapper.toDomain(userEntity);
    }

    @Override
    public void delete(Long id) {
        userEntityJpaRepository.deleteById(id);
    }

    
}
