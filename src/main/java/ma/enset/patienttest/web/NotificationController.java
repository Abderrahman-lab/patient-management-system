package ma.enset.patienttest.web;

import lombok.RequiredArgsConstructor;
import ma.enset.patienttest.entities.Notification;
import ma.enset.patienttest.service.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    // ── Liste de toutes les notifications de l'utilisateur connecté ──
    @GetMapping
    public String list(Authentication auth, Model model) {
        String username = auth.getName();
        List<Notification> toutes = notificationService.getToutesNotifications(username);
        long nonLues = notificationService.compterNonLues(username);
        model.addAttribute("notifications", toutes);
        model.addAttribute("nonLues", nonLues);
        return "notifications/list";
    }

    // ── Marquer une notification comme lue ────────────────────────
    @GetMapping("/{id}/lire")
    public String marquerLue(@PathVariable Long id) {
        notificationService.marquerCommeLue(id);
        return "redirect:/notifications";
    }

    // ── Marquer toutes comme lues ─────────────────────────────────
    @PostMapping("/tout-lire")
    public String marquerToutesLues(Authentication auth) {
        notificationService.marquerToutesCommeLues(auth.getName());
        return "redirect:/notifications";
    }
}
