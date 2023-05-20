package com.icesi.TallerJPA;

import com.icesi.TallerJPA.model.IcesiPermits;
import com.icesi.TallerJPA.model.IcesiRole;
import com.icesi.TallerJPA.model.IcesiUser;
import com.icesi.TallerJPA.repository.RoleRepository;
import com.icesi.TallerJPA.repository.UserRespository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class TallerJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TallerJpaApplication.class, args);
	}

	//@Bean
	public CommandLineRunner commandLineRunner(UserRespository users, RoleRepository role, PasswordEncoder encoder) {

		IcesiPermits rolePermission = IcesiPermits.builder()
				.permitId(UUID.randomUUID())
				.keyIdentifier("admin")
				.path("/role/**")
				.build();

		IcesiPermits userPermission = IcesiPermits.builder()
				.permitId(UUID.randomUUID())
				.keyIdentifier("admin")
				.path("/user/**")
				.build();

		IcesiPermits accountNoEnablePermission = IcesiPermits.builder()
				.permitId(UUID.randomUUID())
				.keyIdentifier("user")
				.path("/account/inactiveAccount/**")
				.build();

		IcesiPermits accountEnablePermission = IcesiPermits.builder()
				.permitId(UUID.randomUUID())
				.keyIdentifier("user")
				.path("/account/activeAccount/**")
				.build();

		IcesiPermits accountPermission = IcesiPermits.builder()
				.permitId(UUID.randomUUID())
				.keyIdentifier("user")
				.path("/account")
				.build();

		IcesiPermits userBankPermission = IcesiPermits.builder()
				.permitId(UUID.randomUUID())
				.keyIdentifier("bank")
				.path("/user/**")
				.build();


		IcesiRole adminRole = IcesiRole.builder()
				.roleId(UUID.randomUUID())
				.description("Role for demo")
				.name("ADMIN")
				.permissionList(List.of(rolePermission, userPermission))
				.build();

		IcesiRole userRole = IcesiRole.builder()
				.roleId(UUID.randomUUID())
				.description("Role for demo")
				.name("USER")
				.permissionList(List.of(accountNoEnablePermission, accountEnablePermission, accountPermission))
				.build();

		IcesiRole bankRole = IcesiRole.builder()
				.roleId(UUID.randomUUID())
				.description("Role for demo")
				.name("BANK")
				.permissionList(List.of(userBankPermission))
				.build();

		IcesiUser admin = IcesiUser.builder()
				.userId(UUID.randomUUID())
				.email("admin@email.com")
				.icesiRole(adminRole)
				.firstName("John")
				.lastName("Doe")
				.phoneNumber("+573226227443")
				.password(encoder.encode("password"))
				.build();

		IcesiUser user = IcesiUser.builder()
				.userId(UUID.randomUUID())
				.email("user@email.com")
				.icesiRole(userRole)
				.firstName("John")
				.lastName("Doe")
				.phoneNumber("+573127312289")
				.password(encoder.encode("password"))
				.build();

		IcesiUser bank = IcesiUser.builder()
				.userId(UUID.randomUUID())
				.email("bank@email.com")
				.icesiRole(bankRole)
				.firstName("John")
				.lastName("Doe")
				.phoneNumber("+573137142007")
				.password(encoder.encode("password"))
				.build();

		return args -> {
			role.save(adminRole);
			role.save(userRole);
			role.save(bankRole);
			users.save(bank);
			users.save(admin);
			users.save(user);
		};
	}

}