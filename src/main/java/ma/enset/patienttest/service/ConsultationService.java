package ma.enset.patienttest.service;

import ma.enset.patienttest.entities.Consultation;

import java.util.List;

public interface ConsultationService {
    List<Consultation> getConsultationsByPatient(Long patientId);
    Consultation getConsultationById(Long id);
    Consultation saveConsultation(Consultation consultation);
    void deleteConsultation(Long id);
}
