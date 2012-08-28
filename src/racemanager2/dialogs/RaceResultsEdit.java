/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package racemanager2.dialogs;

import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import RaceLibrary.RaceDatabase;
import RaceLibrary.RaceManager;
import RaceLibrary.DisplayTable;
import RaceLibrary.LayoutTable;

/**
 *
 * @author Brian
 */
public class RaceResultsEdit extends racemanager2.CustomDialog {

    public static final int PWIDTH              = 40;
    public static final int PHEIGHT             = 20;
    public static final int PCOL_WIDTH          = 50;
    public static final int PROW_HEIGHT         = 30;

    RaceManager     raceManager;
    int             nLanes;

    JLabel          labelHeatCounter;
    JLabel          labelRacer[];
    JTextField      fieldPlace[];
    JLabel          labelPoints[];
    JTextField      fieldTime[];

    JButton         buttonAccept;
    JButton         buttonCancel;

    LayoutTable     tableResults;

    public RaceResultsEdit(RaceManager mgr) {
        super();

        raceManager = mgr;
        int dialogWidth = 420;
        int dialogHeight = 260;

        nLanes = raceManager.getNLanes();

        this.setTitle("Edit Race Results");

        this.setLayout(null);
        this.setResizable(false);
        this.setBackground(java.awt.Color.WHITE);

        this.setSize(dialogWidth, dialogHeight);

        JLabel label;

        labelHeatCounter = new JLabel("Heat - of -");
        labelHeatCounter.setBounds(50,2,100,18);
        this.add(labelHeatCounter);

        tableResults = new LayoutTable();
        tableResults.setStartY(30);

        int nColumns        = 5;
        String cLabel[]     = new String[10];
        int cWidth[]        = new int[10];
        boolean cUsed[]     = new boolean[10];
        int cJustify[]      = new int[10];
        cLabel[0]   =   "Lane";        cWidth[0] = 50;         cUsed[0] = true;    cJustify[0] = DisplayTable.COLUMN_JUSTIFY_CENTER;
        cLabel[1]   =   "Racer";       cWidth[1] = 140;        cUsed[1] = true;    cJustify[1] = DisplayTable.COLUMN_JUSTIFY_CENTER;
        cLabel[2]   =   "Place";       cWidth[2] = 60;         cUsed[2] = true;    cJustify[2] = DisplayTable.COLUMN_JUSTIFY_LEFT;
        cLabel[3]   =   "Points";      cWidth[3] = 60;         cUsed[3] = true;    cJustify[3] = DisplayTable.COLUMN_JUSTIFY_LEFT;
        cLabel[4]   =   "Time";        cWidth[4] = 80;         cUsed[4] = true;    cJustify[4] = DisplayTable.COLUMN_JUSTIFY_LEFT;

        tableResults.setupColumns(nColumns,cWidth, cLabel, cJustify, cUsed);

        JLabel labelLane = new JLabel();
        labelRacer = new JLabel[nLanes];
        fieldPlace = new JTextField[nLanes];
        labelPoints = new JLabel[nLanes];
        fieldTime = new JTextField[nLanes];

        for (int c=0;c<nColumns;c++) {
            label = tableResults.getTitleLabel(c);      this.add(label);
        }
        for (int r=0;r<nLanes;r++) {
            labelLane = new JLabel(String.valueOf(r+1));
            labelLane.setHorizontalAlignment(JLabel.CENTER);
            labelLane.setBounds(tableResults.getFieldRect(r+1,0));
            this.add(labelLane);

            labelRacer[r] = new JLabel();
            labelRacer[r].setBounds(tableResults.getFieldRect(r+1,1));
            labelRacer[r].setHorizontalAlignment(JLabel.LEFT);
            this.add(labelRacer[r]);

            fieldPlace[r] = new JTextField();
            fieldPlace[r].setBounds(tableResults.getFieldRect(r+1,2));
            fieldPlace[r].setHorizontalAlignment(JLabel.RIGHT);
            this.add(fieldPlace[r]);

            labelPoints[r] = new JLabel("-");
            labelPoints[r].setBounds(tableResults.getFieldRect(r+1,3));
            labelPoints[r].setHorizontalAlignment(JLabel.CENTER);
            this.add(labelPoints[r]);

            fieldTime[r] = new JTextField();
            fieldTime[r].setBounds(tableResults.getFieldRect(r+1,4));
            fieldTime[r].setHorizontalAlignment(JLabel.RIGHT);
            this.add(fieldTime[r]);
         }

            for (int n=0;n<nLanes;n++) {
                RaceManager.LaneInfo laneInfo = raceManager.getHeatLane(n+1);
                if (laneInfo == null) continue;
                String tmp = String.format("%1$2d", laneInfo.carID) + ": " + laneInfo.racerName;
                labelRacer[n].setText(tmp);
                fieldPlace[n].setText(String.valueOf(laneInfo.place));
                labelPoints[n].setText(String.valueOf(laneInfo.points));
                fieldTime[n].setText(String.format("%1$5.3f",laneInfo.time));
            }

        String txt = "";
        if (!raceManager.heatsCompleted()) {
            txt = "Heat " + String.valueOf(raceManager.getCurrentHeat()) + " of " + String.valueOf(raceManager.getHeats());
        }
        else {
            txt = "Completed";
        }
        labelHeatCounter.setText(txt);

        buttonAccept = new JButton("Accept");
        buttonAccept.setBounds(getButtonX(0, 2),getPosY(6),buttonWidth,20);
        buttonAccept.setActionCommand("accept");
        buttonAccept.addActionListener(this);
        this.add(buttonAccept);

        buttonCancel = new JButton("Cancel");
        buttonCancel.setBounds(getButtonX(1, 2),getPosY(6),buttonWidth,20);
        buttonCancel.setActionCommand("cancel");
        buttonCancel.addActionListener(this);
        this.add(buttonCancel);


        addWindowListener( new WindowAdapter()
             {
                public void windowOpened( WindowEvent e ) {
                    fieldPlace[0].requestFocus();
                }
         });



    }

    public void display() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        super.actionPerformed(e);

        //if (DEBUG) System.out.println(e.getActionCommand());

        if ("cancel".equals(e.getActionCommand())) {
            setVisible(false);
        }

        if ("accept".equals(e.getActionCommand())) {
            for (int n=0;n<nLanes;n++) {
                RaceManager.LaneInfo laneInfo = raceManager.getHeatLane(n+1);
                if (fieldPlace[n].getText().isEmpty()) laneInfo.place = 0;
                else    laneInfo.place  = Double.valueOf(fieldPlace[n].getText()).intValue();
                if (fieldTime[n].getText().isEmpty()) laneInfo.time = 0.0;
                else laneInfo.time   = Double.valueOf(fieldTime[n].getText());
                raceManager.updateHeatLane(n+1,laneInfo);
            }

            int valid = raceManager.verifyCurrentHeat();
            if (valid < 0) {
                showErrorMsgBox("Invalid Race Results Entered","Save Error");
                return;
            }

            raceManager.saveCurrentHeat();

            raceManager.getCurrentHeat();

            setVisible(false);
        }

    }



}
