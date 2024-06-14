package org.sourceovh.demoex.repository;

import org.sourceovh.demoex.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    User findByEmail(String email);
    User findByNumberPhone(String numberPhone);
    boolean existsByEmail(String email);

    boolean existsByNumberPhone(String numberPhone);
    default boolean existsByEmailOrNumberPhone(String email, String numberPhone) {
        return existsByEmail(email) || existsByNumberPhone(numberPhone);
    }
}
