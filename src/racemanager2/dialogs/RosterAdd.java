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
public class RosterAdd extends racemanager2.CustomDialog {

    protected static final boolean DEBUG   = false;

    int             ngroup;

    JLabel          labelName;
    JLabel          labelGroup;
    JLabel          labelTitle;

    JTextField      fieldNameFirst;
    JTextField      fieldNameLast;
    JTextField      fieldCarID;
    JTextField      fieldWeight;
    JCheckBox       boxPassed;

    JButton         buttonAssign;
    JButton         buttonAdd;
    JButton         buttonCancel;

    public RosterAdd(int ngroup) {
        super();
        this.ngroup = ngroup;
        RosterCreation();
    }

    protected void RosterCreation() {
        JLabel label;

        this.setTitle("Add to Roster");

        this.setLayout(null);
        this.setResizable(false);
        this.setBackground(java.awt.Color.WHITE);

        this.setSize(420, 260);

        labelName = new JLabel("Group");
        labelName.setBounds(getPosX(0), getPosY(0), DIALOG_COL_WIDTH, 20);
        this.add(labelName);

        labelGroup = new JLabel("");
        labelGroup.setBounds(getPosX(1), getPosY(0), DIALOG_COL_WIDTH, 20);
        this.add(labelGroup);

        labelName = new JLabel("Name (First/Last)");
        labelName.setBounds(getPosX(0), getPosY(1), DIALOG_COL_WIDTH, 20);
        this.add(labelName);

        fieldNameFirst = new JTextField("");
        fieldNameFirst.setBounds(getPosX(1), getPosY(1), DIALOG_COL_WIDTH, 20);
        this.add(fieldNameFirst);

        fieldNameLast = new JTextField("");
        fieldNameLast.setBounds(getPosX(2), getPosY(1), DIALOG_COL_WIDTH, 20);
        this.add(fieldNameLast);

        label = new JLabel("Car #");
        label.setBounds(getPosX(0), getPosY(2), DIALOG_COL_WIDTH, 20);
        this.add(label);

        fieldCarID = new JTextField("");
        fieldCarID.setBounds(getPosX(1), getPosY(2), DIALOG_COL_WIDTH / 2, 20);
        this.add(fieldCarID);

        buttonAssign = new JButton("Get #");
        buttonAssign.setBounds(getPosX(1)+DIALOG_COL_WIDTH / 2+10,getPosY(2),buttonWidth,20);
        buttonAssign.setActionCommand("assign");
        buttonAssign.addActionListener(this);
        this.add(buttonAssign);

        label = new JLabel("Weight");
        label.setBounds(getPosX(0), getPosY(3), DIALOG_COL_WIDTH, 20);
        this.add(label);

        fieldWeight = new JTextField("");
        fieldWeight.setBounds(getPosX(1), getPosY(3), DIALOG_COL_WIDTH / 2, 20);
        this.add(fieldWeight);

        label = new JLabel("Inspection");
        label.setBounds(getPosX(0), getPosY(4), DIALOG_COL_WIDTH, 20);
        this.add(label);

        boxPassed = new JCheckBox("Passed");
        boxPassed.setBounds(getPosX(1), getPosY(4), DIALOG_COL_WIDTH, 20);
        this.add(boxPassed);

        buttonAdd = new JButton("Add");
        buttonAdd.setBounds(getButtonX(0, 2),getPosY(6),buttonWidth,20);
        buttonAdd.setActionCommand("add");
        buttonAdd.addActionListener(this);
        this.add(buttonAdd);

        buttonCancel = new JButton("Cancel");
        buttonCancel.setBounds(getButtonX(1, 2),getPosY(6),buttonWidth,20);
        buttonCancel.setActionCommand("cancel");
        buttonCancel.addActionListener(this);
        this.add(buttonCancel);


        addWindowListener( new WindowAdapter()
             {
                public void windowOpened( WindowEvent e ) {
                    fieldNameFirst.requestFocus();
                }
         });

    }

    public void display() {
        labelGroup.setText(database.getRaceConfig(RaceDatabase.DB_CFGID_GROUP1_NAME + ((ngroup-1)*10)));
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        super.actionPerformed(e);

        if (DEBUG) System.out.println(e.getActionCommand());

        if ("cancel".equals(e.getActionCommand())) {
            setVisible(false);
        }

        if ("assign".equals(e.getActionCommand())) {
            actionGetCarID();
        }

        if ("add".equals(e.getActionCommand())) {
            actionAddRoster();
            setVisible(false);
            menuTree.panelDisplay.display();
            menuTree.panelDisplay.repaint();
        }
    }

    protected void actionGetCarID() {
        String sql;

        sql = "SELECT MAX(carid)+1 FROM roster WHERE groupid=" + String.valueOf(ngroup);
            if (DEBUG) System.out.println(sql);

        ResultSet rs = database.execute(sql);
        if (rs == null) {
            fieldCarID.setText("1");
            return;
        }

        try {
            rs.next();
            String tmp = rs.getString(1);
            if (DEBUG) System.out.println(tmp);
            if (tmp == null) tmp = "1";
            fieldCarID.setText(tmp);
        } catch (SQLException ex) {
            
        }

    }

    protected void actionAddRoster() {
        String sql;

        String pass = "0";
        if (boxPassed.isSelected()) pass = "1";

        Double weight = 0.0;
        if (fieldWeight.getText().length() > 0) {
            try {
               weight = Double.valueOf(fieldWeight.getText());
            }
            catch (NumberFormatException ex) {
                weight = 0.0;
            }
        }
        
        sql = "INSERT INTO roster (carid,lastname,firstname,groupid,picid,weight,pass)" +
              " VALUES (" +
              fieldCarID.getText() + "," +
              "'" + fieldNameLast.getText() + "'," +
              "'" + fieldNameFirst.getText() + "'," +
              String.valueOf(ngroup) + "," +
              "0," +
              weight.toString() + "," +
              pass +
              ")";

        //System.out.println(sql);
        database.execute(sql);

    }
}
