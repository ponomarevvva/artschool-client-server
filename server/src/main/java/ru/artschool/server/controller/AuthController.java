package ru.artschool.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.artschool.server.model.AppUser;
import ru.artschool.server.model.Role;
import ru.artschool.server.repository.UserRepository;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public AuthController(UserRepository userRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    public static class RegisterRequest {
        public String username;
        public String password;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody RegisterRequest req) {
        if (req.username == null || req.username.isBlank() || req.password == null || req.password.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username and password are required");
        }
        if (userRepo.findByUsername(req.username.trim()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "username already exists");
        }

        AppUser u = new AppUser();
        u.setUsername(req.username.trim());
        u.setPasswordHash(encoder.encode(req.password));
        u.setRole(Role.USER); // регистрация создаёт USER
        userRepo.save(u);
    }

    @GetMapping("/me")
    public Map<String, String> me(Authentication auth) {
        String role = auth.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring("ROLE_".length()))
                .findFirst()
                .orElse("USER");

        return Map.of("username", auth.getName(), "role", role);
    }
}