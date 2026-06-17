package com.jbkloh.marieeanne.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

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
import com.jbkloh.marieeanne.infra.security.utils.RSAKeyProperties;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

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
    @Bean
    public JwtEncoder jwtEncoder(){
        RSAKeyProperties keys = new RSAKeyProperties();
        JWK jwk = new RSAKey.Builder(keys.getPublicKey()).privateKey(keys.getPrivateKey()).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }
    @Bean
    public JwtDecoder jwtDecoder() {
        RSAKeyProperties keys = new RSAKeyProperties();
        return NimbusJwtDecoder.withPublicKey(keys.getPublicKey()).build();
    }
}
