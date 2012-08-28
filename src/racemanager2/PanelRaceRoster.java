/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package racemanager2;

import RaceLibrary.RaceDatabase;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import RaceLibrary.RaceConfig;

/**
 *
 * @author Brian
 */
public class PanelRaceRoster extends BasicWorkingPanel {

    JTextField      fieldGroupName[];
    JLabel          labelGroupName[];

    JButton         buttonApply;

    public PanelRaceRoster(Rectangle rect) {
        super(rect);

        addStandardTitle("Enter Group Names");

        fieldGroupName = new JTextField[RaceConfig.RACECFG_MAX_GROUPS];
        labelGroupName = new JLabel[RaceConfig.RACECFG_MAX_GROUPS];

        for (int f=0;f<RaceConfig.RACECFG_MAX_GROUPS;f++) {
            fieldGroupName[f] = new JTextField("");
            String slabel;
            slabel = "Group #" + Integer.toString(f+1) + " Name";
            labelGroupName[f] = new JLabel(slabel);
            addStdLabelTextField(f+2,labelGroupName[f],fieldGroupName[f]);
        }

        buttonApply = new JButton("Apply");
        buttonApply.setBounds(getButtonRect(4));
        buttonApply.setActionCommand("apply");
        buttonApply.addActionListener(this);
        buttonApply.setToolTipText("Apply and save any changes made above");
        this.add(buttonApply);

    }

    public void display() {

        super.display();

        int ngroups = database.getRaceConfigInt((int) RaceDatabase.DB_CFGID_NGROUPS);

        if (ngroups < 1 || ngroups > RaceConfig.RACECFG_MAX_GROUPS) ngroups = 1;

        for (int f=0;f<RaceConfig.RACECFG_MAX_GROUPS;f++) {

            fieldGroupName[f].setText(database.getRaceConfig(RaceDatabase.DB_CFGID_GROUP1_NAME + (f*10)));
            
            if (f < ngroups) {
                labelGroupName[f].setVisible(true);
                fieldGroupName[f].setVisible(true);
            }
            else {
                labelGroupName[f].setVisible(false);
                fieldGroupName[f].setVisible(false);
            }

        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        //if (DEBUG) System.out.println(e.getActionCommand());

        if ("apply".equals(e.getActionCommand())) {
            saveGroupNames();
        }

    }

    public void saveGroupNames() {

        database.setRaceConfig(RaceDatabase.DB_CFGID_GROUP1_NAME,fieldGroupName[0].getText());
        database.setRaceConfig(RaceDatabase.DB_CFGID_GROUP2_NAME,fieldGroupName[1].getText());
        database.setRaceConfig(RaceDatabase.DB_CFGID_GROUP3_NAME,fieldGroupName[2].getText());
        database.setRaceConfig(RaceDatabase.DB_CFGID_GROUP4_NAME,fieldGroupName[3].getText());
        menuTree.initTree2();

    }

}
