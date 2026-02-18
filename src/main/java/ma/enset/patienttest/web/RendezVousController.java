package ma.enset.patienttest.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.enset.patienttest.entities.Patient;
import ma.enset.patienttest.entities.RendezVous;
import ma.enset.patienttest.entities.StatutRendezVous;
import ma.enset.patienttest.entities.TypeNotification;
import ma.enset.patienttest.service.NotificationService;
import ma.enset.patienttest.service.PatientService;
import ma.enset.patienttest.service.RendezVousService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
@RequiredArgsConstructor
@RequestMapping("/rendez-vous")
public class RendezVousController {

    private final RendezVousService rendezVousService;
    private final PatientService patientService;
    private final NotificationService notificationService;

    // ── Liste des rendez-vous d'un patient ───────────────────────
    @GetMapping("/patient/{patientId}")
    public String list(@PathVariable Long patientId, Model model) {
        Patient patient = patientService.getPatientById(patientId);
        model.addAttribute("patient", patient);
        model.addAttribute("rendezVousList", rendezVousService.getRendezVousByPatient(patientId));
        model.addAttribute("allStatuts", StatutRendezVous.values());
        return "rendez-vous/list";
    }

    // ── Formulaire ajout (ADMIN) ──────────────────────────────────
    @GetMapping("/patient/{patientId}/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addForm(@PathVariable Long patientId, Model model) {
        Patient patient = patientService.getPatientById(patientId);
        RendezVous rdv = new RendezVous();
        rdv.setDateRendezVous(LocalDate.now());
        rdv.setHeureRendezVous(LocalTime.of(9, 0));
        rdv.setStatut(StatutRendezVous.PLANIFIE);
        rdv.setPatient(patient);
        model.addAttribute("rendezVous", rdv);
        model.addAttribute("patient", patient);
        model.addAttribute("allStatuts", StatutRendezVous.values());
        return "rendez-vous/form";
    }

    // ── Formulaire édition (ADMIN) ────────────────────────────────
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editForm(@PathVariable Long id, Model model) {
        RendezVous rdv = rendezVousService.getRendezVousById(id);
        model.addAttribute("rendezVous", rdv);
        model.addAttribute("patient", rdv.getPatient());
        model.addAttribute("allStatuts", StatutRendezVous.values());
        return "rendez-vous/form";
    }

    // ── Sauvegarder (ADMIN) ───────────────────────────────────────
    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String save(@Valid @ModelAttribute("rendezVous") RendezVous rdv,
                       BindingResult result,
                       @RequestParam Long patientId,
                       Model model,
                       Authentication auth) {
        if (result.hasErrors()) {
            Patient patient = patientService.getPatientById(patientId);
            model.addAttribute("patient", patient);
            model.addAttribute("allStatuts", StatutRendezVous.values());
            return "rendez-vous/form";
        }
        Patient patient = patientService.getPatientById(patientId);
        rdv.setPatient(patient);
        boolean isNew = (rdv.getId() == null);
        rendezVousService.saveRendezVous(rdv);

        String username = auth != null ? auth.getName() : "admin";
        if (isNew) {
            notificationService.creerNotification(
                    "Nouveau rendez-vous planifié",
                    "Un rendez-vous a été planifié le " + rdv.getDateRendezVous()
                            + " à " + rdv.getHeureRendezVous()
                            + " pour " + patient.getNom() + " " + patient.getPrenom()
                            + " — Motif : " + rdv.getMotif(),
                    TypeNotification.RAPPEL,
                    patient,
                    username
            );
        }

        return "redirect:/rendez-vous/patient/" + patientId;
    }

    // ── Supprimer (ADMIN) ─────────────────────────────────────────
    @GetMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        RendezVous rdv = rendezVousService.getRendezVousById(id);
        Long patientId = rdv.getPatient().getId();
        rendezVousService.deleteRendezVous(id);
        return "redirect:/rendez-vous/patient/" + patientId;
    }

    // ── Changer le statut (ADMIN) ─────────────────────────────────
    @PostMapping("/{id}/statut")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateStatut(@PathVariable Long id,
                                @RequestParam StatutRendezVous statut,
                                Authentication auth) {
        RendezVous rdv = rendezVousService.updateStatut(id, statut);
        Patient patient = rdv.getPatient();

        String username = auth != null ? auth.getName() : "admin";
        TypeNotification type = (statut == StatutRendezVous.ANNULE) ? TypeNotification.ALERTE : TypeNotification.INFO;
        notificationService.creerNotification(
                "Statut rendez-vous modifié",
                "Le rendez-vous du " + rdv.getDateRendezVous()
                        + " pour " + patient.getNom() + " " + patient.getPrenom()
                        + " est maintenant : " + statut,
                type,
                patient,
                username
        );

        return "redirect:/rendez-vous/patient/" + patient.getId();
    }
}
