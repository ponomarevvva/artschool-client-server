package ru.artschool.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.artschool.server.model.AppUser;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
}