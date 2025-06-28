package gui_admin.controller;

import entite.Seance;
import entite.Salle; // Importez Salle car Seance contient un objet Salle
import gui_admin.view.seances.SeanceEdit; // Assurez-vous que cette classe existe et est l'interface d'édition
import service.SeanceService; // Importez le service Seance
import service.SalleService; // Importez le service Salle pour récupérer la liste des salles

import javax.swing.JOptionPane;
import java.sql.SQLException; // Importez SQLException
import java.sql.SQLIntegrityConstraintViolationException; // Importez SQLIntegrityConstraintViolationException
import java.time.LocalDateTime; // Importez LocalDateTime
import java.time.format.DateTimeFormatter; // Importez DateTimeFormatter
import java.time.format.DateTimeParseException; // Importez DateTimeParseException
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SeanceController {

    private SeanceService service;
    private SalleService salleService; // Pour obtenir la liste des salles
    private SeanceEdit currentEditPanel; // Référence au panneau d'édition actuel

    // Formateur de date/heure cohérent avec la vue.
    // Il est préférable de le centraliser ou de le réutiliser si le format est le même.
    // Utilisons le même formateur que DemandeInscriptionController pour la cohérence.
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public SeanceController() {
        service = new SeanceService();
        salleService = new SalleService(); // Initialisation du service Salle
    }

    // Méthode pour configurer le contrôleur avec le panneau d'édition à manipuler
    public void setEditPanel(SeanceEdit editPanel) {
        this.currentEditPanel = editPanel;

        // Listener pour le bouton "Enregistrer" (ajout/modification)
        this.currentEditPanel.getSaveButton().addActionListener(e -> {
            System.out.println("--- DIAGNOSTIC: Bouton 'Enregistrer' cliqué dans SeanceController ---");
            try {
                // Peupler l'entité interne du panneau avec les données du formulaire
                currentEditPanel.init();

                // Récupérer l'entité mise à jour après init()
                Seance entiteCourante = (Seance) currentEditPanel.getEntite();

                // Validation: La salle est obligatoire pour une séance
                if (entiteCourante.getSalle() == null || entiteCourante.getSalle().getId() == 0) {
                    JOptionPane.showMessageDialog(currentEditPanel, "Veuillez sélectionner une salle valide pour cette séance.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Validation: La date de début est obligatoire
                if (entiteCourante.getDateDebut() == null) {
                    JOptionPane.showMessageDialog(currentEditPanel, "La date de début est obligatoire.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Validation: La date de fin doit être après la date de début si elle est fournie
                if (entiteCourante.getDateFin() != null && entiteCourante.getDateFin().isBefore(entiteCourante.getDateDebut())) {
                    JOptionPane.showMessageDialog(currentEditPanel, "La date de fin doit être postérieure à la date de début.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                    return;
                }


                // Déterminer s'il s'agit d'un ajout ou d'une modification
                // Pour une entité avec ID auto-incrémenté, ID == 0 signifie nouvel ajout.
                if (entiteCourante.getId() == 0) {
                    // C'est un nouvel ajout
                    service.ajouter(entiteCourante);
                    System.out.println("Nouvelle séance ajoutée pour salle ID: " + entiteCourante.getSalle().getId() + " (ID: " + entiteCourante.getId() + ")");
                    currentEditPanel.clearForm(); // Effacer le formulaire après l'ajout réussi
                } else {
                    // C'est une modification d'une séance existante
                    service.modifier(entiteCourante);
                    System.out.println("Séance modifiée pour ID: " + entiteCourante.getId() + " (Salle ID: " + entiteCourante.getSalle().getId() + ")");
                    // Ne pas effacer le formulaire après modification.
                }

                // Mettre à jour le tableau du panneau actuellement affiché
                currentEditPanel.getCustomTablePanel().updateTableData(convertSeancesToTableData(service.listerTous()));

            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(currentEditPanel, "Format de date/heure invalide. Veuillez utiliser le format AAAA-MM-JJ HH:MM:SS.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (SQLIntegrityConstraintViolationException ex) {
                JOptionPane.showMessageDialog(currentEditPanel,
                        "Impossible d'enregistrer la séance : la salle sélectionnée n'existe peut-être pas, ou un autre problème de contrainte d'intégrité est violé.",
                        "Erreur de contrainte (SQL)",
                        JOptionPane.ERROR_MESSAGE);
                System.err.println("DEBUG: Erreur de contrainte de clé étrangère/intégrité lors de l'enregistrement de la séance.");
                ex.printStackTrace();
            }
            catch (SQLException ex) {
                JOptionPane.showMessageDialog(currentEditPanel, "Erreur de base de données lors de l'opération : " + ex.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(currentEditPanel, "Erreur inattendue lors de l'enregistrement : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        // Listener pour le bouton "Annuler"
        this.currentEditPanel.getCancelButton().addActionListener(e -> {
            System.out.println("Opération annulée. Nettoyage du formulaire.");
            currentEditPanel.clearForm(); // Nettoie le formulaire
            currentEditPanel.setEntite(new Seance()); // Prépare une nouvelle entité vide pour le formulaire
        });

        // Listener pour le bouton "Modifier"
        if (currentEditPanel.getModifyButton() != null) {
            currentEditPanel.getModifyButton().addActionListener(e -> {
                System.out.println("--- DIAGNOSTIC: Bouton 'Modifier' cliqué dans SeanceController ---");
                int selectedRow = currentEditPanel.getCustomTablePanel().getSelectedRow();
                if (selectedRow != -1) {
                    Object idObject = currentEditPanel.getCustomTablePanel().getValueAt(selectedRow, 0); // L'ID est généralement la première colonne
                    try {
                        int seanceId = Integer.parseInt(idObject.toString());
                        Seance seanceToModify = service.trouver(seanceId);
                        if (seanceToModify != null) {
                            currentEditPanel.setEntite(seanceToModify);
                            currentEditPanel.initForm(seanceToModify);
                            System.out.println("Séance sélectionnée pour modification : ID " + seanceToModify.getId() + " (Salle ID: " + seanceToModify.getSalle().getId() + ")");
                        } else {
                            JOptionPane.showMessageDialog(currentEditPanel, "Impossible de trouver la séance avec l'ID " + seanceId + ".", "Erreur", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(currentEditPanel, "L'ID de la colonne sélectionnée n'est pas un nombre valide.", "Erreur de tableau", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(currentEditPanel, "Erreur de base de données lors de la sélection pour modification : " + ex.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(currentEditPanel, "Erreur inattendue lors de la sélection pour modification : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(currentEditPanel, "Veuillez sélectionner une séance dans le tableau pour la modifier.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }

        // Listener pour le bouton "Supprimer"
        if (currentEditPanel.getDeleteButton() != null) {
            currentEditPanel.getDeleteButton().addActionListener(e -> {
                System.out.println("--- DIAGNOSTIC: Bouton 'Supprimer' cliqué dans SeanceController ---");
                int selectedRow = currentEditPanel.getCustomTablePanel().getSelectedRow();
                if (selectedRow != -1) {
                    Object idObject = currentEditPanel.getCustomTablePanel().getValueAt(selectedRow, 0);
                    try {
                        int seanceId = Integer.parseInt(idObject.toString());
                        int confirm = JOptionPane.showConfirmDialog(currentEditPanel,
                                "Êtes-vous sûr de vouloir supprimer la séance avec l'ID " + seanceId + " ?",
                                "Confirmation de suppression", JOptionPane.YES_NO_OPTION);

                        if (confirm == JOptionPane.YES_OPTION) {
                            service.supprimer(seanceId); // Supprimer la séance via le service
                            System.out.println("Séance avec ID: " + seanceId + " supprimée avec succès.");
                            currentEditPanel.clearForm(); // Effacer le formulaire après la suppression
                            currentEditPanel.getCustomTablePanel().updateTableData(convertSeancesToTableData(service.listerTous()));
                        }
                    } catch (SQLIntegrityConstraintViolationException ex) {
                        JOptionPane.showMessageDialog(currentEditPanel,
                                "Impossible de supprimer cette séance car elle est liée à d'autres entités.",
                                "Erreur de suppression (Contrainte de Clé Étrangère)",
                                JOptionPane.ERROR_MESSAGE);
                        System.err.println("DEBUG: Erreur de contrainte de clé étrangère lors de la suppression de la séance.");
                        ex.printStackTrace();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(currentEditPanel, "Erreur de base de données lors de la suppression : " + ex.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(currentEditPanel, "Erreur inattendue lors de la suppression : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(currentEditPanel, "Veuillez sélectionner une séance dans le tableau pour la supprimer.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
    }

    // Méthode pour créer et configurer le panneau SeanceEdit pour un ajout
    public SeanceEdit createAndConfigureEditPanelForAdd() throws SQLException {
        List<Seance> existingSeances = service.listerTous();
        List<Salle> allSalles = salleService.listerTous(); // Récupérer la liste complète des salles

        List<String> columnNames = Arrays.asList("ID", "Date Début", "Date Fin", "ID Salle"); // Noms de colonnes pour le tableau
        List<List<Object>> tableData = convertSeancesToTableData(existingSeances);

        SeanceEdit edit = new SeanceEdit(tableData, columnNames, allSalles); // Passer la liste des salles à la vue
        edit.setEntite(new Seance()); // Initialise l'entité interne du formulaire pour un NOUVEL ajout
        return edit;
    }

    // Méthode pour créer et configurer le panneau SeanceEdit pour une modification
    public SeanceEdit createAndConfigureEditPanelForModify(Seance entiteToModify) throws SQLException {
        List<Seance> existingSeances = service.listerTous();
        List<Salle> allSalles = salleService.listerTous(); // Récupérer la liste complète des salles

        List<String> columnNames = Arrays.asList("ID", "Date Début", "Date Fin", "ID Salle");
        List<List<Object>> tableData = convertSeancesToTableData(existingSeances);

        SeanceEdit edit = new SeanceEdit(tableData, columnNames, allSalles); // Passer la liste des salles à la vue
        edit.setEntite(entiteToModify); // Passe l'entité à modifier au formulaire
        edit.initForm(entiteToModify); // Initialise les champs du formulaire avec les données de l'entité
        return edit;
    }

    // Méthode utilitaire pour convertir une liste de Seance en données pour JTable
    private List<List<Object>> convertSeancesToTableData(List<Seance> seances) {
        List<List<Object>> data = new ArrayList<>();
        for (Seance s : seances) {
            data.add(Arrays.asList(
                    s.getId(),
                    s.getDateDebut() != null ? s.getDateDebut().format(DATE_TIME_FORMATTER) : "N/A",
                    s.getDateFin() != null ? s.getDateFin().format(DATE_TIME_FORMATTER) : "N/A",
                    s.getSalle() != null ? s.getSalle().getId() : "N/A" // Afficher l'ID de la salle
            ));
        }
        return data;
    }
}
