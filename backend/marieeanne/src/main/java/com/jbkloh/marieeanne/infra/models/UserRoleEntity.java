package com.jbkloh.marieeanne.infra.models;

import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleEntity implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="roles_id",unique=true)
    private Long id;

    @Column(name = "autorizacoes")
    @NotEmpty(message="O campo autorizacoes deve ser inserido")
    private String authority;

    @Override
    public String getAuthority() {
        return this.authority;
    }
    
    
}
