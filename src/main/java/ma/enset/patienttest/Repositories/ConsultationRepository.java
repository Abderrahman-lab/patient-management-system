package ma.enset.patienttest.Repositories;

import ma.enset.patienttest.entities.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    List<Consultation> findByPatientIdOrderByDateConsultationDesc(Long patientId);
}
