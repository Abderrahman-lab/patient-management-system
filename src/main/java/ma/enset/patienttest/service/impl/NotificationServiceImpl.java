package ma.enset.patienttest.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.patienttest.Repositories.NotificationRepository;
import ma.enset.patienttest.entities.Notification;
import ma.enset.patienttest.entities.Patient;
import ma.enset.patienttest.entities.TypeNotification;
import ma.enset.patienttest.service.NotificationService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class    NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    @Override
    public void creerNotification(String titre, String message, TypeNotification type,
                                  Patient patient, String destinataire) {
        Notification notification = Notification.builder()
                .titre(titre)
                .message(message)
                .type(type)
                .patient(patient)
                .destinataire(destinataire)
                .build();
        notificationRepository.save(notification);

        // Envoi email vers l'adresse du cabinet
        try {
            String sujet = "[Gestion Patients] " + titre;
            String contenu = "Notification : " + titre + "\n\n" + message;
            if (patient != null) {
                contenu += "\n\nPatient concerné : " + patient.getNom() + " " + patient.getPrenom();
            }
            envoyerEmail("admin@cabinet.fr", sujet, contenu);
        } catch (Exception e) {
            log.warn("Échec de l'envoi email pour la notification '{}' : {}", titre, e.getMessage());
        }
    }

    @Override
    public List<Notification> getNotificationsNonLues(String username) {
        return notificationRepository
                .findByDestinataireAndLueFalseOrderByDateCreationDesc(username);
    }

    @Override
    public long compterNonLues(String username) {
        return notificationRepository.countByDestinataireAndLueFalse(username);
    }

    @Override
    public void marquerCommeLue(Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setLue(true);
            notificationRepository.save(n);
        });
    }

    @Override
    public void marquerToutesCommeLues(String username) {
        List<Notification> nonLues = notificationRepository
                .findByDestinataireAndLueFalseOrderByDateCreationDesc(username);
        nonLues.forEach(n -> n.setLue(true));
        notificationRepository.saveAll(nonLues);
    }

    @Override
    public List<Notification> getToutesNotifications(String username) {
        return notificationRepository
                .findByDestinataireOrderByDateCreationDesc(username);
    }

    @Override
    public void envoyerEmail(String to, String sujet, String contenu) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(to);
            mail.setSubject(sujet);
            mail.setText(contenu);
            mailSender.send(mail);
            log.info("Email envoyé à {} : {}", to, sujet);
        } catch (Exception e) {
            log.error("Erreur envoi email à {} : {}", to, e.getMessage());
        }
    }
}
