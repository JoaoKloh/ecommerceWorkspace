package com.jbkloh.marieeanne.infra.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jbkloh.marieeanne.core.models.Cliente;
import com.jbkloh.marieeanne.core.models.Endereco;
import com.jbkloh.marieeanne.core.ports.AutenticacaoPort;
import com.jbkloh.marieeanne.infra.dtos.cliente.ClienteCheckoutDTO;
import com.jbkloh.marieeanne.infra.dtos.cliente.ClienteRequestDTO;
import com.jbkloh.marieeanne.infra.dtos.cliente.ClienteResponseDTO;
import com.jbkloh.marieeanne.infra.dtos.endereco.EnderecoResponseDto;
import com.jbkloh.marieeanne.infra.service.ClienteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
public class ClienteController {
    @Autowired
    private ClienteService clienteService;
    @Autowired
    private AutenticacaoPort autenticacaoPort;

    @PostMapping("/create")
    public ResponseEntity<Void> criar(@Valid @RequestBody ClienteCheckoutDTO request) {
            clienteService.criar(request.nome(), 
                request.telefone(), 
                autenticacaoPort.getEmailUsuarioLogado());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PostMapping("/upsert")
    public ResponseEntity<Void> registrar(@Valid @RequestBody ClienteRequestDTO request) {
        Endereco endereco = new Endereco();
        endereco.setBairro(request.endereco().bairro());
        endereco.setCep(request.endereco().cep());
        endereco.setCidade(request.endereco().cidade());
        endereco.setComplemento(request.endereco().complemento());
        endereco.setEstado(request.endereco().estado());
        endereco.setNumero(request.endereco().numero());
        endereco.setRua(request.endereco().rua());
        clienteService.registrarOuUpdate(
            request.cpf(),
            request.nome(), 
            request.telefone(), 
            request.dataNascimento(), 
            endereco);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }       
    @GetMapping("/meuPerfil")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ClienteResponseDTO> buscarMeuPerfil() {
        String email = autenticacaoPort.getEmailUsuarioLogado();
        Cliente cliente = clienteService.buscarPorEmail(email);

        EnderecoResponseDto enderecoResponse = new EnderecoResponseDto(
            cliente.getEndereco().getId(),
            cliente.getEndereco().getRua(),
            cliente.getEndereco().getNumero(),
            cliente.getEndereco().getComplemento(),
            cliente.getEndereco().getBairro(),
            cliente.getEndereco().getCidade(),
            cliente.getEndereco().getEstado(),
            cliente.getEndereco().getCep()
        );
        
        ClienteResponseDTO response = new ClienteResponseDTO(
            cliente.getId(),
            cliente.getNome(),
            cliente.getTelefone(),
            enderecoResponse,
            cliente.getDataNascimento()
        );
        
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/excluir/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) { 
        clienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}