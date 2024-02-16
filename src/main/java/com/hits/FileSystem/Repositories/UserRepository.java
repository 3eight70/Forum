package com.hits.FileSystem.Repositories;

import com.hits.FileSystem.Models.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    User findByEmail(String email);

    Optional<User> findUserByEmail(String email);
}
