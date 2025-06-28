package gui_admin.controller;

import entite.DemandeInscription;
import entite.Client;
import gui_admin.view.demande_inscriptions.DemandeInscriptionEdit;
import service.DemandeInscriptionService;
import service.ClientService; // NOUVEAU: Importez ClientService

import javax.swing.JOptionPane;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DemandeInscriptionController {

    private DemandeInscriptionService service;
    private ClientService clientService; // NOUVEAU: Instanciez ClientService
    private DemandeInscriptionEdit currentEditPanel;

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public DemandeInscriptionController() {
        service = new DemandeInscriptionService();
        clientService = new ClientService(); // INITIALISEZ ClientService
    }

    public void setEditPanel(DemandeInscriptionEdit editPanel) {
        this.currentEditPanel = editPanel;

        this.currentEditPanel.getSaveButton().addActionListener(e -> {
            System.out.println("--- DIAGNOSTIC: Bouton 'Enregistrer' cliqué dans DemandeInscriptionController ---");
            try {
                currentEditPanel.init();
                DemandeInscription entiteCourante = (DemandeInscription) currentEditPanel.getEntite();

                if (entiteCourante.getClient() == null || entiteCourante.getClient().getId() == 0) {
                    JOptionPane.showMessageDialog(currentEditPanel, "Veuillez spécifier un client valide.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (entiteCourante.getDateDeDemande() == null) {
                    JOptionPane.showMessageDialog(currentEditPanel, "La date de demande est obligatoire.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (entiteCourante.getId() == 0) {
                    service.ajouter(entiteCourante);
                    System.out.println("Nouvelle demande d'inscription ajoutée pour client ID: " + entiteCourante.getClient().getId() + " (ID: " + entiteCourante.getId() + ")");
                    currentEditPanel.clearForm();
                } else {
                    service.modifier(entiteCourante);
                    System.out.println("Demande d'inscription modifiée pour ID: " + entiteCourante.getId() + " (Client ID: " + entiteCourante.getClient().getId() + ")");
                }

                currentEditPanel.getCustomTablePanel().updateTableData(convertDemandeInscriptionsToTableData(service.listerTous()));

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(currentEditPanel, "Erreur de format numérique (ex: ID client). " + ex.getMessage(), "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(currentEditPanel, "Format de date/heure invalide. Veuillez utiliser le format AAAA-MM-JJ HH:MM:SS.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (SQLIntegrityConstraintViolationException ex) {
                JOptionPane.showMessageDialog(currentEditPanel,
                        "Impossible d'enregistrer la demande : un client avec cet ID n'existe peut-être pas, ou une autre contrainte d'intégrité est violée.",
                        "Erreur de contrainte (SQL)",
                        JOptionPane.ERROR_MESSAGE);
                System.err.println("DEBUG: Erreur de contrainte de clé étrangère/intégrité lors de l'enregistrement de la demande.");
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

        this.currentEditPanel.getCancelButton().addActionListener(e -> {
            System.out.println("Opération annulée. Nettoyage du formulaire.");
            currentEditPanel.clearForm();
            currentEditPanel.setEntite(new DemandeInscription());
        });

        if (currentEditPanel.getModifyButton() != null) {
            currentEditPanel.getModifyButton().addActionListener(e -> {
                System.out.println("--- DIAGNOSTIC: Bouton 'Modifier' cliqué dans DemandeInscriptionController ---");
                int selectedRow = currentEditPanel.getCustomTablePanel().getSelectedRow();
                if (selectedRow != -1) {
                    Object idObject = currentEditPanel.getCustomTablePanel().getValueAt(selectedRow, 0);
                    try {
                        int demandeId = Integer.parseInt(idObject.toString());
                        DemandeInscription demandeToModify = service.trouver(demandeId);
                        if (demandeToModify != null) {
                            currentEditPanel.setEntite(demandeToModify);
                            currentEditPanel.initForm(demandeToModify);
                            System.out.println("Demande d'inscription sélectionnée pour modification : ID " + demandeToModify.getId() + " (Client ID: " + demandeToModify.getClient().getId() + ")");
                        } else {
                            JOptionPane.showMessageDialog(currentEditPanel, "Impossible de trouver la demande d'inscription avec l'ID " + demandeId + ".", "Erreur", JOptionPane.ERROR_MESSAGE);
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
                    JOptionPane.showMessageDialog(currentEditPanel, "Veuillez sélectionner une demande d'inscription dans le tableau pour la modifier.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }

        if (currentEditPanel.getDeleteButton() != null) {
            currentEditPanel.getDeleteButton().addActionListener(e -> {
                System.out.println("--- DIAGNOSTIC: Bouton 'Supprimer' cliqué dans DemandeInscriptionController ---");
                int selectedRow = currentEditPanel.getCustomTablePanel().getSelectedRow();
                if (selectedRow != -1) {
                    Object idObject = currentEditPanel.getCustomTablePanel().getValueAt(selectedRow, 0);
                    try {
                        int demandeId = Integer.parseInt(idObject.toString());
                        int confirm = JOptionPane.showConfirmDialog(currentEditPanel,
                                "Êtes-vous sûr de vouloir supprimer la demande d'inscription avec l'ID " + demandeId + " ?",
                                "Confirmation de suppression", JOptionPane.YES_NO_OPTION);

                        if (confirm == JOptionPane.YES_OPTION) {
                            service.supprimer(demandeId);
                            System.out.println("Demande d'inscription avec ID: " + demandeId + " supprimée avec succès.");
                            currentEditPanel.clearForm();
                            currentEditPanel.getCustomTablePanel().updateTableData(convertDemandeInscriptionsToTableData(service.listerTous()));
                        }
                    } catch (SQLIntegrityConstraintViolationException ex) {
                        JOptionPane.showMessageDialog(currentEditPanel,
                                "Impossible de supprimer cette demande car elle est liée à d'autres entités.",
                                "Erreur de suppression (Contrainte de Clé Étrangère)",
                                JOptionPane.ERROR_MESSAGE);
                        System.err.println("DEBUG: Erreur de contrainte de clé étrangère lors de la suppression de la demande.");
                        ex.printStackTrace();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(currentEditPanel, "Erreur de base de données lors de la suppression : " + ex.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(currentEditPanel, "Erreur inattendue lors de la suppression : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(currentEditPanel, "Veuillez sélectionner une demande d'inscription dans le tableau pour la supprimer.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
    }

    // NOUVEAU: Méthode pour créer et configurer le panneau DemandeInscriptionEdit pour un ajout
    // Elle récupère la liste des clients pour le JComboBox
    public DemandeInscriptionEdit createAndConfigureEditPanelForAdd() throws SQLException {
        List<DemandeInscription> existingDemandes = service.listerTous();
        List<Client> allClients = clientService.listerTous(); // RÉCUPÉRATION DE TOUS LES CLIENTS

        List<String> columnNames = Arrays.asList("ID", "Date Demande", "Date Traitement", "ID Client");
        List<List<Object>> tableData = convertDemandeInscriptionsToTableData(existingDemandes);

        DemandeInscriptionEdit edit = new DemandeInscriptionEdit(tableData, columnNames, allClients); // PASSE LA LISTE DES CLIENTS
        edit.setEntite(new DemandeInscription());
        return edit;
    }

    // NOUVEAU: Méthode pour créer et configurer le panneau DemandeInscriptionEdit pour une modification
    // Elle récupère la liste des clients pour le JComboBox
    public DemandeInscriptionEdit createAndConfigureEditPanelForModify(DemandeInscription entiteToModify) throws SQLException {
        List<DemandeInscription> existingDemandes = service.listerTous();
        List<Client> allClients = clientService.listerTous(); // RÉCUPÉRATION DE TOUS LES CLIENTS

        List<String> columnNames = Arrays.asList("ID", "Date Demande", "Date Traitement", "ID Client");
        List<List<Object>> tableData = convertDemandeInscriptionsToTableData(existingDemandes);

        DemandeInscriptionEdit edit = new DemandeInscriptionEdit(tableData, columnNames, allClients); // PASSE LA LISTE DES CLIENTS
        edit.setEntite(entiteToModify);
        edit.initForm(entiteToModify);
        return edit;
    }

    private List<List<Object>> convertDemandeInscriptionsToTableData(List<DemandeInscription> demandes) {
        List<List<Object>> data = new ArrayList<>();
        for (DemandeInscription di : demandes) {
            data.add(Arrays.asList(
                    di.getId(),
                    di.getDateDeDemande() != null ? di.getDateDeDemande().format(DATE_TIME_FORMATTER) : "N/A",
                    di.getDateDeTraitement() != null ? di.getDateDeTraitement().format(DATE_TIME_FORMATTER) : "N/A",
                    di.getClient() != null ? di.getClient().getId() : "N/A"
            ));
        }
        return data;
    }
}
