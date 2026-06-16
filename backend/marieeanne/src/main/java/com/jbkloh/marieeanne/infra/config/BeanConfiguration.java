package com.jbkloh.marieeanne.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jbkloh.marieeanne.core.ports.AutenticacaoPort;
import com.jbkloh.marieeanne.core.ports.CarrinhoRepositoryPort;
import com.jbkloh.marieeanne.core.ports.ClienteRepositoryPort;
import com.jbkloh.marieeanne.core.ports.EnderecoRepositoryPort;
import com.jbkloh.marieeanne.core.ports.LojaRepositoryPort;
import com.jbkloh.marieeanne.core.ports.OptRepositoryPort;
import com.jbkloh.marieeanne.core.ports.PedidoRepositoryPort;
import com.jbkloh.marieeanne.core.ports.ProdutoLojaRepositoryPort;
import com.jbkloh.marieeanne.core.ports.ProdutoRepositoryPort;
import com.jbkloh.marieeanne.core.ports.UsuarioRepositoryPort;
import com.jbkloh.marieeanne.core.usecases.CarrinhoUseCase;
import com.jbkloh.marieeanne.core.usecases.ClienteUseCase;
import com.jbkloh.marieeanne.core.usecases.EnderecoUseCase;
import com.jbkloh.marieeanne.core.usecases.LojaUseCase;
import com.jbkloh.marieeanne.core.usecases.OtpUseCase;
import com.jbkloh.marieeanne.core.usecases.PagamentoUseCase;
import com.jbkloh.marieeanne.core.usecases.PedidoUseCase;
import com.jbkloh.marieeanne.core.usecases.ProdutoLojaUseCase;
import com.jbkloh.marieeanne.core.usecases.ProdutoUseCase;
import com.jbkloh.marieeanne.core.usecases.UsuarioUseCase;
import com.jbkloh.marieeanne.core.usecases.impl.CarrinhoUseCaseImpl;
import com.jbkloh.marieeanne.core.usecases.impl.ClienteUseCaseImpl;
import com.jbkloh.marieeanne.core.usecases.impl.EnderecoUseCaseImpl;
import com.jbkloh.marieeanne.core.usecases.impl.LojaUseCaseImpl;
import com.jbkloh.marieeanne.core.usecases.impl.OtpUseCaseImpl;
import com.jbkloh.marieeanne.core.usecases.impl.PagamentoUseCaseImpl;
import com.jbkloh.marieeanne.core.usecases.impl.PedidoUseCaseImpl;
import com.jbkloh.marieeanne.core.usecases.impl.ProdutoLojaUseCaseImpl;
import com.jbkloh.marieeanne.core.usecases.impl.ProdutoUseCaseImpl;

@Configuration
public class BeanConfiguration {

    @Bean
    public CarrinhoUseCase carrinhoUseCase(CarrinhoRepositoryPort carrinhoRepositoryPort, AutenticacaoPort autenticacaoPort, ProdutoLojaRepositoryPort produtoRepositoryPort, UsuarioRepositoryPort usuarioRepositoryPort){
        return new CarrinhoUseCaseImpl(carrinhoRepositoryPort, autenticacaoPort, produtoRepositoryPort, usuarioRepositoryPort);
    }
    @Bean 
    public ClienteUseCase clienteUseCase(ClienteRepositoryPort clienteRepositoryPort,AutenticacaoPort autenticacaoPort, UsuarioRepositoryPort usuarioRepositoryPort){
        return new ClienteUseCaseImpl(clienteRepositoryPort, autenticacaoPort, usuarioRepositoryPort);
    }
    @Bean 
    public PagamentoUseCase pagamentoUseCase(PedidoRepositoryPort pedidoRepositoryPort, PedidoUseCase pedidoUseCase){
        return new PagamentoUseCaseImpl(pedidoRepositoryPort, pedidoUseCase);
    }
    @Bean
    public PedidoUseCase pedidoUseCase(PedidoRepositoryPort pedidoRepositoryPort, ProdutoLojaRepositoryPort produtoLojaRepositoryPort, ProdutoLojaUseCase produtoLojaUseCase){
        return new PedidoUseCaseImpl(pedidoRepositoryPort,  produtoLojaRepositoryPort, produtoLojaUseCase);
    }
    @Bean
    public ProdutoUseCase produtoUseCase(ProdutoRepositoryPort produtoRepositoryPort) {
        return new ProdutoUseCaseImpl(produtoRepositoryPort);
    }
    @Bean
    public EnderecoUseCase enderecoUseCase(EnderecoRepositoryPort enderecoRepositoryPort) {
        return new EnderecoUseCaseImpl(enderecoRepositoryPort);
    }
    @Bean
    public OtpUseCase otpUseCase(OptRepositoryPort optRepositoryPort){
        return new OtpUseCaseImpl(optRepositoryPort);
    }
    @Bean 
    public UsuarioUseCase usuarioUseCase(UsuarioRepositoryPort usuarioRepositoryPort){
        return new com.jbkloh.marieeanne.core.usecases.impl.UsuarioUseCaseImpl(usuarioRepositoryPort);
    }   
    @Bean
    public LojaUseCase lojaUseCase(LojaRepositoryPort lojaRepositoryPort){
        return new LojaUseCaseImpl(lojaRepositoryPort);
    }
    @Bean
    public ProdutoLojaUseCase produtoLojaUseCase(ProdutoLojaRepositoryPort produtoLojaRepositoryPort){
        return new ProdutoLojaUseCaseImpl(produtoLojaRepositoryPort);
    }
}
