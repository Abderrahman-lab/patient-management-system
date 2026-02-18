package ma.enset.patienttest.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.enset.patienttest.entities.Patient;
import ma.enset.patienttest.service.PatientService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Tag(name = "Patients API", description = "API REST pour la gestion des patients")
public class PatientRestController {

    private final PatientService patientService;

    // GET tous les patients
    @GetMapping
    @Operation(summary = "Liste tous les patients")
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    // GET par id
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un patient par ID")
    public ResponseEntity<Patient> getPatient(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    // POST créer
    @PostMapping
    @Operation(summary = "Créer un nouveau patient")
    public ResponseEntity<Patient> createPatient(@RequestBody @Valid Patient patient) {
        return ResponseEntity.ok(patientService.savePatient(patient));
    }

    // PUT modifier
    @PutMapping("/{id}")
    @Operation(summary = "Modifier un patient")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id,
                                                 @RequestBody @Valid Patient patient) {
        patient.setId(id);
        return ResponseEntity.ok(patientService.savePatient(patient));
    }

    // DELETE supprimer
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un patient")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}