package gui_admin.controller;

import entite.Ticket;
import entite.Client; // Importez Client car Ticket contient un objet Client
import gui_admin.view.tickets.TicketEdit; // Assurez-vous que cette classe existe et est l'interface d'édition
import service.TicketService; // Importez le service Ticket
import service.ClientService; // Importez le service Client pour récupérer la liste des clients

import javax.swing.JOptionPane;
import java.sql.SQLException; // Importez SQLException
import java.sql.SQLIntegrityConstraintViolationException; // Importez SQLIntegrityConstraintViolationException
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TicketController {

    private TicketService service;
    private ClientService clientService; // Pour obtenir la liste des clients
    private TicketEdit currentEditPanel; // Référence au panneau d'édition actuel

    public TicketController() {
        service = new TicketService();
        clientService = new ClientService(); // Initialisation du service client
    }

    // Méthode pour configurer le contrôleur avec le panneau d'édition à manipuler
    public void setEditPanel(TicketEdit editPanel) {
        this.currentEditPanel = editPanel;

        // Listener pour le bouton "Enregistrer" (ajout/modification)
        this.currentEditPanel.getSaveButton().addActionListener(e -> {
            System.out.println("--- DIAGNOSTIC: Bouton 'Enregistrer' cliqué dans TicketController ---");
            try {
                // Peupler l'entité interne du panneau avec les données du formulaire
                currentEditPanel.init();

                // Récupérer l'entité mise à jour après init()
                Ticket entiteCourante = (Ticket) currentEditPanel.getEntite();

                // Validation: Le client est obligatoire pour un ticket
                if (entiteCourante.getClient() == null || entiteCourante.getClient().getId() == 0) {
                    JOptionPane.showMessageDialog(currentEditPanel, "Veuillez sélectionner un client valide pour ce ticket.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Validation: Le nombre de séances doit être positif
                if (entiteCourante.getNombreDeSeance() <= 0) {
                    JOptionPane.showMessageDialog(currentEditPanel, "Le nombre de séances doit être un nombre positif.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Validation: Le montant doit être positif
                if (entiteCourante.getMontant() <= 0) {
                    JOptionPane.showMessageDialog(currentEditPanel, "Le montant doit être un nombre positif.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Déterminer s'il s'agit d'un ajout ou d'une modification
                // Pour une entité avec ID auto-incrémenté, ID == 0 signifie nouvel ajout.
                if (entiteCourante.getId() == 0) {
                    // C'est un nouvel ajout
                    service.ajouter(entiteCourante);
                    System.out.println("Nouveau ticket ajouté pour client ID: " + entiteCourante.getClient().getId() + " (ID: " + entiteCourante.getId() + ")");
                    currentEditPanel.clearForm(); // Effacer le formulaire après l'ajout réussi
                } else {
                    // C'est une modification d'un ticket existant
                    service.modifier(entiteCourante);
                    System.out.println("Ticket modifié pour ID: " + entiteCourante.getId() + " (Client ID: " + entiteCourante.getClient().getId() + ")");
                    // Ne pas effacer le formulaire après modification.
                }

                // Mettre à jour le tableau du panneau actuellement affiché
                currentEditPanel.getCustomTablePanel().updateTableData(convertTicketsToTableData(service.listerTous()));

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(currentEditPanel, "Erreur de format numérique (ex: nombre de séances, montant). " + ex.getMessage(), "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (SQLIntegrityConstraintViolationException ex) {
                JOptionPane.showMessageDialog(currentEditPanel,
                        "Impossible d'enregistrer le ticket : un client avec cet ID n'existe peut-être pas, ou un autre problème de contrainte d'intégrité est violé.",
                        "Erreur de contrainte (SQL)",
                        JOptionPane.ERROR_MESSAGE);
                System.err.println("DEBUG: Erreur de contrainte de clé étrangère/intégrité lors de l'enregistrement du ticket.");
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
            currentEditPanel.setEntite(new Ticket()); // Prépare une nouvelle entité vide pour le formulaire
        });

        // Listener pour le bouton "Modifier"
        if (currentEditPanel.getModifyButton() != null) {
            currentEditPanel.getModifyButton().addActionListener(e -> {
                System.out.println("--- DIAGNOSTIC: Bouton 'Modifier' cliqué dans TicketController ---");
                int selectedRow = currentEditPanel.getCustomTablePanel().getSelectedRow();
                if (selectedRow != -1) {
                    Object idObject = currentEditPanel.getCustomTablePanel().getValueAt(selectedRow, 0); // L'ID est généralement la première colonne
                    try {
                        int ticketId = Integer.parseInt(idObject.toString());
                        Ticket ticketToModify = service.trouver(ticketId);
                        if (ticketToModify != null) {
                            currentEditPanel.setEntite(ticketToModify);
                            currentEditPanel.initForm(ticketToModify);
                            System.out.println("Ticket sélectionné pour modification : ID " + ticketToModify.getId() + " (Client ID: " + ticketToModify.getClient().getId() + ")");
                        } else {
                            JOptionPane.showMessageDialog(currentEditPanel, "Impossible de trouver le ticket avec l'ID " + ticketId + ".", "Erreur", JOptionPane.ERROR_MESSAGE);
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
                    JOptionPane.showMessageDialog(currentEditPanel, "Veuillez sélectionner un ticket dans le tableau pour le modifier.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }

        // Listener pour le bouton "Supprimer"
        if (currentEditPanel.getDeleteButton() != null) {
            currentEditPanel.getDeleteButton().addActionListener(e -> {
                System.out.println("--- DIAGNOSTIC: Bouton 'Supprimer' cliqué dans TicketController ---");
                int selectedRow = currentEditPanel.getCustomTablePanel().getSelectedRow();
                if (selectedRow != -1) {
                    Object idObject = currentEditPanel.getCustomTablePanel().getValueAt(selectedRow, 0);
                    try {
                        int ticketId = Integer.parseInt(idObject.toString());
                        int confirm = JOptionPane.showConfirmDialog(currentEditPanel,
                                "Êtes-vous sûr de vouloir supprimer le ticket avec l'ID " + ticketId + " ?",
                                "Confirmation de suppression", JOptionPane.YES_NO_OPTION);

                        if (confirm == JOptionPane.YES_OPTION) {
                            service.supprimer(ticketId); // Supprimer le ticket via le service
                            System.out.println("Ticket avec ID: " + ticketId + " supprimé avec succès.");
                            currentEditPanel.clearForm(); // Effacer le formulaire après la suppression
                            currentEditPanel.getCustomTablePanel().updateTableData(convertTicketsToTableData(service.listerTous()));
                        }
                    } catch (SQLIntegrityConstraintViolationException ex) {
                        JOptionPane.showMessageDialog(currentEditPanel,
                                "Impossible de supprimer ce ticket car il est lié à d'autres entités.",
                                "Erreur de suppression (Contrainte de Clé Étrangère)",
                                JOptionPane.ERROR_MESSAGE);
                        System.err.println("DEBUG: Erreur de contrainte de clé étrangère lors de la suppression du ticket.");
                        ex.printStackTrace();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(currentEditPanel, "Erreur de base de données lors de la suppression : " + ex.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(currentEditPanel, "Erreur inattendue lors de la suppression : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(currentEditPanel, "Veuillez sélectionner un ticket dans le tableau pour le supprimer.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
    }

    // Méthode pour créer et configurer le panneau TicketEdit pour un ajout
    public TicketEdit createAndConfigureEditPanelForAdd() throws SQLException {
        List<Ticket> existingTickets = service.listerTous();
        List<Client> allClients = clientService.listerTous(); // Récupérer la liste complète des clients

        List<String> columnNames = Arrays.asList("ID", "Nombre de Séances", "Montant", "ID Client");
        List<List<Object>> tableData = convertTicketsToTableData(existingTickets);

        TicketEdit edit = new TicketEdit(tableData, columnNames, allClients); // Passer la liste des clients à la vue
        edit.setEntite(new Ticket()); // Initialise l'entité interne du formulaire pour un NOUVEL ajout
        return edit;
    }

    // Méthode pour créer et configurer le panneau TicketEdit pour une modification
    public TicketEdit createAndConfigureEditPanelForModify(Ticket entiteToModify) throws SQLException {
        List<Ticket> existingTickets = service.listerTous();
        List<Client> allClients = clientService.listerTous(); // Récupérer la liste complète des clients

        List<String> columnNames = Arrays.asList("ID", "Nombre de Séances", "Montant", "ID Client");
        List<List<Object>> tableData = convertTicketsToTableData(existingTickets);

        TicketEdit edit = new TicketEdit(tableData, columnNames, allClients); // Passer la liste des clients à la vue
        edit.setEntite(entiteToModify); // Passe l'entité à modifier au formulaire
        edit.initForm(entiteToModify); // Initialise les champs du formulaire avec les données de l'entité
        return edit;
    }

    // Méthode utilitaire pour convertir une liste de Ticket en données pour JTable
    private List<List<Object>> convertTicketsToTableData(List<Ticket> tickets) {
        List<List<Object>> data = new ArrayList<>();
        for (Ticket t : tickets) {
            data.add(Arrays.asList(
                    t.getId(),
                    t.getNombreDeSeance(),
                    t.getMontant(),
                    t.getClient() != null ? t.getClient().getId() : "N/A" // Afficher l'ID du client
            ));
        }
        return data;
    }
}
