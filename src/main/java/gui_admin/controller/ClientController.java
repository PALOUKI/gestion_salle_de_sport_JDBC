package gui_admin.controller;

import entite.Client;
import gui_admin.view.clients.ClientEdit; // Assurez-vous que cette classe existe et est l'interface d'édition pour Client
import service.ClientService;

import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.sql.SQLException; // Importez SQLException pour intercepter la violation de contrainte
import java.sql.SQLIntegrityConstraintViolationException; // Importez la classe spécifique si vous voulez être très précis

public class ClientController {

    private ClientService service;
    private ClientEdit currentEditPanel; // Référence au panneau ClientEdit actuellement affiché

    // Le formateur de date doit être cohérent avec ce que l'utilisateur entre dans le champ de texte.
    // Par exemple, "yyyy-MM-dd" pour "AAAA-MM-JJ".
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ClientController() {
        service = new ClientService();
    }

    // Méthode pour configurer le contrôleur avec le panneau ClientEdit à manipuler
    public void setEditPanel(ClientEdit editPanel) {
        this.currentEditPanel = editPanel;

        // Attacher les listeners une seule fois ici, au moment où le panneau est lié au contrôleur
        this.currentEditPanel.getSaveButton().addActionListener(e -> {
            System.out.println("--- DIAGNOSTIC: Bouton 'Enregistrer' cliqué dans ClientController (via setEditPanel) ---");
            try {
                // Peupler l'entité interne du panneau avec les données du formulaire
                currentEditPanel.init();

                // Déterminer s'il s'agit d'un ajout ou d'une modification
                if (currentEditPanel.getEntite().getId() == 0) { // Si l'ID est 0, c'est un nouvel ajout
                    service.ajouter(currentEditPanel.getEntite());
                    System.out.println("Nouveau client ajouté : " + currentEditPanel.getEntite().getNom() + " (ID: " + currentEditPanel.getEntite().getId() + ")");
                    currentEditPanel.clearForm(); // Effacer le formulaire après l'ajout
                } else { // Si l'ID n'est pas 0, c'est une modification
                    service.modifier(currentEditPanel.getEntite());
                    System.out.println("Client modifié : " + currentEditPanel.getEntite().getNom() + " (ID: " + currentEditPanel.getEntite().getId() + ")");
                    // Ne pas effacer le formulaire après modification, l'utilisateur pourrait vouloir continuer à voir/éditer.
                }

                // Mettre à jour le tableau du panneau actuellement affiché
                currentEditPanel.getCustomTablePanel().updateTableData(convertClientsToTableData(service.listerTous()));


            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(currentEditPanel, "Le montant (si applicable) doit être un nombre valide.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(currentEditPanel, "Format de date de naissance invalide. Veuillez utiliser le format AAAA-MM-JJ (ex: 2000-01-15).", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(currentEditPanel, "Erreur lors de l'opération : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        this.currentEditPanel.getCancelButton().addActionListener(e -> {
            System.out.println("Opération d'ajout/modification de client annulée.");
            currentEditPanel.clearForm(); // Nettoie le formulaire
            currentEditPanel.setEntite(new Client()); // Prépare une nouvelle entité vide pour le formulaire
            // Logique pour annuler, si nécessaire (ex: revenir à une page d'accueil)
            // Cela dépendra de la structure de votre CardLayout dans MyWindow1
        });

        // ===========================================
        // ACTIVATION DES LISTENERS POUR MODIFIER / SUPPRIMER
        // ===========================================
        // Assurez-vous que votre 'ClientEdit.java' a bien des getters pour ces boutons.

        if (currentEditPanel.getModifyButton() != null) { // Vérifier si le bouton "Modifier" existe dans la vue
            currentEditPanel.getModifyButton().addActionListener(e -> {
                System.out.println("--- DIAGNOSTIC: Bouton 'Modifier' cliqué dans ClientController ---");
                int selectedRow = currentEditPanel.getCustomTablePanel().getSelectedRow();
                if (selectedRow != -1) { // Une ligne est sélectionnée
                    // Récupérer l'ID du client de la première colonne du tableau
                    Object idObject = currentEditPanel.getCustomTablePanel().getValueAt(selectedRow, 0);
                    try {
                        int clientId = Integer.parseInt(idObject.toString());
                        Client clientToModify = service.trouver(clientId); // Trouver l'entité Client complète via le service
                        if (clientToModify != null) {
                            currentEditPanel.setEntite(clientToModify); // Assigner l'entité à modifier au formulaire
                            currentEditPanel.initForm(clientToModify); // Populer le formulaire avec les données de l'entité
                            System.out.println("Client sélectionné pour modification : " + clientToModify.getNom() + " (ID: " + clientToModify.getId() + ")");
                        } else {
                            JOptionPane.showMessageDialog(currentEditPanel, "Impossible de trouver le client avec l'ID " + clientId + ".", "Erreur", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(currentEditPanel, "L'ID de la colonne sélectionnée n'est pas un nombre valide.", "Erreur de tableau", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(currentEditPanel, "Erreur lors de la sélection du client pour modification : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(currentEditPanel, "Veuillez sélectionner un client dans le tableau pour le modifier.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }

        if (currentEditPanel.getDeleteButton() != null) { // Vérifier si le bouton "Supprimer" existe dans la vue
            currentEditPanel.getDeleteButton().addActionListener(e -> {
                System.out.println("--- DIAGNOSTIC: Bouton 'Supprimer' cliqué dans ClientController ---");
                int selectedRow = currentEditPanel.getCustomTablePanel().getSelectedRow();
                if (selectedRow != -1) {
                    Object idObject = currentEditPanel.getCustomTablePanel().getValueAt(selectedRow, 0);
                    try {
                        int clientId = Integer.parseInt(idObject.toString());
                        // Demander confirmation avant de supprimer
                        int confirm = JOptionPane.showConfirmDialog(currentEditPanel,
                                "Êtes-vous sûr de vouloir supprimer le client avec l'ID " + clientId + " ?",
                                "Confirmation de suppression", JOptionPane.YES_NO_OPTION);

                        if (confirm == JOptionPane.YES_OPTION) {
                            // Supprimer le client via le service
                            // Assurez-vous que votre service.supprimer prend un int ID
                            service.supprimer(clientId);
                            System.out.println("Client avec ID: " + clientId + " supprimé avec succès.");
                            currentEditPanel.clearForm(); // Effacer le formulaire après la suppression
                            // Mettre à jour le tableau
                            currentEditPanel.getCustomTablePanel().updateTableData(convertClientsToTableData(service.listerTous()));
                        }
                    }
                    // --- NOUVEAU BLOC CATCH POUR GÉRER LA VIOLATION DE CLÉ ÉTRANGÈRE ---
                    catch (SQLIntegrityConstraintViolationException ex) {
                        JOptionPane.showMessageDialog(currentEditPanel,
                                "Impossible de supprimer le client car il est lié à d'autres entités (par exemple, un membre, une demande d'inscription, un ticket, etc.)." +
                                        "\nVeuillez d'abord supprimer ces entités liées.",
                                "Erreur de suppression (Contrainte de Clé Étrangère)",
                                JOptionPane.ERROR_MESSAGE);
                        System.err.println("DEBUG: Erreur de contrainte de clé étrangère lors de la suppression du client.");
                        ex.printStackTrace(); // Pour le diagnostic détaillé dans la console
                    }
                    // --- FIN NOUVEAU BLOC CATCH ---
                    catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(currentEditPanel, "L'ID de la colonne sélectionnée n'est pas un nombre valide.", "Erreur de tableau", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(currentEditPanel, "Erreur lors de la suppression du client : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(currentEditPanel, "Veuillez sélectionner un client dans le tableau pour le supprimer.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
    }

    // Méthode pour créer et configurer le panneau ClientEdit pour un ajout
    public ClientEdit createAndConfigureEditPanelForAdd() throws SQLException {
        List<Client> existingClients = service.listerTous();
        List<String> columnNames = Arrays.asList("ID", "Nom", "Prénom", "Date de Naissance", "Email"); // Adaptez les noms de colonnes
        List<List<Object>> tableData = convertClientsToTableData(existingClients);

        ClientEdit edit = new ClientEdit(tableData, columnNames);
        edit.setEntite(new Client()); // Initialise l'entité interne du formulaire pour un NOUVEL ajout
        return edit;
    }

    // Méthode pour créer et configurer le panneau ClientEdit pour une modification
    public ClientEdit createAndConfigureEditPanelForModify(Client entiteToModify) throws SQLException {
        List<Client> existingClients = service.listerTous();
        List<String> columnNames = Arrays.asList("ID", "Nom", "Prénom", "Date de Naissance", "Email"); // Adaptez les noms de colonnes
        List<List<Object>> tableData = convertClientsToTableData(existingClients);

        ClientEdit edit = new ClientEdit(tableData, columnNames);
        edit.setEntite(entiteToModify); // Passe l'entité à modifier au formulaire
        edit.initForm(entiteToModify); // Initialise les champs du formulaire avec les données de l'entité
        return edit;
    }

    // Méthode utilitaire pour convertir une liste de Clients en données pour JTable
    private List<List<Object>> convertClientsToTableData(List<Client> clients) {
        List<List<Object>> data = new ArrayList<>();
        for (Client client : clients) {
            data.add(Arrays.asList(
                    client.getId(),
                    client.getNom(),
                    client.getPrenom(),
                    // Formater la LocalDate en String pour l'affichage dans la table
                    client.getDateNaissance() != null ? client.getDateNaissance().format(DATE_FORMATTER) : "N/A",
                    client.getEmail()
            ));
        }
        return data;
    }
}
