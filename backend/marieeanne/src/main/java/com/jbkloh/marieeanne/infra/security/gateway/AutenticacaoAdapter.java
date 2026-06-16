package com.jbkloh.marieeanne.infra.security.gateway;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.jbkloh.marieeanne.core.ports.AutenticacaoPort;

@Component
public class AutenticacaoAdapter implements AutenticacaoPort{

    @Override
    public boolean estaAutenticado(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null) {
            return false;
        }

        return authentication.isAuthenticated() && 
               !(authentication instanceof AnonymousAuthenticationToken);
    }    

    @Override
    public String getEmailUsuarioLogado() {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !estaAutenticado()) {
            return null;
        }
        return authentication.getName();
    }
}
