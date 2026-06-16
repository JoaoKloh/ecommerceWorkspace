package com.jbkloh.marieeanne.core.ports;

import com.jbkloh.marieeanne.core.models.Endereco;

public interface EnderecoRepositoryPort {
        Endereco save(Endereco endereco);
        Endereco findById(Long id);
        void deleteById(Long id);
    
}
