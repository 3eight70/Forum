package com.hits.user.Repositories;

import com.hits.user.Models.Entities.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUserId(UUID id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM refresh_tokens WHERE id = ?1", nativeQuery = true)
    void deleteRefreshTokenById(UUID id);
}
