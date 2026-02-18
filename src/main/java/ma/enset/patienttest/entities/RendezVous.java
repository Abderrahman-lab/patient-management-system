package ma.enset.patienttest.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "rendez_vous")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RendezVous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La date du rendez-vous est obligatoire")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateRendezVous;

    @NotNull(message = "L'heure du rendez-vous est obligatoire")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime heureRendezVous;

    @NotBlank(message = "Le motif est obligatoire")
    private String motif;

    @Enumerated(EnumType.STRING)
    private StatutRendezVous statut;

    @Column(length = 1000)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
}
