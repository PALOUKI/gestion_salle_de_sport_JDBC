package gui_admin.view.seances; // Assurez-vous que ce package correspond à votre structure de dossiers

import entite.Salle; // Importez la classe Salle
import entite.Seance;
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

public class SeanceEdit extends GenericEdit {

    private JTextField dateDebutField = new JTextField();
    private JTextField dateFinField = new JTextField();
    private JComboBox<Salle> salleComboBox; // JComboBox pour la sélection de la salle
    private Seance entite = new Seance(); // L'entité Seance manipulée par le formulaire

    // Déclaration des boutons Modifier et Supprimer
    private JButton modifyButton = new ActionButton("Modifier", ActionButton.ButtonType.MODIFY);
    private JButton deleteButton = new ActionButton("Supprimer", ActionButton.ButtonType.DELETE);

    // Constructeur principal pour l'initialisation avec les données du tableau et la liste des salles
    public SeanceEdit(List<List<Object>> tableData, List<String> columnNames, List<Salle> allSalles) {
        super(tableData, columnNames);

        // Configuration du panneau de formulaire (this.form) avec GridBagLayout pour un meilleur contrôle
        this.form.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Padding autour de chaque composant
        gbc.fill = GridBagConstraints.HORIZONTAL; // Remplir l'espace horizontalement

        // Titre principal du panneau d'édition des Séances
        JLabel mainTitleLabel = new JLabel("Gestion des Séances");
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
                                        "Détails de la Séance", // Titre du cadre
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

        // Initialisation du JComboBox avec les salles
        if (allSalles == null) {
            allSalles = new ArrayList<>(); // Évite un NullPointerException si la liste est null
        }
        DefaultComboBoxModel<Salle> model = new DefaultComboBoxModel<>(allSalles.toArray(new Salle[0]));
        model.insertElementAt(null, 0); // Ajoute un élément null au début pour une sélection optionnelle
        salleComboBox = new JComboBox<>(model);
        salleComboBox.setSelectedIndex(0); // Sélectionne l'élément null par défaut

        // Rendu personnalisable pour afficher l'ID et le libellé de la salle
        salleComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Salle) {
                    Salle salle = (Salle) value;
                    setText("ID: " + salle.getId() + " - " + salle.getLibelle());
                } else if (value == null) {
                    setText("-- Sélectionner une salle --");
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

        // Salle
        gbcFields.gridx = 0;
        gbcFields.gridy = fieldRow;
        gbcFields.anchor = GridBagConstraints.EAST;
        formFieldsPanel.add(new JLabel("Salle :"), gbcFields);
        gbcFields.gridx = 1;
        gbcFields.anchor = GridBagConstraints.WEST;
        salleComboBox.setPreferredSize(new Dimension(250, 35)); // Taille préférée
        formFieldsPanel.add(salleComboBox, gbcFields); // AJOUT DU JComboBox
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
        JLabel tableTitleLabel = new JLabel("Liste des Séances");
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

    // Second constructeur, utile si vous voulez créer un SeanceEdit directement avec une entité existante
    public SeanceEdit(Seance entite, List<Salle> allSalles) {
        this(new ArrayList<>(), Arrays.asList("ID", "Date Début", "Date Fin", "ID Salle"), allSalles);
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
                // Relance l'exception pour que le contrôleur la gère
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
                // Relance l'exception
                throw new DateTimeParseException("Format de Date de Fin invalide. Utilisez AAAA-MM-JJ HH:MM:SS.", trimmedDateFinText, e.getErrorIndex(), e);
            }
        } else {
            this.entite.setDateFin(null);
        }

        // Salle sélectionnée dans le JComboBox
        Salle selectedSalle = (Salle) salleComboBox.getSelectedItem();

        // Vérifier si une salle a été réellement sélectionnée (non null et avec un ID valide)
        if (selectedSalle != null && selectedSalle.getId() != 0) {
            this.entite.setSalle(selectedSalle);
        } else {
            this.entite.setSalle(null); // Si "Sélectionner une salle" ou rien n'est choisi.
            // Optionnel: lancer une exception si la salle est obligatoire
            // throw new IllegalArgumentException("Veuillez sélectionner une salle pour cette séance.");
        }
    }

    /**
     * @Override
     * Retourne l'entité Seance actuellement manipulée par ce formulaire.
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
        if (obj instanceof Seance) {
            this.entite = (Seance) obj;
        } else {
            throw new IllegalArgumentException("L'objet passé à setEntite doit être de type Seance.");
        }
    }

    /**
     * @Override
     * Initialise les champs du formulaire avec les valeurs de l'entité Seance donnée.
     */
    @Override
    public void initForm(Object obj) {
        if (obj instanceof Seance) {
            Seance seance = (Seance) obj;

            dateDebutField.setText(seance.getDateDebut() != null ? seance.getDateDebut().format(DemandeInscriptionController.DATE_TIME_FORMATTER) : "");
            dateFinField.setText(seance.getDateFin() != null ? seance.getDateFin().format(DemandeInscriptionController.DATE_TIME_FORMATTER) : "");

            // Sélectionner la salle correcte dans le JComboBox
            if (seance.getSalle() != null && seance.getSalle().getId() != 0) {
                boolean found = false;
                for (int i = 0; i < salleComboBox.getItemCount(); i++) {
                    Salle item = salleComboBox.getItemAt(i);
                    // Utiliser .equals() si implémenté dans Salle, sinon comparer l'ID
                    if (item != null && item.getId() == seance.getSalle().getId()) {
                        salleComboBox.setSelectedItem(item);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    System.err.println("SeanceEdit.initForm: Salle avec ID " + seance.getSalle().getId() + " non trouvée dans le JComboBox.");
                    salleComboBox.setSelectedItem(null);
                }
            } else {
                salleComboBox.setSelectedItem(null);
            }

        } else {
            System.err.println("initForm: L'objet passé n'est pas une Seance.");
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
        salleComboBox.setSelectedIndex(0); // Sélectionne le premier élément (souvent "Sélectionner une salle" ou null)
        this.entite = new Seance(); // Réinitialise l'entité interne pour les prochains ajouts
    }

    // Getters pour les boutons "Modifier" et "Supprimer"
    public JButton getModifyButton() {
        return modifyButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }
}