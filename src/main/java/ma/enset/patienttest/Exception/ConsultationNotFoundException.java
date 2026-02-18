package ma.enset.patienttest.Exception;

public class ConsultationNotFoundException extends RuntimeException {

    public ConsultationNotFoundException(Long id) {
        super("Consultation avec l'ID " + id + " introuvable");
    }

    public ConsultationNotFoundException(String message) {
        super(message);
    }
}
