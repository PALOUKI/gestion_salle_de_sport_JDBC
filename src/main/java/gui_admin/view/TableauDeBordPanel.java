package gui_admin.view;

import entite.Client;
import entite.Paiement;
import gui_admin.gui_util.CustomTablePanel;
import service.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static gui_admin.controller.ClientController.DATE_FORMATTER;
import static gui_admin.controller.PaiementController.DATE_TIME_FORMATTER;

public class TableauDeBordPanel extends JPanel {

    private MembreService membreService = new MembreService();
    private PaiementService paiementService = new PaiementService();
    private ClientService clientService = new ClientService();
    private AbonnementService abonnementService = new AbonnementService();
    private DemandeInscriptionService demandeInscriptionService = new DemandeInscriptionService();

    private JLabel titre;
    private String fullTitleText = "Bienvenue sur le tableau de bord !";
    private int charIndex = 0;
    private Timer typingTimer;

    public TableauDeBordPanel() throws SQLException {
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(245, 245, 245));

        // Titre - Initialisé vide pour l'effet de frappe
        titre = new JLabel();
        titre.setFont(new Font("Goldman", Font.BOLD, 30));
        titre.setForeground(new Color(32, 64, 128));
        titre.setBorder(new EmptyBorder(25, 30, 15, 0));
        this.add(titre, BorderLayout.NORTH);

        // Configuration du Timer pour l'effet de frappe (Délai ajusté à 120 ms)
        typingTimer = new Timer(160, new ActionListener() { // <<< Changement ici : 120 ms
            @Override
            public void actionPerformed(ActionEvent e) {
                if (charIndex < fullTitleText.length()) {
                    titre.setText(fullTitleText.substring(0, charIndex + 1));
                    charIndex++;
                } else {
                    typingTimer.stop();
                }
            }
        });
        typingTimer.start();


