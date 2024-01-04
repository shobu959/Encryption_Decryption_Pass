package com.example.keycloak.repository;

import com.example.keycloak.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {

    @Query(value = "SELECT * FROM User WHERE email=?1", nativeQuery = true)
    UserEntity getUserDetails(String email);
}
