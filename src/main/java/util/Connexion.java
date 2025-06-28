package util;

import java.sql.Connection;
import java.sql.DriverManager;

public class Connexion {

    private Connection session;
    private static Connexion instance;

    static {
        instance = new Connexion();
    }

    private Connexion(){

        String url = "jdbc:mysql://localhost:3306/gym";
        String login =  "root";
        String motDePasse = "Azerty1234@";


        try{

            Class.forName("com.mysql.cj.jdbc.Driver");
            session = DriverManager.getConnection(url, login, motDePasse);

        }catch(Exception e){
            System.out.println("Erreur :" + e.getMessage());
        }

    }

    public static Connexion getInstance(){
        return instance;
    }

    public Connection getSession() {
        return session;
    }

    public static Connection getSessionV2() {
        String url = "jdbc:mysql://localhost:3306/gym";
        String login = "root";
        String motDePasse = "Azerty1234@";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, login, motDePasse);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la connexion à la base de données : " + e.getMessage());
        }
    }


}