package ma.enset.patienttest.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50)
    private String prenom;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateNaissance;

    private boolean malade;

    @Min(value = 0, message = "Le score doit être positif")
    @Max(value = 100, message = "Le score ne peut pas dépasser 100")
    private int score;

    // ── Dossier Médical ────────────────────────────────────────────
    private String groupeSanguin;

    @Column(columnDefinition = "TEXT")
    private String allergies;

    @Column(columnDefinition = "TEXT")
    private String antecedents;

    private String medecinTraitant;

    @Column(unique = true)
    private String numeroSecuriteSociale;

    private String telephone;

    @Column(length = 500)
    private String adresse;

    @Email(message = "Format email invalide")
    private String email;

    private String personneContact;

    private String telephoneContact;
}
