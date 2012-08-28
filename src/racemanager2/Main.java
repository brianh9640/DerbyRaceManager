/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package racemanager2;

import java.awt.*;
import java.awt.GraphicsConfiguration;
import java.awt.event.*;
import javax.swing.*;

import RaceLibrary.RaceDatabase;
import RaceLibrary.RaceManager;

/**
 *
 * @author Brian
 */
public class Main extends JPanel implements ActionListener  {

    JFrame frameMain;
    RaceMenuTree panelTree;
    JLabel labelTitle;
    JPanel panelTitle;
    public JPanel panelWorking;

    RaceDatabase db;
    RaceManager  managerRace;

    public static final int TITLE_HEIGHT             = 45;
    public static final int TREE_WIDTH               = 170;

    public Main(JFrame frame) {

        frameMain = frame;
        this.setLayout(null);
        frame.setResizable(false);
//        frame.setResizable(true);

        Dimension panelSize = new Dimension(800,600);
  		this.setPreferredSize(panelSize);

        db = null;
        managerRace = null;

        panelTitle = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
        panelTitle.setBackground(java.awt.Color.WHITE);
        panelTitle.setPreferredSize(new Dimension(2000,TITLE_HEIGHT));

        javax.swing.ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource("images/pageTitle.png"));
        labelTitle = new JLabel("",icon,JLabel.LEFT);
        panelTitle.add(labelTitle);
        panelTitle.setBounds(0,0,panelSize.width,TITLE_HEIGHT);
        this.add(panelTitle);

        panelTree = new RaceMenuTree();
        panelTree.setBounds(0,TITLE_HEIGHT,TREE_WIDTH,panelSize.height - panelTitle.getHeight());
        this.add(panelTree);

        panelWorking = new JPanel();
        panelWorking.setLayout(null);
        panelWorking.setBorder(new javax.swing.border.LineBorder(java.awt.Color.BLACK));

        panelWorking.setBounds(TREE_WIDTH,TITLE_HEIGHT,panelSize.width-TREE_WIDTH,panelSize.height - panelTitle.getHeight());
        this.add(panelWorking);

        panelTree.setPanels(this,frameMain,panelWorking);

    }

	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();

//		if (source == buttonUpdate) {
//	        //textStatus.append("Updated Pressed\n");
//			readSX.cycleStart();
//		}
//		else if (source == buttonExit) {
//			System.exit(0);
//		}

	}

    private static void createAndShowGUI() {

        //GraphicsConfiguration gc;
        JFrame frame = new JFrame("Pinewood Derby Race Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Rectangle bounds = gc.getBounds();
        frame.setLocation(100,50);

        //Add contents to the window.
        frame.add(new Main(frame));

        //Display the window.
        frame.pack();
        frame.setVisible(true);


////If a string was returned, say so.
//if ((s != null) && (s.length() > 0)) {
//    setLabel("Green eggs and... " + s + "!");
//    return;
//}


    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }


}
