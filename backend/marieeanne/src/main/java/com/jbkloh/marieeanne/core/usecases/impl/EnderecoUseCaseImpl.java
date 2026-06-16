package com.jbkloh.marieeanne.core.usecases.impl;

import com.jbkloh.marieeanne.core.models.Endereco;
import com.jbkloh.marieeanne.core.ports.EnderecoRepositoryPort;
import com.jbkloh.marieeanne.core.usecases.EnderecoUseCase;

public class EnderecoUseCaseImpl implements EnderecoUseCase {

    private final EnderecoRepositoryPort enderecoRepository;

    public EnderecoUseCaseImpl(EnderecoRepositoryPort enderecoRepository) {
        this.enderecoRepository = enderecoRepository;
    }

    @Override
    public void registrarEndereco(Endereco endereco) {
        enderecoRepository.save(endereco);
    }

    @Override
    public void atualizarEndereco(Endereco endereco) {
        enderecoRepository.save(endereco);
    }

    @Override
    public Endereco buscarPorId(Long id) {
        return enderecoRepository.findById(id);
    }

    @Override
    public void excluirEndereco(Long id) {
        enderecoRepository.deleteById(id);
    }
    
}
