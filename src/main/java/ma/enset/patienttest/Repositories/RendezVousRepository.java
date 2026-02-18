package ma.enset.patienttest.Repositories;

import ma.enset.patienttest.entities.RendezVous;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {

    List<RendezVous> findByPatientIdOrderByDateRendezVousDescHeureRendezVousDesc(Long patientId);

    // Dashboard : RDV d'un jour précis
    List<RendezVous> findByDateRendezVousOrderByHeureRendezVousAsc(LocalDate date);

    // Dashboard : RDV entre deux dates (agenda semaine)
    List<RendezVous> findByDateRendezVousBetweenOrderByDateRendezVousAscHeureRendezVousAsc(LocalDate start, LocalDate end);
}
