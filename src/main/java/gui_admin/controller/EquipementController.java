package gui_admin.controller;

import entite.Equipement;
import entite.Salle; // Importez Salle car Equipement contient un objet Salle
import gui_admin.view.equipements.EquipementEdit; // Assurez-vous que cette classe existe et est l'interface d'édition
import service.EquipementService; // Importez le service Equipement
import service.SalleService; // Importez le service Salle pour récupérer la liste des salles

import javax.swing.JOptionPane;
import java.sql.SQLException; // Importez SQLException
import java.sql.SQLIntegrityConstraintViolationException; // Importez SQLIntegrityConstraintViolationException
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EquipementController {

    private EquipementService service;
    private SalleService salleService; // Pour obtenir la liste des salles
    private EquipementEdit currentEditPanel; // Référence au panneau d'édition actuel

    public EquipementController() {
        service = new EquipementService();
        salleService = new SalleService(); // Initialisation du service Salle
    }

    // Méthode pour configurer le contrôleur avec le panneau d'édition à manipuler
    public void setEditPanel(EquipementEdit editPanel) {
        this.currentEditPanel = editPanel;

        // Listener pour le bouton "Enregistrer" (ajout/modification)
        this.currentEditPanel.getSaveButton().addActionListener(e -> {
            System.out.println("--- DIAGNOSTIC: Bouton 'Enregistrer' cliqué dans EquipementController ---");
            try {
                // Peupler l'entité interne du panneau avec les données du formulaire
                currentEditPanel.init();

                // Récupérer l'entité mise à jour après init()
                Equipement entiteCourante = (Equipement) currentEditPanel.getEntite();

                // Validation: Le libellé est obligatoire
                if (entiteCourante.getLibelle() == null || entiteCourante.getLibelle().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(currentEditPanel, "Le libellé de l'équipement ne peut pas être vide.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Validation: La salle est obligatoire pour un équipement
                if (entiteCourante.getSalle() == null || entiteCourante.getSalle().getId() == 0) {
                    JOptionPane.showMessageDialog(currentEditPanel, "Veuillez sélectionner une salle valide pour cet équipement.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Déterminer s'il s'agit d'un ajout ou d'une modification
                // Pour une entité avec ID auto-incrémenté, ID == 0 signifie nouvel ajout.
                if (entiteCourante.getId() == 0) {
                    // C'est un nouvel ajout
                    service.ajouter(entiteCourante);
                    System.out.println("Nouvel équipement ajouté : " + entiteCourante.getLibelle() + " (ID: " + entiteCourante.getId() + ", Salle ID: " + entiteCourante.getSalle().getId() + ")");
                    currentEditPanel.clearForm(); // Effacer le formulaire après l'ajout réussi
                } else {
                    // C'est une modification d'un équipement existant
                    service.modifier(entiteCourante);
                    System.out.println("Équipement modifié : " + entiteCourante.getLibelle() + " (ID: " + entiteCourante.getId() + ", Salle ID: " + entiteCourante.getSalle().getId() + ")");
                    // Ne pas effacer le formulaire après modification.
                }

                // Mettre à jour le tableau du panneau actuellement affiché
                currentEditPanel.getCustomTablePanel().updateTableData(convertEquipementsToTableData(service.listerTous()));

            } catch (SQLIntegrityConstraintViolationException ex) {
                JOptionPane.showMessageDialog(currentEditPanel,
                        "Impossible d'enregistrer l'équipement : la salle sélectionnée n'existe peut-être pas, ou un autre problème de contrainte d'intégrité est violé.",
                        "Erreur de contrainte (SQL)",
                        JOptionPane.ERROR_MESSAGE);
                System.err.println("DEBUG: Erreur de contrainte de clé étrangère/intégrité lors de l'enregistrement de l'équipement.");
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
            currentEditPanel.setEntite(new Equipement()); // Prépare une nouvelle entité vide pour le formulaire
        });

        // Listener pour le bouton "Modifier"
        if (currentEditPanel.getModifyButton() != null) {
            currentEditPanel.getModifyButton().addActionListener(e -> {
                System.out.println("--- DIAGNOSTIC: Bouton 'Modifier' cliqué dans EquipementController ---");
                int selectedRow = currentEditPanel.getCustomTablePanel().getSelectedRow();
                if (selectedRow != -1) {
                    Object idObject = currentEditPanel.getCustomTablePanel().getValueAt(selectedRow, 0); // L'ID est généralement la première colonne
                    try {
                        int equipementId = Integer.parseInt(idObject.toString());
                        Equipement equipementToModify = service.trouver(equipementId);
                        if (equipementToModify != null) {
                            currentEditPanel.setEntite(equipementToModify);
                            currentEditPanel.initForm(equipementToModify);
                            System.out.println("Équipement sélectionné pour modification : ID " + equipementToModify.getId() + " (Libellé: " + equipementToModify.getLibelle() + ", Salle ID: " + equipementToModify.getSalle().getId() + ")");
                        } else {
                            JOptionPane.showMessageDialog(currentEditPanel, "Impossible de trouver l'équipement avec l'ID " + equipementId + ".", "Erreur", JOptionPane.ERROR_MESSAGE);
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
                    JOptionPane.showMessageDialog(currentEditPanel, "Veuillez sélectionner un équipement dans le tableau pour le modifier.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }

        // Listener pour le bouton "Supprimer"
        if (currentEditPanel.getDeleteButton() != null) {
            currentEditPanel.getDeleteButton().addActionListener(e -> {
                System.out.println("--- DIAGNOSTIC: Bouton 'Supprimer' cliqué dans EquipementController ---");
                int selectedRow = currentEditPanel.getCustomTablePanel().getSelectedRow();
                if (selectedRow != -1) {
                    Object idObject = currentEditPanel.getCustomTablePanel().getValueAt(selectedRow, 0);
                    try {
                        int equipementId = Integer.parseInt(idObject.toString());
                        int confirm = JOptionPane.showConfirmDialog(currentEditPanel,
                                "Êtes-vous sûr de vouloir supprimer l'équipement avec l'ID " + equipementId + " ?",
                                "Confirmation de suppression", JOptionPane.YES_NO_OPTION);

                        if (confirm == JOptionPane.YES_OPTION) {
                            service.supprimer(equipementId); // Supprimer l'équipement via le service
                            System.out.println("Équipement avec ID: " + equipementId + " supprimé avec succès.");
                            currentEditPanel.clearForm(); // Effacer le formulaire après la suppression
                            currentEditPanel.getCustomTablePanel().updateTableData(convertEquipementsToTableData(service.listerTous()));
                        }
                    } catch (SQLIntegrityConstraintViolationException ex) {
                        JOptionPane.showMessageDialog(currentEditPanel,
                                "Impossible de supprimer cet équipement car il est lié à d'autres entités.",
                                "Erreur de suppression (Contrainte de Clé Étrangère)",
                                JOptionPane.ERROR_MESSAGE);
                        System.err.println("DEBUG: Erreur de contrainte de clé étrangère lors de la suppression de l'équipement.");
                        ex.printStackTrace();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(currentEditPanel, "Erreur de base de données lors de la suppression : " + ex.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(currentEditPanel, "Erreur inattendue lors de la suppression : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(currentEditPanel, "Veuillez sélectionner un équipement dans le tableau pour le supprimer.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
    }

    // Méthode pour créer et configurer le panneau EquipementEdit pour un ajout
    public EquipementEdit createAndConfigureEditPanelForAdd() throws SQLException {
        List<Equipement> existingEquipements = service.listerTous();
        List<Salle> allSalles = salleService.listerTous(); // Récupérer la liste complète des salles

        List<String> columnNames = Arrays.asList("ID", "Libellé", "Description", "ID Salle"); // Noms de colonnes pour le tableau
        List<List<Object>> tableData = convertEquipementsToTableData(existingEquipements);

        EquipementEdit edit = new EquipementEdit(tableData, columnNames, allSalles); // Passer la liste des salles à la vue
        edit.setEntite(new Equipement()); // Initialise l'entité interne du formulaire pour un NOUVEL ajout
        return edit;
    }

    // Méthode pour créer et configurer le panneau EquipementEdit pour une modification
    public EquipementEdit createAndConfigureEditPanelForModify(Equipement entiteToModify) throws SQLException {
        List<Equipement> existingEquipements = service.listerTous();
        List<Salle> allSalles = salleService.listerTous(); // Récupérer la liste complète des salles

        List<String> columnNames = Arrays.asList("ID", "Libellé", "Description", "ID Salle");
        List<List<Object>> tableData = convertEquipementsToTableData(existingEquipements);

        EquipementEdit edit = new EquipementEdit(tableData, columnNames, allSalles); // Passer la liste des salles à la vue
        edit.setEntite(entiteToModify); // Passe l'entité à modifier au formulaire
        edit.initForm(entiteToModify); // Initialise les champs du formulaire avec les données de l'entité
        return edit;
    }

    // Méthode utilitaire pour convertir une liste d'Equipement en données pour JTable
    private List<List<Object>> convertEquipementsToTableData(List<Equipement> equipements) {
        List<List<Object>> data = new ArrayList<>();
        for (Equipement e : equipements) {
            data.add(Arrays.asList(
                    e.getId(),
                    e.getLibelle(),
                    e.getDescription(),
                    e.getSalle() != null ? e.getSalle().getId() : "N/A" // Afficher l'ID de la salle
            ));
        }
        return data;
    }
}
