package ng.farmsPot.config;

import ng.farmsPot.data.models.Farmer;
import ng.farmsPot.data.models.Role;
import ng.farmsPot.data.repositories.FarmersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class AdminSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminSeeder.class);

    private final FarmersRepository farmersRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final String userName;
    private final String password;
    private final String fullName;
    private final String email;
    private final String phoneNumber;

    public AdminSeeder(FarmersRepository farmersRepository,
                       @Value("${app.admin.username:}") String userName,
                       @Value("${app.admin.password:}") String password,
                       @Value("${app.admin.full-name:FarmsPot Admin}") String fullName,
                       @Value("${app.admin.email:}") String email,
                       @Value("${app.admin.phone-number:0000000000}") String phoneNumber) {
        this.farmersRepository = farmersRepository;
        this.userName = userName;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void run(String... args) {
        if (userName.isBlank() || password.isBlank()) {
            logger.info("ADMIN_USERNAME / ADMIN_PASSWORD not set, skipping admin setup.");
            return;
        }

        Optional<Farmer> existing = farmersRepository.findByUserName(userName);
        if (existing.isPresent()) {
            // Never promote an existing account: anyone could have registered this username first.
            if (existing.get().getRole() != Role.ADMIN) {
                logger.warn("Admin was not created: a regular user named '{}' already exists. "
                        + "Choose a different ADMIN_USERNAME.", userName);
            }
            return;
        }

        if (farmersRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            logger.warn("Admin was not created: phone number {} is already used. Set the admin phone number to a different value.", phoneNumber);
            return;
        }

        Farmer admin = new Farmer();
        admin.setFullName(fullName);
        admin.setUserName(userName);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setEmail(email.isBlank() ? null : email);
        admin.setPhoneNumber(phoneNumber);
        admin.setFarmLocation("FarmsPot HQ");
        admin.setRole(Role.ADMIN);
        farmersRepository.save(admin);
        logger.info("Admin user '{}' created.", userName);
    }
}