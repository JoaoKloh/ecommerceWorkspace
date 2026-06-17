package com.jbkloh.marieeanne.infra.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jbkloh.marieeanne.core.models.Cliente;
import com.jbkloh.marieeanne.core.models.Endereco;
import com.jbkloh.marieeanne.core.models.Usuario;
import com.jbkloh.marieeanne.core.ports.AutenticacaoPort;
import com.jbkloh.marieeanne.core.usecases.ClienteUseCase;
import com.jbkloh.marieeanne.core.usecases.UsuarioUseCase;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ClienteService {
    private final ClienteUseCase clienteUseCase;
    private final AutenticacaoPort autenticacaoPort;
    private final UsuarioUseCase usuarioUseCase;

    @Transactional
    public void registrarOuUpdate(String cpf,String nome, String telefone, LocalDate dataNascimento, Endereco endereco) {
        String email = autenticacaoPort.getEmailUsuarioLogado();
        Optional<Usuario> user = usuarioUseCase.findByEmail(email);

        Optional<Cliente> cliente = clienteUseCase.buscarPorUserId(user.get().getId());
            if(!cliente.isPresent()){
                Cliente novo = new Cliente();
                novo.setUser(user.get());
                novo.setDataNascimento(dataNascimento);
                novo.setEndereco(endereco);
                novo.setNome(nome);
                novo.setTelefone(telefone);
                novo.setCpf(cpf);
                clienteUseCase.registrarCliente(novo);
            } else {
                Cliente clienteExistente = cliente.get();
                clienteExistente.setDataNascimento(dataNascimento);
                clienteExistente.setNome(nome);
                clienteExistente.setCpf(cpf);
                clienteExistente.setTelefone(telefone);
                if (clienteExistente.getEndereco() != null && endereco != null) {
                    
                    Endereco enderecoAtual = clienteExistente.getEndereco();
                    enderecoAtual.setNumero(endereco.getNumero());
                    enderecoAtual.setBairro(endereco.getBairro());
                    enderecoAtual.setCep(endereco.getCep());
                    enderecoAtual.setCidade(endereco.getCidade());
                    enderecoAtual.setEstado(endereco.getEstado());
                    enderecoAtual.setComplemento(endereco.getComplemento());
                    enderecoAtual.setRua(endereco.getRua());
                    
                } else {
                    clienteExistente.setEndereco(endereco);
                }
    
                clienteUseCase.atualizarCadastro(clienteExistente);    
        }
    }  
    @Transactional(readOnly = true)
    public Long getAuthenticatedUserId() {
        String email = autenticacaoPort.getEmailUsuarioLogado();
        Optional<Usuario> user = usuarioUseCase.findByEmail(email);
        return user.get().getId();
    }
    @Transactional
    public void criar(String nome, String telefone, String email) {
        Cliente cliente = new Cliente();
        cliente.setNome(nome);
        cliente.setTelefone(telefone);
        clienteUseCase.registrarCliente(cliente);
    }
    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) {
        return clienteUseCase.buscarPorId(id);
    }
    @Transactional(readOnly = true)
    public Cliente buscarPorEmail(String email){
        return clienteUseCase.buscarPorEmail(email);
    }
    @Transactional
    public void atualizar(Cliente cliente) {
        clienteUseCase.atualizarCadastro(cliente);
    }
    @Transactional
    public void deletar(Long id) {
        clienteUseCase.excluirConta(id);
    }
}