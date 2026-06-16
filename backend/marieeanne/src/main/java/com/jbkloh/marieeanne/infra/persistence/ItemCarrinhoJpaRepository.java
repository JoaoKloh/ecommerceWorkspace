package com.jbkloh.marieeanne.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jbkloh.marieeanne.infra.models.ItemCarrinhoEntity;

@Repository
public interface  ItemCarrinhoJpaRepository extends JpaRepository<ItemCarrinhoEntity, Long>{
    
}
