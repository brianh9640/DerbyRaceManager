/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package racemanager2.dialogs;

import RaceLibrary.RaceManager;
import RaceLibrary.RaceDatabase;
import RaceLibrary.ScheduleRace;

import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author Brian
 */
public class DeleteMainSchedule extends racemanager2.CustomDialog {

    public static final boolean DEBUG   = false;

    int groupID;

    JLabel          labelWarning;
    JLabel          labelDescription;

    JButton         buttonDelete;
    JButton         buttonCancel;

    RaceManager     raceManager;

    public DeleteMainSchedule(int raceID) {
        super();
        this.groupID = raceID;
        raceManager = null;

        this.setTitle("Delete Previous Race Schedule");

        this.setLayout(null);
        this.setResizable(false);
        //this.setPreferredSize(new Dimension(150,200));
        this.setBackground(java.awt.Color.WHITE);

        this.setSize(320, 140);

        labelWarning = new JLabel("<html>WARNING: This will delete the previous race schedule and RESULTS!!!!</html>");
        labelWarning.setBounds(10, 5, this.getWidth()-20, 40);
        labelWarning.setHorizontalAlignment(JLabel.CENTER);
        labelWarning.setForeground(Color.red);
        this.add(labelWarning);

//        labelDescription = new JLabel("Create a Race Schedule");
//        labelDescription.setBounds(10, 30, this.getWidth()-20, 20);
//        labelDescription.setHorizontalAlignment(JLabel.CENTER);
//        this.add(labelDescription);
//
        buttonDelete = new JButton("Delete");
        buttonDelete.setForeground(Color.red);
        buttonDelete.setBounds(getButtonX(0, 2),80,buttonWidth,20);
        buttonDelete.setActionCommand("delete");
        buttonDelete.addActionListener(this);
        this.add(buttonDelete);

        buttonCancel = new JButton("Cancel");
        buttonCancel.setBounds(getButtonX(1, 2),80,buttonWidth,20);
        buttonCancel.setActionCommand("cancel");
        buttonCancel.addActionListener(this);
        this.add(buttonCancel);
        
    }

    public void setManager(RaceManager mgr) {
        raceManager = mgr;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        super.actionPerformed(e);

        if ("cancel".equals(e.getActionCommand())) {
            setVisible(false);
        }

        if ("delete".equals(e.getActionCommand())) {
            deleteSchedule();
            setVisible(false);
            menuTree.panelDisplay.display();
        }
    }

    protected void deleteSchedule() {
        int raceID = 0;

        String sql = "SELECT raceid FROM races WHERE groupid=" +
                     String.valueOf(this.groupID) + " AND racetype=1";
        ResultSet rs = database.execute(sql);
        if (rs == null) return;

        try {
            while (rs.next()) {
                raceID = rs.getInt(1);
                if (raceID > 0) {
                    sql = "DELETE FROM results WHERE raceid=" + String.valueOf(raceID);
                    database.execute(sql);

                    sql = "DELETE FROM races WHERE raceid=" + String.valueOf(raceID);
                    database.execute(sql);
                }
            }
        } catch (SQLException ex) {
            return;
        }

        if (raceManager != null) {
            if (raceID == raceManager.getRaceID()) {
                raceManager.clearAll();
                database.setRaceConfig(RaceDatabase.DB_CFGID_CURRENT_RACEID,"0");
            }
        }
    }

}
