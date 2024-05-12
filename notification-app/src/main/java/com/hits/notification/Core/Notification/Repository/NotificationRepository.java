package com.hits.notification.Core.Notification.Repository;

import com.hits.notification.Core.Notification.Entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    @Query(value = "SELECT COUNT(*) FROM notifications WHERE is_read = false AND user_login = :userLogin", nativeQuery = true)
    Integer countByIsReadFalse(String userLogin);

    @Query(value = "SELECT * FROM notifications WHERE is_read = false AND user_login = :userLogin", nativeQuery = true)
    List<Notification> findAllUnreadNotificationsByUserLogin(String userLogin);

    Page<Notification> findAllByUserLoginOrderByIsReadAsc(Pageable pageable, String userLogin);

    Optional<Notification> findByUserLoginAndId(String userLogin, UUID id);
}
