package ru.artschool.server;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.artschool.server.model.AppUser;
import ru.artschool.server.model.Role;
import ru.artschool.server.repository.UserRepository;

@Configuration
public class UserDataLoader {

    @Bean
    CommandLineRunner initUsers(UserRepository userRepo, PasswordEncoder encoder) {
        return args -> {
            if (userRepo.findByUsername("admin").isEmpty()) {
                AppUser admin = new AppUser();
                admin.setUsername("admin");
                admin.setPasswordHash(encoder.encode("admin"));
                admin.setRole(Role.ADMIN);
                userRepo.save(admin);
            }
        };
    }
}