package gui_admin.gui_util; // Ou gui_admin.gui_components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D; // Pour dessiner des rectangles arrondis

/**
 * Un JButton stylisé pour les actions courantes, avec des couleurs personnalisables
 * et un effet de survol, ajusté pour avoir une taille et une forme similaires à MyButton
 * en contrôlant son propre rendu.
 */
public class ActionButton extends JButton {

    public enum ButtonType {
        ADD,
        MODIFY,
        DELETE,
        GENERIC
    }

    private ButtonType type;
    private Color defaultBgColor;
    private Color hoverBgColor;
    private Color currentBgColor; // La couleur actuellement affichée

    private static final int BORDER_RADIUS = 10; // Rayon pour les coins arrondis (peut être ajusté)

    /**
     * Constructeur pour les boutons avec un type prédéfini (MODIFY, DELETE, GENERIC).
     * Les couleurs sont déterminées par le ButtonType.
     * @param text Le texte affiché sur le bouton.
     * @param type Le type prédéfini du bouton.
     */
    public ActionButton(String text, ButtonType type) {
        super(text);
        this.type = type;
        setColorsBasedOnType();
        initStyle();
    }

    /**
     * Nouveau constructeur pour personnaliser entièrement le bouton.
     * @param text Le texte affiché sur le bouton.
     * @param bgColor La couleur de fond par défaut du bouton.
     * @param hoverColor La couleur de fond du bouton lorsque la souris le survole.
     */
    public ActionButton(String text, Color bgColor, Color hoverColor) {
        super(text);
        this.type = ButtonType.GENERIC;
        this.defaultBgColor = bgColor;
        this.hoverBgColor = hoverColor;
        initStyle();
    }

    private void setColorsBasedOnType() {
        switch (type) {
            case ADD:
                defaultBgColor = new Color(9, 28, 243); // Bleu plus foncé
                hoverBgColor = new Color(56, 122, 243);
                break;
            case MODIFY:
                defaultBgColor = new Color(0, 144, 2); // Bleu plus foncé
                hoverBgColor = new Color(121, 229, 123);
                break;
            case DELETE:
                defaultBgColor = new Color(200, 50, 50); // Rouge pour la suppression
                hoverBgColor = new Color(220, 70, 70);
                break;
            case GENERIC:
            default:
                defaultBgColor = new Color(70, 130, 180); // Bleu acier par défaut
                hoverBgColor = new Color(90, 150, 200);
                break;
        }
    }

    private void initStyle() {
        this.setFont(new Font("Goldman", Font.BOLD, 15)); // Taille de police 15
        this.setForeground(Color.WHITE); // Texte blanc
        this.setBorderPainted(false); // Ne pas peindre la bordure par défaut de Swing
        this.setFocusPainted(false); // Enlève le contour de focus
        this.setContentAreaFilled(false); // Permet de peindre le fond manuellement
        this.setOpaque(false); // Important pour un rendu transparent du fond par Swing

        // Appliquer un padding interne pour l'espacement du texte
        this.setBorder(new EmptyBorder(9, 20, 9, 20)); // Padding vertical et horizontal


        // Initialiser la couleur de fond actuelle
        this.currentBgColor = defaultBgColor;

        // Ajouter des listeners pour les effets de survol
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                currentBgColor = hoverBgColor;
                repaint(); // Redessiner pour montrer l'effet de survol
            }

            @Override
            public void mouseExited(MouseEvent e) {
                currentBgColor = defaultBgColor;
                repaint(); // Redessiner pour revenir à la couleur par défaut
            }

            // Pas besoin de mousePressed/Released ici pour garder la simplicité et l'effet visuel
            // de MyButton est lié à un état "sélectionné" que cet ActionButton n'a pas.
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dessine le fond avec des coins arrondis
        g2.setColor(currentBgColor);
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), BORDER_RADIUS, BORDER_RADIUS));

        // Pour dessiner des bords si nécessaire (optionnel, pour l'instant pas dans MyButton)
        // g2.setColor(Color.LIGHT_GRAY);
        // g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, BORDER_RADIUS, BORDER_RADIUS));

        g2.dispose();
        super.paintComponent(g); // Peindre le texte et les icônes par-dessus
    }

    @Override
    public Dimension getPreferredSize() {
        // Appeler la méthode par défaut de JButton pour obtenir la taille du contenu (texte + padding)
        Dimension d = super.getPreferredSize();
        // S'assurer que le bouton a au moins une certaine largeur/hauteur minimale si le texte est très court
        d.width = Math.max(d.width, 100); // Minimum 100px de large
        d.height = Math.max(d.height, 40); // Minimum 40px de haut
        return d;
    }
}