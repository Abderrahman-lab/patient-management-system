package ma.enset.patienttest.Config;

import lombok.RequiredArgsConstructor;
import ma.enset.patienttest.entities.AppRole;
import ma.enset.patienttest.entities.AppUser;
import ma.enset.patienttest.entities.Patient;
import ma.enset.patienttest.Repositories.PatientRepository;
import ma.enset.patienttest.Repositories.RoleRepository;
import ma.enset.patienttest.Repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initData(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PatientRepository patientRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // ── Rôles ──────────────────────────────────────────────
            AppRole adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() -> roleRepository.save(
                            AppRole.builder().name("ROLE_ADMIN").build()));

            AppRole userRole = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> roleRepository.save(
                            AppRole.builder().name("ROLE_USER").build()));

            // ── Utilisateurs ───────────────────────────────────────
            if (userRepository.findByUsername("admin").isEmpty()) {
                userRepository.save(AppUser.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .enabled(true)
                        .roles(Set.of(adminRole))
                        .build());
                log.info("Utilisateur 'admin' cree avec succes.");
            }

            if (userRepository.findByUsername("user").isEmpty()) {
                userRepository.save(AppUser.builder()
                        .username("user")
                        .password(passwordEncoder.encode("user123"))
                        .enabled(true)
                        .roles(Set.of(userRole))
                        .build());
                log.info("Utilisateur 'user' cree avec succes.");
            }

            // ── Patients de test ───────────────────────────────────
            if (patientRepository.count() == 0) {
                patientRepository.save(Patient.builder().nom("El Amrani").prenom("Youssef")
                        .dateNaissance(LocalDate.of(1990, 5, 12)).malade(true).score(78).build());
                patientRepository.save(Patient.builder().nom("Benali").prenom("Fatima")
                        .dateNaissance(LocalDate.of(1985, 3, 22)).malade(false).score(95).build());
                patientRepository.save(Patient.builder().nom("Cherkaoui").prenom("Mehdi")
                        .dateNaissance(LocalDate.of(1978, 11, 7)).malade(true).score(45).build());
                patientRepository.save(Patient.builder().nom("Idrissi").prenom("Sara")
                        .dateNaissance(LocalDate.of(1995, 8, 30)).malade(false).score(88).build());
                patientRepository.save(Patient.builder().nom("Tazi").prenom("Omar")
                        .dateNaissance(LocalDate.of(1982, 1, 15)).malade(true).score(60).build());
                patientRepository.save(Patient.builder().nom("Hassani").prenom("Nadia")
                        .dateNaissance(LocalDate.of(1993, 6, 19)).malade(false).score(92).build());
                log.info("6 patients de test crees avec succes.");
            }
        };
    }
}
