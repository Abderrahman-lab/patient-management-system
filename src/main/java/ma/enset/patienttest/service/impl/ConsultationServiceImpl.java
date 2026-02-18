package ma.enset.patienttest.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ma.enset.patienttest.Exception.ConsultationNotFoundException;
import ma.enset.patienttest.entities.Consultation;
import ma.enset.patienttest.Repositories.ConsultationRepository;
import ma.enset.patienttest.service.ConsultationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ConsultationServiceImpl implements ConsultationService {

    private final ConsultationRepository consultationRepository;

    @Override
    public List<Consultation> getConsultationsByPatient(Long patientId) {
        return consultationRepository.findByPatientIdOrderByDateConsultationDesc(patientId);
    }

    @Override
    public Consultation getConsultationById(Long id) {
        return consultationRepository.findById(id)
                .orElseThrow(() -> new ConsultationNotFoundException(id));
    }

    @Override
    public Consultation saveConsultation(Consultation consultation) {
        return consultationRepository.save(consultation);
    }

    @Override
    public void deleteConsultation(Long id) {
        if (!consultationRepository.existsById(id)) {
            throw new ConsultationNotFoundException(id);
        }
        consultationRepository.deleteById(id);
    }
}
