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
public class RaceOpenDatabase extends CustomDialog {

    protected static final boolean DEBUG   = false;

    JLabel          labelName;
    JTextField      fieldName;
    JLabel          labelTitle;

    JButton         buttonCreate;
    JButton         buttonCancel;

    public RaceOpenDatabase() {

        super();

        this.setTitle("Open Existing Race");

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
 
        buttonCreate = new JButton("Open");
        buttonCreate.setBounds(getButtonX(0, 2),80,buttonWidth,20);
        buttonCreate.setActionCommand("open");
        buttonCreate.addActionListener(this);
        buttonCreate.setMnemonic(KeyEvent.VK_ENTER);
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

        if ("open".equals(e.getActionCommand())) {
            actionOpenDatabase();
        }
    }

    protected void actionOpenDatabase() {
        int tableType = 0;

        if (fieldName.getText().equals("")) {
            showErrorMsgBox("Database Name cannot be blank","Open Error");
            return;
        }
        //String tableName = fieldName.getText();


        if (database.openDatabase(fieldName.getText())!=0) {
            showErrorMsgBox("Could Not Open Race Database","Open Error");
                return;
        }

        this.setVisible(false);

        menuTree.initTree2();
        menuTree.configTree.setSelectionPath(menuTree.configTree.getPathForRow(1));
    }

}
