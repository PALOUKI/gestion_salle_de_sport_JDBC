package gui_admin.view.abonnements; // Assurez-vous que ce package correspond à votre structure de dossiers

import entite.Abonnement;
import entite.Membre; // Importez la classe Membre
import entite.TypeAbonnement; // Importez la classe TypeAbonnement
import gui_admin.gui_util.ActionButton;
import gui_admin.gui_util.GenericEdit;
import gui_admin.controller.DemandeInscriptionController; // Pour le DATE_TIME_FORMATTER

import javax.swing.*;
import javax.swing.border.EmptyBorder; // Importation pour le padding
import javax.swing.border.TitledBorder; // Importation pour le titre du cadre
import java.awt.*;
import java.time.LocalDateTime; // Importez LocalDateTime
import java.time.format.DateTimeParseException; // Importez DateTimeParseException
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AbonnementEdit extends GenericEdit {

    private JTextField dateDebutField = new JTextField();
    private JTextField dateFinField = new JTextField();
    private JComboBox<Membre> membreComboBox; // JComboBox pour la sélection du membre
    private JComboBox<TypeAbonnement> typeAbonnementComboBox; // JComboBox pour la sélection du type d'abonnement
    private Abonnement entite = new Abonnement(); // L'entité Abonnement manipulée par le formulaire

    // Déclaration des boutons Modifier et Supprimer
    private JButton modifyButton = new ActionButton("Modifier", ActionButton.ButtonType.MODIFY);
    private JButton deleteButton = new ActionButton("Supprimer", ActionButton.ButtonType.DELETE);

    // Constructeur principal pour l'initialisation avec les données du tableau et les listes des entités liées
    public AbonnementEdit(List<List<Object>> tableData, List<String> columnNames, List<Membre> allMembres, List<TypeAbonnement> allTypeAbonnements) {
        super(tableData, columnNames); // Appelle le constructeur de GenericEdit

        // Configuration du panneau de formulaire (this.form) avec GridBagLayout pour un meilleur contrôle
        this.form.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Padding autour de chaque composant
        gbc.fill = GridBagConstraints.HORIZONTAL; // Remplir l'espace horizontalement

        // Titre principal du panneau d'édition des Abonnements
        JLabel mainTitleLabel = new JLabel("Gestion des Abonnements");
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
                                        "Détails de l'Abonnement", // Titre du cadre
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

        // Initialisation du JComboBox pour les membres
        if (allMembres == null) {
            allMembres = new ArrayList<>(); // Évite un NullPointerException
        }
        DefaultComboBoxModel<Membre> membreModel = new DefaultComboBoxModel<>(allMembres.toArray(new Membre[0]));
        membreModel.insertElementAt(null, 0); // Ajoute un élément null au début pour "sélectionner"
        membreComboBox = new JComboBox<>(membreModel);
        membreComboBox.setSelectedIndex(0); // Sélectionne l'élément null par défaut
        // Rendu personnalisable pour afficher le nom du membre
        membreComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Membre) {
                    Membre membre = (Membre) value;
                    // Assurez-vous que l'objet Client dans Membre n'est pas null avant d'y accéder
                    String clientInfo = (membre.getClient() != null) ? membre.getClient().getNom() + " " + membre.getClient().getPrenom() : "Client Inconnu";
                    setText("ID: " + membre.getId() + " - " + clientInfo);
                } else if (value == null) {
                    setText("-- Sélectionner un membre --");
                }
                return this;
            }
        });

        // Initialisation du JComboBox pour les types d'abonnement
        if (allTypeAbonnements == null) {
            allTypeAbonnements = new ArrayList<>(); // Évite un NullPointerException
        }
        DefaultComboBoxModel<TypeAbonnement> typeAbonnementModel = new DefaultComboBoxModel<>(allTypeAbonnements.toArray(new TypeAbonnement[0]));
        typeAbonnementModel.insertElementAt(null, 0); // Ajoute un élément null au début pour "sélectionner"
        typeAbonnementComboBox = new JComboBox<>(typeAbonnementModel);
        typeAbonnementComboBox.setSelectedIndex(0); // Sélectionne l'élément null par défaut
        // Rendu personnalisable pour afficher le libellé du type d'abonnement
        typeAbonnementComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof TypeAbonnement) {
                    TypeAbonnement typeAbo = (TypeAbonnement) value;
                    setText(typeAbo.getLibelle() + " (" + typeAbo.getMontant() + " F)");
                } else if (value == null) {
                    setText("-- Sélectionner un type d'abonnement --");
                }
                return this;
            }
        });

        // Ajout des labels et champs au formFieldsPanel avec GridBagLayout
        int fieldRow = 0;

        // Date de Début
        gbcFields.gridx = 0;
        gbcFields.gridy = fieldRow;
        gbcFields.gridwidth = 1;
        gbcFields.anchor = GridBagConstraints.EAST; // Aligne le label à droite
        formFieldsPanel.add(new JLabel("Date de Début (AAAA-MM-JJ HH:MM:SS) :"), gbcFields);
        gbcFields.gridx = 1;
        gbcFields.anchor = GridBagConstraints.WEST; // Aligne le champ à gauche
        dateDebutField.setPreferredSize(new Dimension(250, 35)); // Taille préférée
        formFieldsPanel.add(dateDebutField, gbcFields);
        fieldRow++;

        // Date de Fin
        gbcFields.gridx = 0;
        gbcFields.gridy = fieldRow;
        gbcFields.anchor = GridBagConstraints.EAST;
        formFieldsPanel.add(new JLabel("Date de Fin (AAAA-MM-JJ HH:MM:SS) (optionnel) :"), gbcFields);
        gbcFields.gridx = 1;
        gbcFields.anchor = GridBagConstraints.WEST;
        dateFinField.setPreferredSize(new Dimension(250, 35));
        formFieldsPanel.add(dateFinField, gbcFields);
        fieldRow++;

        // Membre
        gbcFields.gridx = 0;
        gbcFields.gridy = fieldRow;
        gbcFields.anchor = GridBagConstraints.EAST;
        formFieldsPanel.add(new JLabel("Membre :"), gbcFields);
        gbcFields.gridx = 1;
        gbcFields.anchor = GridBagConstraints.WEST;
        membreComboBox.setPreferredSize(new Dimension(250, 35));
        formFieldsPanel.add(membreComboBox, gbcFields);
        fieldRow++;

        // Type d'Abonnement
        gbcFields.gridx = 0;
        gbcFields.gridy = fieldRow;
        gbcFields.anchor = GridBagConstraints.EAST;
        formFieldsPanel.add(new JLabel("Type d'Abonnement :"), gbcFields);
        gbcFields.gridx = 1;
        gbcFields.anchor = GridBagConstraints.WEST;
        typeAbonnementComboBox.setPreferredSize(new Dimension(250, 35));
        formFieldsPanel.add(typeAbonnementComboBox, gbcFields);
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
        JLabel tableTitleLabel = new JLabel("Liste des Abonnements");
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

    // Second constructeur, utile si vous voulez créer un AbonnementEdit directement avec une entité existante
    public AbonnementEdit(Abonnement entite, List<Membre> allMembres, List<TypeAbonnement> allTypeAbonnements) {
        this(new ArrayList<>(), Arrays.asList("ID", "Date Début", "Date Fin", "Membre", "Type Abo"), allMembres, allTypeAbonnements);
        this.entite = entite;
        this.initForm(entite);
    }

    /**
     * @Override
     * Met à jour l'objet 'entite' avec les valeurs actuelles des champs du formulaire.
     */
    @Override
    public void init() {
        // Date de Début
        String dateDebutText = dateDebutField.getText();
        String trimmedDateDebutText = dateDebutText != null ? dateDebutText.trim() : null;

        if (trimmedDateDebutText != null && !trimmedDateDebutText.isEmpty()) {
            try {
                this.entite.setDateDebut(LocalDateTime.parse(trimmedDateDebutText, DemandeInscriptionController.DATE_TIME_FORMATTER));
            } catch (DateTimeParseException e) {
                throw new DateTimeParseException("Format de Date de Début invalide. Utilisez AAAA-MM-JJ HH:MM:SS.", trimmedDateDebutText, e.getErrorIndex(), e);
            }
        } else {
            this.entite.setDateDebut(null); // Ou lancer une exception si obligatoire
        }

        // Date de Fin (peut être null)
        String dateFinText = dateFinField.getText();
        String trimmedDateFinText = dateFinText != null ? dateFinText.trim() : null;

        if (trimmedDateFinText != null && !trimmedDateFinText.isEmpty()) {
            try {
                this.entite.setDateFin(LocalDateTime.parse(trimmedDateFinText, DemandeInscriptionController.DATE_TIME_FORMATTER));
            } catch (DateTimeParseException e) {
                throw new DateTimeParseException("Format de Date de Fin invalide. Utilisez AAAA-MM-JJ HH:MM:SS.", trimmedDateFinText, e.getErrorIndex(), e);
            }
        } else {
            this.entite.setDateFin(null);
        }

        // Membre sélectionné dans le JComboBox
        Membre selectedMembre = (Membre) membreComboBox.getSelectedItem();
        // Le cas `null` est géré par l'insertion d'un `null` comme première option, donc un `selectedMembre` `null` indique "Sélectionner un membre"
        if (selectedMembre != null && selectedMembre.getId() != 0) { // Vérifie aussi l'ID du membre
            this.entite.setMembre(selectedMembre);
        } else {
            this.entite.setMembre(null);
            throw new IllegalArgumentException("Veuillez sélectionner un membre valide pour cet abonnement.");
        }

        // TypeAbonnement sélectionné dans le JComboBox
        TypeAbonnement selectedTypeAbonnement = (TypeAbonnement) typeAbonnementComboBox.getSelectedItem();
        // Le cas `null` est géré par l'insertion d'un `null` comme première option
        if (selectedTypeAbonnement != null && selectedTypeAbonnement.getCode() != null && !selectedTypeAbonnement.getCode().trim().isEmpty()) {
            this.entite.setTypeAbonnement(selectedTypeAbonnement);
        } else {
            this.entite.setTypeAbonnement(null);
            throw new IllegalArgumentException("Veuillez sélectionner un type d'abonnement valide.");
        }
    }

    /**
     * @Override
     * Retourne l'entité Abonnement actuellement manipulée par ce formulaire.
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
        if (obj instanceof Abonnement) {
            this.entite = (Abonnement) obj;
        } else {
            throw new IllegalArgumentException("L'objet passé à setEntite doit être de type Abonnement.");
        }
    }

    /**
     * @Override
     * Initialise les champs du formulaire avec les valeurs de l'entité Abonnement donnée.
     */
    @Override
    public void initForm(Object obj) {
        if (obj instanceof Abonnement) {
            Abonnement abonnement = (Abonnement) obj;

            dateDebutField.setText(abonnement.getDateDebut() != null ? abonnement.getDateDebut().format(DemandeInscriptionController.DATE_TIME_FORMATTER) : "");
            dateFinField.setText(abonnement.getDateFin() != null ? abonnement.getDateFin().format(DemandeInscriptionController.DATE_TIME_FORMATTER) : "");

            // Sélectionner le membre correct dans le JComboBox
            if (abonnement.getMembre() != null && abonnement.getMembre().getId() != 0) {
                boolean found = false;
                for (int i = 0; i < membreComboBox.getItemCount(); i++) {
                    Membre item = membreComboBox.getItemAt(i);
                    // Comparer par ID car l'égalité des objets Membre pourrait ne pas être implémentée
                    if (item != null && item.getId() == abonnement.getMembre().getId()) {
                        membreComboBox.setSelectedItem(item);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    System.err.println("AbonnementEdit.initForm: Membre avec ID " + abonnement.getMembre().getId() + " non trouvé dans le JComboBox. Réinitialisation.");
                    membreComboBox.setSelectedItem(null); // Si non trouvé, ne rien sélectionner
                }
            } else {
                membreComboBox.setSelectedItem(null);
            }

            // Sélectionner le type d'abonnement correct dans le JComboBox
            if (abonnement.getTypeAbonnement() != null && abonnement.getTypeAbonnement().getCode() != null && !abonnement.getTypeAbonnement().getCode().trim().isEmpty()) {
                boolean found = false;
                for (int i = 0; i < typeAbonnementComboBox.getItemCount(); i++) {
                    TypeAbonnement item = typeAbonnementComboBox.getItemAt(i);
                    // Comparer par Code
                    if (item != null && item.getCode().equals(abonnement.getTypeAbonnement().getCode())) {
                        typeAbonnementComboBox.setSelectedItem(item);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    System.err.println("AbonnementEdit.initForm: TypeAbonnement avec code " + abonnement.getTypeAbonnement().getCode() + " non trouvé dans le JComboBox. Réinitialisation.");
                    typeAbonnementComboBox.setSelectedItem(null); // Si non trouvé, ne rien sélectionner
                }
            } else {
                typeAbonnementComboBox.setSelectedItem(null);
            }

        } else {
            System.err.println("initForm: L'objet passé n'est pas un Abonnement.");
            clearForm();
        }
    }

    /**
     * @Override
     * Efface le contenu de tous les champs du formulaire.
     */
    @Override
    public void clearForm() {
        dateDebutField.setText("");
        dateFinField.setText("");
        membreComboBox.setSelectedIndex(0); // Sélectionne le premier élément (le null)
        typeAbonnementComboBox.setSelectedIndex(0); // Sélectionne le premier élément (le null)
        this.entite = new Abonnement(); // Réinitialise l'entité interne
    }

    // Getters pour les boutons "Modifier" et "Supprimer"
    public JButton getModifyButton() {
        return modifyButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }
}
