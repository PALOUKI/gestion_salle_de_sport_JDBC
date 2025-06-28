package gui_admin.view.paiements; // Assurez-vous que ce package correspond à votre structure de dossiers

import entite.Abonnement; // Importez la classe Abonnement
import entite.MoyenDePaiement; // Importez la classe MoyenDePaiement
import entite.Paiement;
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

public class PaiementEdit extends GenericEdit {

    private JTextField montantField = new JTextField();
    private JTextField dateDePaiementField = new JTextField(); // Pour la date de paiement
    private JComboBox<MoyenDePaiement> moyenDePaiementComboBox; // JComboBox pour le moyen de paiement
    private JComboBox<Abonnement> abonnementComboBox; // JComboBox pour l'abonnement
    private Paiement entite = new Paiement(); // L'entité Paiement manipulée par le formulaire

    // Déclaration des boutons Modifier et Supprimer
    private JButton modifyButton = new ActionButton("Modifier", ActionButton.ButtonType.MODIFY);
    private JButton deleteButton = new ActionButton("Supprimer", ActionButton.ButtonType.DELETE);

    // Constructeur principal pour l'initialisation avec les données du tableau et les listes des entités liées
    public PaiementEdit(List<List<Object>> tableData, List<String> columnNames, List<MoyenDePaiement> allMoyensDePaiement, List<Abonnement> allAbonnements) {
        super(tableData, columnNames);

        // Configuration du panneau de formulaire (this.form) avec GridBagLayout pour un meilleur contrôle
        this.form.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Padding autour de chaque composant
        gbc.fill = GridBagConstraints.HORIZONTAL; // Remplir l'espace horizontalement

        // Titre principal du panneau d'édition des Paiements
        JLabel mainTitleLabel = new JLabel("Gestion des Paiements");
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
                                        "Détails du Paiement", // Titre du cadre
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

        // Initialisation du JComboBox pour les moyens de paiement
        if (allMoyensDePaiement == null) {
            allMoyensDePaiement = new ArrayList<>(); // Évite un NullPointerException
        }
        DefaultComboBoxModel<MoyenDePaiement> mdpModel = new DefaultComboBoxModel<>(allMoyensDePaiement.toArray(new MoyenDePaiement[0]));
        mdpModel.insertElementAt(null, 0); // Ajoute un élément null au début pour une sélection optionnelle
        moyenDePaiementComboBox = new JComboBox<>(mdpModel);
        moyenDePaiementComboBox.setSelectedIndex(0); // Sélectionne l'élément null par défaut
        // Rendu personnalisable pour afficher le code et le libellé du moyen de paiement
        moyenDePaiementComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof MoyenDePaiement) {
                    MoyenDePaiement mdp = (MoyenDePaiement) value;
                    setText("Code: " + mdp.getCode() + " - " + mdp.getLibelle());
                } else if (value == null) {
                    setText("-- Sélectionner un moyen de paiement --");
                }
                return this;
            }
        });


        // Initialisation du JComboBox pour les abonnements
        if (allAbonnements == null) {
            allAbonnements = new ArrayList<>(); // Évite un NullPointerException
        }
        DefaultComboBoxModel<Abonnement> abonnementModel = new DefaultComboBoxModel<>(allAbonnements.toArray(new Abonnement[0]));
        abonnementModel.insertElementAt(null, 0); // Ajoute un élément null au début pour une sélection optionnelle
        abonnementComboBox = new JComboBox<>(abonnementModel);
        abonnementComboBox.setSelectedIndex(0); // Sélectionne l'élément null par défaut
        // Rendu personnalisable pour afficher l'ID de l'abonnement et ses dates
        abonnementComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Abonnement) {
                    Abonnement abo = (Abonnement) value;
                    String debut = abo.getDateDebut() != null ? abo.getDateDebut().format(DemandeInscriptionController.DATE_TIME_FORMATTER) : "N/A";
                    String fin = abo.getDateFin() != null ? abo.getDateFin().format(DemandeInscriptionController.DATE_TIME_FORMATTER) : "N/A";
                    setText("ID: " + abo.getId() + " (Du " + debut + " au " + fin + ")");
                } else if (value == null) {
                    setText("-- Sélectionner un abonnement --");
                }
                return this;
            }
        });

        // Ajout des labels et champs au formFieldsPanel avec GridBagLayout
        int fieldRow = 0;

        // Montant
        gbcFields.gridx = 0;
        gbcFields.gridy = fieldRow;
        gbcFields.gridwidth = 1;
        gbcFields.anchor = GridBagConstraints.EAST; // Aligne le label à droite
        formFieldsPanel.add(new JLabel("Montant :"), gbcFields);
        gbcFields.gridx = 1;
        gbcFields.anchor = GridBagConstraints.WEST; // Aligne le champ à gauche
        montantField.setPreferredSize(new Dimension(250, 35)); // Taille préférée
        formFieldsPanel.add(montantField, gbcFields);
        fieldRow++;

        // Date de Paiement
        gbcFields.gridx = 0;
        gbcFields.gridy = fieldRow;
        gbcFields.anchor = GridBagConstraints.EAST;
        formFieldsPanel.add(new JLabel("Date de Paiement (AAAA-MM-JJ HH:MM:SS) :"), gbcFields);
        gbcFields.gridx = 1;
        gbcFields.anchor = GridBagConstraints.WEST;
        dateDePaiementField.setPreferredSize(new Dimension(250, 35));
        formFieldsPanel.add(dateDePaiementField, gbcFields);
        fieldRow++;

        // Moyen de Paiement
        gbcFields.gridx = 0;
        gbcFields.gridy = fieldRow;
        gbcFields.anchor = GridBagConstraints.EAST;
        formFieldsPanel.add(new JLabel("Moyen de Paiement :"), gbcFields);
        gbcFields.gridx = 1;
        gbcFields.anchor = GridBagConstraints.WEST;
        moyenDePaiementComboBox.setPreferredSize(new Dimension(250, 35)); // Taille préférée
        formFieldsPanel.add(moyenDePaiementComboBox, gbcFields); // AJOUT DU JComboBox
        fieldRow++;

        // Abonnement
        gbcFields.gridx = 0;
        gbcFields.gridy = fieldRow;
        gbcFields.anchor = GridBagConstraints.EAST;
        formFieldsPanel.add(new JLabel("Abonnement :"), gbcFields);
        gbcFields.gridx = 1;
        gbcFields.anchor = GridBagConstraints.WEST;
        abonnementComboBox.setPreferredSize(new Dimension(250, 35)); // Taille préférée
        formFieldsPanel.add(abonnementComboBox, gbcFields); // AJOUT DU JComboBox
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
        JLabel tableTitleLabel = new JLabel("Liste des Paiements");
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

    // Second constructeur, utile si vous voulez créer un PaiementEdit directement avec une entité existante
    public PaiementEdit(Paiement entite, List<MoyenDePaiement> allMoyensDePaiement, List<Abonnement> allAbonnements) {
        this(new ArrayList<>(), Arrays.asList("ID", "Montant", "Date Paiement", "Code Moyen", "ID Abonnement"), allMoyensDePaiement, allAbonnements);
        this.entite = entite;
        this.initForm(entite);
    }

    /**
     * Met à jour l'objet 'entite' avec les valeurs actuelles des champs du formulaire.
     */
    @Override
    public void init() {
        // Montant
        String montantText = montantField.getText();
        if (montantText != null && !montantText.trim().isEmpty()) {
            try {
                this.entite.setMontant(Integer.parseInt(montantText.trim()));
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Le montant doit être un nombre entier valide.");
            }
        } else {
            this.entite.setMontant(0); // Ou lancer une exception si obligatoire
        }

        // Date de Paiement
        String datePaiementText = dateDePaiementField.getText();
        String trimmedDatePaiementText = datePaiementText != null ? datePaiementText.trim() : null;

        if (trimmedDatePaiementText != null && !trimmedDatePaiementText.isEmpty()) {
            try {
                this.entite.setDateDePaiement(LocalDateTime.parse(trimmedDatePaiementText, DemandeInscriptionController.DATE_TIME_FORMATTER));
            } catch (DateTimeParseException e) {
                throw new DateTimeParseException("Format de Date de Paiement invalide. Utilisez AAAA-MM-JJ HH:MM:SS.", trimmedDatePaiementText, e.getErrorIndex(), e);
            }
        } else {
            this.entite.setDateDePaiement(null); // Ou lancer une exception si obligatoire
        }

        // Moyen de Paiement sélectionné dans le JComboBox
        MoyenDePaiement selectedMoyenDePaiement = (MoyenDePaiement) moyenDePaiementComboBox.getSelectedItem();
        if (selectedMoyenDePaiement != null && selectedMoyenDePaiement.getCode() != null && !selectedMoyenDePaiement.getCode().trim().isEmpty()) {
            this.entite.setMoyenDePaiement(selectedMoyenDePaiement);
        } else {
            this.entite.setMoyenDePaiement(null);
            // Optionnel: throw new IllegalArgumentException("Veuillez sélectionner un moyen de paiement.");
        }

        // Abonnement sélectionné dans le JComboBox
        Abonnement selectedAbonnement = (Abonnement) abonnementComboBox.getSelectedItem();
        if (selectedAbonnement != null && selectedAbonnement.getId() != 0) {
            this.entite.setAbonnement(selectedAbonnement);
        } else {
            this.entite.setAbonnement(null);
            // Optionnel: throw new IllegalArgumentException("Veuillez sélectionner un abonnement pour ce paiement.");
        }
    }

    /**
     * Retourne l'entité Paiement actuellement manipulée par ce formulaire.
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
        if (obj instanceof Paiement) {
            this.entite = (Paiement) obj;
        } else {
            throw new IllegalArgumentException("L'objet passé à setEntite doit être de type Paiement.");
        }
    }

    /**
     * Initialise les champs du formulaire avec les valeurs de l'entité Paiement donnée.
     */
    @Override
    public void initForm(Object obj) {
        if (obj instanceof Paiement) {
            Paiement paiement = (Paiement) obj;

            montantField.setText(String.valueOf(paiement.getMontant()));
            dateDePaiementField.setText(paiement.getDateDePaiement() != null ? paiement.getDateDePaiement().format(DemandeInscriptionController.DATE_TIME_FORMATTER) : "");

            // Sélectionner le moyen de paiement correct dans le JComboBox
            if (paiement.getMoyenDePaiement() != null && paiement.getMoyenDePaiement().getCode() != null && !paiement.getMoyenDePaiement().getCode().trim().isEmpty()) {
                boolean found = false;
                for (int i = 0; i < moyenDePaiementComboBox.getItemCount(); i++) {
                    MoyenDePaiement item = moyenDePaiementComboBox.getItemAt(i);
                    if (item != null && item.getCode().equals(paiement.getMoyenDePaiement().getCode())) {
                        moyenDePaiementComboBox.setSelectedItem(item);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    System.err.println("PaiementEdit.initForm: MoyenDePaiement avec code " + paiement.getMoyenDePaiement().getCode() + " non trouvé dans le JComboBox.");
                    moyenDePaiementComboBox.setSelectedItem(null);
                }
            } else {
                moyenDePaiementComboBox.setSelectedItem(null);
            }

            // Sélectionner l'abonnement correct dans le JComboBox
            if (paiement.getAbonnement() != null && paiement.getAbonnement().getId() != 0) {
                boolean found = false;
                for (int i = 0; i < abonnementComboBox.getItemCount(); i++) {
                    Abonnement item = abonnementComboBox.getItemAt(i);
                    if (item != null && item.getId() == paiement.getAbonnement().getId()) {
                        abonnementComboBox.setSelectedItem(item);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    System.err.println("PaiementEdit.initForm: Abonnement avec ID " + paiement.getAbonnement().getId() + " non trouvé dans le JComboBox.");
                    abonnementComboBox.setSelectedItem(null);
                }
            } else {
                abonnementComboBox.setSelectedItem(null);
            }

        } else {
            System.err.println("initForm: L'objet passé n'est pas un Paiement.");
            clearForm();
        }
    }

    /**
     * Efface le contenu de tous les champs du formulaire.
     */
    @Override
    public void clearForm() {
        montantField.setText("");
        dateDePaiementField.setText("");
        moyenDePaiementComboBox.setSelectedIndex(0); // Sélectionne le premier élément
        abonnementComboBox.setSelectedIndex(0); // Sélectionne le premier élément
        this.entite = new Paiement(); // Réinitialise l'entité interne
    }

    // Getters pour les boutons "Modifier" et "Supprimer"
    public JButton getModifyButton() {
        return modifyButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }
}