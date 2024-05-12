package com.hits.forum.Core.Favorite.Repository;

import com.hits.forum.Core.Favorite.Entity.Favorite;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, UUID> {
    Optional<Favorite> findFavoriteByThemeIdAndUserId(UUID themeId, UUID userId);
    List<Favorite> findAllByUserId(UUID userId, Pageable pageable);
}
