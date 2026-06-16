package com.jbkloh.marieeanne.infra.mappers;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.jbkloh.marieeanne.core.models.Usuario;
import com.jbkloh.marieeanne.core.models.enums.AutoridadesUsuario;
import com.jbkloh.marieeanne.infra.models.UserEntity;
import com.jbkloh.marieeanne.infra.models.UserRoleEntity;
import com.jbkloh.marieeanne.infra.persistence.UserRoleJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UsuarioMapper {

    public static Usuario toDomain(UserEntity entity) {
        if (entity == null) return null;
        Usuario domain = new Usuario();
        domain.setId(entity.getId());
        domain.setEmail(entity.getEmail());
        
        entity.getAuthority().forEach(role -> {
            domain.addAuthority(AutoridadesUsuario.valueOf(role.getAuthority()));
        });
        
        return domain;
    }

    public static UserEntity toEntity(Usuario domain, UserRoleJpaRepository userRoleJpaRepository) {
        if (domain == null) return null;

        UserEntity entity = new UserEntity();
        entity.setId(domain.getId());
        entity.setEmail(domain.getEmail());

        Set<UserRoleEntity> roles = domain.getAuthorities().stream()
            .map(auth -> userRoleJpaRepository.findByAuthority(auth.name())
                .orElseThrow(() -> new RuntimeException("Role não encontrada: " + auth.name())))
            .collect(Collectors.toSet());

        entity.setAuthority(roles); 
        return entity;
    }
}