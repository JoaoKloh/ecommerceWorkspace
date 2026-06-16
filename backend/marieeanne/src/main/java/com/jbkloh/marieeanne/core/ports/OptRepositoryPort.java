package com.jbkloh.marieeanne.core.ports;

public interface OptRepositoryPort {
    void armazenarCodigo(String codigo, String email);
    String getCodigo(String email);
    void removerCodigo(String email);
}
