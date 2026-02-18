package ma.enset.patienttest.service;

import ma.enset.patienttest.entities.RendezVous;

import java.util.List;
import java.util.Map;

public interface DashboardService {

    /**
     * Retourne toutes les statistiques pour le dashboard.
     * Clés du map :
     * - totalPatients (long)
     * - patientsMalades (long)
     * - patientsActifs (long)
     * - rdvAujourdhui (long)
     * - totalConsultations (long)
     * - groupesSanguinsLabels (List<String>)
     * - groupesSanguinsData (List<Long>)
     * - agendaSemaine (List<RendezVous>)
     * - rdvDuJour (List<RendezVous>)
     */
    Map<String, Object> getStatistiques();
}
