package ma.enset.patienttest.web;

import lombok.RequiredArgsConstructor;
import ma.enset.patienttest.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationRestController {

    private final NotificationService notificationService;

    /**
     * Retourne le nombre de notifications non-lues pour le badge de la navbar.
     * Appelé en JS au chargement de chaque page.
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> count(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.ok(Map.of("count", 0L));
        }
        long count = notificationService.compterNonLues(auth.getName());
        return ResponseEntity.ok(Map.of("count", count));
    }
}
