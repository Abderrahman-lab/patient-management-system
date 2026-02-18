package ma.enset.patienttest.service;

import ma.enset.patienttest.Exception.PatientNotFoundException;
import ma.enset.patienttest.Repositories.PatientRepository;
import ma.enset.patienttest.entities.Patient;
import ma.enset.patienttest.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientServiceImpl patientService;

    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = Patient.builder()
                .id(1L)
                .nom("Benali")
                .prenom("Fatima")
                .dateNaissance(LocalDate.of(1990, 1, 1))
                .malade(false)
                .score(90)
                .build();
    }

    // ── getPatientById ──────────────────────────────────────────────

    @Test
    void getPatientById_shouldReturnPatient_whenExists() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        Patient result = patientService.getPatientById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getNom()).isEqualTo("Benali");
    }

    @Test
    void getPatientById_shouldThrowPatientNotFoundException_whenNotExists() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.getPatientById(99L))
                .isInstanceOf(PatientNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── savePatient ─────────────────────────────────────────────────

    @Test
    void savePatient_shouldReturnSavedPatient() {
        when(patientRepository.save(patient)).thenReturn(patient);

        Patient result = patientService.savePatient(patient);

        assertThat(result).isNotNull();
        assertThat(result.getPrenom()).isEqualTo("Fatima");
        verify(patientRepository, times(1)).save(patient);
    }

    // ── deletePatient ───────────────────────────────────────────────

    @Test
    void deletePatient_shouldDeleteSuccessfully_whenExists() {
        when(patientRepository.existsById(1L)).thenReturn(true);

        patientService.deletePatient(1L);

        verify(patientRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletePatient_shouldThrowPatientNotFoundException_whenNotExists() {
        when(patientRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> patientService.deletePatient(99L))
                .isInstanceOf(PatientNotFoundException.class)
                .hasMessageContaining("99");

        verify(patientRepository, never()).deleteById(any());
    }

    // ── searchPatients ──────────────────────────────────────────────

    @Test
    void searchPatients_shouldReturnPageOfPatients() {
        Page<Patient> page = new PageImpl<>(List.of(patient));
        when(patientRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
                "ben", "ben", PageRequest.of(0, 5))).thenReturn(page);

        Page<Patient> result = patientService.searchPatients("ben", 0, 5);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getNom()).isEqualTo("Benali");
    }

    // ── getAllPatients ──────────────────────────────────────────────

    @Test
    void getAllPatients_shouldReturnAllPatients() {
        when(patientRepository.findAll()).thenReturn(List.of(patient));

        List<Patient> result = patientService.getAllPatients();

        assertThat(result).hasSize(1);
    }
}
