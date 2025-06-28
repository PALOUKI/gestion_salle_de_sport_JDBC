package entite;

import java.time.LocalDateTime;

public class Client {

    private int id;
    private String nom;
    private String prenom;
    private LocalDateTime dateNaissance;
    private String email;
    private Membre membre;
    private DemandeInscription demandeInscription;
    private Ticket ticket;

    public Client(){

    }

    public Client(String nom, String prenom, LocalDateTime dateNaissance, String email){
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.email = email;
    }

    public Client(int id, String nom, String prenom, LocalDateTime dateNaissance, String email){
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public LocalDateTime getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDateTime dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Membre getMembre() {
        return membre;
    }

    public void setMembre(Membre membre) {
        this.membre = membre;
    }

    public DemandeInscription getDemandeInscription() {
        return demandeInscription;
    }

    public void setDemandeInscription(DemandeInscription demandeInscription) {
        this.demandeInscription = demandeInscription;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    // NOUVEAU : Override de toString() pour un affichage lisible dans le JComboBox
    @Override
    public String toString() {
        return nom + " " + prenom;
    }

    // Il est également bon d'implémenter equals() et hashCode() si vous utilisez des objets Client
    // dans des collections comme des HashSet ou si vous comparez des objets Client.
    // Pour JComboBox.setSelectedItem(), equals() est important pour la comparaison d'objets.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        // On compare par l'ID car c'est la clé primaire
        return id == client.id;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }


}
