package gui_admin.gui_util;

import javax.swing.*;
import java.awt.*;

public class MyWindow extends JFrame {

    protected JPanel west = new JPanel();
    protected JPanel north = new JPanel();
    protected JPanel south = new JPanel();
    protected JPanel center = new JPanel();


    public MyWindow(){
        this.setSize(1400, 900);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);


        west.setPreferredSize(new Dimension(250, 800));

        //set background color
        north.setBackground(new Color(32, 64, 128));
        south.setBackground(new Color(173, 216, 230));
        west.setBackground(new Color(26, 26, 69));
        center.setBackground(new Color(245, 245, 245));




    }

}
