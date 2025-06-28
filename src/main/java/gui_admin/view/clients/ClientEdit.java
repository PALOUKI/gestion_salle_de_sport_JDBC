package gui_admin.view.clients; // Assurez-vous que le package est correct

import entite.Client;
import gui_admin.gui_util.ActionButton;
import gui_admin.gui_util.CustomTablePanel;
import gui_admin.gui_util.GenericEdit;
import gui_admin.controller.ClientController; // Importez ClientController pour le DATE_FORMATTER

import javax.swing.*;
import javax.swing.border.EmptyBorder; // Importation pour le padding
import javax.swing.border.TitledBorder; // Importation pour le titre du cadre
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientEdit extends GenericEdit {

    private JTextField nomField = new JTextField();
    private JTextField prenomField = new JTextField();
    private JTextField dateNaissanceField = new JTextField();
    private JTextField emailField = new JTextField();
    private Client entite = new Client(); // L'entité Client manipulée par le formulaire

    // Déclaration des nouveaux boutons pour Modifier et Supprimer
    private JButton modifyButton = new ActionButton("Modifier", ActionButton.ButtonType.MODIFY);
    private JButton deleteButton = new ActionButton("Supprimer", ActionButton.ButtonType.DELETE);

    // Constructeur principal pour l'initialisation avec les données du tableau
    public ClientEdit(List<List<Object>> tableData, List<String> columnNames) {
        // Appelle le constructeur de GenericEdit, qui configure la structure de base du panneau
        super(tableData, columnNames);

        // Configuration du panneau de formulaire (this.form) avec GridBagLayout pour un meilleur contrôle
        this.form.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Padding autour de chaque composant
        gbc.fill = GridBagConstraints.HORIZONTAL; // Remplir l'espace horizontalement

        // Titre principal du panneau d'édition des Clients
        JLabel mainTitleLabel = new JLabel("Gestion des Clients");
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
                                        "Détails du Client", // Titre du cadre
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

        // Ajout des labels et champs au formFieldsPanel avec GridBagLayout
        int fieldRow = 0;

        // Nom
        gbcFields.gridx = 0;
        gbcFields.gridy = fieldRow;
        gbcFields.gridwidth = 1;
        gbcFields.anchor = GridBagConstraints.EAST; // Aligne le label à droite
        formFieldsPanel.add(new JLabel("Nom :"), gbcFields);
        gbcFields.gridx = 1;
        gbcFields.anchor = GridBagConstraints.WEST; // Aligne le champ à gauche
        nomField.setPreferredSize(new Dimension(250, 35)); // Taille préférée
        formFieldsPanel.add(nomField, gbcFields);
        fieldRow++;

        // Prénom
        gbcFields.gridx = 0;
        gbcFields.gridy = fieldRow;
        gbcFields.anchor = GridBagConstraints.EAST;
        formFieldsPanel.add(new JLabel("Prénom :"), gbcFields);
        gbcFields.gridx = 1;
        gbcFields.anchor = GridBagConstraints.WEST;
        prenomField.setPreferredSize(new Dimension(250, 35));
        formFieldsPanel.add(prenomField, gbcFields);
        fieldRow++;

        // Date de Naissance
        gbcFields.gridx = 0;
        gbcFields.gridy = fieldRow;
        gbcFields.anchor = GridBagConstraints.EAST;
        formFieldsPanel.add(new JLabel("Date de Naissance (AAAA-MM-JJ) :"), gbcFields);
        gbcFields.gridx = 1;
        gbcFields.anchor = GridBagConstraints.WEST;
        dateNaissanceField.setPreferredSize(new Dimension(250, 35));
        formFieldsPanel.add(dateNaissanceField, gbcFields);
        fieldRow++;

        // Email
        gbcFields.gridx = 0;
        gbcFields.gridy = fieldRow;
        gbcFields.anchor = GridBagConstraints.EAST;
        formFieldsPanel.add(new JLabel("Email :"), gbcFields);
        gbcFields.gridx = 1;
        gbcFields.anchor = GridBagConstraints.WEST;
        emailField.setPreferredSize(new Dimension(250, 35));
        formFieldsPanel.add(emailField, gbcFields);
        fieldRow++;

        // Ajout du formFieldsPanel (avec son cadre) au panneau principal du formulaire (this.form)
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
        JLabel tableTitleLabel = new JLabel("Liste des Clients");
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

        // Ajout des nouveaux boutons au ButtonPanel (hérité de GenericEdit)
        this.buttonPanel.add(modifyButton);
        this.buttonPanel.add(deleteButton);
    }

    // Second constructeur, utile si vous voulez créer un ClientEdit directement avec une entité Client
    public ClientEdit(Client entite) {
        this(new ArrayList<>(), Arrays.asList("ID", "Nom", "Prénom", "Date de Naissance", "Email"));
        this.entite = entite;
        this.initForm(entite);
    }

    /**
     * @Override
     * Met à jour l'objet 'entite' avec les valeurs actuelles des champs du formulaire.
     * Cette méthode est appelée par le contrôleur avant d'envoyer l'entité au service (ajouter/modifier).
     */
    @Override
    public void init() {
        this.entite.setNom(nomField.getText());
        this.entite.setPrenom(prenomField.getText());
        this.entite.setEmail(emailField.getText());

        // Gérer la conversion du texte de la date de naissance en LocalDateTime
        String dateText = dateNaissanceField.getText();
        if (dateText != null && !dateText.trim().isEmpty()) {
            try {
                // Parse la chaîne de caractères en un objet LocalDate en utilisant le formateur défini dans ClientController
                LocalDate parsedDate = LocalDate.parse(dateText, ClientController.DATE_FORMATTER);
                // Convertit le LocalDate parsé en LocalDateTime en lui ajoutant l'heure de début de journée (minuit)
                this.entite.setDateNaissance(parsedDate.atStartOfDay());
            } catch (DateTimeParseException e) {
                // Relance l'exception pour que le ClientController puisse l'intercepter et afficher un message d'erreur à l'utilisateur
                throw new DateTimeParseException("Format de date de naissance invalide. Veuillez utiliser le format AAAA-MM-JJ (ex: 2000-01-15).", dateText, e.getErrorIndex(), e);
            }
        } else {
            this.entite.setDateNaissance(null);
        }
    }

    //Retourne l'entité Client actuellement manipulée par ce formulaire.
    @Override
    public Client getEntite() {
        return entite;
    }


    //Permet de définir l'entité Client que le formulaire manipulera: obj L'objet entité (doit être un Client)
    @Override
    public void setEntite(Object obj) {
        if (obj instanceof Client) {
            this.entite = (Client) obj;
        } else {
            throw new IllegalArgumentException("L'objet passé à setEntite doit être de type Client.");
        }
    }

    //Initialise les champs du formulaire avec les valeurs de l'entité Client donnée.
    @Override
    public void initForm(Object obj) {
        if (obj instanceof Client) {
            Client client = (Client) obj;
            nomField.setText(client.getNom());
            prenomField.setText(client.getPrenom());
            emailField.setText(client.getEmail());
            // Formate le LocalDateTime de l'entité en String pour l'affichage dans le champ
            dateNaissanceField.setText(client.getDateNaissance() != null ? client.getDateNaissance().format(ClientController.DATE_FORMATTER) : "");
        } else {
            System.err.println("initForm: L'objet passé n'est pas un Client.");
            clearForm(); // Ou lancer une exception
        }
    }

    // Efface le contenu de tous les champs du formulaire.
    @Override
    public void clearForm() {
        nomField.setText("");
        prenomField.setText("");
        dateNaissanceField.setText("");
        emailField.setText("");
        this.entite = new Client();
    }

    // Getters pour les boutons Modifier et Supprimer
    public JButton getModifyButton() {
        return modifyButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }
}
