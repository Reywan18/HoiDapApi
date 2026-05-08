package com.hoidap.hoidapdemo;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import com.hoidap.hoidapdemo.repository.admin.AdminJpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class HoidapdemoApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

		SpringApplication.run(HoidapdemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner updateAdminAccount(AdminJpaRepository adminRepo, PasswordEncoder passwordEncoder) {
		return args -> {
			adminRepo.findByEmail("admin").ifPresent(admin -> {
				admin.setEmail("admin@thanglong.edu.vn");
				admin.setPassword(passwordEncoder.encode("111111"));
				adminRepo.save(admin);
				System.out.println("====== Da cap nhat tai khoan admin thanh admin@thanglong.edu.vn ======");
			});
		};
	}

}
