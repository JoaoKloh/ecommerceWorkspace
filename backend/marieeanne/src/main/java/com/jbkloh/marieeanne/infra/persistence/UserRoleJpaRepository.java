package com.jbkloh.marieeanne.infra.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jbkloh.marieeanne.infra.models.UserRoleEntity;

@Repository
public interface  UserRoleJpaRepository extends JpaRepository<UserRoleEntity, Long>{
    Optional<UserRoleEntity> findByAuthority(String authority);
}
