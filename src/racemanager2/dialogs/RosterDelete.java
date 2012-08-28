/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package racemanager2.dialogs;

import RaceLibrary.RaceDatabase;
import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author Brian
 */
public class RosterDelete extends racemanager2.CustomDialog {

    protected static final boolean DEBUG   = false;

    int             ngroup;
    String          racerID;

    JLabel          labelName;
    JLabel          labelRacerName;
    JLabel          labelTitle;

    JButton         buttonDelete;
    JButton         buttonCancel;


    public RosterDelete(int ngroup,String racerID) {

        super();

        this.ngroup = ngroup;
        this.racerID = racerID;
        this.setTitle("Delete Existing Racer");

        this.setLayout(null);
        this.setResizable(false);
        //this.setPreferredSize(new Dimension(150,200));
        this.setBackground(java.awt.Color.WHITE);

        this.setSize(320, 140);

        labelName = new JLabel("Racer Name");
        labelName.setBounds(20, 30, 120, 20);
        this.add(labelName);

        labelRacerName = new JLabel("");
        labelRacerName.setBounds(140, 30, 140, 20);
        this.add(labelRacerName);

        buttonDelete = new JButton("Delete");
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

    @Override
    public void display() {
        String sql;

        sql = "SELECT lastname,firstname,carid FROM roster WHERE racerid=" + racerID;

        ResultSet rs = database.execute(sql);
        if (rs == null) return;

        try {
            if (rs.next()) {
                String tmp = rs.getString(2) + " " + rs.getString(1);
                labelRacerName.setText(tmp);
            }
        } catch (SQLException ex) {

        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        super.actionPerformed(e);

        if (DEBUG) System.out.println(e.getActionCommand());

        if ("cancel".equals(e.getActionCommand())) {
            setVisible(false);
        }

        if ("delete".equals(e.getActionCommand())) {
            actionDeleteRacer();
            setVisible(false);
            menuTree.panelDisplay.display();
            menuTree.panelDisplay.repaint();
        }
    }

    protected void actionDeleteRacer() {
        String sql;

        sql = "DELETE FROM roster WHERE racerid=" + racerID;
        database.execute(sql);
    }

}
