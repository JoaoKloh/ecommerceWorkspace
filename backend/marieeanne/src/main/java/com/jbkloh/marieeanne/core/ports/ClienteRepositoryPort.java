package com.jbkloh.marieeanne.core.ports;

import java.util.Optional;

import com.jbkloh.marieeanne.core.models.Cliente;

public interface ClienteRepositoryPort {
    void save(Cliente cliente);
    Optional<Cliente> findById(Long id);
    void deleteById(Long id);
    void update(Cliente cliente);
    Optional<Cliente> findByUserId(Long userId);
}