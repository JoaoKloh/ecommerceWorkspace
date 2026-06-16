package com.jbkloh.marieeanne.core.usecases.impl;
import java.util.Optional;

import com.jbkloh.marieeanne.core.models.Cliente;
import com.jbkloh.marieeanne.core.models.Usuario;
import com.jbkloh.marieeanne.core.ports.AutenticacaoPort;
import com.jbkloh.marieeanne.core.ports.ClienteRepositoryPort;
import com.jbkloh.marieeanne.core.ports.UsuarioRepositoryPort;
import com.jbkloh.marieeanne.core.usecases.ClienteUseCase;

public class ClienteUseCaseImpl implements ClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final AutenticacaoPort autenticacaoPort;

    public ClienteUseCaseImpl(ClienteRepositoryPort clienteRepositoryPort,AutenticacaoPort autenticacaoPort, UsuarioRepositoryPort usuarioRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.autenticacaoPort = autenticacaoPort;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    @Override
    public void registrarCliente(Cliente cliente) {
        clienteRepositoryPort.save(cliente);
    }
    @Override
    public void atualizarCadastro(Cliente cliente) {
        validarAcesso(cliente);
        if (cliente.getId() == null || clienteRepositoryPort.findById(cliente.getId()).isEmpty()) {
            throw new IllegalArgumentException("Cliente não encontrado para atualização.");
        }
        clienteRepositoryPort.update(cliente);
    }
    @Override
    public Cliente buscarPorId(Long id) {
        Cliente cliente =  clienteRepositoryPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));
        validarAcesso(cliente);      
        return cliente;  
    }
    @Override
    public void excluirConta(Long id) {
        buscarPorId(id);
        clienteRepositoryPort.deleteById(id);
    }
    @Override
    public Cliente buscarPorEmail(String email){
        Optional<Usuario> user = usuarioRepositoryPort.findByUsuarioEmail(email);
        System.out.println("Buscando cliente para o email: " + email);
        System.out.println("ID do usuário encontrado: " + user.get().getId());
        Cliente cliente = clienteRepositoryPort.findByUserId(user.get().getId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));
        
        validarAcesso(cliente);
        return cliente;
    }
    @Override 
    public  Optional<Cliente> buscarPorUserId(Long userId){
        Optional<Cliente> cliente = clienteRepositoryPort.findByUserId(userId);
        cliente.ifPresent(this::validarAcesso);
        return cliente;
    }

    private void validarAcesso(Cliente cliente){
        String email = autenticacaoPort.getEmailUsuarioLogado();
        Usuario user = cliente.getUser();
        if(email==null||!email.equals(user.getEmail())){
            throw new IllegalArgumentException("Voce não tem permissão para modificar dados de uma outra pessoa.");
        }
    }
}