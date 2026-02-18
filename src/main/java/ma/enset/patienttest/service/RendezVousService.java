package ma.enset.patienttest.service;

import ma.enset.patienttest.entities.RendezVous;
import ma.enset.patienttest.entities.StatutRendezVous;

import java.util.List;

public interface RendezVousService {

    List<RendezVous> getRendezVousByPatient(Long patientId);

    RendezVous getRendezVousById(Long id);

    RendezVous saveRendezVous(RendezVous rdv);

    void deleteRendezVous(Long id);

    RendezVous updateStatut(Long id, StatutRendezVous statut);
}
