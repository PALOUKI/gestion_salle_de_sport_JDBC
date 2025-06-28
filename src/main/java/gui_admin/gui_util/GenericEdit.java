package gui_admin.gui_util;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// Rendre la classe abstraite car elle contient des méthodes abstraites
public abstract class GenericEdit extends JPanel {

    protected JPanel contentPane = new JPanel();

    protected CustomTablePanel customTablePanel;
    protected JPanel form = new JPanel();

    protected ButtonPanel buttonPanel = new ButtonPanel();

    public GenericEdit(List<List<Object>> tableData, List<String> columnNames) {
        this.setSize(400, 400); // Taille par défaut, peut être ignorée par le LayoutManager parent
        this.setLayout(new BorderLayout());

        JPanel littleRightMargin = new JPanel();
        JPanel littleLeftMargin = new JPanel();

        // Initialiser le CustomTablePanel avec les données passées en paramètre
        this.customTablePanel = new CustomTablePanel(tableData, columnNames);

        contentPane.setLayout(new BorderLayout());


        contentPane.add(form, BorderLayout.CENTER);
        contentPane.add(customTablePanel, BorderLayout.SOUTH);

        form.setLayout(new GridLayout(0, 1)); // Utilise GridLayout pour empiler les champs

        littleRightMargin.setPreferredSize(new Dimension(30, 400));
        littleRightMargin.setLayout(new BorderLayout()); // Utilise un BorderLayout pour pouvoir y ajouter du contenu si besoin

        littleLeftMargin.setPreferredSize(new Dimension(30, 400));
        littleLeftMargin.setLayout(new BorderLayout()); // Utilise un BorderLayout pour pouvoir y ajouter du contenu si besoin

        this.add(buttonPanel, BorderLayout.SOUTH); // Le panel de boutons est en bas
        this.add(contentPane, BorderLayout.CENTER); // Le contenu principal (formulaire + tableau) est au centre
        this.add(littleLeftMargin, BorderLayout.WEST); // Marges latérales
        this.add(littleRightMargin, BorderLayout.EAST);
    }

    // --- Méthodes Abstraites à implémenter par les sous-classes ---
    // Ces méthodes forcent les classes filles à définir comment elles gèrent leur entité spécifique.
    public abstract void init(); // Met à jour l'entité avec les données des champs du formulaire
    public abstract Object getEntite(); // Retourne l'entité actuellement manipulée par le formulaire
    public abstract void setEntite(Object entite); // Définit l'entité que le formulaire doit manipuler
    public abstract void initForm(Object entite); // Initialise les champs du formulaire avec les données d'une entité
    public abstract void clearForm(); // Efface le contenu des champs du formulaire


    public JPanel getForm() {
        return form;
    }

    public CustomTablePanel getCustomTablePanel() {
        return customTablePanel;
    }

    public JButton getSaveButton() {
        return buttonPanel.getSaveButton();
    }

    public JButton getCancelButton() {
        return buttonPanel.getCancelButton();
    }

    // La méthode 'afficher' n'est généralement pas utilisée pour les JPanel intégrés dans un CardLayout.
    // Sa visibilité est gérée par le conteneur parent (MyWindow1).
    public void afficher(){
        this.setVisible(true);
    }
}
