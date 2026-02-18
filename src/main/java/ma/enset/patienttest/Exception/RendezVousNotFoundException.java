package ma.enset.patienttest.Exception;

public class RendezVousNotFoundException extends RuntimeException {

    public RendezVousNotFoundException(Long id) {
        super("Rendez-vous avec l'ID " + id + " introuvable");
    }

    public RendezVousNotFoundException(String message) {
        super(message);
    }
}
