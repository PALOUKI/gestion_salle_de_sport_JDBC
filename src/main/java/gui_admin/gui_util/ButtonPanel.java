package gui_admin.gui_util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ButtonPanel extends JPanel {
    private JButton saveButton = new ActionButton("Enregistrer", ActionButton.ButtonType.ADD);
    private JButton cancelButton = new ActionButton("Annuler", ActionButton.ButtonType.GENERIC);



    public ButtonPanel() {
        this.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 5)); // Espacement horizontal de 10 pixels
/*
        // Embellir le bouton "Enregistrer"
        saveButton.setBackground(new Color(32, 64, 128)); // Bleu foncé
        saveButton.setForeground(Color.WHITE); // Texte blanc
        saveButton.setFont(new Font("Goldman", Font.BOLD, 14)); // Police Goldman, gras, taille 14
        saveButton.setFocusPainted(false); // Pas de contour de focus
        saveButton.setBorder(new EmptyBorder(10, 20, 10, 20)); // Marge interne
        saveButton.putClientProperty("JButton.buttonType", "roundRect"); // Coins arrondis

 */

        // Ajout de l'effet de survol pour le bouton Enregistrer
        saveButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                saveButton.setBackground(new Color(45, 80, 160)); // Légèrement plus clair au survol
            }

            @Override
            public void mouseExited(MouseEvent e) {
                saveButton.setBackground(new Color(32, 64, 128)); // Retour à la couleur normale
            }
        });

        /*
        // Embellir le bouton "Annuler"
        cancelButton.setBackground(new Color(200, 50, 50)); // Rouge pour l'annulation
        cancelButton.setForeground(Color.WHITE); // Texte blanc
        cancelButton.setFont(new Font("Goldman", Font.BOLD, 14)); // Police Goldman, gras, taille 14
        cancelButton.setFocusPainted(false); // Pas de contour de focus
        cancelButton.setBorder(new EmptyBorder(10, 20, 10, 20)); // Marge interne
        cancelButton.putClientProperty("JButton.buttonType", "roundRect"); // Coins arrondis

         */

        // Ajout de l'effet de survol pour le bouton Annuler
        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                cancelButton.setBackground(new Color(220, 70, 70)); // Légèrement plus clair au survol
            }

            @Override
            public void mouseExited(MouseEvent e) {
                cancelButton.setBackground(new Color(200, 50, 50)); // Retour à la couleur normale
            }
        });

        this.add(cancelButton);
        this.add(saveButton);

    }

    public JButton getSaveButton() {
        return saveButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }
}