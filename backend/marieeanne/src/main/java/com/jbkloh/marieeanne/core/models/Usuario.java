package com.jbkloh.marieeanne.core.models;

import java.util.HashSet;
import java.util.Set;

import com.jbkloh.marieeanne.core.models.enums.AutoridadesUsuario;

public class Usuario {
    Long id;
    Set<AutoridadesUsuario> authorities = new HashSet<>();
    String email;
    
    public Usuario(){
        
    }

    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public Usuario(String email) {
        this.email = email;
    }

      public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public Set<AutoridadesUsuario> getAuthorities() {
        return authorities;
    }

    public void addAuthority(AutoridadesUsuario authority) {
            this.authorities.add(authority);
    }

}
