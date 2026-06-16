package com.jbkloh.marieeanne.core.usecases;


import java.util.Optional;

import com.jbkloh.marieeanne.core.models.Cliente;


public interface ClienteUseCase {

     void registrarCliente(Cliente cliente);
     void atualizarCadastro(Cliente cliente) ;
     Optional<Cliente> buscarPorUserId(Long userId);
     Cliente buscarPorId(Long id);  
     void excluirConta(Long id) ;
     Cliente buscarPorEmail(String email);
}