        // Centre principal vertical
        JPanel centre = new JPanel();
        centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));
        centre.setBorder(new EmptyBorder(10, 30, 30, 30));
        centre.setBackground(new Color(245, 245, 245));
        this.add(centre, BorderLayout.CENTER);

        // Statistiques (ligne de 3 panels)
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 25, 0));
        statsPanel.setBackground(new Color(245, 245, 245));

        // Style de base pour les panneaux de statistiques
        LineBorder cardBorder = new LineBorder(new Color(200, 200, 220), 1, true);
        EmptyBorder cardPadding = new EmptyBorder(20, 20, 20, 20);

        // --- Panel Membres inscrits ---
        JPanel membresPanel = new JPanel(new BorderLayout(15, 5));
        membresPanel.setBackground(new Color(235, 240, 255));
        membresPanel.setBorder(BorderFactory.createCompoundBorder(cardBorder, cardPadding));

        JLabel iconMembre = new JLabel("👤");
        iconMembre.setFont(new Font("Goldman", Font.PLAIN, 48));
        iconMembre.setForeground(new Color(0, 100, 0));

        int nombreMembres = membreService.listerTous().size();
        JLabel valeurMembre = new JLabel(String.valueOf(nombreMembres));
        valeurMembre.setFont(new Font("Goldman", Font.BOLD, 36));
        valeurMembre.setForeground(new Color(50, 50, 50));
        valeurMembre.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel labelMembre = new JLabel("Membres inscrits");
        labelMembre.setFont(new Font("Goldman", Font.PLAIN, 15));
        labelMembre.setForeground(new Color(80, 80, 80));
        labelMembre.setHorizontalAlignment(SwingConstants.CENTER);

        membresPanel.add(iconMembre, BorderLayout.WEST);
        membresPanel.add(valeurMembre, BorderLayout.CENTER);
        membresPanel.add(labelMembre, BorderLayout.SOUTH);
        statsPanel.add(membresPanel);

        // --- Panel Abonnements expirés ---
        JPanel abonnementsPanel = new JPanel(new BorderLayout(15, 5));
        abonnementsPanel.setBackground(new Color(255, 245, 230));
        abonnementsPanel.setBorder(BorderFactory.createCompoundBorder(cardBorder, cardPadding));

        JLabel iconAbonnement = new JLabel("🗓️");
        iconAbonnement.setFont(new Font("Goldman", Font.PLAIN, 48));
        iconAbonnement.setForeground(new Color(200, 100, 0));

        int nombreAbonnements = abonnementService.listerTous().size();
        JLabel valeurAbonnement = new JLabel(String.valueOf(nombreAbonnements));
        valeurAbonnement.setFont(new Font("Goldman", Font.BOLD, 36));
        valeurAbonnement.setForeground(new Color(50, 50, 50));
        valeurAbonnement.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel labelAbonnement = new JLabel("Abonnements");
        labelAbonnement.setFont(new Font("Goldman", Font.PLAIN, 15));
        labelAbonnement.setForeground(new Color(80, 80, 80));
        labelAbonnement.setHorizontalAlignment(SwingConstants.CENTER);

        abonnementsPanel.add(iconAbonnement, BorderLayout.WEST);
        abonnementsPanel.add(valeurAbonnement, BorderLayout.CENTER);
        abonnementsPanel.add(labelAbonnement, BorderLayout.SOUTH);
        statsPanel.add(abonnementsPanel);

        // --- Panel Nouvelles inscriptions avec bouton ---
        JPanel inscriptionsPanel = new JPanel(new BorderLayout(15, 5));
        inscriptionsPanel.setBackground(new Color(230, 255, 235));
        inscriptionsPanel.setBorder(BorderFactory.createCompoundBorder(cardBorder, cardPadding));

        JLabel iconInscription = new JLabel("✉");
        iconInscription.setFont(new Font("Goldman", Font.PLAIN, 48));
        iconInscription.setForeground(new Color(0, 150, 0));

        int nombreInscriptions = demandeInscriptionService.listerTous().size();
        JLabel valeurInscription = new JLabel(String.valueOf(nombreInscriptions));
        valeurInscription.setFont(new Font("Goldman", Font.BOLD, 36));
        valeurInscription.setForeground(new Color(50, 50, 50));
        valeurInscription.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel labelInscription = new JLabel("Demandes Inscriptions");
        labelInscription.setFont(new Font("Goldman", Font.PLAIN, 15));
        labelInscription.setForeground(new Color(80, 80, 80));
        labelInscription.setHorizontalAlignment(SwingConstants.CENTER);

        JButton voirBtn = new JButton("Voir les inscriptions");
        voirBtn.setBackground(new Color(0, 55, 144));
        voirBtn.setForeground(Color.WHITE);
        voirBtn.setFont(new Font("Goldman", Font.BOLD, 14));
        voirBtn.setFocusPainted(false);
        voirBtn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        voirBtn.putClientProperty("JButton.buttonType", "roundRect");

        JPanel inscriptionBottom = new JPanel(new BorderLayout());
        inscriptionBottom.setBackground(inscriptionsPanel.getBackground());
        inscriptionBottom.add(labelInscription, BorderLayout.NORTH);
        inscriptionBottom.add(valeurInscription, BorderLayout.CENTER);

        inscriptionsPanel.add(iconInscription, BorderLayout.WEST);
        inscriptionsPanel.add(inscriptionBottom, BorderLayout.CENTER);
        inscriptionsPanel.add(voirBtn, BorderLayout.SOUTH);
        statsPanel.add(inscriptionsPanel);

        centre.add(statsPanel);
        centre.add(Box.createVerticalStrut(40));

        // --- Historique de paiement ---
        JPanel historiquePanel = new JPanel(new BorderLayout());
        historiquePanel.setBackground(Color.WHITE);
        historiquePanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 220), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel histoLabel = new JLabel("Historique des paiements récents");
        histoLabel.setFont(new Font("Goldman", Font.BOLD, 20));
        histoLabel.setForeground(new Color(32, 64, 128));
        histoLabel.setBorder(new EmptyBorder(0, 0, 15, 0));

        java.util.List<Paiement> existingPaiements = paiementService.listerTous();
        List<Paiement> recentPaiements = existingPaiements.size() > 5 ?
                existingPaiements.subList(existingPaiements.size() - 5, existingPaiements.size()) :
                existingPaiements;
        List<String> columnsPaiement = Arrays.asList("ID", "Montant", "Date Paiement", "Moyen", "Abonnement ID");
        List<List<Object>> PtableData = convertPaiementsToTableData(recentPaiements);

        CustomTablePanel recentPaiementTablePanel = new CustomTablePanel(PtableData, columnsPaiement);

        historiquePanel.add(histoLabel, BorderLayout.NORTH);
        historiquePanel.add(recentPaiementTablePanel, BorderLayout.CENTER);
        centre.add(historiquePanel);

        centre.add(Box.createVerticalStrut(40));

        // --- Clients récents ---
        JPanel recentClientPanel = new JPanel(new BorderLayout());
        recentClientPanel.setBackground(Color.WHITE);
        recentClientPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 220), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel recentClientLabel = new JLabel("Nouveaux clients");
        recentClientLabel.setFont(new Font("Goldman", Font.BOLD, 20));
        recentClientLabel.setForeground(new Color(32, 64, 128));
        recentClientLabel.setBorder(new EmptyBorder(0, 0, 15, 0));

        java.util.List<Client> existingClients = clientService.listerTous();
        List<Client> latestClients = existingClients.size() > 5 ?
                existingClients.subList(existingClients.size() - 5, existingClients.size()) :
                existingClients;
        java.util.List<String> columnNamesClients = Arrays.asList("ID", "Nom", "Prénom", "Date Naissance", "Email");
        java.util.List<java.util.List<Object>> tableDataClients = convertClientsToTableData(latestClients);

        CustomTablePanel recentClientTablePanel = new CustomTablePanel(tableDataClients, columnNamesClients);

        recentClientPanel.add(recentClientLabel, BorderLayout.NORTH);
        recentClientPanel.add(recentClientTablePanel, BorderLayout.CENTER);

        centre.add(recentClientPanel);

    }

    private java.util.List<java.util.List<Object>> convertClientsToTableData(java.util.List<Client> clients) {
        java.util.List<java.util.List<Object>> data = new ArrayList<>();
        for (Client client : clients) {
            data.add(Arrays.asList(
                    client.getId(),
                    client.getNom(),
                    client.getPrenom(),
                    client.getDateNaissance() != null ? client.getDateNaissance().format(DATE_FORMATTER) : "N/A",
                    client.getEmail()
            ));
        }
        return data;
    }

    private java.util.List<java.util.List<Object>> convertPaiementsToTableData(java.util.List<Paiement> paiements) {
        List<List<Object>> data = new ArrayList<>();
        for (Paiement p : paiements) {
            data.add(Arrays.asList(
                    p.getId(),
                    p.getMontant(),
                    p.getDateDePaiement() != null ? p.getDateDePaiement().format(DATE_TIME_FORMATTER) : "N/A",
                    p.getMoyenDePaiement() != null ? p.getMoyenDePaiement().getLibelle() : "N/A",
                    p.getAbonnement() != null ? p.getAbonnement().getId() : "N/A"
            ));
        }
        return data;
    }
}