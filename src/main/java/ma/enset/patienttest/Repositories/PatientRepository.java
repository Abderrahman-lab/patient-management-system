package ma.enset.patienttest.Repositories;


import ma.enset.patienttest.entities.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // Recherche par nom ou prénom (insensible à la casse)
    Page<Patient> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
            String nom, String prenom, Pageable pageable);

    // Dashboard : compteurs
    long countByMalade(boolean malade);

    // Dashboard : distribution des groupes sanguins
    @Query("SELECT p.groupeSanguin, COUNT(p) FROM Patient p WHERE p.groupeSanguin IS NOT NULL GROUP BY p.groupeSanguin")
    List<Object[]> countByGroupeSanguinGrouped();

    Optional<Patient> findByNumeroSecuriteSociale(String numeroSecuriteSociale);
}
