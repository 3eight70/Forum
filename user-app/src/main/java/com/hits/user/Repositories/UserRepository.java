package com.hits.user.Repositories;

<<<<<<< HEAD
import com.hits.user.Models.Entity.User;
=======
import com.hits.user.Models.Entities.User;
>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    User findByEmail(String email);

    Optional<User> findUserByEmail(String email);
}
