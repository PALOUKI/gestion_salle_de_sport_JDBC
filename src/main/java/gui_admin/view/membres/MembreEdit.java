package gui_admin.view.membres; // Assurez-vous que ce package correspond à votre structure de dossiers

import entite.Client; // Importez la classe Client
import entite.Membre;
import gui_admin.gui_util.ActionButton;
import gui_admin.gui_util.GenericEdit;
import gui_admin.controller.DemandeInscriptionController; // Utilisation du même formateur de date/heure que DemandeInscription

import javax.swing.*;
import javax.swing.border.EmptyBorder; // Importation pour le padding
import javax.swing.border.TitledBorder; // Importation pour le titre du cadre
import java.awt.*;
import java.time.LocalDateTime; // Importez LocalDateTime
import java.time.format.DateTimeParseException; // Importez DateTimeParseException
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MembreEdit extends GenericEdit {

    private JTextField dateInscriptionField = new JTextField();
    private JComboBox<Client> clientComboBox; // JComboBox pour la sélection du client
    private Membre entite = new Membre(); // L'entité Membre manipulée par le formulaire

    // Déclaration des boutons Modifier et Supprimer
    private JButton modifyButton = new ActionButton("Modifier", ActionButton.ButtonType.MODIFY);
    private JButton deleteButton = new ActionButton("Supprimer", ActionButton.ButtonType.DELETE);

    // Constructeur principal pour l'initialisation avec les données du tableau et la liste des clients
    public MembreEdit(List<List<Object>> tableData, List<String> columnNames, List<Client> allClients) {
        super(tableData, columnNames);

        // Configuration du panneau de formulaire (this.form) avec GridBagLayout pour un meilleur contrôle
        this.form.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Padding autour de chaque composant
        gbc.fill = GridBagConstraints.HORIZONTAL; // Remplir l'espace horizontalement

        // Titre principal du panneau d'édition des Membres
        JLabel mainTitleLabel = new JLabel("Gestion des Membres");
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
                                        "Détails du Membre", // Titre du cadre
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
        model.insertElementAt(null, 0); // Ajoute un élément null au début pour une sélection optionnelle
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

        // Date d'Inscription
        gbcFields.gridx = 0;
        gbcFields.gridy = fieldRow;
        gbcFields.gridwidth = 1;
        gbcFields.anchor = GridBagConstraints.EAST; // Aligne le label à droite
        formFieldsPanel.add(new JLabel("Date d'Inscription (AAAA-MM-JJ HH:MM:SS) :"), gbcFields);
        gbcFields.gridx = 1;
        gbcFields.anchor = GridBagConstraints.WEST; // Aligne le champ à gauche
        dateInscriptionField.setPreferredSize(new Dimension(250, 35)); // Taille préférée
        formFieldsPanel.add(dateInscriptionField, gbcFields);
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
        JLabel tableTitleLabel = new JLabel("Liste des Membres");
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

    // Second constructeur, utile si vous voulez créer un MembreEdit directement avec une entité existante
    public MembreEdit(Membre entite, List<Client> allClients) {
        this(new ArrayList<>(), Arrays.asList("ID", "Date Inscription", "ID Client"), allClients);
        this.entite = entite;
        this.initForm(entite);
    }

    /**
     * Met à jour l'objet 'entite' avec les valeurs actuelles des champs du formulaire.
     */
    @Override
    public void init() {
        // Date d'Inscription
        String dateInscriptionText = dateInscriptionField.getText();
        String trimmedDateInscriptionText = dateInscriptionText != null ? dateInscriptionText.trim() : null;

        if (trimmedDateInscriptionText != null && !trimmedDateInscriptionText.isEmpty()) {
            try {
                this.entite.setDateInscription(LocalDateTime.parse(trimmedDateInscriptionText, DemandeInscriptionController.DATE_TIME_FORMATTER));
            } catch (DateTimeParseException e) {
                // Relance l'exception pour que le contrôleur la gère
                throw new DateTimeParseException("Format de Date d'Inscription invalide. Utilisez AAAA-MM-JJ HH:MM:SS.", trimmedDateInscriptionText, e.getErrorIndex(), e);
            }
        } else {
            this.entite.setDateInscription(null); // Gérer si la date est optionnelle
        }

        // Client sélectionné dans le JComboBox
        Client selectedClient = (Client) clientComboBox.getSelectedItem();

        // Vérifier si un client a été réellement sélectionné (non null et avec un ID valide)
        if (selectedClient != null && selectedClient.getId() != 0) {
            this.entite.setClient(selectedClient);
        } else {
            this.entite.setClient(null); // Si "Sélectionner un client" ou rien n'est choisi.
            // Optionnel: lancer une exception si le client est obligatoire
            // throw new IllegalArgumentException("Veuillez sélectionner un client.");
        }
    }

    /**
     * Retourne l'entité Membre actuellement manipulée par ce formulaire.
     */
    @Override
    public Object getEntite() {
        return entite;
    }

    /**
     * Permet de définir l'entité que le formulaire manipulera.
     */
    @Override
    public void setEntite(Object obj) {
        if (obj instanceof Membre) {
            this.entite = (Membre) obj;
        } else {
            throw new IllegalArgumentException("L'objet passé à setEntite doit être de type Membre.");
        }
    }

    /**
     * Initialise les champs du formulaire avec les valeurs de l'entité Membre donnée.
     */
    @Override
    public void initForm(Object obj) {
        if (obj instanceof Membre) {
            Membre membre = (Membre) obj;

            dateInscriptionField.setText(membre.getDateInscription() != null ? membre.getDateInscription().format(DemandeInscriptionController.DATE_TIME_FORMATTER) : "");

            // Sélectionner le client correct dans le JComboBox
            if (membre.getClient() != null && membre.getClient().getId() != 0) {
                boolean found = false;
                for (int i = 0; i < clientComboBox.getItemCount(); i++) {
                    Client item = clientComboBox.getItemAt(i);
                    // Utiliser .equals() si implémenté dans Client, sinon comparer l'ID
                    if (item != null && item.getId() == membre.getClient().getId()) {
                        clientComboBox.setSelectedItem(item);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    System.err.println("MembreEdit.initForm: Client avec ID " + membre.getClient().getId() + " non trouvé dans le JComboBox.");
                    clientComboBox.setSelectedItem(null);
                }
            } else {
                clientComboBox.setSelectedItem(null);
            }

        } else {
            System.err.println("initForm: L'objet passé n'est pas un Membre.");
            clearForm();
        }
    }

    /**
     * Efface le contenu de tous les champs du formulaire.
     */
    @Override
    public void clearForm() {
        dateInscriptionField.setText("");
        clientComboBox.setSelectedIndex(0); // Sélectionne le premier élément (souvent "Sélectionner un client" ou null)
        this.entite = new Membre(); // Réinitialise l'entité interne pour les prochains ajouts
    }

    // Getters pour les boutons "Modifier" et "Supprimer"
    public JButton getModifyButton() {
        return modifyButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }
}