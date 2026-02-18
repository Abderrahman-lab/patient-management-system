package ma.enset.patienttest.Exception;

public class PatientNotFoundException extends RuntimeException{
    public PatientNotFoundException(Long id) {
        super("Patient avec l'ID " + id + " introuvable");
    }

    public PatientNotFoundException(String message) {
        super(message);
    }
}
