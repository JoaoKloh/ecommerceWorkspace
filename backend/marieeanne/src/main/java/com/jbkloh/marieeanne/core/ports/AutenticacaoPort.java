package com.jbkloh.marieeanne.core.ports;

public interface AutenticacaoPort {
    boolean estaAutenticado();
    String getEmailUsuarioLogado();
}
