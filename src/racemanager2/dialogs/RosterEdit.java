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
public class RosterEdit extends RosterAdd {

    int racerID;

    public RosterEdit(int ngroup,int racerID,RaceDatabase db) {
        super(ngroup);

        this.racerID = racerID;

        database = db;

        String sql;
        sql = "SELECT carid,lastname,firstname,groupid,picid,weight,pass" +
              " FROM roster WHERE racerid=" + String.valueOf(racerID);

        ResultSet rs = database.execute(sql);
        if (rs == null) return;

        try {
            int r = 0;
            while (rs.next()) {
                fieldCarID.setText(rs.getString(1));
                fieldNameLast.setText(rs.getString(2));
                fieldNameFirst.setText(rs.getString(3));
                fieldWeight.setText(rs.getString(6));
                if (Integer.valueOf(rs.getString(7))==1) boxPassed.setSelected(true);
                else boxPassed.setSelected(false);
            }
        } catch (SQLException ex) {

        }

        buttonAdd.setText("Update");
        buttonAdd.setActionCommand("update");
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if ("update".equals(e.getActionCommand())) {
            actionUpdateRoster();
            setVisible(false);
            menuTree.panelDisplay.display();
            menuTree.panelDisplay.repaint();
        }

        super.actionPerformed(e);
    }

    protected void actionUpdateRoster() {
       String sql;

        String pass = "0";
        if (boxPassed.isSelected()) pass = "1";

        Double weight = 0.0;
        if (fieldWeight.getText().length() > 0) weight = Double.valueOf(fieldWeight.getText());

        sql = "UPDATE roster SET " +
              "carid=" + fieldCarID.getText() + "," +
              "lastname=" + "'" + fieldNameLast.getText() + "'," +
              "firstname=" + "'" + fieldNameFirst.getText() + "'," +
              "groupid=" + String.valueOf(ngroup) + "," +
              "picid=" + "0," +
              "weight=" + weight.toString() + "," +
              "pass=" + pass +
              "WHERE racerid=" + String.valueOf(racerID);

        //System.out.println(sql);
        database.execute(sql);

    }

}
