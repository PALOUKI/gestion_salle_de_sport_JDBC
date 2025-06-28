package org.example;

import entite.*;
import service.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    private static TypeAbonnementService typeAbonnementService;
    private static SalleService salleService;
    private static TicketService ticketService;
    private static PaiementService paiementService;
    private static MembreService membreService;
    private static EquipementService equipementService;
    private static ClientService clientService;
    private static DemandeInscriptionService demandeInscriptionService;
    private static AbonnementService abonnementService;
    private static SeanceService seanceService;
    private static MoyenDePaiementService moyenDePaiementService;

    static {
        typeAbonnementService = new TypeAbonnementService();
        salleService = new SalleService();
        ticketService = new TicketService();
        paiementService = new PaiementService();
        membreService = new MembreService();
        equipementService = new EquipementService();
        clientService = new ClientService();
        demandeInscriptionService = new DemandeInscriptionService();
        abonnementService = new AbonnementService();
        seanceService = new SeanceService();
        moyenDePaiementService = new MoyenDePaiementService();
    }

    public static void main(String[] args) throws SQLException {
        // --- ÉTAPE 0 : Nettoyage de la base de données (UTILISEZ AVEC PRUDENCE) ---
        // Cette ligne videra toutes les tables pour garantir un état propre à chaque exécution.
        // Commentez-la si vous ne souhaitez pas vider la base de données à chaque fois.
        System.out.println("--- Début du nettoyage de la base de données ---");
        System.out.println("--- Fin du nettoyage de la base de données ---");
        System.out.println("\n");

        // --- ÉTAPE 1 : Ajout des entités parentes et dépendantes ---

        System.out.println("--- Démarrage des ajouts initiaux ---");

        // Ajout du type d'abonnement
        TypeAbonnement typeAbonnement = new TypeAbonnement("HEBDO", "Abonnement hebdomadaire", 2000);
        typeAbonnementService.ajouter(typeAbonnement);
        System.out.println("TypeAbonnement ajouté : " + typeAbonnement.getCode());

        // Ajout d'un client
        Client client = new Client("Jefto", "2.0", LocalDateTime.of(2024, 6, 5, 10, 30).toLocalDate().atStartOfDay(), "jefto@gmail.com");
        clientService.ajouter(client);
        System.out.println("Client ajouté : " + client.getNom() + " " + client.getPrenom() + " (ID: " + client.getId() + ")");


        // Ajout d'une salle
        Salle salle = new Salle("Salle 1", "Salle de musculation");
        salleService.ajouter(salle);
        System.out.println("Salle ajoutée : " + salle.getLibelle() + " (ID: " + salle.getId() + ")");


        // Ajout d'un moyen de paiement
        MoyenDePaiement moyenDePaiement = new MoyenDePaiement("001L", "Premier Paiement");
        moyenDePaiementService.ajouter(moyenDePaiement);
        System.out.println("Moyen de Paiement ajouté : " + moyenDePaiement.getLibelle() + " (Code: " + moyenDePaiement.getCode() + ")");


        // Ajout d'une demande d'inscription (dépend de Client)
        DemandeInscription demandeInscription = new DemandeInscription(LocalDateTime.of(2024, 6, 5, 10, 30), LocalDateTime.of(2024, 6, 10, 16, 0), client);
        demandeInscriptionService.ajouter(demandeInscription);
        System.out.println("Demande d'inscription ajoutée pour client ID: " + demandeInscription.getClient().getId() + " (ID: " + demandeInscription.getId() + ")");


        // Ajout d'un membre (dépend de Client)
        Membre membre = new Membre(LocalDateTime.now(), client);
        membreService.ajouter(membre);
        System.out.println("Membre ajouté pour client ID: " + membre.getClient().getId() + " (ID: " + membre.getId() + ")");


        // Ajout d'un équipement (dépend de Salle)
        Equipement equipement = new Equipement("Alter", "Bout de poids pour chaques mains", salle);
        equipementService.ajouter(equipement);
        System.out.println("Équipement ajouté : " + equipement.getLibelle() + " (ID: " + equipement.getId() + ")");


        // Ajout d'une séance (dépend de Salle)
        Seance seance = new Seance(LocalDateTime.of(2024, 6, 5, 10, 30), LocalDateTime.of(2024, 6, 10, 16, 0), salle);
        seanceService.ajouter(seance);
        System.out.println("Séance ajoutée pour salle ID: " + seance.getSalle().getId() + " (ID: " + seance.getId() + ")");


        // Ajout d'un ticket (dépend de Client)
        Ticket ticket = new Ticket(1, 5000, client);
        ticketService.ajouter(ticket);
        System.out.println("Ticket ajouté pour client ID: " + ticket.getClient().getId() + " (ID: " + ticket.getId() + ")");


        // Ajout d'un abonnement (dépend de Membre et TypeAbonnement)
        Abonnement abonnement = new Abonnement(LocalDateTime.of(2024, 6, 5, 10, 30), LocalDateTime.of(2024, 7, 5, 10, 30), membre, typeAbonnement);
        abonnementService.ajouter(abonnement);
        System.out.println("Abonnement ajouté pour membre ID: " + abonnement.getMembre().getId() + " (ID: " + abonnement.getId() + ")");


        // Ajout d'un paiement (dépend de MoyenDePaiement et Abonnement)
        Paiement paiement = new Paiement(LocalDateTime.now(), 100000, moyenDePaiement, abonnement);
        paiementService.ajouter(paiement);
        System.out.println("Paiement ajouté pour abonnement ID: " + paiement.getAbonnement().getId() + " (ID: " + paiement.getId() + ")");


        System.out.println("--- Fin des ajouts initiaux ---");
        System.out.println("\n");


        // --- ÉTAPE 2 : DÉMONSTRATION DES OPÉRATIONS CRUD POUR CHAQUE CLASSE ---

        // ===============================================
        // DÉMONSTRATION CRUD POUR ABONNEMENT
        // ===============================================
        System.out.println("--- Démonstration des opérations CRUD sur l'Abonnement ---");

        // Lister tous les abonnements après l'ajout initial
        System.out.println("\n--- Liste des abonnements après l'ajout initial ---");
        List<Abonnement> abonnementsApresAjout = abonnementService.listerTous();
        if (abonnementsApresAjout.isEmpty()) {
            System.out.println("Aucun abonnement trouvé.");
        } else {
            abonnementsApresAjout.forEach(a -> System.out.println("ID: " + a.getId() + ", Début: " + a.getDateDebut() + ", Fin: " + a.getDateFin() + ", Membre ID: " + (a.getMembre() != null ? a.getMembre().getId() : "N/A") + ", Type: " + (a.getTypeAbonnement() != null ? a.getTypeAbonnement().getCode() : "N/A")));
        }

        // Modification d'un abonnement
        if (abonnement.getId() != 0) {
            System.out.println("\n--- Modification de l'abonnement ---");
            Abonnement abonnementAModifier = abonnementService.trouver(abonnement.getId());

            if (abonnementAModifier != null) {
                System.out.println("Abonnement avant modification: ID=" + abonnementAModifier.getId() + ", Fin=" + abonnementAModifier.getDateFin());
                abonnementAModifier.setDateFin(abonnementAModifier.getDateFin().plusMonths(1));
                abonnementService.modifier(abonnementAModifier);
                System.out.println("Abonnement modifié avec succès. Nouvelle date de fin: " + abonnementAModifier.getDateFin());

                Abonnement abonnementVerifie = abonnementService.trouver(abonnement.getId());
                if (abonnementVerifie != null) {
                    System.out.println("Abonnement après re-recherche: ID=" + abonnementVerifie.getId() + ", Fin=" + abonnementVerifie.getDateFin());
                }
            } else {
                System.out.println("L'abonnement avec l'ID " + abonnement.getId() + " n'a pas été trouvé pour modification.");
            }
        } else {
            System.out.println("Impossible de modifier : l'abonnement n'a pas d'ID valide après l'ajout.");
        }

        // Lister tous les abonnements après la modification
        System.out.println("\n--- Liste des abonnements après modification ---");
        List<Abonnement> abonnementsApresModif = abonnementService.listerTous();
        if (abonnementsApresModif.isEmpty()) {
            System.out.println("Aucun abonnement trouvé.");
        } else {
            abonnementsApresModif.forEach(a -> System.out.println("ID: " + a.getId() + ", Début: " + a.getDateDebut() + ", Fin: " + a.getDateFin() + ", Membre ID: " + (a.getMembre() != null ? a.getMembre().getId() : "N/A") + ", Type: " + (a.getTypeAbonnement() != null ? a.getTypeAbonnement().getCode() : "N/A")));
        }

        // Suppression d'un abonnement
        if (abonnement.getId() != 0) {
            System.out.println("\n--- Suppression de l'abonnement ---");
            abonnementService.supprimer(abonnement.getId());
            System.out.println("Tentative de suppression de l'abonnement avec ID: " + abonnement.getId());

            Abonnement abonnementSupprime = abonnementService.trouver(abonnement.getId());
            if (abonnementSupprime == null) {
                System.out.println("Abonnement avec ID: " + abonnement.getId() + " a été supprimé avec succès (non trouvé après suppression).");
            } else {
                System.out.println("Abonnement avec ID: " + abonnement.getId() + " n'a PAS été supprimé. Il est toujours trouvé.");
            }
        } else {
            System.out.println("Impossible de supprimer : l'abonnement n'a pas d'ID valide.");
        }

        // ===============================================
        // DÉMONSTRATION CRUD POUR CLIENT
        // ===============================================
        System.out.println("\n--- Démonstration des opérations CRUD sur le Client ---");

        // Lister tous les clients
        System.out.println("\n--- Liste des clients ---");
        List<Client> clients = clientService.listerTous();
        if (clients.isEmpty()) {
            System.out.println("Aucun client trouvé.");
        } else {
            clients.forEach(c -> System.out.println("ID: " + c.getId() + ", Nom: " + c.getNom() + ", Prénom: " + c.getPrenom() + ", Email: " + c.getEmail()));
        }

        // Modification d'un client
        if (client.getId() != 0) {
            System.out.println("\n--- Modification du client ---");
            Client clientAModifier = clientService.trouver(client.getId());
            if (clientAModifier != null) {
                System.out.println("Client avant modification: ID=" + clientAModifier.getId() + ", Email=" + clientAModifier.getEmail());
                clientAModifier.setEmail("jefto.nouveau@gmail.com");
                clientService.modifier(clientAModifier);
                System.out.println("Client modifié avec succès. Nouvel email: " + clientAModifier.getEmail());
                Client clientVerifie = clientService.trouver(client.getId());
                System.out.println("Client après re-recherche: ID=" + clientVerifie.getId() + ", Email=" + clientVerifie.getEmail());
            } else {
                System.out.println("Le client avec l'ID " + client.getId() + " n'a pas été trouvé pour modification.");
            }
        }

        // Suppression d'un client (Attention: risque de contraintes de clé étrangère si membre/demande/ticket existe)
        // Pour éviter les erreurs, nous allons supprimer les entités dépendantes avant le client.
        // C'est pourquoi l'ordre de suppression est important !
        System.out.println("\n--- Tentative de suppression du client ---");
        // Supprimer d'abord les entités dépendantes du client (ticket, demandeInscription, membre)
        if(ticket.getId() != 0) ticketService.supprimer(ticket.getId());
        if(demandeInscription.getId() != 0) demandeInscriptionService.supprimer(demandeInscription.getId());
        if(membre.getId() != 0) membreService.supprimer(membre.getId()); // Membre peut avoir des abonnements, donc il faut supprimer l'abonnement avant le membre.
        // Si l'abonnement (abonnement) dépend du membre (membre), il faut le supprimer AVANT le membre
        // Dans ce scénario, nous avons déjà supprimé l'abonnement dans sa section de test CRUD.
        // Sinon, ajoutez : if(abonnement.getId() != 0) abonnementService.supprimer(abonnement);

        if (client.getId() != 0) {
            clientService.supprimer(client.getId());
            Client clientSupprime = clientService.trouver(client.getId());
            if (clientSupprime == null) {
                System.out.println("Client avec ID: " + client.getId() + " a été supprimé avec succès.");
            } else {
                System.out.println("Client avec ID: " + client.getId() + " n'a PAS été supprimé.");
            }
        }

        // ===============================================
        // DÉMONSTRATION CRUD POUR SALLE
        // ===============================================
        System.out.println("\n--- Démonstration des opérations CRUD sur la Salle ---");

        // Lister toutes les salles
        System.out.println("\n--- Liste des salles ---");
        List<Salle> salles = salleService.listerTous();
        salles.forEach(s -> System.out.println("ID: " + s.getId() + ", Libelle: " + s.getLibelle()));

        // Modification d'une salle
        if (salle.getId() != 0) {
            System.out.println("\n--- Modification de la salle ---");
            Salle salleAModifier = salleService.trouver(salle.getId());
            if (salleAModifier != null) {
                System.out.println("Salle avant modification: ID=" + salleAModifier.getId() + ", Description=" + salleAModifier.getDescription());
                salleAModifier.setDescription("Salle de cardio et musculation");
                salleService.modifier(salleAModifier);
                System.out.println("Salle modifiée avec succès. Nouvelle description: " + salleAModifier.getDescription());
            }
        }

        // Suppression d'une salle (Attention: équipement et séance dépendent de salle)
        System.out.println("\n--- Tentative de suppression de la salle ---");
        if(equipement.getId() != 0) equipementService.supprimer(equipement.getId()); // Supprimer l'équipement avant la salle
        if(seance.getId() != 0) seanceService.supprimer(seance.getId()); // Supprimer la séance avant la salle

        if (salle.getId() != 0) {
            salleService.supprimer(salle.getId());
            Salle salleSupprimee = salleService.trouver(salle.getId());
            if (salleSupprimee == null) {
                System.out.println("Salle avec ID: " + salle.getId() + " a été supprimée avec succès.");
            } else {
                System.out.println("Salle avec ID: " + salle.getId() + " n'a PAS été supprimée.");
            }
        }

        // ===============================================
        // DÉMONSTRATION CRUD POUR MOYEN DE PAIEMENT
        // ===============================================
        System.out.println("\n--- Démonstration des opérations CRUD sur le MoyenDePaiement ---");

        // Lister tous les moyens de paiement
        System.out.println("\n--- Liste des moyens de paiement ---");
        List<MoyenDePaiement> moyens = moyenDePaiementService.listerTous();
        moyens.forEach(m -> System.out.println("Code: " + m.getCode() + ", Libelle: " + m.getLibelle()));

        // Modification d'un moyen de paiement
        if (moyenDePaiement.getCode() != null && !moyenDePaiement.getCode().isEmpty()) {
            System.out.println("\n--- Modification du moyen de paiement ---");
            MoyenDePaiement mdpAModifier = moyenDePaiementService.trouver(moyenDePaiement.getCode());
            if (mdpAModifier != null) {
                System.out.println("Moyen de paiement avant modification: Code=" + mdpAModifier.getCode() + ", Libelle=" + mdpAModifier.getLibelle());
                mdpAModifier.setLibelle("Paiement via Carte Bancaire");
                moyenDePaiementService.modifier(mdpAModifier);
                System.out.println("Moyen de paiement modifié avec succès. Nouveau libellé: " + mdpAModifier.getLibelle());
            }
        }

        // Suppression d'un moyen de paiement (attention: le paiement dépend de lui)
        System.out.println("\n--- Tentative de suppression du moyen de paiement ---");
        // Le paiement a déjà été supprimé dans sa section de test CRUD.
        if (moyenDePaiement.getCode() != null && !moyenDePaiement.getCode().isEmpty()) {
            moyenDePaiementService.supprimer(moyenDePaiement.getCode());
            MoyenDePaiement mdpSupprime = moyenDePaiementService.trouver(moyenDePaiement.getCode());
            if (mdpSupprime == null) {
                System.out.println("Moyen de paiement avec code: " + moyenDePaiement.getCode() + " a été supprimé avec succès.");
            } else {
                System.out.println("Moyen de paiement avec code: " + moyenDePaiement.getCode() + " n'a PAS été supprimé.");
            }
        }

        // ===============================================
        // DÉMONSTRATION CRUD POUR DEMANDE_INSCRIPTION
        // ===============================================
        System.out.println("\n--- Démonstration des opérations CRUD sur DemandeInscription ---");

        // Lister toutes les demandes d'inscription
        System.out.println("\n--- Liste des demandes d'inscription ---");
        List<DemandeInscription> demandes = demandeInscriptionService.listerTous();
        demandes.forEach(d -> System.out.println("ID: " + d.getId() + ", Client ID: " + d.getClient().getId() + ", Date demande: " + d.getDateDeDemande()));

        // Modification d'une demande d'inscription
        if (demandeInscription.getId() != 0) {
            System.out.println("\n--- Modification de la demande d'inscription ---");
            DemandeInscription diAModifier = demandeInscriptionService.trouver(demandeInscription.getId());
            if (diAModifier != null) {
                System.out.println("Demande avant modification: ID=" + diAModifier.getId() + ", Date traitement=" + diAModifier.getDateDeTraitement());
                diAModifier.setDateDeTraitement(LocalDateTime.now().plusDays(1)); // Met à jour la date de traitement
                demandeInscriptionService.modifier(diAModifier);
                System.out.println("Demande modifiée avec succès. Nouvelle date de traitement: " + diAModifier.getDateDeTraitement());
            }
        }

        // Suppression d'une demande d'inscription (déjà fait dans la section client pour gérer la FK)
        System.out.println("\n--- La suppression de la demande d'inscription a été gérée avant la suppression du client. ---");


        // ===============================================
        // DÉMONSTRATION CRUD POUR MEMBRE
        // ===============================================
        System.out.println("\n--- Démonstration des opérations CRUD sur Membre ---");

        // Lister tous les membres
        System.out.println("\n--- Liste des membres ---");
        List<Membre> membres = membreService.listerTous();
        membres.forEach(m -> System.out.println("ID: " + m.getId() + ", Client ID: " + m.getClient().getId() + ", Date inscription: " + m.getDateInscription()));

        // Modification d'un membre
        if (membre.getId() != 0) {
            System.out.println("\n--- Modification du membre ---");
            Membre membreAModifier = membreService.trouver(membre.getId());
            if (membreAModifier != null) {
                System.out.println("Membre avant modification: ID=" + membreAModifier.getId() + ", Date inscription=" + membreAModifier.getDateInscription());
                membreAModifier.setDateInscription(LocalDateTime.now().minusDays(5)); // Change la date d'inscription
                membreService.modifier(membreAModifier);
                System.out.println("Membre modifié avec succès. Nouvelle date d'inscription: " + membreAModifier.getDateInscription());
            }
        }

        // Suppression d'un membre (déjà fait dans la section client pour gérer la FK)
        System.out.println("\n--- La suppression du membre a été gérée avant la suppression du client. ---");


        // ===============================================
        // DÉMONSTRATION CRUD POUR ÉQUIPEMENT
        // ===============================================
        System.out.println("\n--- Démonstration des opérations CRUD sur Equipement ---");

        // Lister tous les équipements
        System.out.println("\n--- Liste des équipements ---");
        List<Equipement> equipements = equipementService.listerTous();
        equipements.forEach(e -> System.out.println("ID: " + e.getId() + ", Libelle: " + e.getLibelle() + ", Salle ID: " + e.getSalle().getId()));

        // Modification d'un équipement
        if (equipement.getId() != 0) {
            System.out.println("\n--- Modification de l'équipement ---");
            Equipement equipementAModifier = equipementService.trouver(equipement.getId());
            if (equipementAModifier != null) {
                System.out.println("Équipement avant modification: ID=" + equipementAModifier.getId() + ", Description=" + equipementAModifier.getDescription());
                equipementAModifier.setDescription("Poids libres et haltères variés");
                equipementService.modifier(equipementAModifier);
                System.out.println("Équipement modifié avec succès. Nouvelle description: " + equipementAModifier.getDescription());
            }
        }

        // Suppression d'un équipement (déjà fait dans la section salle pour gérer la FK)
        System.out.println("\n--- La suppression de l'équipement a été gérée avant la suppression de la salle. ---");


        // ===============================================
        // DÉMONSTRATION CRUD POUR SÉANCE
        // ===============================================
        System.out.println("\n--- Démonstration des opérations CRUD sur Séance ---");

        // Lister toutes les séances
        System.out.println("\n--- Liste des séances ---");
        List<Seance> seances = seanceService.listerTous();
        seances.forEach(s -> System.out.println("ID: " + s.getId() + ", Début: " + s.getDateDebut() + ", Fin: " + s.getDateFin() + ", Salle ID: " + s.getSalle().getId()));

        // Modification d'une séance
        if (seance.getId() != 0) {
            System.out.println("\n--- Modification de la séance ---");
            Seance seanceAModifier = seanceService.trouver(seance.getId());
            if (seanceAModifier != null) {
                System.out.println("Séance avant modification: ID=" + seanceAModifier.getId() + ", Date fin=" + seanceAModifier.getDateFin());
                seanceAModifier.setDateFin(seanceAModifier.getDateFin().plusHours(2)); // Prolonge la séance
                seanceService.modifier(seanceAModifier);
                System.out.println("Séance modifiée avec succès. Nouvelle date de fin: " + seanceAModifier.getDateFin());
            }
        }

        // Suppression d'une séance (déjà fait dans la section salle pour gérer la FK)
        System.out.println("\n--- La suppression de la séance a été gérée avant la suppression de la salle. ---");


        // ===============================================
        // DÉMONSTRATION CRUD POUR TICKET
        // ===============================================
        System.out.println("\n--- Démonstration des opérations CRUD sur Ticket ---");

        // Lister tous les tickets
        System.out.println("\n--- Liste des tickets ---");
        List<Ticket> tickets = ticketService.listerTous();
        tickets.forEach(t -> System.out.println("ID: " + t.getId() + ", Nb séances: " + t.getNombreDeSeance() + ", Client ID: " + t.getClient().getId()));

        // Modification d'un ticket
        if (ticket.getId() != 0) {
            System.out.println("\n--- Modification du ticket ---");
            Ticket ticketAModifier = ticketService.trouver(ticket.getId());
            if (ticketAModifier != null) {
                System.out.println("Ticket avant modification: ID=" + ticketAModifier.getId() + ", Montant=" + ticketAModifier.getMontant());
                ticketAModifier.setMontant(6000); // Change le montant
                ticketService.modifier(ticketAModifier);
                System.out.println("Ticket modifié avec succès. Nouveau montant: " + ticketAModifier.getMontant());
            }
        }

        // Suppression d'un ticket (déjà fait dans la section client pour gérer la FK)
        System.out.println("\n--- La suppression du ticket a été gérée avant la suppression du client. ---");


        // ===============================================
        // DÉMONSTRATION CRUD POUR PAIEMENT
        // ===============================================
        System.out.println("\n--- Démonstration des opérations CRUD sur Paiement ---");

        // Lister tous les paiements
        System.out.println("\n--- Liste des paiements ---");
        List<Paiement> paiements = paiementService.listerTous();
        paiements.forEach(p -> System.out.println("ID: " + p.getId() + ", Montant: " + p.getMontant() + ", Moyen: " + p.getMoyenDePaiement().getCode() + ", Abonnement ID: " + p.getAbonnement().getId()));

        // Modification d'un paiement
        if (paiement.getId() != 0) {
            System.out.println("\n--- Modification du paiement ---");
            Paiement paiementAModifier = paiementService.trouver(paiement.getId());
            if (paiementAModifier != null) {
                System.out.println("Paiement avant modification: ID=" + paiementAModifier.getId() + ", Montant=" + paiementAModifier.getMontant());
                paiementAModifier.setMontant(120000); // Change le montant
                paiementService.modifier(paiementAModifier);
                System.out.println("Paiement modifié avec succès. Nouveau montant: " + paiementAModifier.getMontant());
            }
        }

        // Suppression d'un paiement (Attention: si un paiement est lié à un abonnement, il doit être supprimé avant l'abonnement)
        // L'abonnement a déjà été supprimé, donc le paiement est normalement déjà orphelin ou supprimé par cascade si configuré.
        System.out.println("\n--- Tentative de suppression du paiement ---");
        if (paiement.getId() != 0) {
            paiementService.supprimer(paiement.getId());
            Paiement paiementSupprime = paiementService.trouver(paiement.getId());
            if (paiementSupprime == null) {
                System.out.println("Paiement avec ID: " + paiement.getId() + " a été supprimé avec succès.");
            } else {
                System.out.println("Paiement avec ID: " + paiement.getId() + " n'a PAS été supprimé.");
            }
        }

        // ===============================================
        // DÉMONSTRATION CRUD POUR TYPE_ABONNEMENT
        // ===============================================
        System.out.println("\n--- Démonstration des opérations CRUD sur TypeAbonnement ---");

        // Lister tous les types d'abonnement
        System.out.println("\n--- Liste des types d'abonnement ---");
        List<TypeAbonnement> types = typeAbonnementService.listerTous();
        types.forEach(t -> System.out.println("Code: " + t.getCode() + ", Libelle: " + t.getLibelle() + ", Montant: " + t.getMontant()));

        // Modification d'un type d'abonnement
        if (typeAbonnement.getCode() != null && !typeAbonnement.getCode().isEmpty()) {
            System.out.println("\n--- Modification du type d'abonnement ---");
            TypeAbonnement taAModifier = typeAbonnementService.trouver(typeAbonnement.getCode());
            if (taAModifier != null) {
                System.out.println("Type Abonnement avant modification: Code=" + taAModifier.getCode() + ", Montant=" + taAModifier.getMontant());
                taAModifier.setMontant(2500); // Change le montant
                typeAbonnementService.modifier(taAModifier);
                System.out.println("Type Abonnement modifié avec succès. Nouveau montant: " + taAModifier.getMontant());
            }
        }

        // Suppression d'un type d'abonnement (attention: l'abonnement dépend de lui)
        // Comme l'abonnement a été supprimé plus haut, la FK ne devrait pas poser de problème ici.
        System.out.println("\n--- Tentative de suppression du type d'abonnement ---");
        if (typeAbonnement.getCode() != null && !typeAbonnement.getCode().isEmpty()) {
            typeAbonnementService.supprimer(typeAbonnement);
            TypeAbonnement taSupprime = typeAbonnementService.trouver(typeAbonnement.getCode());
            if (taSupprime == null) {
                System.out.println("Type Abonnement avec code: " + typeAbonnement.getCode() + " a été supprimé avec succès.");
            } else {
                System.out.println("Type Abonnement avec code: " + typeAbonnement.getCode() + " n'a PAS été supprimé.");
            }
        }

        System.out.println("\n--- Fin de la démonstration complète ---");
    }
}
