package gui_admin.controller;

import entite.MoyenDePaiement;
import gui_admin.view.moyen_de_paiements.MoyenDePaiementEdit; // Assurez-vous que cette classe existe et est l'interface d'édition
import service.MoyenDePaiementService;

import javax.swing.JOptionPane;
import java.sql.SQLException; // Importez SQLException
import java.sql.SQLIntegrityConstraintViolationException; // Importez SQLIntegrityConstraintViolationException
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MoyenDePaiementController {

    private MoyenDePaiementService service;
    private MoyenDePaiementEdit currentEditPanel; // Référence au panneau d'édition actuel

    public MoyenDePaiementController() {
        service = new MoyenDePaiementService();
    }

    // Méthode pour configurer le contrôleur avec le panneau d'édition à manipuler
    public void setEditPanel(MoyenDePaiementEdit editPanel) {
        this.currentEditPanel = editPanel;

        // Listener pour le bouton "Enregistrer" (ajout/modification)
        this.currentEditPanel.getSaveButton().addActionListener(e -> {
            System.out.println("--- DIAGNOSTIC: Bouton 'Enregistrer' cliqué dans MoyenDePaiementController ---");
            try {
                // Peupler l'entité interne du panneau avec les données du formulaire
                currentEditPanel.init();

                // Récupérer l'entité mise à jour après init()
                MoyenDePaiement entiteCourante = (MoyenDePaiement) currentEditPanel.getEntite();

                if (entiteCourante == null || entiteCourante.getCode() == null || entiteCourante.getCode().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(currentEditPanel, "Le code du moyen de paiement ne peut pas être vide.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Vérifier si c'est un ajout ou une modification (basé sur l'existence du code)
                if (service.trouver(entiteCourante.getCode()) == null) {
                    // C'est un nouvel ajout
                    service.ajouter(entiteCourante);
                    System.out.println("Nouveau moyen de paiement ajouté : " + entiteCourante.getLibelle() + " (Code: " + entiteCourante.getCode() + ")");
                    currentEditPanel.clearForm(); // Effacer le formulaire après l'ajout réussi
                } else {
                    // C'est une modification d'un moyen de paiement existant
                    service.modifier(entiteCourante);
                    System.out.println("Moyen de paiement modifié : " + entiteCourante.getLibelle() + " (Code: " + entiteCourante.getCode() + ")");
                    // Ne pas effacer le formulaire après modification.
                }

                // Mettre à jour le tableau du panneau actuellement affiché
                currentEditPanel.getCustomTablePanel().updateTableData(convertMoyensDePaiementToTableData(service.listerTous()));

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
            currentEditPanel.setEntite(new MoyenDePaiement()); // Prépare une nouvelle entité vide pour le formulaire
        });

        // Listener pour le bouton "Modifier"
        if (currentEditPanel.getModifyButton() != null) {
            currentEditPanel.getModifyButton().addActionListener(e -> {
                System.out.println("--- DIAGNOSTIC: Bouton 'Modifier' cliqué dans MoyenDePaiementController ---");
                int selectedRow = currentEditPanel.getCustomTablePanel().getSelectedRow();
                if (selectedRow != -1) {
                    Object codeObject = currentEditPanel.getCustomTablePanel().getValueAt(selectedRow, 0);
                    try {
                        String moyenDePaiementCode = codeObject.toString();
                        MoyenDePaiement moyenDePaiementToModify = service.trouver(moyenDePaiementCode);
                        if (moyenDePaiementToModify != null) {
                            currentEditPanel.setEntite(moyenDePaiementToModify);
                            currentEditPanel.initForm(moyenDePaiementToModify);
                            System.out.println("Moyen de paiement sélectionné pour modification : " + moyenDePaiementToModify.getLibelle() + " (Code: " + moyenDePaiementToModify.getCode() + ")");
                        } else {
                            JOptionPane.showMessageDialog(currentEditPanel, "Impossible de trouver le moyen de paiement avec le code " + moyenDePaiementCode + ".", "Erreur", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(currentEditPanel, "Erreur de base de données lors de la sélection pour modification : " + ex.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(currentEditPanel, "Erreur inattendue lors de la sélection pour modification : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(currentEditPanel, "Veuillez sélectionner un moyen de paiement dans le tableau pour le modifier.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }

        // Listener pour le bouton "Supprimer"
        if (currentEditPanel.getDeleteButton() != null) {
            currentEditPanel.getDeleteButton().addActionListener(e -> {
                System.out.println("--- DIAGNOSTIC: Bouton 'Supprimer' cliqué dans MoyenDePaiementController ---");
                int selectedRow = currentEditPanel.getCustomTablePanel().getSelectedRow();
                if (selectedRow != -1) {
                    Object codeObject = currentEditPanel.getCustomTablePanel().getValueAt(selectedRow, 0);
                    try {
                        String moyenDePaiementCode = codeObject.toString();
                        int confirm = JOptionPane.showConfirmDialog(currentEditPanel,
                                "Êtes-vous sûr de vouloir supprimer le moyen de paiement avec le code " + moyenDePaiementCode + " ?\nAttention : cela peut entraîner la suppression d'entités liées (paiements) !",
                                "Confirmation de suppression", JOptionPane.YES_NO_OPTION);

                        if (confirm == JOptionPane.YES_OPTION) {
                            service.supprimer(moyenDePaiementCode); // Supprimer le moyen de paiement via le service
                            System.out.println("Moyen de paiement avec code: " + moyenDePaiementCode + " supprimé avec succès.");
                            currentEditPanel.clearForm(); // Effacer le formulaire après la suppression
                            currentEditPanel.getCustomTablePanel().updateTableData(convertMoyensDePaiementToTableData(service.listerTous()));
                        }
                    } catch (SQLIntegrityConstraintViolationException ex) {
                        JOptionPane.showMessageDialog(currentEditPanel,
                                "Impossible de supprimer ce moyen de paiement car il est lié à des paiements existants." +
                                        "\nVeuillez d'abord supprimer les paiements associés.",
                                "Erreur de suppression (Contrainte de Clé Étrangère)",
                                JOptionPane.ERROR_MESSAGE);
                        System.err.println("DEBUG: Erreur de contrainte de clé étrangère lors de la suppression du moyen de paiement.");
                        ex.printStackTrace();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(currentEditPanel, "Erreur de base de données lors de la suppression : " + ex.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(currentEditPanel, "Erreur inattendue lors de la suppression : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(currentEditPanel, "Veuillez sélectionner un moyen de paiement dans le tableau pour le supprimer.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
    }

    // Méthode pour créer et configurer le panneau MoyenDePaiementEdit pour un ajout
    public MoyenDePaiementEdit createAndConfigureEditPanelForAdd() throws SQLException { // Déclare la propagation d'exception
        List<MoyenDePaiement> existingMoyens = service.listerTous();
        List<String> columnNames = Arrays.asList("Code", "Libellé"); // Adaptez les noms de colonnes
        List<List<Object>> tableData = convertMoyensDePaiementToTableData(existingMoyens);

        MoyenDePaiementEdit edit = new MoyenDePaiementEdit(tableData, columnNames);
        edit.setEntite(new MoyenDePaiement()); // Initialise l'entité interne du formulaire pour un NOUVEL ajout
        return edit;
    }

    // Méthode pour créer et configurer le panneau MoyenDePaiementEdit pour une modification
    public MoyenDePaiementEdit createAndConfigureEditPanelForModify(MoyenDePaiement entiteToModify) throws SQLException { // Déclare la propagation d'exception
        List<MoyenDePaiement> existingMoyens = service.listerTous();
        List<String> columnNames = Arrays.asList("Code", "Libellé"); // Adaptez les noms de colonnes
        List<List<Object>> tableData = convertMoyensDePaiementToTableData(existingMoyens);

        MoyenDePaiementEdit edit = new MoyenDePaiementEdit(tableData, columnNames);
        edit.setEntite(entiteToModify); // Passe l'entité à modifier au formulaire
        edit.initForm(entiteToModify); // Initialise les champs du formulaire avec les données de l'entité
        return edit;
    }

    // Méthode utilitaire pour convertir une liste de MoyensDePaiement en données pour JTable
    private List<List<Object>> convertMoyensDePaiementToTableData(List<MoyenDePaiement> moyens) {
        List<List<Object>> data = new ArrayList<>();
        for (MoyenDePaiement mdp : moyens) {
            data.add(Arrays.asList(mdp.getCode(), mdp.getLibelle()));
        }
        return data;
    }
}
