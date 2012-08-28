/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package racemanager2;

import racemanager2.dialogs.RaceNewDatabase;
import racemanager2.dialogs.RaceOpenDatabase;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author Brian
 */
public class PanelRaceMain extends BasicWorkingPanel {

    public static final boolean DEBUG   = false;

    JLabel          labelNew;
    JLabel          labelOpen;

    JButton         buttonNew;
    JButton         buttonOpen;

    public PanelRaceMain(Rectangle rect) {
        super(rect);
    }

    @Override
    public void display() {

        super.display();

        String title = "Race Manager Main Page";
        addStandardTitle(title);

        labelNew = new JLabel("Create New Race",JLabel.LEFT);
        labelNew.setBounds(this.getPosX(1),this.getPosY(2),PWIDTH*3,PHEIGHT);
        this.add(labelNew);

        buttonNew = new JButton("New");
        buttonNew.setVerticalTextPosition(AbstractButton.CENTER);
        buttonNew.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
        //buttonApply.setMnemonic(KeyEvent.VK_D);
        buttonNew.setActionCommand("new");
        buttonNew.setBounds(this.getPosX(5),this.getPosY(2),PWIDTH*2,PHEIGHT);
        buttonNew.addActionListener(this);
        buttonNew.setToolTipText("Create a new empty race database");
        this.add(buttonNew);

        labelOpen = new JLabel("Open Existing Race",JLabel.LEFT);
        labelOpen.setBounds(this.getPosX(1),this.getPosY(4),PWIDTH*3,PHEIGHT);
        this.add(labelOpen);

        buttonOpen = new JButton("Open");
        buttonOpen.setVerticalTextPosition(AbstractButton.CENTER);
        buttonOpen.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
        //buttonApply.setMnemonic(KeyEvent.VK_D);
        buttonOpen.setActionCommand("open");
        buttonOpen.setBounds(this.getPosX(5),this.getPosY(4),PWIDTH*2,PHEIGHT);
        buttonOpen.addActionListener(this);
        buttonOpen.setToolTipText("Open an existing race database");
        this.add(buttonOpen);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (DEBUG) System.out.println(e.getActionCommand());
        if ("new".equals(e.getActionCommand())) {
            if (DEBUG) System.out.println("Create new frame/panel");

            RaceNewDatabase createRace = new RaceNewDatabase();
            createRace.setDatabase(database);
            createRace.setMenuTree(menuTree);
            createRace.setLocationRelativeTo(this);
            createRace.setVisible(true);

            if (DEBUG) System.out.println("Create Table Done");

        }

        if ("open".equals(e.getActionCommand())) {
            if (DEBUG) System.out.println("open race");

            RaceOpenDatabase openRace = new RaceOpenDatabase();
            openRace.setDatabase(database);
            openRace.setMenuTree(menuTree);
            openRace.setLocationRelativeTo(this);
            openRace.setVisible(true);

        }

    }

}
