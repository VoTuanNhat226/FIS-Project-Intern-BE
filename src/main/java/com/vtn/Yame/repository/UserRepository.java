package com.vtn.Yame.repository;

import com.vtn.Yame.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("update User u set u.password = :password where u.email = :email")
    void updatePassword(@Param("email") String email, @Param("password") String password);

    @Query("SELECT COUNT(id) FROM User ")
    Integer countUsers();

}
