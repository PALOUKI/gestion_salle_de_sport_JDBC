package gui_admin.controller;

import entite.Membre;
import entite.Client; // Importez Client car Membre contient un objet Client
import gui_admin.view.membres.MembreEdit; // Assurez-vous que cette classe existe et est l'interface d'édition
import service.MembreService; // Importez le service Membre
import service.ClientService; // Importez le service Client pour récupérer la liste des clients

import javax.swing.JOptionPane;
import java.sql.SQLException; // Importez SQLException
import java.sql.SQLIntegrityConstraintViolationException; // Importez SQLIntegrityConstraintViolationException
import java.time.LocalDateTime; // Importez LocalDateTime
import java.time.format.DateTimeFormatter; // Importez DateTimeFormatter
import java.time.format.DateTimeParseException; // Importez DateTimeParseException
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MembreController {

    private MembreService service;
    private ClientService clientService; // Pour obtenir la liste des clients
    private MembreEdit currentEditPanel; // Référence au panneau d'édition actuel

    // Formateur de date/heure cohérent avec la vue.
    // Il est préférable de le centraliser ou de le réutiliser si le format est le même.
    // Utilisons celui de DemandeInscriptionController pour la cohérence.
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public MembreController() {
        service = new MembreService();
        clientService = new ClientService(); // Initialisation du service client
    }

    // Méthode pour configurer le contrôleur avec le panneau d'édition à manipuler
    public void setEditPanel(MembreEdit editPanel) {
        this.currentEditPanel = editPanel;

        // Listener pour le bouton "Enregistrer" (ajout/modification)
        this.currentEditPanel.getSaveButton().addActionListener(e -> {
            System.out.println("--- DIAGNOSTIC: Bouton 'Enregistrer' cliqué dans MembreController ---");
            try {
                // Peupler l'entité interne du panneau avec les données du formulaire
                currentEditPanel.init();

                // Récupérer l'entité mise à jour après init()
                Membre entiteCourante = (Membre) currentEditPanel.getEntite();

                // Validation: Le client est obligatoire pour un membre
                if (entiteCourante.getClient() == null || entiteCourante.getClient().getId() == 0) {
                    JOptionPane.showMessageDialog(currentEditPanel, "Veuillez sélectionner un client valide pour ce membre.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Validation: La date d'inscription est obligatoire
                if (entiteCourante.getDateInscription() == null) {
                    JOptionPane.showMessageDialog(currentEditPanel, "La date d'inscription est obligatoire.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Déterminer s'il s'agit d'un ajout ou d'une modification
                // Pour une entité avec ID auto-incrémenté, ID == 0 signifie nouvel ajout.
                if (entiteCourante.getId() == 0) {
                    // C'est un nouvel ajout
                    service.ajouter(entiteCourante);
                    System.out.println("Nouveau membre ajouté pour client ID: " + entiteCourante.getClient().getId() + " (ID: " + entiteCourante.getId() + ")");
                    currentEditPanel.clearForm(); // Effacer le formulaire après l'ajout réussi
                } else {
                    // C'est une modification d'un membre existant
                    service.modifier(entiteCourante);
                    System.out.println("Membre modifié pour ID: " + entiteCourante.getId() + " (Client ID: " + entiteCourante.getClient().getId() + ")");
                    // Ne pas effacer le formulaire après modification.
                }

                // Mettre à jour le tableau du panneau actuellement affiché
                currentEditPanel.getCustomTablePanel().updateTableData(convertMembresToTableData(service.listerTous()));

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(currentEditPanel, "Erreur de format numérique. " + ex.getMessage(), "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(currentEditPanel, "Format de date/heure invalide. Veuillez utiliser le format AAAA-MM-JJ HH:MM:SS.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (SQLIntegrityConstraintViolationException ex) {
                JOptionPane.showMessageDialog(currentEditPanel,
                        "Impossible d'enregistrer le membre : un client avec cet ID n'existe peut-être pas, ou un autre problème de contrainte d'intégrité est violé (par exemple, un client est déjà membre).",
                        "Erreur de contrainte (SQL)",
                        JOptionPane.ERROR_MESSAGE);
                System.err.println("DEBUG: Erreur de contrainte de clé étrangère/intégrité lors de l'enregistrement du membre.");
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
            currentEditPanel.setEntite(new Membre()); // Prépare une nouvelle entité vide pour le formulaire
        });

        // Listener pour le bouton "Modifier"
        if (currentEditPanel.getModifyButton() != null) {
            currentEditPanel.getModifyButton().addActionListener(e -> {
                System.out.println("--- DIAGNOSTIC: Bouton 'Modifier' cliqué dans MembreController ---");
                int selectedRow = currentEditPanel.getCustomTablePanel().getSelectedRow();
                if (selectedRow != -1) {
                    Object idObject = currentEditPanel.getCustomTablePanel().getValueAt(selectedRow, 0); // L'ID est généralement la première colonne
                    try {
                        int membreId = Integer.parseInt(idObject.toString());
                        Membre membreToModify = service.trouver(membreId);
                        if (membreToModify != null) {
                            currentEditPanel.setEntite(membreToModify);
                            currentEditPanel.initForm(membreToModify);
                            System.out.println("Membre sélectionné pour modification : ID " + membreToModify.getId() + " (Client ID: " + membreToModify.getClient().getId() + ")");
                        } else {
                            JOptionPane.showMessageDialog(currentEditPanel, "Impossible de trouver le membre avec l'ID " + membreId + ".", "Erreur", JOptionPane.ERROR_MESSAGE);
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
                    JOptionPane.showMessageDialog(currentEditPanel, "Veuillez sélectionner un membre dans le tableau pour le modifier.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }

        // Listener pour le bouton "Supprimer"
        if (currentEditPanel.getDeleteButton() != null) {
            currentEditPanel.getDeleteButton().addActionListener(e -> {
                System.out.println("--- DIAGNOSTIC: Bouton 'Supprimer' cliqué dans MembreController ---");
                int selectedRow = currentEditPanel.getCustomTablePanel().getSelectedRow();
                if (selectedRow != -1) {
                    Object idObject = currentEditPanel.getCustomTablePanel().getValueAt(selectedRow, 0);
                    try {
                        int membreId = Integer.parseInt(idObject.toString());
                        int confirm = JOptionPane.showConfirmDialog(currentEditPanel,
                                "Êtes-vous sûr de vouloir supprimer le membre avec l'ID " + membreId + " ?",
                                "Confirmation de suppression", JOptionPane.YES_NO_OPTION);

                        if (confirm == JOptionPane.YES_OPTION) {
                            service.supprimer(membreId); // Supprimer le membre via le service
                            System.out.println("Membre avec ID: " + membreId + " supprimé avec succès.");
                            currentEditPanel.clearForm(); // Effacer le formulaire après la suppression
                            currentEditPanel.getCustomTablePanel().updateTableData(convertMembresToTableData(service.listerTous()));
                        }
                    } catch (SQLIntegrityConstraintViolationException ex) {
                        JOptionPane.showMessageDialog(currentEditPanel,
                                "Impossible de supprimer ce membre car il est lié à d'autres entités (par exemple, abonnements, paiements, tickets).",
                                "Erreur de suppression (Contrainte de Clé Étrangère)",
                                JOptionPane.ERROR_MESSAGE);
                        System.err.println("DEBUG: Erreur de contrainte de clé étrangère lors de la suppression du membre.");
                        ex.printStackTrace();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(currentEditPanel, "Erreur de base de données lors de la suppression : " + ex.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(currentEditPanel, "Erreur inattendue lors de la suppression : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(currentEditPanel, "Veuillez sélectionner un membre dans le tableau pour le supprimer.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
    }

    // Méthode pour créer et configurer le panneau MembreEdit pour un ajout
    public MembreEdit createAndConfigureEditPanelForAdd() throws SQLException {
        List<Membre> existingMembres = service.listerTous();
        List<Client> allClients = clientService.listerTous(); // Récupérer la liste complète des clients

        List<String> columnNames = Arrays.asList("ID", "Date Inscription", "ID Client");
        List<List<Object>> tableData = convertMembresToTableData(existingMembres);

        MembreEdit edit = new MembreEdit(tableData, columnNames, allClients); // Passer la liste des clients à la vue
        edit.setEntite(new Membre()); // Initialise l'entité interne du formulaire pour un NOUVEL ajout
        return edit;
    }

    // Méthode pour créer et configurer le panneau MembreEdit pour une modification
    public MembreEdit createAndConfigureEditPanelForModify(Membre entiteToModify) throws SQLException {
        List<Membre> existingMembres = service.listerTous();
        List<Client> allClients = clientService.listerTous(); // Récupérer la liste complète des clients

        List<String> columnNames = Arrays.asList("ID", "Date Inscription", "ID Client");
        List<List<Object>> tableData = convertMembresToTableData(existingMembres);

        MembreEdit edit = new MembreEdit(tableData, columnNames, allClients); // Passer la liste des clients à la vue
        edit.setEntite(entiteToModify); // Passe l'entité à modifier au formulaire
        edit.initForm(entiteToModify); // Initialise les champs du formulaire avec les données de l'entité
        return edit;
    }

    // Méthode utilitaire pour convertir une liste de Membre en données pour JTable
    private List<List<Object>> convertMembresToTableData(List<Membre> membres) {
        List<List<Object>> data = new ArrayList<>();
        for (Membre m : membres) {
            data.add(Arrays.asList(
                    m.getId(),
                    m.getDateInscription() != null ? m.getDateInscription().format(DATE_TIME_FORMATTER) : "N/A",
                    m.getClient() != null ? m.getClient().getId() : "N/A" // Afficher l'ID du client
            ));
        }
        return data;
    }
}
