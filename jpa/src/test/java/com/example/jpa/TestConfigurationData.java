package com.example.jpa;

import com.example.jpa.model.IcesiRole;
import com.example.jpa.model.IcesiUser;
import com.example.jpa.repository.RoleRepository;
import com.example.jpa.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@TestConfiguration
public class TestConfigurationData {

    @Bean
    CommandLineRunner commandLineRunner(UserRepository users, RoleRepository roleRepository, PasswordEncoder encoder) {
        IcesiRole icesiRole = IcesiRole.builder()
                .roleId(UUID.randomUUID())
                .description("Role for demo")
                .name("ADMIN")
                .build();
        IcesiRole icesiRole2 = IcesiRole.builder()
                .roleId(UUID.randomUUID())
                .description("Role for demo")
                .name("USER")
                .build();
        IcesiUser icesiUser = IcesiUser.builder()
                .userId(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .password(encoder.encode("password"))
                .email("johndoe@email.com")
                .phoneNumber("123456789")
                .role(icesiRole)
                .build();
        IcesiUser icesiUser2 = IcesiUser.builder()
                .userId(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .password(encoder.encode("password"))
                .email("johndoe2@email.com")
                .phoneNumber("135791113")
                .role(icesiRole2)
                .build();
        return args -> {
            roleRepository.save(icesiRole);
            roleRepository.save(icesiRole2);
            users.save(icesiUser);
            users.save(icesiUser2);
        };
    }
}