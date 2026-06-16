package com.jbkloh.marieeanne.core.ports;
import java.util.List;

import com.jbkloh.marieeanne.core.models.Produto;

public interface ProdutoRepositoryPort {
    Produto saveProduto(Produto produto);
    void save(Produto produto);
    Produto findById(Long id);
    void deleteById(Long id);
    Produto findByName(String name);
    Boolean existsByName(String name);
    Boolean existsById(Long id);
    List<Produto> findAll();
}
