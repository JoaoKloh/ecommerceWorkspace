package com.jbkloh.marieeanne.core.ports;

import java.util.List;
import java.util.Optional;

import com.jbkloh.marieeanne.core.models.Loja;

public interface LojaRepositoryPort {

    Optional<Loja> findById(Long id);
    void save(Loja loja);
    void delete(Long id);
    List<Loja> findAll();
}
