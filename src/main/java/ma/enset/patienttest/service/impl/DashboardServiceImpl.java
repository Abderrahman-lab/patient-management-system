package ma.enset.patienttest.service.impl;

import lombok.RequiredArgsConstructor;
import ma.enset.patienttest.Repositories.ConsultationRepository;
import ma.enset.patienttest.Repositories.PatientRepository;
import ma.enset.patienttest.Repositories.RendezVousRepository;
import ma.enset.patienttest.entities.RendezVous;
import ma.enset.patienttest.service.DashboardService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final PatientRepository patientRepository;
    private final ConsultationRepository consultationRepository;
    private final RendezVousRepository rendezVousRepository;

    @Override
    public Map<String, Object> getStatistiques() {
        Map<String, Object> stats = new HashMap<>();

        LocalDate today = LocalDate.now();
        LocalDate endOfWeek = today.plusDays(7);

        // ── Compteurs patients ──────────────────────────────────────
        long totalPatients = patientRepository.count();
        long patientsMalades = patientRepository.countByMalade(true);
        long patientsActifs = totalPatients - patientsMalades;

        stats.put("totalPatients", totalPatients);
        stats.put("patientsMalades", patientsMalades);
        stats.put("patientsActifs", patientsActifs);

        // ── Compteurs consultations ─────────────────────────────────
        long totalConsultations = consultationRepository.count();
        stats.put("totalConsultations", totalConsultations);

        // ── Rendez-vous ─────────────────────────────────────────────
        List<RendezVous> rdvDuJour = rendezVousRepository
                .findByDateRendezVousOrderByHeureRendezVousAsc(today);
        List<RendezVous> agendaSemaine = rendezVousRepository
                .findByDateRendezVousBetweenOrderByDateRendezVousAscHeureRendezVousAsc(today, endOfWeek);

        stats.put("rdvAujourdhui", (long) rdvDuJour.size());
        stats.put("rdvDuJour", rdvDuJour);
        stats.put("agendaSemaine", agendaSemaine);

        // ── Groupes sanguins ────────────────────────────────────────
        List<Object[]> rawGroupes = patientRepository.countByGroupeSanguinGrouped();
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();

        for (Object[] row : rawGroupes) {
            labels.add((String) row[0]);
            data.add((Long) row[1]);
        }

        stats.put("groupesSanguinsLabels", labels);
        stats.put("groupesSanguinsData", data);

        return stats;
    }
}
