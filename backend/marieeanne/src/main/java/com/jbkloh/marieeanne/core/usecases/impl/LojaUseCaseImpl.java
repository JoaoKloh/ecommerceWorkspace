package com.jbkloh.marieeanne.core.usecases.impl;

import java.util.List;
import java.util.Optional;

import com.jbkloh.marieeanne.core.models.Loja;
import com.jbkloh.marieeanne.core.ports.LojaRepositoryPort;
import com.jbkloh.marieeanne.core.usecases.LojaUseCase;


public class LojaUseCaseImpl implements LojaUseCase {

    private final LojaRepositoryPort lojaRepositoryPort;

    public LojaUseCaseImpl(LojaRepositoryPort lojaRepositoryPort){
        this.lojaRepositoryPort = lojaRepositoryPort;
    }
    
    @Override
    public void save(Loja loja) {
        validarDados(loja);
        lojaRepositoryPort.save(loja);  
        
    }

    @Override
    public Loja findById(Long idLoja) {
        if (idLoja == null) {
            throw new IllegalArgumentException("O ID da loja fornecido não pode ser nulo.");
        }
        Optional<Loja> loja = lojaRepositoryPort.findById(idLoja);
        if(loja.isEmpty()){
            throw new RuntimeException("Não existe loja com o id fornecido.");
        }
        return loja.get();
    }

    @Override
    public void deletarLoja(Long idLoja){
        lojaRepositoryPort.delete(idLoja);
    }
    @Override
    public List<Loja> buscarTodos() {
        return lojaRepositoryPort.findAll();
    }

    private void validarDados(Loja loja){
        if (loja == null) {
            throw new IllegalArgumentException("Os dados da loja não podem ser nulos.");
        }

        if (loja.getNome() == null || loja.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome da loja é obrigatório e não pode estar em branco.");
        }

        if (loja.getEndereco() == null) {
            throw new IllegalArgumentException("O endereço da loja é obrigatório.");
        }
    }
    
}
