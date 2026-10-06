package com.anushka.parcel.auth.service;

import com.anushka.parcel.auth.dto.*;
import com.anushka.parcel.auth.exception.BadRequestException;
import com.anushka.parcel.auth.exception.UnauthorizedException;
import com.anushka.parcel.auth.model.Role;
import com.anushka.parcel.auth.model.User;
import com.anushka.parcel.auth.repository.UserRepository;
import com.anushka.parcel.auth.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository users, PasswordEncoder encoder, JwtUtil jwtUtil) {
        this.users = users;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest r) {
        String email = r.email().trim().toLowerCase();
        if (users.existsByEmail(email)) {
            throw new BadRequestException("Email already registered");
        }
        User u = new User();
        u.setName(r.name().trim());
        u.setEmail(email);
        u.setPassword(encoder.encode(r.password()));
        // DEMO ONLY: lets you create ADMIN/AGENT users easily. In production, only admins may assign roles.
        u.setRole(r.role() == null ? Role.CUSTOMER : r.role());
        users.save(u);
        return toResponse(u);
    }

    public AuthResponse login(LoginRequest r) {
        User u = users.findByEmail(r.email().trim().toLowerCase())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
        if (!encoder.matches(r.password(), u.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }
        return toResponse(u);
    }

    private AuthResponse toResponse(User u) {
        String token = jwtUtil.generate(u.getEmail(), u.getRole().name());
        return new AuthResponse(token, "Bearer", u.getName(), u.getEmail(), u.getRole().name());
    }
}
