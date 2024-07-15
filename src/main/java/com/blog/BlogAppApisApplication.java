package com.blog;

import com.blog.auth.entities.Role;
import com.blog.auth.repositories.RoleRepository;
import com.blog.utils.AppConstants;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.AbstractEnvironment;

import java.util.Set;

@SpringBootApplication
public class BlogAppApisApplication implements CommandLineRunner {// interface này có 1 method duy nhất là run dùng để thực hiện việc khởi tạo
	private final RoleRepository roleRepository;

    public BlogAppApisApplication(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public static void main(String[] args) {
		System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "prod");
		SpringApplication.run(BlogAppApisApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Role roleUser = new Role();
		Role roleAdmin = new Role();

		roleUser.setRoleId(AppConstants.ROLE_USER);
		roleAdmin.setRoleId(AppConstants.ROLE_ADMIN);

		roleUser.setName("ROLE_USER");
		roleAdmin.setName("ROLE_ADMIN");

		Set<Role> roles = Set.of(roleUser, roleAdmin);

		roleRepository.saveAll(roles);
	}
}
