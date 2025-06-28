package gui_admin.controller;

import entite.Salle;
import gui_admin.view.salles.SalleEdit; // Assurez-vous que cette classe existe et est l'interface d'édition
import service.SalleService; // Importez le service Salle

import javax.swing.JOptionPane;
import java.sql.SQLException; // Importez SQLException
import java.sql.SQLIntegrityConstraintViolationException; // Importez SQLIntegrityConstraintViolationException
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SalleController {

    private SalleService service;
    private SalleEdit currentEditPanel; // Référence au panneau d'édition actuel

    public SalleController() {
        service = new SalleService();
    }

    // Méthode pour configurer le contrôleur avec le panneau d'édition à manipuler
    public void setEditPanel(SalleEdit editPanel) {
        this.currentEditPanel = editPanel;

        // Listener pour le bouton "Enregistrer" (ajout/modification)
        this.currentEditPanel.getSaveButton().addActionListener(e -> {
            System.out.println("--- DIAGNOSTIC: Bouton 'Enregistrer' cliqué dans SalleController ---");
            try {
                // Peupler l'entité interne du panneau avec les données du formulaire
                currentEditPanel.init();

                // Récupérer l'entité mise à jour après init()
                Salle entiteCourante = (Salle) currentEditPanel.getEntite();

                // Validation simple: le libellé est obligatoire
                if (entiteCourante.getLibelle() == null || entiteCourante.getLibelle().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(currentEditPanel, "Le libellé de la salle ne peut pas être vide.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Déterminer s'il s'agit d'un ajout ou d'une modification
                // Pour une entité avec ID auto-incrémenté, ID == 0 signifie nouvel ajout.
                if (entiteCourante.getId() == 0) {
                    // C'est un nouvel ajout
                    service.ajouter(entiteCourante);
                    System.out.println("Nouvelle salle ajoutée : " + entiteCourante.getLibelle() + " (ID: " + entiteCourante.getId() + ")");
                    currentEditPanel.clearForm(); // Effacer le formulaire après l'ajout réussi
                } else {
                    // C'est une modification d'une salle existante
                    service.modifier(entiteCourante);
                    System.out.println("Salle modifiée : " + entiteCourante.getLibelle() + " (ID: " + entiteCourante.getId() + ")");
                    // Ne pas effacer le formulaire après modification.
                }

                // Mettre à jour le tableau du panneau actuellement affiché
                currentEditPanel.getCustomTablePanel().updateTableData(convertSallesToTableData(service.listerTous()));

            } catch (SQLException ex) { // Capturer les exceptions SQL du service/DAO
                JOptionPane.showMessageDialog(currentEditPanel, "Erreur de base de données lors de l'opération : " + ex.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (Exception ex) { // Capturer toute autre exception inattendue
                JOptionPane.showMessageDialog(currentEditPanel, "Erreur inattendue lors de l'enregistrement : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        // Listener pour le bouton "Annuler"
        this.currentEditPanel.getCancelButton().addActionListener(e -> {
            System.out.println("Opération annulée. Nettoyage du formulaire.");
            currentEditPanel.clearForm(); // Nettoie le formulaire
            currentEditPanel.setEntite(new Salle()); // Prépare une nouvelle entité vide pour le formulaire
        });

        // Listener pour le bouton "Modifier"
        if (currentEditPanel.getModifyButton() != null) {
            currentEditPanel.getModifyButton().addActionListener(e -> {
                System.out.println("--- DIAGNOSTIC: Bouton 'Modifier' cliqué dans SalleController ---");
                int selectedRow = currentEditPanel.getCustomTablePanel().getSelectedRow();
                if (selectedRow != -1) {
                    Object idObject = currentEditPanel.getCustomTablePanel().getValueAt(selectedRow, 0); // L'ID est généralement la première colonne
                    try {
                        int salleId = Integer.parseInt(idObject.toString());
                        Salle salleToModify = service.trouver(salleId);
                        if (salleToModify != null) {
                            currentEditPanel.setEntite(salleToModify);
                            currentEditPanel.initForm(salleToModify);
                            System.out.println("Salle sélectionnée pour modification : ID " + salleToModify.getId() + " (Libellé: " + salleToModify.getLibelle() + ")");
                        } else {
                            JOptionPane.showMessageDialog(currentEditPanel, "Impossible de trouver la salle avec l'ID " + salleId + ".", "Erreur", JOptionPane.ERROR_MESSAGE);
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
                    JOptionPane.showMessageDialog(currentEditPanel, "Veuillez sélectionner une salle dans le tableau pour la modifier.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }

        // Listener pour le bouton "Supprimer"
        if (currentEditPanel.getDeleteButton() != null) {
            currentEditPanel.getDeleteButton().addActionListener(e -> {
                System.out.println("--- DIAGNOSTIC: Bouton 'Supprimer' cliqué dans SalleController ---");
                int selectedRow = currentEditPanel.getCustomTablePanel().getSelectedRow();
                if (selectedRow != -1) {
                    Object idObject = currentEditPanel.getCustomTablePanel().getValueAt(selectedRow, 0);
                    try {
                        int salleId = Integer.parseInt(idObject.toString());
                        int confirm = JOptionPane.showConfirmDialog(currentEditPanel,
                                "Êtes-vous sûr de vouloir supprimer la salle avec l'ID " + salleId + " ?\nAttention : cela peut entraîner la suppression d'entités liées (horaires, séances) !",
                                "Confirmation de suppression", JOptionPane.YES_NO_OPTION);

                        if (confirm == JOptionPane.YES_OPTION) {
                            service.supprimer(salleId); // Supprimer la salle via le service
                            System.out.println("Salle avec ID: " + salleId + " supprimée avec succès.");
                            currentEditPanel.clearForm(); // Effacer le formulaire après la suppression
                            currentEditPanel.getCustomTablePanel().updateTableData(convertSallesToTableData(service.listerTous()));
                        }
                    } catch (SQLIntegrityConstraintViolationException ex) {
                        JOptionPane.showMessageDialog(currentEditPanel,
                                "Impossible de supprimer cette salle car elle est liée à d'autres entités (par exemple, des horaires de salle ou des séances).",
                                "Erreur de suppression (Contrainte de Clé Étrangère)",
                                JOptionPane.ERROR_MESSAGE);
                        System.err.println("DEBUG: Erreur de contrainte de clé étrangère lors de la suppression de la salle.");
                        ex.printStackTrace();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(currentEditPanel, "Erreur de base de données lors de la suppression : " + ex.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(currentEditPanel, "Erreur inattendue lors de la suppression : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(currentEditPanel, "Veuillez sélectionner une salle dans le tableau pour la supprimer.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
    }

    // Méthode pour créer et configurer le panneau SalleEdit pour un ajout
    public SalleEdit createAndConfigureEditPanelForAdd() throws SQLException {
        List<Salle> existingSalles = service.listerTous();
        List<String> columnNames = Arrays.asList("ID", "Libellé", "Description"); // Noms de colonnes
        List<List<Object>> tableData = convertSallesToTableData(existingSalles);

        SalleEdit edit = new SalleEdit(tableData, columnNames); // Passe les données du tableau
        edit.setEntite(new Salle()); // Initialise l'entité interne du formulaire pour un NOUVEL ajout
        return edit;
    }

    // Méthode pour créer et configurer le panneau SalleEdit pour une modification
    public SalleEdit createAndConfigureEditPanelForModify(Salle entiteToModify) throws SQLException {
        List<Salle> existingSalles = service.listerTous();
        List<String> columnNames = Arrays.asList("ID", "Libellé", "Description");
        List<List<Object>> tableData = convertSallesToTableData(existingSalles);

        SalleEdit edit = new SalleEdit(tableData, columnNames); // Passe les données du tableau
        edit.setEntite(entiteToModify); // Passe l'entité à modifier au formulaire
        edit.initForm(entiteToModify); // Initialise les champs du formulaire avec les données de l'entité
        return edit;
    }

    // Méthode utilitaire pour convertir une liste de Salles en données pour JTable
    private List<List<Object>> convertSallesToTableData(List<Salle> salles) {
        List<List<Object>> data = new ArrayList<>();
        for (Salle salle : salles) {
            data.add(Arrays.asList(
                    salle.getId(),
                    salle.getLibelle(),
                    salle.getDescription()
            ));
        }
        return data;
    }
}
