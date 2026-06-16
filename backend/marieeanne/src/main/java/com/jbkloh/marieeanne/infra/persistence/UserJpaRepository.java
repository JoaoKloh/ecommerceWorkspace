package com.jbkloh.marieeanne.infra.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jbkloh.marieeanne.infra.models.UserEntity;

@Repository
public interface  UserJpaRepository extends JpaRepository<UserEntity,Long>{

Optional<UserEntity> findByEmail(String email);    
Boolean existsByEmail(String email);
    
}
