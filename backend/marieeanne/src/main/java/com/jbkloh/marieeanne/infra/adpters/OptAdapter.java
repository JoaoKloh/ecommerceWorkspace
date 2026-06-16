package com.jbkloh.marieeanne.infra.adpters;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import com.jbkloh.marieeanne.core.ports.OptRepositoryPort;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor 
public class OptAdapter implements OptRepositoryPort {

    private final CacheManager cacheManager;

    @Override
    public void armazenarCodigo(String email,String codigo) {
        getCache().put(email, codigo);
    }
    @Override
    public String getCodigo(String email) {
        return getCache().get(email, String.class);
    }
    @Override
    public void removerCodigo(String email) {
        getCache().evict(email);
    }
    private Cache getCache(){
        return cacheManager.getCache("mariecache");
    }
}
