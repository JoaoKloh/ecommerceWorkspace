package com.jbkloh.marieeanne.infra.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jbkloh.marieeanne.core.models.Endereco;
import com.jbkloh.marieeanne.core.models.Loja;
import com.jbkloh.marieeanne.core.usecases.LojaUseCase;
import com.jbkloh.marieeanne.infra.dtos.loja.LojaCreateRequestDTO;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LojaService {
    @Autowired
    private final LojaUseCase lojaUseCase;

    @Transactional()
    public void criarLoja(LojaCreateRequestDTO req){
        Loja loja = new Loja();
        loja.setNome(req.nome());
        Endereco endereco = new Endereco();
        endereco.setEstado(req.endereco().estado());
        endereco.setCidade(req.endereco().cidade());
        endereco.setBairro(req.endereco().bairro());
        endereco.setNumero(req.endereco().numero());
        endereco.setRua(req.endereco().rua());
        endereco.setComplemento(req.endereco().complemento());
        endereco.setCep(req.endereco().cep());

        loja.setEndereco(endereco);
        lojaUseCase.save(loja);
    }

    @Transactional
    public void deletarLoja(Long idLoja){
        lojaUseCase.deletarLoja(idLoja);
    }

    @Transactional(readOnly=true)
    public List<Loja> buscarTodos(){
        return lojaUseCase.buscarTodos();
    }
}
