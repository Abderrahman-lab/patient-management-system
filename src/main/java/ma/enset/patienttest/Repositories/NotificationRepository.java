package ma.enset.patienttest.Repositories;

import ma.enset.patienttest.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Notifications non-lues pour un destinataire (ou "ALL")
    List<Notification> findByDestinataireAndLueFalseOrderByDateCreationDesc(String destinataire);

    // Compteur non-lues
    long countByDestinataireAndLueFalse(String destinataire);

    // Toutes les notifications d'un destinataire (lues + non-lues)
    List<Notification> findByDestinataireOrderByDateCreationDesc(String destinataire);
}
