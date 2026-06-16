package com.jbkloh.marieeanne.infra.models;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="tb_users")
@NoArgsConstructor
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="user_id", unique=true)
    private Long id;

    @Email(message="É necessário um email válido")
    @NotEmpty(message="O campo email deve ser inserido")
    @Column(name="user_email",unique=true)
    private String email;


    @ManyToMany(fetch=FetchType.EAGER,cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name="user_autorizacoes",
        joinColumns = @JoinColumn(name="user_id",referencedColumnName = "user_id"),
        inverseJoinColumns= @JoinColumn(name="role_id")
    )
    private Set<UserRoleEntity> authority;

    public UserEntity(String emailString, Set<UserRoleEntity> authority){
        super();
        this.authority = authority;
        this.email = emailString;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authority.stream()
            .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
            .collect(Collectors.toList());

    }

    @Override
    public @Nullable String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return this.email;
    }
    
}
