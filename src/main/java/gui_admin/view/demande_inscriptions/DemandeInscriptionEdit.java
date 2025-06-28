package gui_admin.view.demande_inscriptions;

import entite.Client;
import entite.DemandeInscription;
import gui_admin.gui_util.ActionButton;
import gui_admin.gui_util.GenericEdit;
import gui_admin.controller.DemandeInscriptionController;

import javax.swing.*;
import javax.swing.border.EmptyBorder; // Importation pour le padding
import javax.swing.border.TitledBorder; // Importation pour le titre du cadre
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DemandeInscriptionEdit extends GenericEdit {

    private JTextField dateDeDemandeField = new JTextField();
    private JTextField dateDeTraitementField = new JTextField();
    private JComboBox<Client> clientComboBox; // JComboBox pour la sélection du client
    private DemandeInscription entite = new DemandeInscription();

    private JButton modifyButton = new ActionButton("Modifier", ActionButton.ButtonType.MODIFY);
    private JButton deleteButton = new ActionButton("Supprimer", ActionButton.ButtonType.DELETE);

    // Constructeur principal, maintenant avec une liste de tous les clients
    public DemandeInscriptionEdit(List<List<Object>> tableData, List<String> columnNames, List<Client> allClients) {
        super(tableData, columnNames);

        // Configuration du panneau de formulaire (this.form) avec GridBagLayout pour un meilleur contrôle
        this.form.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Padding autour de chaque composant
        gbc.fill = GridBagConstraints.HORIZONTAL; // Remplir l'espace horizontalement

        // Titre principal du panneau d'édition des Demandes d'Inscription
        JLabel mainTitleLabel = new JLabel("Gestion des Demandes d'Inscription");
        mainTitleLabel.setFont(new Font("Goldman", Font.BOLD, 22)); // Titre plus grand et audacieux
        mainTitleLabel.setForeground(new Color(32, 64, 128)); // Couleur du titre
        mainTitleLabel.setBorder(new EmptyBorder(10, 0, 20, 0)); // Padding sous le titre

        gbc.gridx = 0; // Colonne 0
        gbc.gridy = 0; // Ligne 0
        gbc.gridwidth = 2; // S'étend sur 2 colonnes
        gbc.anchor = GridBagConstraints.CENTER; // Centre le label
        this.form.add(mainTitleLabel, gbc);

        // --- Début du panneau pour les champs du formulaire (style "carte") ---
        JPanel formFieldsPanel = new JPanel(new GridBagLayout());
        formFieldsPanel.setBackground(Color.WHITE); // Fond blanc pour la "carte"
        formFieldsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10), // Marge extérieure pour "ombre" ou espacement
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220), 1), // Bordure très fine et claire (effet de "lift")
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createEmptyBorder(15, 15, 15, 15), // Padding interne pour le contenu
                                BorderFactory.createTitledBorder(
                                        BorderFactory.createLineBorder(new Color(32, 64, 128), 2), // Bordure bleue, épaisseur 2
                                        "Détails de la Demande", // Titre du cadre
                                        TitledBorder.LEFT, TitledBorder.TOP, // Position du titre
                                        new Font("Goldman", Font.BOLD, 16), // Police du titre
                                        new Color(32, 64, 128) // Couleur du titre
                                )
                        )
                )
        ));
        GridBagConstraints gbcFields = new GridBagConstraints();
        gbcFields.insets = new Insets(5, 5, 5, 5); // Padding interne au cadre
        gbcFields.fill = GridBagConstraints.HORIZONTAL;

        // Initialisation du JComboBox avec les clients
        if (allClients == null) {
            allClients = new ArrayList<>(); // Évite un NullPointerException si la liste est null
        }
        DefaultComboBoxModel<Client> model = new DefaultComboBoxModel<>(allClients.toArray(new Client[0]));
        model.insertElementAt(null, 0); // Ajoute un élément null au début pour "Sélectionner un client"
        clientComboBox = new JComboBox<>(model);
        clientComboBox.setSelectedIndex(0); // Sélectionne l'élément null par défaut

        // Rendu personnalisable pour afficher le nom et prénom du client
        clientComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Client) {
                    Client client = (Client) value;
                    setText("ID: " + client.getId() + " - " + client.getNom() + " " + client.getPrenom());
                } else if (value == null) {
                    setText("-- Sélectionner un client --");
                }
                return this;
            }
        });

        // Ajout des labels et champs au formFieldsPanel avec GridBagLayout
        int fieldRow = 0;

        // Date de Demande
        gbcFields.gridx = 0;
        gbcFields.gridy = fieldRow;
        gbcFields.gridwidth = 1;
        gbcFields.anchor = GridBagConstraints.EAST; // Aligne le label à droite
        formFieldsPanel.add(new JLabel("Date de Demande (AAAA-MM-JJ HH:MM:SS) :"), gbcFields);
        gbcFields.gridx = 1;
        gbcFields.anchor = GridBagConstraints.WEST; // Aligne le champ à gauche
        dateDeDemandeField.setPreferredSize(new Dimension(250, 35)); // Taille préférée
        formFieldsPanel.add(dateDeDemandeField, gbcFields);
        fieldRow++;

        // Date de Traitement
        gbcFields.gridx = 0;
        gbcFields.gridy = fieldRow;
        gbcFields.anchor = GridBagConstraints.EAST;
        formFieldsPanel.add(new JLabel("Date de Traitement (AAAA-MM-JJ HH:MM:SS) (optionnel) :"), gbcFields);
        gbcFields.gridx = 1;
        gbcFields.anchor = GridBagConstraints.WEST;
        dateDeTraitementField.setPreferredSize(new Dimension(250, 35));
        formFieldsPanel.add(dateDeTraitementField, gbcFields);
        fieldRow++;

        // Client
        gbcFields.gridx = 0;
        gbcFields.gridy = fieldRow;
        gbcFields.anchor = GridBagConstraints.EAST;
        formFieldsPanel.add(new JLabel("Client :"), gbcFields);
        gbcFields.gridx = 1;
        gbcFields.anchor = GridBagConstraints.WEST;
        clientComboBox.setPreferredSize(new Dimension(250, 35)); // Taille préférée
        formFieldsPanel.add(clientComboBox, gbcFields); // AJOUT DU JComboBox
        fieldRow++;

        // Ajouter le formFieldsPanel (avec son cadre) au panneau principal du formulaire (this.form)
        gbc.gridx = 0;
        gbc.gridy = 1; // Sous le titre principal
        gbc.gridwidth = 2; // S'étend sur les deux colonnes
        gbc.weightx = 1.0; // Prend toute la largeur disponible
        gbc.fill = GridBagConstraints.HORIZONTAL; // Remplir l'espace horizontalement
        this.form.add(formFieldsPanel, gbc);
        // --- Fin du panneau pour les champs du formulaire ---

        // --- Début du cadre pour le tableau avec style "carte" (similaire à TicketEdit) ---
        JPanel tablePanelWrapper = new JPanel(new BorderLayout());
        tablePanelWrapper.setBackground(Color.WHITE); // Fond blanc pour la "carte"
        tablePanelWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15), // Marge intérieure
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220), 1), // Bordure légère
                        BorderFactory.createEmptyBorder(10, 10, 10, 10) // Padding à l'intérieur du cadre
                )
        ));

        // Création du titre pour le tableau, à l'intérieur du wrapper
        JLabel tableTitleLabel = new JLabel("Liste des Demandes d'Inscription");
        tableTitleLabel.setFont(new Font("Goldman", Font.BOLD, 18));
        tableTitleLabel.setForeground(new Color(32, 64, 128));
        tableTitleLabel.setBorder(new EmptyBorder(0, 0, 10, 0)); // Padding sous le titre du tableau

        tablePanelWrapper.add(tableTitleLabel, BorderLayout.NORTH); // Titre en haut du wrapper du tableau
        tablePanelWrapper.add(this.customTablePanel, BorderLayout.CENTER); // Ajouter le customTablePanel existant

        // Ajouter le wrapper du tableau au panneau principal du formulaire
        gbc.gridx = 0;
        gbc.gridy = 2; // Sous le panneau de champs (formFieldsPanel)
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0; // Permet au tableau de prendre l'espace restant verticalement
        gbc.fill = GridBagConstraints.BOTH; // Remplir l'espace horizontalement et verticalement
        this.form.add(tablePanelWrapper, gbc);
        // --- Fin du cadre pour le tableau ---

        // Ajout des boutons "Modifier" et "Supprimer" au ButtonPanel (hérité de GenericEdit)
        this.buttonPanel.add(modifyButton);
        this.buttonPanel.add(deleteButton);
    }

    // Second constructeur (pour initialisation avec entité), doit appeler le nouveau constructeur principal
    public DemandeInscriptionEdit(DemandeInscription entite, List<Client> allClients) {
        this(new ArrayList<>(), Arrays.asList("ID", "Date Demande", "Date Traitement", "ID Client"), allClients);
        this.entite = entite;
        this.initForm(entite);
    }

    @Override
    public void init() {
        // Date de Demande
        String dateDemandeText = dateDeDemandeField.getText();
        String trimmedDateDemandeText = dateDemandeText != null ? dateDemandeText.trim() : null;

        if (trimmedDateDemandeText != null && !trimmedDateDemandeText.isEmpty()) {
            try {
                this.entite.setDateDeDemande(LocalDateTime.parse(trimmedDateDemandeText, DemandeInscriptionController.DATE_TIME_FORMATTER));
            } catch (DateTimeParseException e) {
                throw new DateTimeParseException("Format de Date de Demande invalide. Utilisez AAAA-MM-JJ HH:MM:SS.", trimmedDateDemandeText, e.getErrorIndex(), e);
            }
        } else {
            this.entite.setDateDeDemande(null);
        }

        // Date de Traitement (peut être null)
        String dateTraitementText = dateDeTraitementField.getText();
        String trimmedDateTraitementText = dateTraitementText != null ? dateTraitementText.trim() : null;

        if (trimmedDateTraitementText != null && !trimmedDateTraitementText.isEmpty()) {
            try {
                this.entite.setDateDeTraitement(LocalDateTime.parse(trimmedDateTraitementText, DemandeInscriptionController.DATE_TIME_FORMATTER));
            } catch (DateTimeParseException e) {
                throw new DateTimeParseException("Format de Date de Traitement invalide. Utilisez AAAA-MM-JJ HH:MM:SS.", trimmedDateTraitementText, e.getErrorIndex(), e);
            }
        } else {
            this.entite.setDateDeTraitement(null);
        }

        // Client sélectionné dans le JComboBox
        Client selectedClient = (Client) clientComboBox.getSelectedItem();

        // Vérifier si un client a été réellement sélectionné (non null et avec un ID valide)
        if (selectedClient != null && selectedClient.getId() != 0) { // S'assurer que l'ID n'est pas le 0 par défaut d'un Client vide
            this.entite.setClient(selectedClient);
        } else {
            this.entite.setClient(null); // Si "Sélectionner un client" ou rien n'est choisi.
            // Optionnel: throw new IllegalArgumentException("Veuillez sélectionner un client pour la demande d'inscription.");
        }
    }

    @Override
    public Object getEntite() {
        return entite;
    }

    @Override
    public void setEntite(Object obj) {
        if (obj instanceof DemandeInscription) {
            this.entite = (DemandeInscription) obj;
        } else {
            throw new IllegalArgumentException("L'objet passé à setEntite doit être de type DemandeInscription.");
        }
    }

    @Override
    public void initForm(Object obj) {
        if (obj instanceof DemandeInscription) {
            DemandeInscription demande = (DemandeInscription) obj;

            dateDeDemandeField.setText(demande.getDateDeDemande() != null ? demande.getDateDeDemande().format(DemandeInscriptionController.DATE_TIME_FORMATTER) : "");
            dateDeTraitementField.setText(demande.getDateDeTraitement() != null ? demande.getDateDeTraitement().format(DemandeInscriptionController.DATE_TIME_FORMATTER) : "");

            // Sélectionner le client correct dans le JComboBox
            if (demande.getClient() != null && demande.getClient().getId() != 0) {
                boolean found = false;
                for (int i = 0; i < clientComboBox.getItemCount(); i++) {
                    Client item = clientComboBox.getItemAt(i);
                    // Utiliser .equals() si implémenté dans Client, sinon comparer l'ID
                    if (item != null && item.getId() == demande.getClient().getId()) {
                        clientComboBox.setSelectedItem(item);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    // Si le client n'est pas trouvé dans la liste (ex: supprimé de la base ou pas chargé)
                    System.err.println("DemandeInscriptionEdit.initForm: Client avec ID " + demande.getClient().getId() + " non trouvé dans le JComboBox. Réinitialisation.");
                    clientComboBox.setSelectedItem(null); // Déselectionner
                }
            } else {
                clientComboBox.setSelectedItem(null); // Déselectionner s'il n'y a pas de client
            }

        } else {
            System.err.println("initForm: L'objet passé n'est pas une DemandeInscription.");
            clearForm();
        }
    }

    @Override
    public void clearForm() {
        dateDeDemandeField.setText("");
        dateDeTraitementField.setText("");
        clientComboBox.setSelectedIndex(0); // Sélectionne le premier élément (souvent "Sélectionner un client" ou null)
        this.entite = new DemandeInscription(); // Réinitialise l'entité interne pour les prochains ajouts
    }

    public JButton getModifyButton() {
        return modifyButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }
}