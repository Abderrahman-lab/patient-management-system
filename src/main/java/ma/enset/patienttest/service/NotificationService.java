package ma.enset.patienttest.service;

import ma.enset.patienttest.entities.Notification;
import ma.enset.patienttest.entities.Patient;
import ma.enset.patienttest.entities.TypeNotification;

import java.util.List;

public interface NotificationService {

    void creerNotification(String titre, String message, TypeNotification type,
                           Patient patient, String destinataire);

    List<Notification> getNotificationsNonLues(String username);

    long compterNonLues(String username);

    void marquerCommeLue(Long id);

    void marquerToutesCommeLues(String username);

    List<Notification> getToutesNotifications(String username);

    void envoyerEmail(String to, String sujet, String contenu);
}
