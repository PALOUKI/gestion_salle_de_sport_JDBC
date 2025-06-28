package gui_admin.gui_util;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MyButton extends JButton {

    private Color defaultBackgroundColor = new Color(26, 26, 69); // Couleur de fond par défaut (même que le panneau west)
    private Color hoverBackgroundColor = new Color(40, 40, 100); // Couleur de fond au survol
    private Color pressedBackgroundColor = new Color(15, 15, 50); // Couleur de fond au clic
    private Color selectedBackgroundColor = new Color(50, 50, 120); // Couleur de fond quand le bouton est "sélectionné" (actif)

    private boolean isSelected = false; // État pour indiquer si le bouton est actif

    public MyButton(String label) {
        super(label);
        this.setFont(new Font("Goldman", Font.BOLD, 15));
        this.setForeground(Color.white); // Couleur du texte
        this.setBorderPainted(false); // Pas de bordure par défaut
        this.setFocusPainted(false); // Enlève le contour de focus
        this.setContentAreaFilled(false); // Permet de peindre le fond manuellement
        this.setOpaque(true); // Indique que le composant est opaque pour peindre son fond

        // Appliquer un padding interne pour l'espacement du texte
        this.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20)); // Padding vertical et horizontal

        updateButtonBackground(); // Initialiser la couleur de fond

        // Ajouter des listeners pour les effets de survol et de clic
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isSelected) { // Ne change pas la couleur de survol si déjà sélectionné
                    setBackground(hoverBackgroundColor);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!isSelected) { // Revert à la couleur par défaut si non sélectionné
                    updateButtonBackground();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (!isSelected) {
                    setBackground(pressedBackgroundColor);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!isSelected) {
                    updateButtonBackground(); // Revert après le relâchement, sauf si sélectionné
                }
            }
        });
    }

    // Peindre le fond du bouton pour un contrôle total de l'apparence
    @Override
    protected void paintComponent(Graphics g) {
        if (isOpaque()) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        super.paintComponent(g); // Peindre le texte et les icônes par-dessus
    }

    

    /**
     * Définit l'état "sélectionné" ou "actif" du bouton.
     * @param selected true pour sélectionner le bouton, false pour le désélectionner.
     */
    public void setSelected(boolean selected) {
        if (this.isSelected != selected) {
            this.isSelected = selected;
            updateButtonBackground();
            // Demande un rafraîchissement visuel
            repaint();
        }
    }

    /**
     * Met à jour la couleur de fond du bouton en fonction de son état (sélectionné ou non).
     */
    private void updateButtonBackground() {
        if (isSelected) {
            setBackground(selectedBackgroundColor);
        } else {
            setBackground(defaultBackgroundColor);
        }
    }
}
