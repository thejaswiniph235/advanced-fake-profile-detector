package com.fakeprofile.controller;

import com.fakeprofile.model.*;
import com.fakeprofile.repository.UserRepository;
import com.fakeprofile.security.JwtService;
import jakarta.validation.constraints.*;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthController(UserRepository users, PasswordEncoder encoder, JwtService jwt){
        this.users=users; this.encoder=encoder; this.jwt=jwt;
    }

    public record RegisterRequest(@Email @NotBlank String email, @Size(min=6) String password){}
    public record LoginRequest(@Email @NotBlank String email, @NotBlank String password){}

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest r){
        if(users.findByEmail(r.email()).isPresent())
            return ResponseEntity.badRequest().body(java.util.Map.of("error","Email already registered"));
        User u=new User(); u.setEmail(r.email()); u.setPassword(encoder.encode(r.password()));
        users.save(u);
        return ResponseEntity.ok(java.util.Map.of("message","Registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest r){
        var u=users.findByEmail(r.email()).orElse(null);
        if(u==null || !encoder.matches(r.password(),u.getPassword()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(java.util.Map.of("error","Invalid credentials"));
        return ResponseEntity.ok(java.util.Map.of(
                "token",jwt.generate(u.getEmail(),u.getRole().name()),
                "email",u.getEmail(),
                "role",u.getRole().name()));
    }
}
