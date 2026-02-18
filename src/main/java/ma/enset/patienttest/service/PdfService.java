package ma.enset.patienttest.service;

import ma.enset.patienttest.entities.Consultation;
import ma.enset.patienttest.entities.Patient;
import ma.enset.patienttest.entities.RendezVous;

import java.util.List;

public interface PdfService {

    /**
     * Génère la fiche complète d'un patient au format PDF.
     *
     * @param patient       Le patient
     * @param consultations Ses consultations (peut être vide)
     * @param rendezVous    Ses rendez-vous (peut être vide)
     * @return Tableau de bytes représentant le fichier PDF
     */
    byte[] genererFichePatient(Patient patient,
                               List<Consultation> consultations,
                               List<RendezVous> rendezVous);
}
