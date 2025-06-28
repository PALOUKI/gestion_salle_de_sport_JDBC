package gui_admin.gui_util;

import entite.TypeAbonnement;
import entite.Client; // Importez la classe Client
import gui_admin.controller.*;
import gui_admin.view.TableauDeBordPanel;
import gui_admin.view.abonnements.AbonnementEdit; // Correction du package AbonnementEdit
import gui_admin.view.demande_inscriptions.DemandeInscriptionEdit;
import gui_admin.view.equipements.EquipementEdit;
import gui_admin.view.membres.MembreEdit;
import gui_admin.view.moyen_de_paiements.MoyenDePaiementEdit;
import gui_admin.view.paiements.PaiementEdit; // Correction du package PaiementEdit
import gui_admin.view.salles.SalleEdit;
import gui_admin.view.seances.SeanceEdit;
import gui_admin.view.tickets.TicketEdit;
import gui_admin.view.type_abonnements.Edit;
import gui_admin.view.clients.ClientEdit; // Correction du package ClientEdit

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyWindow1 extends MyWindow {

    // Gardez une référence aux contrôleurs
    private TypeAbonnementController typeAbonnementController;
    private ClientController clientController;
    private MoyenDePaiementController moyenDePaiementController;
    private DemandeInscriptionController demandeInscriptionController;
    private SalleController salleController;
    private MembreController membreController;
    private TicketController ticketController;
    private EquipementController equipementController;
    private SeanceController seanceController;
    private AbonnementController abonnementController;
    private PaiementController paiementController;

    // Gardez une référence au CardLayout et au panneau central pour le changement de page
    private CardLayout cardLayout;
    private JPanel centerPanel;

    // Liste de tous les boutons du menu pour la gestion de l'état "sélectionné"
    private List<MyButton> menuButtons;
    private MyButton currentSelectedButton; // Pour suivre le bouton actuellement sélectionné

    public MyWindow1() throws SQLException {
        super();
        Container c = this.getContentPane();

        // Initialisation des contrôleurs
        typeAbonnementController = new TypeAbonnementController();
        clientController = new ClientController();
        moyenDePaiementController = new MoyenDePaiementController();
        demandeInscriptionController = new DemandeInscriptionController();
        membreController = new MembreController();
        salleController = new SalleController();
        ticketController = new TicketController();
        equipementController = new EquipementController();
        seanceController = new SeanceController();
        abonnementController = new AbonnementController();
        paiementController = new PaiementController();

        // Layouts
        BorderLayout borderLayout = new BorderLayout();
        FlowLayout flowRightLayout = new FlowLayout(FlowLayout.RIGHT);
        FlowLayout flowLeftLayout = new FlowLayout(FlowLayout.LEADING);
        FlowLayout flowCenterLayout = new FlowLayout(FlowLayout.CENTER);

        cardLayout = new CardLayout();

        MyLabel user = new MyLabel("connecté: username");
        user.setForeground(Color.WHITE);

        MyLabel tableauDeBordLabel = new MyLabel("CHEZ ROOT@HSA"); // Renommé pour éviter la confusion avec le panel
        tableauDeBordLabel.setForeground(Color.WHITE);
        tableauDeBordLabel.setFont(new Font("Goldman", Font.BOLD, 20));

        JPanel spacePanel = new JPanel();
        spacePanel.setPreferredSize(new Dimension(800, 50));
        spacePanel.setBackground(new Color(32, 64, 128));

        MyLabel copyright = new MyLabel("@ copyright...chez root@hsa");
       // MyLabel menuTitleLabel = new MyLabel("Menu"); // Label pour le titre du menu
        //menuTitleLabel.setForeground(Color.WHITE);
        //menuTitleLabel.setFont(new Font("Goldman", Font.BOLD, 18));
        //menuTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Centre le label dans le BoxLayout

        // Buttons
        MyButton btn0 = new MyButton("Tableau de bord");
        MyButton btn1 = new MyButton("Types abonnements");
        MyButton btn2 = new MyButton("Clients");
        MyButton btn3 = new MyButton("Moyen de paiements");
        MyButton btn4 = new MyButton("Demande Inscription");
        MyButton btn5 = new MyButton("Membres");
        MyButton btn6 = new MyButton("Salles");
        MyButton btn7 = new MyButton("Tickets");
        MyButton btn8 = new MyButton("Equipements");
        MyButton btn9 = new MyButton("Seances");
        MyButton btn10 = new MyButton("Abonnements");
        MyButton btn11 = new MyButton("Paiements");

        // Initialiser la liste des boutons
        menuButtons = Arrays.asList(
                btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn10, btn11
        );

        // create pages
        // Page "Tableau de bord"
        JPanel page0 = new TableauDeBordPanel();

        // Page "Types abonnements"
        JPanel typeAbonnementPage = typeAbonnementController.createAndConfigureEditPanelForAdd();
        typeAbonnementController.setEditPanel((Edit) typeAbonnementPage);

        // Page "Clients"
        JPanel clientPage = clientController.createAndConfigureEditPanelForAdd();
        clientController.setEditPanel((ClientEdit) clientPage);

        // Page "Moyen de paiement"
        JPanel moyenDePaiementPage = moyenDePaiementController.createAndConfigureEditPanelForAdd();
        moyenDePaiementController.setEditPanel((MoyenDePaiementEdit) moyenDePaiementPage);

        // Page "Demande d'inscriptions"
        JPanel demandeInscriptionPage;
        try {
            demandeInscriptionPage = demandeInscriptionController.createAndConfigureEditPanelForAdd();
            demandeInscriptionController.setEditPanel((DemandeInscriptionEdit) demandeInscriptionPage);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement initial des demandes d'inscription: " + e.getMessage(), "Erreur de chargement", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            demandeInscriptionPage = createPage(Color.RED, "Erreur de chargement des Demandes d'Inscription");
        }

        // Page "Membres"
        JPanel membrePage = membreController.createAndConfigureEditPanelForAdd();
        membreController.setEditPanel((MembreEdit) membrePage);

        // Page "Salles"
        JPanel sallePage = salleController.createAndConfigureEditPanelForAdd();
        salleController.setEditPanel((SalleEdit) sallePage);

        // Page "Tickets"
        JPanel ticketPage = ticketController.createAndConfigureEditPanelForAdd();
        ticketController.setEditPanel((TicketEdit) ticketPage);

        // Page "Equipements"
        JPanel equipementPage = equipementController.createAndConfigureEditPanelForAdd();
        equipementController.setEditPanel((EquipementEdit) equipementPage);

        // Page "Seances"
        JPanel seancePage = seanceController.createAndConfigureEditPanelForAdd();
        seanceController.setEditPanel((SeanceEdit) seancePage);

        // Page "Abonnements"
        JPanel abonnementPage = abonnementController.createAndConfigureEditPanelForAdd();
        abonnementController.setEditPanel((AbonnementEdit) abonnementPage);

        // Page "Paiements"
        JPanel paiementPage = paiementController.createAndConfigureEditPanelForAdd();
        paiementController.setEditPanel((PaiementEdit) paiementPage);


        // north section
        north.setLayout(new BorderLayout()); // Utiliser BorderLayout pour tableauDeBordLabel et user
        JPanel northContent = new JPanel(new FlowLayout(FlowLayout.LEFT));
        northContent.setBackground(new Color(32, 64, 128));
        northContent.add(tableauDeBordLabel);
        north.add(northContent, BorderLayout.WEST);
        north.add(spacePanel, BorderLayout.CENTER); // spacePanel au centre
        JPanel northRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        northRight.setBackground(new Color(32, 64, 128));
        northRight.add(user);
        north.add(northRight, BorderLayout.EAST);


        // west section - Refonte
        west.setLayout(new GridBagLayout()); // Utilise GridBagLayout pour plus de contrôle
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(5, 0, 5, 0); // Padding vertical pour chaque composant
        gbc.fill = GridBagConstraints.HORIZONTAL; // Remplit horizontalement

        // Ajouter le titre "Menu"
        gbc.gridy = 0;
        gbc.weighty = 0; // Le label ne prend pas d'espace supplémentaire
        gbc.insets = new Insets(10, 0, 20, 0); // Plus de marge pour le titre
        //west.add(menuTitleLabel, gbc);
        gbc.insets = new Insets(5, 0, 5, 0); // Réinitialise l'insets pour les boutons

        // Ajouter les boutons avec la nouvelle gestion d'état
        addMenuButton(btn0, "Page0_TableauDeBord", gbc, 1);
        addMenuButton(btn1, "Page1_TypeAbonnement", gbc, 2);
        addMenuButton(btn2, "Page2_Clients", gbc, 3);
        addMenuButton(btn3, "Page3_MoyenPaiements", gbc, 4);
        addMenuButton(btn4, "Page4_DemandeInscription", gbc, 5);
        addMenuButton(btn5, "Page5_Membres", gbc, 6);
        addMenuButton(btn6, "Page6_Salles", gbc, 7);
        addMenuButton(btn7, "Page7_Tickets", gbc, 8);
        addMenuButton(btn8, "Page8_Equipements", gbc, 9);
        addMenuButton(btn9, "Page9_Seances", gbc, 10);
        addMenuButton(btn10, "Page10_Abonnements", gbc, 11);
        addMenuButton(btn11, "Page11_Paiements", gbc, 12);

        // Ajouter un "glue" en bas pour pousser les boutons vers le haut
        gbc.gridy = 13;
        gbc.weighty = 1.0; // Prend tout l'espace vertical restant
        west.add(Box.createVerticalGlue(), gbc);

        // south section
        south.setLayout(flowCenterLayout);
        south.add(copyright);

        // center section
        centerPanel = new JPanel();
        centerPanel.setLayout(cardLayout);
        centerPanel.add(page0, "Page0_TableauDeBord");
        centerPanel.add(typeAbonnementPage, "Page1_TypeAbonnement");
        centerPanel.add(clientPage, "Page2_Clients");
        centerPanel.add(moyenDePaiementPage, "Page3_MoyenPaiements");
        centerPanel.add(demandeInscriptionPage, "Page4_DemandeInscription");
        centerPanel.add(membrePage, "Page5_Membres");
        centerPanel.add(sallePage, "Page6_Salles");
        centerPanel.add(ticketPage, "Page7_Tickets");
        centerPanel.add(equipementPage, "Page8_Equipements");
        centerPanel.add(seancePage, "Page9_Seances");
        centerPanel.add(abonnementPage, "Page10_Abonnements");
        centerPanel.add(paiementPage, "Page11_Paiements");

        c.setLayout(borderLayout);
        c.add(west, BorderLayout.WEST);
        c.add(north, BorderLayout.NORTH);
        c.add(south, BorderLayout.SOUTH);
        c.add(centerPanel, BorderLayout.CENTER);

        // Sélectionner le premier bouton par défaut (Tableau de bord)
        if (!menuButtons.isEmpty()) {
            menuButtons.get(0).doClick(); // Simule un clic pour définir l'état initial et afficher la page
        }
    }

    /**
     * Méthode utilitaire pour ajouter un bouton au panneau de menu et lui attacher un listener.
     * Gère la sélection visuelle des boutons.
     * @param button Le bouton MyButton à ajouter.
     * @param cardName Le nom de la carte associée à ce bouton dans le CardLayout.
     * @param gbc Les contraintes GridBagConstraints pour le positionnement.
     * @param gridY La position verticale dans le GridBagLayout.
     */
    private void addMenuButton(MyButton button, String cardName, GridBagConstraints gbc, int gridY) {
        gbc.gridy = gridY;
        gbc.weighty = 0; // Les boutons ne prennent pas d'espace vertical supplémentaire
        west.add(button, gbc);

        button.addActionListener(e -> {
            cardLayout.show(centerPanel, cardName);
            // Mettre à jour l'état visuel des boutons
            if (currentSelectedButton != null) {
                currentSelectedButton.setSelected(false);
            }
            button.setSelected(true);
            currentSelectedButton = button;
        });
    }

    private JPanel createPage(Color backgroundColor, String title) {
        JPanel jp = new JPanel();
        jp.setBackground(backgroundColor);
        jp.add(new MyLabel(title));
        return jp;
    }

    public void showPanel(String panelName) {
        cardLayout.show(centerPanel, panelName);
    }
}
