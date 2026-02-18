package ma.enset.patienttest.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ma.enset.patienttest.Exception.PatientNotFoundException;
import ma.enset.patienttest.Repositories.ConsultationRepository;
import ma.enset.patienttest.Repositories.PatientRepository;
import ma.enset.patienttest.Repositories.RendezVousRepository;
import ma.enset.patienttest.entities.Patient;
import ma.enset.patienttest.service.PatientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final ConsultationRepository consultationRepository;
    private final RendezVousRepository rendezVousRepository;

    @Override
    public Page<Patient> searchPatients(String keyword, int page, int size) {
        return patientRepository
                .findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
                        keyword, keyword, PageRequest.of(page, size));
    }

    @Override
    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));
    }

    @Override
    public Patient savePatient(Patient patient) {
        // Convertir les chaînes vides en null pour les champs uniques
        if (patient.getNumeroSecuriteSociale() != null && patient.getNumeroSecuriteSociale().isBlank()) {
            patient.setNumeroSecuriteSociale(null);
        }
        return patientRepository.save(patient);
    }

    @Override
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new PatientNotFoundException(id);
        }
        rendezVousRepository.deleteAll(
                rendezVousRepository.findByPatientIdOrderByDateRendezVousDescHeureRendezVousDesc(id));
        consultationRepository.deleteAll(
                consultationRepository.findByPatientIdOrderByDateConsultationDesc(id));
        patientRepository.deleteById(id);
    }

    @Override
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @Override
    public Optional<Patient> findByNumeroSecuriteSociale(String numeroSecuriteSociale) {
        return patientRepository.findByNumeroSecuriteSociale(numeroSecuriteSociale);
    }
}
