package ma.enset.patienttest.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ma.enset.patienttest.Exception.RendezVousNotFoundException;
import ma.enset.patienttest.Repositories.RendezVousRepository;
import ma.enset.patienttest.entities.RendezVous;
import ma.enset.patienttest.entities.StatutRendezVous;
import ma.enset.patienttest.service.RendezVousService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RendezVousServiceImpl implements RendezVousService {

    private final RendezVousRepository rendezVousRepository;

    @Override
    public List<RendezVous> getRendezVousByPatient(Long patientId) {
        return rendezVousRepository.findByPatientIdOrderByDateRendezVousDescHeureRendezVousDesc(patientId);
    }

    @Override
    public RendezVous getRendezVousById(Long id) {
        return rendezVousRepository.findById(id)
                .orElseThrow(() -> new RendezVousNotFoundException(id));
    }

    @Override
    public RendezVous saveRendezVous(RendezVous rdv) {
        return rendezVousRepository.save(rdv);
    }

    @Override
    public void deleteRendezVous(Long id) {
        if (!rendezVousRepository.existsById(id)) {
            throw new RendezVousNotFoundException(id);
        }
        rendezVousRepository.deleteById(id);
    }

    @Override
    public RendezVous updateStatut(Long id, StatutRendezVous statut) {
        RendezVous rdv = getRendezVousById(id);
        rdv.setStatut(statut);
        return rendezVousRepository.save(rdv);
    }
}
