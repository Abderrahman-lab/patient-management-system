package ma.enset.patienttest.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.enset.patienttest.entities.Consultation;
import ma.enset.patienttest.entities.Patient;
import ma.enset.patienttest.entities.TypeNotification;
import ma.enset.patienttest.service.ConsultationService;
import ma.enset.patienttest.service.NotificationService;
import ma.enset.patienttest.service.PatientService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/consultations")
public class ConsultationController {

    private final ConsultationService consultationService;
    private final PatientService patientService;
    private final NotificationService notificationService;

    // ── Liste des consultations d'un patient ──────────────────────
    @GetMapping("/patient/{patientId}")
    public String list(@PathVariable Long patientId, Model model) {
        Patient patient = patientService.getPatientById(patientId);
        model.addAttribute("patient", patient);
        model.addAttribute("consultations", consultationService.getConsultationsByPatient(patientId));
        return "consultations/list";
    }

    // ── Formulaire ajout (ADMIN) ───────────────────────────────────
    @GetMapping("/patient/{patientId}/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addForm(@PathVariable Long patientId, Model model) {
        Patient patient = patientService.getPatientById(patientId);
        Consultation consultation = new Consultation();
        consultation.setDateConsultation(LocalDate.now());
        consultation.setPatient(patient);
        model.addAttribute("consultation", consultation);
        model.addAttribute("patient", patient);
        return "consultations/form";
    }

    // ── Formulaire édition (ADMIN) ─────────────────────────────────
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editForm(@PathVariable Long id, Model model) {
        Consultation consultation = consultationService.getConsultationById(id);
        model.addAttribute("consultation", consultation);
        model.addAttribute("patient", consultation.getPatient());
        return "consultations/form";
    }

    // ── Sauvegarder (ADMIN) ────────────────────────────────────────
    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String save(@Valid @ModelAttribute Consultation consultation,
                       BindingResult result,
                       @RequestParam Long patientId,
                       Model model,
                       Authentication auth) {
        if (result.hasErrors()) {
            model.addAttribute("patient", patientService.getPatientById(patientId));
            return "consultations/form";
        }
        Patient patient = patientService.getPatientById(patientId);
        consultation.setPatient(patient);
        boolean isNew = (consultation.getId() == null);
        consultationService.saveConsultation(consultation);

        if (isNew) {
            String username = auth != null ? auth.getName() : "admin";
            notificationService.creerNotification(
                    "Nouvelle consultation enregistrée",
                    "Une consultation a été enregistrée le " + consultation.getDateConsultation()
                            + " pour " + patient.getNom() + " " + patient.getPrenom()
                            + " — Motif : " + consultation.getMotif(),
                    TypeNotification.INFO,
                    patient,
                    username
            );
        }

        return "redirect:/consultations/patient/" + patientId;
    }

    // ── Supprimer (ADMIN) ──────────────────────────────────────────
    @GetMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        Consultation consultation = consultationService.getConsultationById(id);
        Long patientId = consultation.getPatient().getId();
        consultationService.deleteConsultation(id);
        return "redirect:/consultations/patient/" + patientId;
    }
}
