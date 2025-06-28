package gui_admin.controller;

import entite.Paiement;
import entite.Abonnement; // Importez Abonnement
import entite.MoyenDePaiement; // Importez MoyenDePaiement
import gui_admin.view.paiements.PaiementEdit; // Assurez-vous que cette classe existe
import service.PaiementService; // Importez le service Paiement
import service.AbonnementService; // Importez le service Abonnement
import service.MoyenDePaiementService; // Importez le service MoyenDePaiement

import javax.swing.JOptionPane;
import java.sql.SQLException; // Importez SQLException
import java.sql.SQLIntegrityConstraintViolationException; // Importez SQLIntegrityConstraintViolationException
import java.time.LocalDateTime; // Importez LocalDateTime
import java.time.format.DateTimeFormatter; // Importez DateTimeFormatter
import java.time.format.DateTimeParseException; // Importez DateTimeParseException
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PaiementController {

    private PaiementService service;
    private AbonnementService abonnementService; // Pour obtenir la liste des abonnements
    private MoyenDePaiementService moyenDePaiementService; // Pour obtenir la liste des moyens de paiement
    private PaiementEdit currentEditPanel; // Référence au panneau d'édition actuel

    // Formateur de date/heure cohérent avec la vue.
    // Utilisons le même formateur que DemandeInscriptionController pour la cohérence.
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DemandeInscriptionController.DATE_TIME_FORMATTER;

    public PaiementController() {
        service = new PaiementService();
        abonnementService = new AbonnementService(); // Initialisation du service Abonnement
        moyenDePaiementService = new MoyenDePaiementService(); // Initialisation du service MoyenDePaiement
    }

    // Méthode pour configurer le contrôleur avec le panneau d'édition à manipuler
    public void setEditPanel(PaiementEdit editPanel) {
        this.currentEditPanel = editPanel;

        // Listener pour le bouton "Enregistrer" (ajout/modification)
        this.currentEditPanel.getSaveButton().addActionListener(e -> {
            System.out.println("--- DIAGNOSTIC: Bouton 'Enregistrer' cliqué dans PaiementController ---");
            try {
                // Peupler l'entité interne du panneau avec les données du formulaire
                currentEditPanel.init();

                // Récupérer l'entité mise à jour après init()
                Paiement entiteCourante = (Paiement) currentEditPanel.getEntite();

                // Validation: Le montant est obligatoire et doit être positif
                if (entiteCourante.getMontant() <= 0) {
                    JOptionPane.showMessageDialog(currentEditPanel, "Le montant doit être un nombre positif.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Validation: La date de paiement est obligatoire
                if (entiteCourante.getDateDePaiement() == null) {
                    JOptionPane.showMessageDialog(currentEditPanel, "La date de paiement est obligatoire.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Validation: Le moyen de paiement est obligatoire
                if (entiteCourante.getMoyenDePaiement() == null || entiteCourante.getMoyenDePaiement().getCode() == null || entiteCourante.getMoyenDePaiement().getCode().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(currentEditPanel, "Veuillez sélectionner un moyen de paiement valide.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Validation: L'abonnement est obligatoire
                if (entiteCourante.getAbonnement() == null || entiteCourante.getAbonnement().getId() == 0) {
                    JOptionPane.showMessageDialog(currentEditPanel, "Veuillez sélectionner un abonnement valide pour ce paiement.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Déterminer s'il s'agit d'un ajout ou d'une modification
                // Pour une entité avec ID auto-incrémenté, ID == 0 signifie nouvel ajout.
                if (entiteCourante.getId() == 0) {
                    // C'est un nouvel ajout
                    service.ajouter(entiteCourante);
                    System.out.println("Nouveau paiement ajouté pour abonnement ID: " + entiteCourante.getAbonnement().getId() + " (ID: " + entiteCourante.getId() + ")");
                    currentEditPanel.clearForm(); // Effacer le formulaire après l'ajout réussi
                } else {
                    // C'est une modification d'un paiement existant
                    service.modifier(entiteCourante);
                    System.out.println("Paiement modifié pour ID: " + entiteCourante.getId() + " (Abonnement ID: " + entiteCourante.getAbonnement().getId() + ")");
                    // Ne pas effacer le formulaire après modification.
                }

                // Mettre à jour le tableau du panneau actuellement affiché
                currentEditPanel.getCustomTablePanel().updateTableData(convertPaiementsToTableData(service.listerTous()));

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(currentEditPanel, "Erreur de format numérique (ex: montant). " + ex.getMessage(), "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(currentEditPanel, "Format de date/heure invalide. Veuillez utiliser le format AAAA-MM-JJ HH:MM:SS.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (SQLIntegrityConstraintViolationException ex) {
                JOptionPane.showMessageDialog(currentEditPanel,
                        "Impossible d'enregistrer le paiement : l'abonnement ou le moyen de paiement sélectionné n'existe pas, ou un autre problème de contrainte d'intégrité est violé.",
                        "Erreur de contrainte (SQL)",
                        JOptionPane.ERROR_MESSAGE);
                System.err.println("DEBUG: Erreur de contrainte de clé étrangère/intégrité lors de l'enregistrement du paiement.");
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
            currentEditPanel.setEntite(new Paiement()); // Prépare une nouvelle entité vide pour le formulaire
        });

        // Listener pour le bouton "Modifier"
        if (currentEditPanel.getModifyButton() != null) {
            currentEditPanel.getModifyButton().addActionListener(e -> {
                System.out.println("--- DIAGNOSTIC: Bouton 'Modifier' cliqué dans PaiementController ---");
                int selectedRow = currentEditPanel.getCustomTablePanel().getSelectedRow();
                if (selectedRow != -1) {
                    Object idObject = currentEditPanel.getCustomTablePanel().getValueAt(selectedRow, 0); // L'ID est généralement la première colonne
                    try {
                        int paiementId = Integer.parseInt(idObject.toString());
                        Paiement paiementToModify = service.trouver(paiementId);
                        if (paiementToModify != null) {
                            currentEditPanel.setEntite(paiementToModify);
                            currentEditPanel.initForm(paiementToModify);
                            System.out.println("Paiement sélectionné pour modification : ID " + paiementToModify.getId() + " (Abonnement ID: " + paiementToModify.getAbonnement().getId() + ", Moyen: " + paiementToModify.getMoyenDePaiement().getCode() + ")");
                        } else {
                            JOptionPane.showMessageDialog(currentEditPanel, "Impossible de trouver le paiement avec l'ID " + paiementId + ".", "Erreur", JOptionPane.ERROR_MESSAGE);
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
                    JOptionPane.showMessageDialog(currentEditPanel, "Veuillez sélectionner un paiement dans le tableau pour le modifier.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }

        // Listener pour le bouton "Supprimer"
        if (currentEditPanel.getDeleteButton() != null) {
            currentEditPanel.getDeleteButton().addActionListener(e -> {
                System.out.println("--- DIAGNOSTIC: Bouton 'Supprimer' cliqué dans PaiementController ---");
                int selectedRow = currentEditPanel.getCustomTablePanel().getSelectedRow();
                if (selectedRow != -1) {
                    Object idObject = currentEditPanel.getCustomTablePanel().getValueAt(selectedRow, 0);
                    try {
                        int paiementId = Integer.parseInt(idObject.toString());
                        int confirm = JOptionPane.showConfirmDialog(currentEditPanel,
                                "Êtes-vous sûr de vouloir supprimer le paiement avec l'ID " + paiementId + " ?",
                                "Confirmation de suppression", JOptionPane.YES_NO_OPTION);

                        if (confirm == JOptionPane.YES_OPTION) {
                            service.supprimer(paiementId); // Supprimer le paiement via le service
                            System.out.println("Paiement avec ID: " + paiementId + " supprimé avec succès.");
                            currentEditPanel.clearForm(); // Effacer le formulaire après la suppression
                            currentEditPanel.getCustomTablePanel().updateTableData(convertPaiementsToTableData(service.listerTous()));
                        }
                    } catch (SQLIntegrityConstraintViolationException ex) {
                        JOptionPane.showMessageDialog(currentEditPanel,
                                "Impossible de supprimer ce paiement car il est lié à d'autres entités.",
                                "Erreur de suppression (Contrainte de Clé Étrangère)",
                                JOptionPane.ERROR_MESSAGE);
                        System.err.println("DEBUG: Erreur de contrainte de clé étrangère lors de la suppression du paiement.");
                        ex.printStackTrace();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(currentEditPanel, "Erreur de base de données lors de la suppression : " + ex.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(currentEditPanel, "Erreur inattendue lors de la suppression : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(currentEditPanel, "Veuillez sélectionner un paiement dans le tableau pour le supprimer.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
    }

    // Méthode pour créer et configurer le panneau PaiementEdit pour un ajout
    public PaiementEdit createAndConfigureEditPanelForAdd() throws SQLException {
        List<Paiement> existingPaiements = service.listerTous();
        List<MoyenDePaiement> allMoyensDePaiement = moyenDePaiementService.listerTous(); // Récupérer les moyens de paiement
        List<Abonnement> allAbonnements = abonnementService.listerTous(); // Récupérer les abonnements

        List<String> columnNames = Arrays.asList("ID", "Montant", "Date Paiement", "Code Moyen", "ID Abonnement"); // Noms de colonnes pour le tableau
        List<List<Object>> tableData = convertPaiementsToTableData(existingPaiements);

        PaiementEdit edit = new PaiementEdit(tableData, columnNames, allMoyensDePaiement, allAbonnements); // Passer les listes
        edit.setEntite(new Paiement()); // Initialise l'entité interne du formulaire pour un NOUVEL ajout
        return edit;
    }

    // Méthode pour créer et configurer le panneau PaiementEdit pour une modification
    public PaiementEdit createAndConfigureEditPanelForModify(Paiement entiteToModify) throws SQLException {
        List<Paiement> existingPaiements = service.listerTous();
        List<MoyenDePaiement> allMoyensDePaiement = moyenDePaiementService.listerTous(); // Récupérer les moyens de paiement
        List<Abonnement> allAbonnements = abonnementService.listerTous(); // Récupérer les abonnements

        List<String> columnNames = Arrays.asList("ID", "Montant", "Date Paiement", "Code Moyen", "ID Abonnement");
        List<List<Object>> tableData = convertPaiementsToTableData(existingPaiements);

        PaiementEdit edit = new PaiementEdit(tableData, columnNames, allMoyensDePaiement, allAbonnements); // Passer les listes
        edit.setEntite(entiteToModify); // Passe l'entité à modifier au formulaire
        edit.initForm(entiteToModify); // Initialise les champs du formulaire avec les données de l'entité
        return edit;
    }

    // Méthode utilitaire pour convertir une liste de Paiement en données pour JTable
    private List<List<Object>> convertPaiementsToTableData(List<Paiement> paiements) {
        List<List<Object>> data = new ArrayList<>();
        for (Paiement p : paiements) {
            data.add(Arrays.asList(
                    p.getId(),
                    p.getMontant(),
                    p.getDateDePaiement() != null ? p.getDateDePaiement().format(DATE_TIME_FORMATTER) : "N/A",
                    p.getMoyenDePaiement() != null ? p.getMoyenDePaiement().getCode() : "N/A", // Afficher le code du moyen de paiement
                    p.getAbonnement() != null ? p.getAbonnement().getId() : "N/A" // Afficher l'ID de l'abonnement
            ));
        }
        return data;
    }
}
