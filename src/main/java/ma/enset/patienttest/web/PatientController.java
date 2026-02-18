package ma.enset.patienttest.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.enset.patienttest.entities.Patient;
import ma.enset.patienttest.entities.TypeNotification;
import ma.enset.patienttest.service.ConsultationService;
import ma.enset.patienttest.service.NotificationService;
import ma.enset.patienttest.service.PatientService;
import ma.enset.patienttest.service.PdfService;
import ma.enset.patienttest.service.RendezVousService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final ConsultationService consultationService;
    private final RendezVousService rendezVousService;
    private final PdfService pdfService;
    private final NotificationService notificationService;

    // ── Liste des patients (accessible à tous les connectés) ──────
    @GetMapping("/index")
    public String index(Model model,
                        @RequestParam(defaultValue = "0")  int page,
                        @RequestParam(defaultValue = "5")  int size,
                        @RequestParam(defaultValue = "")   String keyword) {

        Page<Patient> patients = patientService.searchPatients(keyword, page, size);
        int totalPages = patients.getTotalPages();

        model.addAttribute("patients",     patients.getContent());
        model.addAttribute("totalPages",   totalPages);
        model.addAttribute("currentPage",  page);
        model.addAttribute("keyword",      keyword);

        return "patients/index";
    }

    // ── Détails d'un patient ───────────────────────────────────────
    @GetMapping("/patient/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("patient", patientService.getPatientById(id));
        return "patients/detail";
    }

    // ── Formulaire d'ajout (ADMIN) ─────────────────────────────────
    @GetMapping("/admin/addPatient")
    @PreAuthorize("hasRole('ADMIN')")
    public String addForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "patients/form";
    }

    // ── Formulaire d'édition (ADMIN) ───────────────────────────────
    @GetMapping("/admin/editPatient/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editForm(@PathVariable Long id, Model model) {
        Patient patient = patientService.getPatientById(id);
        model.addAttribute("patient", patient);
        return "patients/form";
    }

    // ── Sauvegarder (ADMIN) ────────────────────────────────────────
    @PostMapping("/admin/savePatient")
    @PreAuthorize("hasRole('ADMIN')")
    public String save(@Valid @ModelAttribute Patient patient,
                       BindingResult result,
                       @RequestParam(defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "0") int page,
                       Authentication auth) {

        if (result.hasErrors()) return "patients/form";

        // Vérifier que le numéro de sécurité sociale n'appartient pas à un autre patient
        if (patient.getNumeroSecuriteSociale() != null && !patient.getNumeroSecuriteSociale().isBlank()) {
            patientService.findByNumeroSecuriteSociale(patient.getNumeroSecuriteSociale())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(patient.getId())) {
                            result.rejectValue("numeroSecuriteSociale", "duplicate",
                                    "Ce numéro de sécurité sociale est déjà utilisé par un autre patient.");
                        }
                    });
        }
        if (result.hasErrors()) return "patients/form";

        boolean isNew = (patient.getId() == null);
        Patient saved = patientService.savePatient(patient);

        String username = auth != null ? auth.getName() : "admin";
        if (isNew) {
            notificationService.creerNotification(
                    "Nouveau patient ajouté",
                    "Le patient " + saved.getNom() + " " + saved.getPrenom() + " a été ajouté.",
                    TypeNotification.INFO,
                    saved,
                    username
            );
        } else {
            notificationService.creerNotification(
                    "Patient modifié",
                    "Le dossier de " + saved.getNom() + " " + saved.getPrenom() + " a été mis à jour.",
                    TypeNotification.INFO,
                    saved,
                    username
            );
        }

        return "redirect:/index?page=" + page + "&keyword=" + keyword;
    }

    // ── Supprimer (ADMIN) ──────────────────────────────────────────
    @GetMapping("/admin/deletePatient/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id,
                         @RequestParam(defaultValue = "") String keyword,
                         @RequestParam(defaultValue = "0") int page,
                         Authentication auth) {

        Patient patient = patientService.getPatientById(id);
        String nom = patient.getNom() + " " + patient.getPrenom();
        patientService.deletePatient(id);

        String username = auth != null ? auth.getName() : "admin";
        notificationService.creerNotification(
                "Patient supprimé",
                "Le patient " + nom + " a été supprimé du système.",
                TypeNotification.ALERTE,
                null,
                username
        );

        return "redirect:/index?page=" + page + "&keyword=" + keyword;
    }

    // ── Export PDF de la fiche patient ────────────────────────────
    @GetMapping("/patient/{id}/pdf")
    public ResponseEntity<byte[]> exportPdf(@PathVariable Long id) {
        Patient patient = patientService.getPatientById(id);
        byte[] pdf = pdfService.genererFichePatient(
                patient,
                consultationService.getConsultationsByPatient(id),
                rendezVousService.getRendezVousByPatient(id)
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment",
                "patient-" + patient.getNom() + "-" + patient.getPrenom() + ".pdf");
        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    // ── Page accès refusé ──────────────────────────────────────────
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/access-denied";
    }
}
