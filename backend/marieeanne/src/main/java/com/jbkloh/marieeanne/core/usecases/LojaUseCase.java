package com.jbkloh.marieeanne.core.usecases;

import java.util.List;

import com.jbkloh.marieeanne.core.models.Loja;

public interface LojaUseCase {

    Loja findById(Long idLoja);
    void save(Loja loja);
    void deletarLoja(Long idLoja);
    List<Loja> buscarTodos();
}
