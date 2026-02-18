package ma.enset.patienttest.service;

import ma.enset.patienttest.entities.Patient;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface PatientService {
    Page<Patient> searchPatients(String keyword, int page, int size);
    Patient getPatientById(Long id);
    Patient savePatient(Patient patient);
    void deletePatient(Long id);
    List<Patient> getAllPatients();
    Optional<Patient> findByNumeroSecuriteSociale(String numeroSecuriteSociale);
}
