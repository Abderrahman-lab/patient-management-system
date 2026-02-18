package ma.enset.patienttest.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    @Column(length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    private TypeNotification type;

    @Builder.Default
    private boolean lue = false;

    @Builder.Default
    private LocalDateTime dateCreation = LocalDateTime.now();

    // Nullable : certaines notifications sont globales (pas liées à un patient)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    // Nom d'utilisateur destinataire, ou "ALL" pour tous
    private String destinataire;
}
