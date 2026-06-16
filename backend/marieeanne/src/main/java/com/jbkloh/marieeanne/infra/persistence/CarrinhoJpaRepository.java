package com.jbkloh.marieeanne.infra.persistence;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jbkloh.marieeanne.infra.models.CarrinhoEntity;

@Repository
public interface CarrinhoJpaRepository extends JpaRepository<CarrinhoEntity, Long> {
    Optional<CarrinhoEntity> findCarrinhoByUserId(Long id);
    Optional<CarrinhoEntity> findCarrinhoByUserEmail(String id);
    @Query("SELECT c FROM CarrinhoEntity c LEFT JOIN FETCH c.itens i LEFT JOIN FETCH i.produto WHERE c.user.email = :email")
    Optional<CarrinhoEntity> findByEmailWithItens(@Param("email") String email);
    
}
