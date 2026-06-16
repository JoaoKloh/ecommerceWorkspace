package com.jbkloh.marieeanne.core.usecases;

import com.jbkloh.marieeanne.core.models.Endereco;


public interface EnderecoUseCase {

    void registrarEndereco(Endereco endereco);
    void atualizarEndereco(Endereco endereco) ;
    Endereco buscarPorId(Long id);  
    void excluirEndereco(Long id) ;
    
}
