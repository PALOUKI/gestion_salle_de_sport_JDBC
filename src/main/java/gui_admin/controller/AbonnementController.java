package gui_admin.controller;

import entite.Abonnement;
import entite.Membre; // Importez Membre
import entite.TypeAbonnement; // Importez TypeAbonnement
import gui_admin.view.abonnements.AbonnementEdit; // Assurez-vous que cette classe existe
import service.AbonnementService; // Importez le service Abonnement
import service.MembreService; // Importez le service Membre
import service.TypeAbonnementService; // Importez le service TypeAbonnement

import javax.swing.JOptionPane;
import java.sql.SQLException; // Importez SQLException
import java.sql.SQLIntegrityConstraintViolationException; // Importez SQLIntegrityConstraintViolationException
import java.time.LocalDateTime; // Importez LocalDateTime
import java.time.format.DateTimeFormatter; // Importez DateTimeFormatter
import java.time.format.DateTimeParseException; // Importez DateTimeParseException
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AbonnementController {

    private AbonnementService service;
    private MembreService membreService; // Pour obtenir la liste des membres
    private TypeAbonnementService typeAbonnementService; // Pour obtenir la liste des types d'abonnement
    private AbonnementEdit currentEditPanel; // Référence au panneau d'édition actuel

    // Formateur de date/heure cohérent avec la vue.
    // Utilisons le même formateur que DemandeInscriptionController pour la cohérence.
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DemandeInscriptionController.DATE_TIME_FORMATTER;

    public AbonnementController() {
        service = new AbonnementService();
        membreService = new MembreService(); // Initialisation du service Membre
        typeAbonnementService = new TypeAbonnementService(); // Initialisation du service TypeAbonnement
    }

    // Méthode pour configurer le contrôleur avec le panneau d'édition à manipuler
    public void setEditPanel(AbonnementEdit editPanel) {
        this.currentEditPanel = editPanel;

        // Listener pour le bouton "Enregistrer" (ajout/modification)
        this.currentEditPanel.getSaveButton().addActionListener(e -> {
            System.out.println("--- DIAGNOSTIC: Bouton 'Enregistrer' cliqué dans AbonnementController ---");
            try {
                // Peupler l'entité interne du panneau avec les données du formulaire
                currentEditPanel.init();

                // Récupérer l'entité mise à jour après init()
                Abonnement entiteCourante = (Abonnement) currentEditPanel.getEntite();

                // Validation: Le membre est obligatoire
                if (entiteCourante.getMembre() == null || entiteCourante.getMembre().getId() == 0) {
                    JOptionPane.showMessageDialog(currentEditPanel, "Veuillez sélectionner un membre valide pour cet abonnement.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Validation: Le type d'abonnement est obligatoire
                if (entiteCourante.getTypeAbonnement() == null || entiteCourante.getTypeAbonnement().getCode() == null || entiteCourante.getTypeAbonnement().getCode().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(currentEditPanel, "Veuillez sélectionner un type d'abonnement valide.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Validation: La date de début est obligatoire
                if (entiteCourante.getDateDebut() == null) {
                    JOptionPane.showMessageDialog(currentEditPanel, "La date de début est obligatoire.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Validation: La date de fin doit être après la date de début si elle est fournie
                if (entiteCourante.getDateFin() != null && entiteCourante.getDateFin().isBefore(entiteCourante.getDateDebut())) {
                    JOptionPane.showMessageDialog(currentEditPanel, "La date de fin doit être postérieure ou égale à la date de début.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                    return;
                }


                // Déterminer s'il s'agit d'un ajout ou d'une modification
                // Pour une entité avec ID auto-incrémenté, ID == 0 signifie nouvel ajout.
                if (entiteCourante.getId() == 0) {
                    // C'est un nouvel ajout
                    service.ajouter(entiteCourante);
                    System.out.println("Nouvel abonnement ajouté pour membre ID: " + entiteCourante.getMembre().getId() + " (ID: " + entiteCourante.getId() + ")");
                    currentEditPanel.clearForm(); // Effacer le formulaire après l'ajout réussi
                } else {
                    // C'est une modification d'un abonnement existant
                    service.modifier(entiteCourante);
                    System.out.println("Abonnement modifié pour ID: " + entiteCourante.getId() + " (Membre ID: " + entiteCourante.getMembre().getId() + ")");
                    // Ne pas effacer le formulaire après modification.
                }

                // Mettre à jour le tableau du panneau actuellement affiché
                currentEditPanel.getCustomTablePanel().updateTableData(convertAbonnementsToTableData(service.listerTous()));

            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(currentEditPanel, "Format de date/heure invalide. Veuillez utiliser le format AAAA-MM-JJ HH:MM:SS.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (SQLIntegrityConstraintViolationException ex) {
                JOptionPane.showMessageDialog(currentEditPanel,
                        "Impossible d'enregistrer l'abonnement : le membre ou le type d'abonnement sélectionné n'existe pas, ou un autre problème de contrainte d'intégrité est violé.",
                        "Erreur de contrainte (SQL)",
                        JOptionPane.ERROR_MESSAGE);
                System.err.println("DEBUG: Erreur de contrainte de clé étrangère/intégrité lors de l'enregistrement de l'abonnement.");
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
            currentEditPanel.setEntite(new Abonnement()); // Prépare une nouvelle entité vide pour le formulaire
        });

        // Listener pour le bouton "Modifier"
        if (currentEditPanel.getModifyButton() != null) {
            currentEditPanel.getModifyButton().addActionListener(e -> {
                System.out.println("--- DIAGNOSTIC: Bouton 'Modifier' cliqué dans AbonnementController ---");
                int selectedRow = currentEditPanel.getCustomTablePanel().getSelectedRow();
                if (selectedRow != -1) {
                    Object idObject = currentEditPanel.getCustomTablePanel().getValueAt(selectedRow, 0); // L'ID est généralement la première colonne
                    try {
                        int abonnementId = Integer.parseInt(idObject.toString());
                        Abonnement abonnementToModify = service.trouver(abonnementId);
                        if (abonnementToModify != null) {
                            currentEditPanel.setEntite(abonnementToModify);
                            currentEditPanel.initForm(abonnementToModify);
                            System.out.println("Abonnement sélectionné pour modification : ID " + abonnementToModify.getId() + " (Membre ID: " + abonnementToModify.getMembre().getId() + ", Type: " + abonnementToModify.getTypeAbonnement().getCode() + ")");
                        } else {
                            JOptionPane.showMessageDialog(currentEditPanel, "Impossible de trouver l'abonnement avec l'ID " + abonnementId + ".", "Erreur", JOptionPane.ERROR_MESSAGE);
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
                    JOptionPane.showMessageDialog(currentEditPanel, "Veuillez sélectionner un abonnement dans le tableau pour le modifier.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }

        // Listener pour le bouton "Supprimer"
        if (currentEditPanel.getDeleteButton() != null) {
            currentEditPanel.getDeleteButton().addActionListener(e -> {
                System.out.println("--- DIAGNOSTIC: Bouton 'Supprimer' cliqué dans AbonnementController ---");
                int selectedRow = currentEditPanel.getCustomTablePanel().getSelectedRow();
                if (selectedRow != -1) {
                    Object idObject = currentEditPanel.getCustomTablePanel().getValueAt(selectedRow, 0);
                    try {
                        int abonnementId = Integer.parseInt(idObject.toString());
                        int confirm = JOptionPane.showConfirmDialog(currentEditPanel,
                                "Êtes-vous sûr de vouloir supprimer l'abonnement avec l'ID " + abonnementId + " ?",
                                "Confirmation de suppression", JOptionPane.YES_NO_OPTION);

                        if (confirm == JOptionPane.YES_OPTION) {
                            service.supprimer(abonnementId); // Supprimer l'abonnement via le service
                            System.out.println("Abonnement avec ID: " + abonnementId + " supprimé avec succès.");
                            currentEditPanel.clearForm(); // Effacer le formulaire après la suppression
                            currentEditPanel.getCustomTablePanel().updateTableData(convertAbonnementsToTableData(service.listerTous()));
                        }
                    } catch (SQLIntegrityConstraintViolationException ex) {
                        JOptionPane.showMessageDialog(currentEditPanel,
                                "Impossible de supprimer cet abonnement car il est lié à d'autres entités (par exemple, des paiements).",
                                "Erreur de suppression (Contrainte de Clé Étrangère)",
                                JOptionPane.ERROR_MESSAGE);
                        System.err.println("DEBUG: Erreur de contrainte de clé étrangère lors de la suppression de l'abonnement.");
                        ex.printStackTrace();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(currentEditPanel, "Erreur de base de données lors de la suppression : " + ex.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(currentEditPanel, "Erreur inattendue lors de la suppression : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(currentEditPanel, "Veuillez sélectionner un abonnement dans le tableau pour le supprimer.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
    }

    // Méthode pour créer et configurer le panneau AbonnementEdit pour un ajout
    public AbonnementEdit createAndConfigureEditPanelForAdd() throws SQLException {
        List<Abonnement> existingAbonnements = service.listerTous();
        List<Membre> allMembres = membreService.listerTous(); // Récupérer la liste complète des membres
        List<TypeAbonnement> allTypeAbonnements = typeAbonnementService.listerTous(); // Récupérer la liste complète des types d'abonnement

        List<String> columnNames = Arrays.asList("ID", "Date Début", "Date Fin", "ID Membre", "Code Type Abo"); // Noms de colonnes pour le tableau
        List<List<Object>> tableData = convertAbonnementsToTableData(existingAbonnements);

        AbonnementEdit edit = new AbonnementEdit(tableData, columnNames, allMembres, allTypeAbonnements); // Passer les listes
        edit.setEntite(new Abonnement()); // Initialise l'entité interne du formulaire pour un NOUVEL ajout
        return edit;
    }

    // Méthode pour créer et configurer le panneau AbonnementEdit pour une modification
    public AbonnementEdit createAndConfigureEditPanelForModify(Abonnement entiteToModify) throws SQLException {
        List<Abonnement> existingAbonnements = service.listerTous();
        List<Membre> allMembres = membreService.listerTous(); // Récupérer la liste complète des membres
        List<TypeAbonnement> allTypeAbonnements = typeAbonnementService.listerTous(); // Récupérer la liste complète des types d'abonnement

        List<String> columnNames = Arrays.asList("ID", "Date Début", "Date Fin", "ID Membre", "Code Type Abo");
        List<List<Object>> tableData = convertAbonnementsToTableData(existingAbonnements);

        AbonnementEdit edit = new AbonnementEdit(tableData, columnNames, allMembres, allTypeAbonnements); // Passer les listes
        edit.setEntite(entiteToModify); // Passe l'entité à modifier au formulaire
        edit.initForm(entiteToModify); // Initialise les champs du formulaire avec les données de l'entité
        return edit;
    }

    // Méthode utilitaire pour convertir une liste d'Abonnement en données pour JTable
    private List<List<Object>> convertAbonnementsToTableData(List<Abonnement> abonnements) {
        List<List<Object>> data = new ArrayList<>();
        for (Abonnement a : abonnements) {
            data.add(Arrays.asList(
                    a.getId(),
                    a.getDateDebut() != null ? a.getDateDebut().format(DATE_TIME_FORMATTER) : "N/A",
                    a.getDateFin() != null ? a.getDateFin().format(DATE_TIME_FORMATTER) : "N/A",
                    a.getMembre() != null ? a.getMembre().getId() : "N/A", // Afficher l'ID du membre
                    a.getTypeAbonnement() != null ? a.getTypeAbonnement().getCode() : "N/A" // Afficher le code du type d'abonnement
            ));
        }
        return data;
    }
}
