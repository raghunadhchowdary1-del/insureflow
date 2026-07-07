package com.insureflow.insureflow.config;

import com.insureflow.insureflow.entity.Role;
import com.insureflow.insureflow.entity.User;
import com.insureflow.insureflow.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("admin@insureflow.com").isEmpty()) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@insureflow.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }

        if (userRepository.findByEmail("agent@insureflow.com").isEmpty()) {
            User agent = new User();
            agent.setName("Agent");
            agent.setEmail("agent@insureflow.com");
            agent.setPassword(passwordEncoder.encode("agent123"));
            agent.setRole(Role.AGENT);
            userRepository.save(agent);
        }
    }
}