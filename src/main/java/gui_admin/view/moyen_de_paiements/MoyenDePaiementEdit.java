package gui_admin.view.moyen_de_paiements; // Assurez-vous que ce package correspond à votre structure de dossiers

import entite.MoyenDePaiement;
import gui_admin.gui_util.ActionButton;
import gui_admin.gui_util.CustomTablePanel; // Importez si CustomTablePanel est utilisé directement
import gui_admin.gui_util.GenericEdit;

import javax.swing.*;
import javax.swing.border.EmptyBorder; // Importation pour le padding
import javax.swing.border.TitledBorder; // Importation pour le titre du cadre
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MoyenDePaiementEdit extends GenericEdit {

    private JTextField codeField = new JTextField();
    private JTextField libelleField = new JTextField();
    private MoyenDePaiement entite = new MoyenDePaiement(); // L'entité MoyenDePaiement manipulée par le formulaire

    // Déclaration des boutons Modifier et Supprimer
    private JButton modifyButton = new ActionButton("Modifier", ActionButton.ButtonType.MODIFY);
    private JButton deleteButton = new ActionButton("Supprimer", ActionButton.ButtonType.DELETE);

    // Constructeur principal pour l'initialisation avec les données du tableau
    public MoyenDePaiementEdit(List<List<Object>> tableData, List<String> columnNames) {
        // Appelle le constructeur de GenericEdit, qui configure la structure de base du panneau
        super(tableData, columnNames);

        // Configuration du panneau de formulaire (this.form) avec GridBagLayout pour un meilleur contrôle
        this.form.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Padding autour de chaque composant
        gbc.fill = GridBagConstraints.HORIZONTAL; // Remplir l'espace horizontalement

        // Titre principal du panneau d'édition des Moyens de Paiement
        JLabel mainTitleLabel = new JLabel("Gestion des Moyens de Paiement");
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
                                        "Détails du Moyen de Paiement", // Titre du cadre
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

        // Code
        gbcFields.gridx = 0;
        gbcFields.gridy = fieldRow;
        gbcFields.gridwidth = 1;
        gbcFields.anchor = GridBagConstraints.EAST; // Aligne le label à droite
        formFieldsPanel.add(new JLabel("Code :"), gbcFields);
        gbcFields.gridx = 1;
        gbcFields.anchor = GridBagConstraints.WEST; // Aligne le champ à gauche
        codeField.setPreferredSize(new Dimension(250, 35)); // Taille préférée
        formFieldsPanel.add(codeField, gbcFields);
        fieldRow++;

        // Libellé
        gbcFields.gridx = 0;
        gbcFields.gridy = fieldRow;
        gbcFields.anchor = GridBagConstraints.EAST;
        formFieldsPanel.add(new JLabel("Libellé :"), gbcFields);
        gbcFields.gridx = 1;
        gbcFields.anchor = GridBagConstraints.WEST;
        libelleField.setPreferredSize(new Dimension(250, 35)); // Taille préférée
        formFieldsPanel.add(libelleField, gbcFields);
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
        JLabel tableTitleLabel = new JLabel("Liste des Moyens de Paiement");
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

    // Second constructeur, utile si vous voulez créer un MoyenDePaiementEdit directement avec une entité
    public MoyenDePaiementEdit(MoyenDePaiement entite) {
        // Appelle le constructeur principal avec des listes vides pour les données initiales du tableau
        // et les noms de colonnes par défaut pour un MoyenDePaiement.
        this(new ArrayList<>(), Arrays.asList("Code", "Libellé"));
        this.entite = entite; // La référence à l'entité est mise à jour avec l'entité passée
        this.initForm(entite); // Initialise les champs du formulaire avec les données de cette entité
    }

    /**
     * @Override
     * Met à jour l'objet 'entite' avec les valeurs actuelles des champs du formulaire.
     * Cette méthode est appelée par le contrôleur avant d'envoyer l'entité au service (ajouter/modifier).
     */
    @Override
    public void init() {
        this.entite.setCode(codeField.getText());
        this.entite.setLibelle(libelleField.getText());
        // Aucun champ numérique ou date à convertir ici, simple récupération du texte
    }

    /**
     * @Override
     * Retourne l'entité MoyenDePaiement actuellement manipulée par ce formulaire.
     * C'est l'objet qui contient les données des champs, potentiellement mis à jour par init().
     */
    @Override
    public Object getEntite() { // Le type de retour est Object pour correspondre à l'abstract method de GenericEdit
        return entite;
    }

    /**
     * @Override
     * Permet de définir l'entité que le formulaire manipulera.
     * Utilisé par le contrôleur pour passer une entité existante (pour modification) ou une nouvelle (pour ajout).
     *
     * @param obj L'objet entité (doit être un MoyenDePaiement)
     */
    @Override
    public void setEntite(Object obj) { // Le paramètre est Object pour correspondre à l'abstract method de GenericEdit
        if (obj instanceof MoyenDePaiement) {
            this.entite = (MoyenDePaiement) obj;
        } else {
            throw new IllegalArgumentException("L'objet passé à setEntite doit être de type MoyenDePaiement.");
        }
    }

    /**
     * @Override
     * Initialise les champs du formulaire avec les valeurs de l'entité MoyenDePaiement donnée.
     * Cette méthode est principalement utilisée pour le mode "modifier" ou pour afficher les détails.
     *
     * @param obj L'objet entité (doit être un MoyenDePaiement) dont les données seront affichées
     */
    @Override
    public void initForm(Object obj) { // Le paramètre est Object pour correspondre à l'abstract method de GenericEdit
        if (obj instanceof MoyenDePaiement) {
            MoyenDePaiement mdp = (MoyenDePaiement) obj;
            codeField.setText(mdp.getCode());
            libelleField.setText(mdp.getLibelle());
        } else {
            System.err.println("initForm: L'objet passé n'est pas un MoyenDePaiement.");
            clearForm(); // Ou lancer une exception
        }
    }

    /**
     * @Override
     * Efface le contenu de tous les champs du formulaire.
     * Utilisé généralement après un ajout réussi ou une annulation.
     */
    @Override
    public void clearForm() {
        codeField.setText("");
        libelleField.setText("");
        this.entite = new MoyenDePaiement(); // Réinitialise l'entité interne pour les prochains ajouts
    }

    // Getters pour les boutons "Modifier" et "Supprimer", nécessaires pour que le contrôleur puisse leur attacher des listeners
    public JButton getModifyButton() {
        return modifyButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }
}