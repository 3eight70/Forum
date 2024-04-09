package com.hits.user.Core.User.Repository;

import com.hits.user.Core.User.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findUserByLogin(String login);
    Optional<User> findByLogin(String login);
    Optional<User> findUserById(UUID userId);
    Optional<User> findByEmailOrLoginOrPhoneNumber(String email, String login, String phoneNumber);
    Optional<User> findByPhoneNumber(String phoneNumber);
}
