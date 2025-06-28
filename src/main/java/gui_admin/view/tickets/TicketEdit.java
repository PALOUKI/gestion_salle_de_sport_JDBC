package gui_admin.view.tickets; // Assurez-vous que ce package correspond à votre structure de dossiers

import entite.Client; // Importez la classe Client
import entite.Ticket;
import gui_admin.gui_util.ActionButton;
import gui_admin.gui_util.GenericEdit;

import javax.swing.*;
import javax.swing.border.EmptyBorder; // Importation pour le padding
import javax.swing.border.TitledBorder; // Importation pour le titre du cadre
import java.awt.*;
import java.time.LocalDateTime; // Importez LocalDateTime (si votre entité Ticket ou sa logique l'utilise)
import java.time.format.DateTimeParseException; // Importez DateTimeParseException (si votre entité Ticket ou sa logique l'utilise)
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Assurez-vous d'importer le ClientController si vous utilisez son DATE_FORMATTER ou d'autres constantes
import gui_admin.controller.ClientController;

public class TicketEdit extends GenericEdit {

    private JTextField nombreDeSeanceField = new JTextField();
    private JTextField montantField = new JTextField();
    private JComboBox<Client> clientComboBox; // JComboBox pour la sélection du client
    private Ticket entite = new Ticket(); // L'entité Ticket manipulée par le formulaire

    // Déclaration des boutons Modifier et Supprimer
    private JButton modifyButton = new ActionButton("Modifier", ActionButton.ButtonType.MODIFY);
    private JButton deleteButton = new ActionButton("Supprimer", ActionButton.ButtonType.DELETE);

    // Constructeur principal pour l'initialisation avec les données du tableau et la liste des clients
    public TicketEdit(List<List<Object>> tableData, List<String> columnNames, List<Client> allClients) {
        super(tableData, columnNames);

        // Configuration du panneau de formulaire avec GridBagLayout pour un meilleur contrôle
        this.form.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Padding autour de chaque composant
        gbc.fill = GridBagConstraints.HORIZONTAL; // Remplir l'espace horizontalement

        // Titre principal du panneau d'édition des Tickets
        JLabel mainTitleLabel = new JLabel("Gestion des Tickets");
        mainTitleLabel.setFont(new Font("Goldman", Font.BOLD, 22));
        mainTitleLabel.setForeground(new Color(32, 64, 128));
        mainTitleLabel.setBorder(new EmptyBorder(10, 0, 20, 0)); // Padding sous le titre

        gbc.gridx = 0; // Colonne 0
        gbc.gridy = 0; // Ligne 0
        gbc.gridwidth = 2; // S'étend sur 2 colonnes
        gbc.anchor = GridBagConstraints.CENTER; // Centre le label
        this.form.add(mainTitleLabel, gbc);

        // Création d'un sous-panneau pour les champs de formulaire qui aura le cadre
        JPanel formFieldsPanel = new JPanel(new GridBagLayout());
        // Ajout du cadre (TitledBorder) au sous-panneau des champs
        formFieldsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(32, 64, 128), 2), // Couleur et épaisseur du cadre
                "Détails du Ticket", // Titre du cadre
                TitledBorder.LEFT, TitledBorder.TOP, // Position du titre
                new Font("Goldman", Font.BOLD, 16), // Police du titre
                new Color(32, 64, 128) // Couleur du titre
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

        int fieldRow = 0; // Ligne pour les champs à l'intérieur de formFieldsPanel

        // Nombre de Séances
        gbcFields.gridx = 0;
        gbcFields.gridy = fieldRow;
        gbcFields.gridwidth = 1;
        gbcFields.anchor = GridBagConstraints.EAST; // Aligne le label à droite
        formFieldsPanel.add(new JLabel("Nombre de Séances :"), gbcFields);
        gbcFields.gridx = 1;
        gbcFields.anchor = GridBagConstraints.WEST; // Aligne le champ à gauche
        nombreDeSeanceField.setPreferredSize(new Dimension(250, 35));
        formFieldsPanel.add(nombreDeSeanceField, gbcFields);
        fieldRow++;

        // Montant
        gbcFields.gridx = 0;
        gbcFields.gridy = fieldRow;
        gbcFields.anchor = GridBagConstraints.EAST;
        formFieldsPanel.add(new JLabel("Montant :"), gbcFields);
        gbcFields.gridx = 1;
        gbcFields.anchor = GridBagConstraints.WEST;
        montantField.setPreferredSize(new Dimension(250, 35));
        formFieldsPanel.add(montantField, gbcFields);
        fieldRow++;

        // Client
        gbcFields.gridx = 0;
        gbcFields.gridy = fieldRow;
        gbcFields.anchor = GridBagConstraints.EAST;
        formFieldsPanel.add(new JLabel("Client :"), gbcFields);
        gbcFields.gridx = 1;
        gbcFields.anchor = GridBagConstraints.WEST;
        clientComboBox.setPreferredSize(new Dimension(250, 35));
        formFieldsPanel.add(clientComboBox, gbcFields);
        fieldRow++;

        // Ajouter le formFieldsPanel (avec son cadre) au panneau principal du formulaire
        gbc.gridx = 0;
        gbc.gridy = 1; // Sous le titre principal
        gbc.gridwidth = 2; // S'étend sur les deux colonnes
        gbc.weightx = 1.0; // Prend toute la largeur disponible
        gbc.fill = GridBagConstraints.HORIZONTAL; // Remplir l'espace horizontalement
        this.form.add(formFieldsPanel, gbc);

