package ma.enset.patienttest.web;

import ma.enset.patienttest.entities.Patient;
import ma.enset.patienttest.service.ConsultationService;
import ma.enset.patienttest.service.NotificationService;
import ma.enset.patienttest.service.PatientService;
import ma.enset.patienttest.service.PdfService;
import ma.enset.patienttest.service.RendezVousService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientService patientService;

    @MockitoBean
    private ConsultationService consultationService;

    @MockitoBean
    private RendezVousService rendezVousService;

    @MockitoBean
    private PdfService pdfService;

    @MockitoBean
    private NotificationService notificationService;

    // ── GET /index ──────────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "USER")
    void index_shouldReturn200_forAuthenticatedUser() throws Exception {
        Patient patient = Patient.builder()
                .id(1L).nom("Benali").prenom("Fatima")
                .dateNaissance(LocalDate.of(1990, 1, 1))
                .malade(false).score(90).build();

        when(patientService.searchPatients(anyString(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(patient)));

        mockMvc.perform(get("/index"))
                .andExpect(status().isOk())
                .andExpect(view().name("patients/index"))
                .andExpect(model().attributeExists("patients"));
    }

    @Test
    void index_shouldRedirectToLogin_forUnauthenticatedUser() throws Exception {
        mockMvc.perform(get("/index"))
                .andExpect(status().is3xxRedirection());
    }

    // ── GET /admin/addPatient ───────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void addForm_shouldReturn200_forAdmin() throws Exception {
        mockMvc.perform(get("/admin/addPatient"))
                .andExpect(status().isOk())
                .andExpect(view().name("patients/form"))
                .andExpect(model().attributeExists("patient"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void addForm_shouldReturn403_forUser() throws Exception {
        mockMvc.perform(get("/admin/addPatient"))
                .andExpect(status().isForbidden());
    }

    // ── GET /admin/deletePatient/{id} ───────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_shouldRedirectToIndex_afterDeletion() throws Exception {
        Patient patient = Patient.builder()
                .id(1L).nom("Benali").prenom("Fatima").build();
        when(patientService.getPatientById(1L)).thenReturn(patient);

        mockMvc.perform(get("/admin/deletePatient/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/index*"));
    }

    // ── POST /admin/savePatient ─────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void save_shouldRedirectToIndex_whenValidPatient() throws Exception {
        Patient saved = Patient.builder()
                .id(1L).nom("Benali").prenom("Fatima")
                .dateNaissance(LocalDate.of(1990, 1, 1))
                .malade(false).score(80).build();

        when(patientService.savePatient(any(Patient.class))).thenReturn(saved);

        mockMvc.perform(post("/admin/savePatient")
                        .with(csrf())
                        .param("nom", "Benali")
                        .param("prenom", "Fatima")
                        .param("dateNaissance", "1990-01-01")
                        .param("malade", "false")
                        .param("score", "80"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/index*"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void save_shouldReturnForm_whenValidationFails() throws Exception {
        mockMvc.perform(post("/admin/savePatient")
                        .with(csrf())
                        .param("nom", "")       // invalide : champ obligatoire
                        .param("prenom", "")
                        .param("score", "150")) // invalide : > 100
                .andExpect(status().isOk())
                .andExpect(view().name("patients/form"));
    }
}
