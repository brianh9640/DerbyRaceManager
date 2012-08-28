/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package racemanager2.dialogs;

import racemanager2.*;
import RaceLibrary.RaceDatabase;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author Brian
 */
public class RaceNewDatabase extends CustomDialog {

    protected static final boolean DEBUG   = false;

    JLabel          labelName;
    JTextField      fieldName;
    JLabel          labelTitle;

    JButton         buttonCreate;
    JButton         buttonCancel;

    public RaceNewDatabase() {

        super();

        this.setTitle("Create New Race");

        this.setLayout(null);
        this.setResizable(false);
        //this.setPreferredSize(new Dimension(150,200));
        this.setBackground(java.awt.Color.WHITE);

        this.setSize(320, 140);

        labelName = new JLabel("Race Name");
        labelName.setBounds(20, 30, 120, 20);
        this.add(labelName);

        fieldName = new JTextField("");
        fieldName.setBounds(140, 30, 140, 20);
        this.add(fieldName);
 
        buttonCreate = new JButton("Create");
        buttonCreate.setBounds(getButtonX(0, 2),80,buttonWidth,20);
        buttonCreate.setActionCommand("create");
        buttonCreate.addActionListener(this);
        this.add(buttonCreate);

        buttonCancel = new JButton("Cancel");
        buttonCancel.setBounds(getButtonX(1, 2),80,buttonWidth,20);
        buttonCancel.setActionCommand("cancel");
        buttonCancel.addActionListener(this);
        this.add(buttonCancel);

        addWindowListener( new WindowAdapter()
             {
                public void windowOpened( WindowEvent e ) {
                    fieldName.requestFocus();
                }
         });

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        super.actionPerformed(e);

        if (DEBUG) System.out.println(e.getActionCommand());

        if ("cancel".equals(e.getActionCommand())) {
            setVisible(false);
        }

        if ("create".equals(e.getActionCommand())) {
            actionCreateTable();
        }
    }

    protected void actionCreateTable() {

        if (fieldName.getText().equals("")) {
            showErrorMsgBox("Database Name cannot be blank","Create Error");
            return;
        }
        String tableName = fieldName.getText();

        database.createRace(fieldName.getText().toLowerCase());

        this.setVisible(false);

        menuTree.initTree2();
        menuTree.configTree.setSelectionPath(menuTree.configTree.getPathForRow(1));
    }

}