        // --- NOUVEAU : Cadre pour le tableau avec style "carte" ---
        JPanel tablePanelWrapper = new JPanel(new BorderLayout());
        tablePanelWrapper.setBackground(Color.WHITE); // Fond blanc pour la "carte"
        tablePanelWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15), // Marge intérieure
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220), 1), // Bordure légère
                        BorderFactory.createEmptyBorder(10, 10, 10, 10) // Padding à l'intérieur du cadre
                )
        ));
        // Effet d'ombre (nécessite une classe personnalisée ou une librairie)
        // Pour un effet simple, on peut tricher avec une bordure épaisse de couleur légèrement plus foncée
        // ou des JLayeredPane, mais c'est complexe pour Swing sans lib externe.
        // Je vais simuler une légère ombre avec un EmptyBorder en bas/droite de la couleur du fond du panel parent si disponible.
        // Ou, plus simplement, utiliser un BorderFactory.createEtchedBorder ou BevelBorder pour un effet 3D.
        // Pour rester simple et se rapprocher de l'image, je vais utiliser un simple EmptyBorder pour le padding
        // et laisser le LineBorder pour le visuel du cadre.

        // Création du titre pour le tableau, à l'intérieur du wrapper
        JLabel tableTitleLabel = new JLabel("Liste des Tickets");
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
        // --- FIN NOUVEAU : Cadre pour le tableau ---

        // Ajout des boutons "Modifier" et "Supprimer" au ButtonPanel (hérité de GenericEdit)
        // Note: GenericEdit ajoute déjà saveButton et cancelButton. Ces boutons seront ajoutés après.
        this.buttonPanel.add(modifyButton);
        this.buttonPanel.add(deleteButton);
    }

    // Second constructeur, utile si vous voulez créer un TicketEdit directement avec une entité existante
    public TicketEdit(Ticket entite, List<Client> allClients) {
        this(new ArrayList<>(), Arrays.asList("ID", "Nombre de Séances", "Montant", "ID Client"), allClients);
        this.entite = entite;
        this.initForm(entite);
    }

    /**
     * @Override
     * Met à jour l'objet 'entite' avec les valeurs actuelles des champs du formulaire.
     */
    @Override
    public void init() {
        // Nombre de Séances
        String nbSeanceText = nombreDeSeanceField.getText();
        if (nbSeanceText != null && !nbSeanceText.trim().isEmpty()) {
            try {
                this.entite.setNombreDeSeance(Integer.parseInt(nbSeanceText.trim()));
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Le nombre de séances doit être un nombre entier valide.");
            }
        } else {
            this.entite.setNombreDeSeance(0); // Valeur par défaut si vide, ou lancer une erreur si obligatoire
        }

        // Montant
        String montantText = montantField.getText();
        if (montantText != null && !montantText.trim().isEmpty()) {
            try {
                this.entite.setMontant(Integer.parseInt(montantText.trim()));
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Le montant doit être un nombre entier valide.");
            }
        } else {
            this.entite.setMontant(0); // Valeur par défaut si vide, ou lancer une erreur si obligatoire
        }

        // Client sélectionné dans le JComboBox
        Client selectedClient = (Client) clientComboBox.getSelectedItem();

        // Vérifier si un client a été réellement sélectionné (non null et avec un ID valide)
        if (selectedClient != null && selectedClient.getId() != 0) {
            this.entite.setClient(selectedClient);
        } else {
            this.entite.setClient(null); // Si "Sélectionner un client" ou rien n'est choisi.
            // Optionnel: lancer une exception si le client est obligatoire
            // throw new IllegalArgumentException("Veuillez sélectionner un client pour ce ticket.");
        }
    }

    /**
     * @Override
     * Retourne l'entité Ticket actuellement manipulée par ce formulaire.
     */
    @Override
    public Object getEntite() {
        return entite;
    }

    /**
     * @Override
     * Permet de définir l'entité que le formulaire manipulera.
     */
    @Override
    public void setEntite(Object obj) {
        if (obj instanceof Ticket) {
            this.entite = (Ticket) obj;
        } else {
            throw new IllegalArgumentException("L'objet passé à setEntite doit être de type Ticket.");
        }
    }

    /**
     * @Override
     * Initialise les champs du formulaire avec les valeurs de l'entité Ticket donnée.
     */
    @Override
    public void initForm(Object obj) {
        if (obj instanceof Ticket) {
            Ticket ticket = (Ticket) obj;

            nombreDeSeanceField.setText(String.valueOf(ticket.getNombreDeSeance()));
            montantField.setText(String.valueOf(ticket.getMontant()));

            // Sélectionner le client correct dans le JComboBox
            if (ticket.getClient() != null && ticket.getClient().getId() != 0) {
                boolean found = false;
                for (int i = 0; i < clientComboBox.getItemCount(); i++) {
                    Client item = clientComboBox.getItemAt(i);
                    // Utiliser .equals() si implémenté dans Client, sinon comparer l'ID
                    if (item != null && item.getId() == ticket.getClient().getId()) {
                        clientComboBox.setSelectedItem(item);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    System.err.println("TicketEdit.initForm: Client avec ID " + ticket.getClient().getId() + " non trouvé dans le JComboBox.");
                    clientComboBox.setSelectedItem(null);
                }
            } else {
                clientComboBox.setSelectedItem(null);
            }

        } else {
            System.err.println("initForm: L'objet passé n'est pas un Ticket.");
            clearForm();
        }
    }

    /**
     * @Override
     * Efface le contenu de tous les champs du formulaire.
     */
    @Override
    public void clearForm() {
        nombreDeSeanceField.setText("");
        montantField.setText("");
        clientComboBox.setSelectedIndex(0); // Sélectionne le premier élément (souvent "Sélectionner un client" ou null)
        this.entite = new Ticket(); // Réinitialise l'entité interne pour les prochains ajouts
    }

    // Getters pour les boutons "Modifier" et "Supprimer"
    public JButton getModifyButton() {
        return modifyButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }
}
