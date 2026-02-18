package ma.enset.patienttest.service.impl;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.patienttest.entities.Consultation;
import ma.enset.patienttest.entities.Patient;
import ma.enset.patienttest.entities.RendezVous;
import ma.enset.patienttest.service.PdfService;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfServiceImpl implements PdfService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    // ── Couleurs ─────────────────────────────────────────────────────
    private static final Color ROUGE       = new Color(220, 53, 69);
    private static final Color VERT        = new Color(25, 135, 84);
    private static final Color BLEU        = new Color(13, 110, 253);
    private static final Color GRIS_CLAIR  = new Color(248, 249, 250);
    private static final Color GRIS_ENTETE = new Color(233, 236, 239);

    // ── Polices ──────────────────────────────────────────────────────
    private Font titreFont()     { return new Font(Font.HELVETICA, 20, Font.BOLD,   Color.WHITE); }
    private Font sousTitreFont() { return new Font(Font.HELVETICA, 13, Font.BOLD,   BLEU); }
    private Font labelFont()     { return new Font(Font.HELVETICA, 10, Font.BOLD); }
    private Font valeurFont()    { return new Font(Font.HELVETICA, 10, Font.NORMAL); }
    private Font petitFont()     { return new Font(Font.HELVETICA,  9, Font.NORMAL, Color.GRAY); }

    @Override
    public byte[] genererFichePatient(Patient patient,
                                      List<Consultation> consultations,
                                      List<RendezVous> rendezVous) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document doc = new Document(PageSize.A4, 36, 36, 50, 36);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            // ══ EN-TÊTE ════════════════════════════════════════════════
            Color couleurEntete = patient.isMalade() ? ROUGE : VERT;
            PdfPTable entete = new PdfPTable(1);
            entete.setWidthPercentage(100);

            PdfPCell cellEntete = new PdfPCell();
            cellEntete.setBackgroundColor(couleurEntete);
            cellEntete.setPadding(14);
            cellEntete.setBorder(Rectangle.NO_BORDER);

            Paragraph titreP = new Paragraph(patient.getNom() + " " + patient.getPrenom(), titreFont());
            titreP.setAlignment(Element.ALIGN_CENTER);
            cellEntete.addElement(titreP);

            String statut = patient.isMalade() ? "● Patient malade" : "● En bonne santé";
            Paragraph statutP = new Paragraph(statut, new Font(Font.HELVETICA, 11, Font.NORMAL, Color.WHITE));
            statutP.setAlignment(Element.ALIGN_CENTER);
            cellEntete.addElement(statutP);

            entete.addCell(cellEntete);
            doc.add(entete);
            doc.add(Chunk.NEWLINE);

            // ══ INFORMATIONS PERSONNELLES ═════════════════════════════
            doc.add(new Paragraph("Informations personnelles", sousTitreFont()));
            doc.add(separateur());
            doc.add(Chunk.NEWLINE);

            PdfPTable infoTable = new PdfPTable(4);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{1.2f, 2f, 1.2f, 2f});

            ajouterLigne(infoTable, "ID", String.valueOf(patient.getId()),
                    "Date de naissance", patient.getDateNaissance() != null
                            ? patient.getDateNaissance().format(DATE_FMT) : "—");
            ajouterLigne(infoTable, "Téléphone",       nvl(patient.getTelephone()),
                         "Email",                      nvl(patient.getEmail()));
            ajouterLigne(infoTable, "Médecin traitant", nvl(patient.getMedecinTraitant()),
                         "Groupe sanguin",              nvl(patient.getGroupeSanguin()));
            ajouterLigne(infoTable, "N° Sécu. Sociale", nvl(patient.getNumeroSecuriteSociale()),
                         "Score santé",                 patient.getScore() + " / 100");

            if (patient.getAdresse() != null && !patient.getAdresse().isBlank()) {
                PdfPCell labelAdresse = labelCell("Adresse");
                PdfPCell valAdresse   = new PdfPCell(new Phrase(patient.getAdresse(), valeurFont()));
                valAdresse.setColspan(3);
                valAdresse.setPadding(6);
                valAdresse.setBackgroundColor(GRIS_CLAIR);
                infoTable.addCell(labelAdresse);
                infoTable.addCell(valAdresse);
            }

            doc.add(infoTable);

            // ══ CONTACT D'URGENCE ══════════════════════════════════════
            if (patient.getPersonneContact() != null && !patient.getPersonneContact().isBlank()) {
                doc.add(Chunk.NEWLINE);
                doc.add(new Paragraph("Contact d'urgence", sousTitreFont()));
                doc.add(separateur());
                doc.add(Chunk.NEWLINE);
                PdfPTable urgTable = new PdfPTable(4);
                urgTable.setWidthPercentage(100);
                urgTable.setWidths(new float[]{1.2f, 2f, 1.2f, 2f});
                ajouterLigne(urgTable, "Nom", nvl(patient.getPersonneContact()),
                        "Téléphone", nvl(patient.getTelephoneContact()));
                doc.add(urgTable);
            }

            // ══ ALLERGIES ══════════════════════════════════════════════
            if (patient.getAllergies() != null && !patient.getAllergies().isBlank()) {
                doc.add(Chunk.NEWLINE);
                doc.add(new Paragraph("Allergies connues", new Font(Font.HELVETICA, 12, Font.BOLD, ROUGE)));
                PdfPTable allergTable = new PdfPTable(1);
                allergTable.setWidthPercentage(100);
                PdfPCell cellAllerg = new PdfPCell(new Phrase(patient.getAllergies(), valeurFont()));
                cellAllerg.setBackgroundColor(new Color(255, 243, 205));
                cellAllerg.setPadding(8);
                allergTable.addCell(cellAllerg);
                doc.add(allergTable);
            }

            // ══ ANTÉCÉDENTS ═══════════════════════════════════════════
            if (patient.getAntecedents() != null && !patient.getAntecedents().isBlank()) {
                doc.add(Chunk.NEWLINE);
                doc.add(new Paragraph("Antécédents médicaux",
                        new Font(Font.HELVETICA, 12, Font.BOLD, new Color(13, 202, 240))));
                PdfPTable antecTable = new PdfPTable(1);
                antecTable.setWidthPercentage(100);
                PdfPCell cellAntec = new PdfPCell(new Phrase(patient.getAntecedents(), valeurFont()));
                cellAntec.setBackgroundColor(new Color(207, 244, 252));
                cellAntec.setPadding(8);
                antecTable.addCell(cellAntec);
                doc.add(antecTable);
            }

            // ══ CONSULTATIONS ══════════════════════════════════════════
            if (!consultations.isEmpty()) {
                doc.add(Chunk.NEWLINE);
                doc.add(new Paragraph(
                        "Historique des consultations (" + consultations.size() + ")", sousTitreFont()));
                doc.add(separateur());
                doc.add(Chunk.NEWLINE);

                PdfPTable consultTable = new PdfPTable(4);
                consultTable.setWidthPercentage(100);
                consultTable.setWidths(new float[]{1.5f, 2.5f, 2.5f, 2.5f});

                for (String h : new String[]{"Date", "Motif", "Diagnostic", "Traitement"}) {
                    PdfPCell cell = new PdfPCell(new Phrase(h, labelFont()));
                    cell.setBackgroundColor(GRIS_ENTETE);
                    cell.setPadding(6);
                    consultTable.addCell(cell);
                }

                for (Consultation c : consultations) {
                    consultTable.addCell(valCell(c.getDateConsultation() != null
                            ? c.getDateConsultation().format(DATE_FMT) : "—"));
                    consultTable.addCell(valCell(nvl(c.getMotif())));
                    consultTable.addCell(valCell(nvl(c.getDiagnostic())));
                    consultTable.addCell(valCell(nvl(c.getTraitement())));
                }
                doc.add(consultTable);
            }

            // ══ RENDEZ-VOUS ════════════════════════════════════════════
            if (!rendezVous.isEmpty()) {
                doc.add(Chunk.NEWLINE);
                doc.add(new Paragraph(
                        "Rendez-vous (" + rendezVous.size() + ")", sousTitreFont()));
                doc.add(separateur());
                doc.add(Chunk.NEWLINE);

                PdfPTable rdvTable = new PdfPTable(4);
                rdvTable.setWidthPercentage(100);
                rdvTable.setWidths(new float[]{2f, 1.5f, 3f, 2f});

                for (String h : new String[]{"Date", "Heure", "Motif", "Statut"}) {
                    PdfPCell cell = new PdfPCell(new Phrase(h, labelFont()));
                    cell.setBackgroundColor(GRIS_ENTETE);
                    cell.setPadding(6);
                    rdvTable.addCell(cell);
                }

                for (RendezVous rdv : rendezVous) {
                    rdvTable.addCell(valCell(rdv.getDateRendezVous() != null
                            ? rdv.getDateRendezVous().format(DATE_FMT) : "—"));
                    rdvTable.addCell(valCell(rdv.getHeureRendezVous() != null
                            ? rdv.getHeureRendezVous().format(TIME_FMT) : "—"));
                    rdvTable.addCell(valCell(nvl(rdv.getMotif())));
                    rdvTable.addCell(valCell(rdv.getStatut() != null
                            ? rdv.getStatut().name() : "—"));
                }
                doc.add(rdvTable);
            }

            // ══ PIED DE PAGE ══════════════════════════════════════════
            doc.add(Chunk.NEWLINE);
            Paragraph footer = new Paragraph(
                    "Document généré par Gestion Patients — " +
                    java.time.LocalDate.now().format(DATE_FMT), petitFont());
            footer.setAlignment(Element.ALIGN_RIGHT);
            doc.add(footer);

            doc.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Erreur génération PDF patient {} : {}", patient.getId(), e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────

    /**
     * Ligne de séparation visuelle : une table pleine largeur avec bordure inférieure.
     */
    private PdfPTable separateur() {
        PdfPTable sep = new PdfPTable(1);
        sep.setWidthPercentage(100);
        sep.setSpacingAfter(4);
        PdfPCell cell = new PdfPCell(new Phrase(" "));
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(BLEU);
        cell.setBorderWidth(1f);
        cell.setPadding(2);
        sep.addCell(cell);
        return sep;
    }

    private void ajouterLigne(PdfPTable table,
                               String label1, String val1,
                               String label2, String val2) {
        table.addCell(labelCell(label1));
        table.addCell(valCell(val1));
        table.addCell(labelCell(label2));
        table.addCell(valCell(val2));
    }

    private PdfPCell labelCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, labelFont()));
        cell.setBackgroundColor(GRIS_ENTETE);
        cell.setPadding(6);
        return cell;
    }

    private PdfPCell valCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, valeurFont()));
        cell.setBackgroundColor(GRIS_CLAIR);
        cell.setPadding(6);
        return cell;
    }

    private String nvl(String value) {
        return value != null && !value.isBlank() ? value : "—";
    }
}
