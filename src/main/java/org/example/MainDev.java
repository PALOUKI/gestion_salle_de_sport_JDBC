package org.example;

import entite.TypeAbonnement;
import gui_admin.controller.TypeAbonnementController;
import gui_admin.gui_util.MyWindow1;
import service.TypeAbonnementService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class MainDev {
    public static void main(String[] args) throws SQLException {

        /*
        TypeAbonnementController controller = new TypeAbonnementController();
        JFrame fenetre = new JFrame();
        fenetre.setSize(400, 400);

        Container c = fenetre.getContentPane();
        c.setLayout(new BorderLayout());
        c.add(controller.ajouter());

        fenetre.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        fenetre.setVisible(true);

         */
        /*
        TypeAbonnementController controller = new TypeAbonnementController();
        TypeAbonnementService service =  new TypeAbonnementService();
        TypeAbonnement entite = service.trouver("HEBDO");
        controller.ajouter();

         */
        //controller.modifier(entite);

        MyWindow1 f1 = new MyWindow1();
        f1.setVisible(true);





    }
}
