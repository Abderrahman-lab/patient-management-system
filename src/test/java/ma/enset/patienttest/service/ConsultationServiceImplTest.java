package ma.enset.patienttest.service;

import ma.enset.patienttest.Exception.ConsultationNotFoundException;
import ma.enset.patienttest.Repositories.ConsultationRepository;
import ma.enset.patienttest.entities.Consultation;
import ma.enset.patienttest.entities.Patient;
import ma.enset.patienttest.service.impl.ConsultationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultationServiceImplTest {

    @Mock
    private ConsultationRepository consultationRepository;

    @InjectMocks
    private ConsultationServiceImpl consultationService;

    private Consultation consultation;
    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = Patient.builder()
                .id(1L)
                .nom("Benali")
                .prenom("Fatima")
                .build();

        consultation = Consultation.builder()
                .id(10L)
                .dateConsultation(LocalDate.of(2024, 6, 15))
                .motif("Fièvre")
                .patient(patient)
                .build();
    }

    // ── getConsultationById ─────────────────────────────────────────

    @Test
    void getConsultationById_shouldReturnConsultation_whenExists() {
        when(consultationRepository.findById(10L)).thenReturn(Optional.of(consultation));

        Consultation result = consultationService.getConsultationById(10L);

        assertThat(result).isNotNull();
        assertThat(result.getMotif()).isEqualTo("Fièvre");
    }

    @Test
    void getConsultationById_shouldThrowConsultationNotFoundException_whenNotExists() {
        when(consultationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> consultationService.getConsultationById(99L))
                .isInstanceOf(ConsultationNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── deleteConsultation ──────────────────────────────────────────

    @Test
    void deleteConsultation_shouldDeleteSuccessfully_whenExists() {
        when(consultationRepository.existsById(10L)).thenReturn(true);

        consultationService.deleteConsultation(10L);

        verify(consultationRepository, times(1)).deleteById(10L);
    }

    @Test
    void deleteConsultation_shouldThrowConsultationNotFoundException_whenNotExists() {
        when(consultationRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> consultationService.deleteConsultation(99L))
                .isInstanceOf(ConsultationNotFoundException.class)
                .hasMessageContaining("99");

        verify(consultationRepository, never()).deleteById(any());
    }

    // ── getConsultationsByPatient ───────────────────────────────────

    @Test
    void getConsultationsByPatient_shouldReturnConsultationsOrderedByDate() {
        when(consultationRepository.findByPatientIdOrderByDateConsultationDesc(1L))
                .thenReturn(List.of(consultation));

        List<Consultation> result = consultationService.getConsultationsByPatient(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMotif()).isEqualTo("Fièvre");
    }

    // ── saveConsultation ────────────────────────────────────────────

    @Test
    void saveConsultation_shouldReturnSavedConsultation() {
        when(consultationRepository.save(consultation)).thenReturn(consultation);

        Consultation result = consultationService.saveConsultation(consultation);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        verify(consultationRepository, times(1)).save(consultation);
    }
}
