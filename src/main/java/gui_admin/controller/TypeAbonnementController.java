package gui_admin.controller;

import entite.TypeAbonnement;
import gui_admin.view.type_abonnements.Edit;
import service.TypeAbonnementService;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.SwingUtilities;

public class TypeAbonnementController {

    private TypeAbonnementService service;
    private Edit currentEditPanel; // Référence au panneau Edit actuellement affiché

    public TypeAbonnementController() {
        service = new TypeAbonnementService();
    }

    public void setEditPanel(Edit editPanel) {
        this.currentEditPanel = editPanel;

        this.currentEditPanel.getSaveButton().addActionListener(e -> {
            System.out.println("--- DIAGNOSTIC: Bouton 'Enregistrer' cliqué dans TypeAbonnementController (via setEditPanel) ---");
            if (currentEditPanel == null) {
                System.err.println("ERREUR CRITIQUE: currentEditPanel est NULL dans le listener SaveButton !");
                JOptionPane.showMessageDialog(null, "Erreur interne: le panneau d'édition est null.", "Erreur Grave", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Étape 1: Assurez-vous que l'entité interne du panneau est non-null et de type TypeAbonnement.
                // Cela devrait être géré par createAndConfigureEditPanelForAdd() ou setEntite().
                Object rawEntite = currentEditPanel.getEntite();
                if (rawEntite == null) {
                    System.err.println("ERREUR: currentEditPanel.getEntite() retourne NULL AVANT init() !");
                    // Tente de créer une nouvelle entité pour éviter l'erreur, même si cela indique un problème de flux
                    currentEditPanel.setEntite(new TypeAbonnement());
                    rawEntite = currentEditPanel.getEntite(); // Récupère la nouvelle entité
                    if (rawEntite == null) { // Si ça reste null, c'est un problème plus profond
                        JOptionPane.showMessageDialog(currentEditPanel, "Erreur interne critique: l'entité TypeAbonnement est null après réinitialisation.", "Erreur", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                // Étape 2: Peupler l'entité avec les données du formulaire.
                currentEditPanel.init(); // Cette méthode met à jour les champs de l'entité interne du panneau

                // Étape 3: Récupérer l'entité mise à jour après init().
                TypeAbonnement entiteCourante = (TypeAbonnement) currentEditPanel.getEntite();

                if (entiteCourante == null) {
                    System.err.println("ERREUR: currentEditPanel.getEntite() retourne NULL APRES init() !");
                    JOptionPane.showMessageDialog(currentEditPanel, "Erreur interne: l'entité 'TypeAbonnement' est nulle après l'initialisation du formulaire.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return; // Arrêter l'exécution pour éviter le NullPointerException
                }

                // Vérifier si c'est un ajout ou une modification
                // Pour TypeAbonnement, le 'code' est la clé primaire. Si le code n'existe pas en BDD, c'est un ajout.
                // Si le code existe, c'est une modification.
                if (service.trouver(entiteCourante.getCode()) == null) {
                    // C'est un nouvel ajout
                    System.out.println("DEBUG: Tentative d'ajout d'un nouveau type d'abonnement avec code: " + entiteCourante.getCode());
                    service.ajouter(entiteCourante);
                    System.out.println("Nouvel abonnement ajouté : " + entiteCourante.getLibelle() + " (Code: " + entiteCourante.getCode() + ")");
                    currentEditPanel.clearForm(); // Effacer le formulaire après l'ajout réussi
                } else {
                    // C'est une modification d'un abonnement existant
                    System.out.println("DEBUG: Tentative de modification du type d'abonnement avec code: " + entiteCourante.getCode());
                    service.modifier(entiteCourante);
                    System.out.println("Abonnement modifié : " + entiteCourante.getLibelle() + " (Code: " + entiteCourante.getCode() + ")");
                    // Ne pas effacer le formulaire après modification, l'utilisateur pourrait vouloir continuer à voir/éditer.
                }

                // Mettre à jour le tableau du panneau actuellement affiché
                currentEditPanel.getCustomTablePanel().updateTableData(convertAbonnementsToTableData(service.listerTous()));

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(currentEditPanel, "Le montant doit être un nombre valide.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (Exception ex) { // Capturer toutes les exceptions pour un débogage initial
                JOptionPane.showMessageDialog(currentEditPanel, "Erreur lors de l'opération d'enregistrement : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                System.err.println("DEBUG: Exception complète lors de l'enregistrement:");
                ex.printStackTrace();
            }
        });

        this.currentEditPanel.getCancelButton().addActionListener(e -> {
            System.out.println("Opération d'ajout/modification annulée. Nettoyage du formulaire.");
            currentEditPanel.clearForm(); // Nettoie le formulaire
            currentEditPanel.setEntite(new TypeAbonnement()); // Prépare une nouvelle entité vide pour le formulaire
        });

        // ===========================================
        // AJOUT DES LISTENERS POUR MODIFIER / SUPPRIMER
        // ===========================================
        // Assurez-vous que votre 'Edit.java' pour TypeAbonnement a bien des getters pour ces boutons

        if (currentEditPanel.getModifyButton() != null) {
            currentEditPanel.getModifyButton().addActionListener(e -> {
                System.out.println("--- DIAGNOSTIC: Bouton 'Modifier' cliqué dans TypeAbonnementController ---");
                int selectedRow = currentEditPanel.getCustomTablePanel().getSelectedRow();
                if (selectedRow != -1) {
                    Object codeObject = currentEditPanel.getCustomTablePanel().getValueAt(selectedRow, 0);
                    try {
                        String typeAbonnementCode = codeObject.toString();
                        TypeAbonnement typeAbonnementToModify = service.trouver(typeAbonnementCode);
                        if (typeAbonnementToModify != null) {
                            currentEditPanel.setEntite(typeAbonnementToModify);
                            currentEditPanel.initForm(typeAbonnementToModify);
                            System.out.println("Type Abonnement sélectionné pour modification : " + typeAbonnementToModify.getLibelle() + " (Code: " + typeAbonnementToModify.getCode() + ")");
                        } else {
                            JOptionPane.showMessageDialog(currentEditPanel, "Impossible de trouver le type d'abonnement avec le code " + typeAbonnementCode + ".", "Erreur", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(currentEditPanel, "Erreur lors de la sélection pour modification : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(currentEditPanel, "Veuillez sélectionner un type d'abonnement dans le tableau pour le modifier.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }

        if (currentEditPanel.getDeleteButton() != null) {
            currentEditPanel.getDeleteButton().addActionListener(e -> {
                System.out.println("--- DIAGNOSTIC: Bouton 'Supprimer' cliqué dans TypeAbonnementController ---");
                int selectedRow = currentEditPanel.getCustomTablePanel().getSelectedRow();
                if (selectedRow != -1) {
                    Object codeObject = currentEditPanel.getCustomTablePanel().getValueAt(selectedRow, 0);
                    try {
                        String typeAbonnementCode = codeObject.toString();
                        int confirm = JOptionPane.showConfirmDialog(currentEditPanel,
                                "Êtes-vous sûr de vouloir supprimer le type d'abonnement avec le code " + typeAbonnementCode + " ?\nAttention : cela peut entraîner des erreurs si des abonnements y sont liés !",
                                "Confirmation de suppression", JOptionPane.YES_NO_OPTION);

                        if (confirm == JOptionPane.YES_OPTION) {
                            TypeAbonnement typeAbonnementToDelete = service.trouver(typeAbonnementCode);
                            if (typeAbonnementToDelete != null) {
                                service.supprimer(typeAbonnementToDelete); // Le service doit gérer la suppression
                                System.out.println("Type Abonnement avec code: " + typeAbonnementCode + " supprimé.");
                                currentEditPanel.clearForm();
                                currentEditPanel.getCustomTablePanel().updateTableData(convertAbonnementsToTableData(service.listerTous()));
                            } else {
                                JOptionPane.showMessageDialog(currentEditPanel, "Le type d'abonnement n'existe plus en base de données.", "Erreur", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(currentEditPanel, "Erreur lors de la suppression : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(currentEditPanel, "Veuillez sélectionner un type d'abonnement dans le tableau pour le supprimer.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
    }

    public Edit createAndConfigureEditPanelForAdd() {
        List<TypeAbonnement> existingAbonnements = service.listerTous();
        List<String> columnNames = Arrays.asList("Code", "Libellé", "Montant");
        List<List<Object>> tableData = convertAbonnementsToTableData(existingAbonnements);

        Edit edit = new Edit(tableData, columnNames);
        edit.setEntite(new TypeAbonnement()); // Prépare l'entité pour un nouvel ajout
        return edit;
    }

    public Edit createAndConfigureEditPanelForModify(TypeAbonnement entiteToModify) {
        List<TypeAbonnement> existingAbonnements = service.listerTous();
        List<String> columnNames = Arrays.asList("Code", "Libellé", "Montant");
        List<List<Object>> tableData = convertAbonnementsToTableData(existingAbonnements);

        Edit edit = new Edit(tableData, columnNames);
        edit.setEntite(entiteToModify); // Initialiser l'entité du formulaire avec l'entité à modifier
        edit.initForm(entiteToModify); // Populer les champs du formulaire avec les données de l'entité
        return edit;
    }

    private List<List<Object>> convertAbonnementsToTableData(List<TypeAbonnement> abonnements) {
        List<List<Object>> data = new ArrayList<>();
        for (TypeAbonnement ta : abonnements) {
            data.add(Arrays.asList(ta.getCode(), ta.getLibelle(), ta.getMontant()));
        }
        return data;
    }
}
