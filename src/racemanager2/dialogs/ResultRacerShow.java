/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package racemanager2.dialogs;

import RaceLibrary.RaceDatabase;
import RaceLibrary.DisplayTable;
import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author Brian
 */
public class ResultRacerShow extends racemanager2.CustomDialog {

    JPanel           workingPanel;
    Container        dialogContainer;

    int raceID;
    int racerID;
    DisplayTable     displayResults;

    protected       Font largeFont  = new Font("Arial", Font.PLAIN, 24);

    public ResultRacerShow(int raceID,int racerID,RaceDatabase db) {
        super();

        this.raceID  = raceID;
        this.racerID = racerID;

        database = db;

        //dialogContainer = this.getContentPane();

        this.setTitle("Show Racer's Results");

        this.setLayout(null);
        this.setResizable(false);

        this.setSize(400, 250);
        workingPanel = new JPanel();
        workingPanel.setLayout(null);
        workingPanel.setBounds(0,0,400,250);
        workingPanel.setBackground(java.awt.Color.WHITE);
        this.setContentPane(workingPanel);


        JLabel labelName = new JLabel("Name");
        labelName.setForeground(Color.blue);
        labelName.setFont(largeFont);
        labelName.setBounds(this.getPosX(0), this.getPosY(0), DIALOG_COL_WIDTH*2, 30);
        workingPanel.add(labelName);

        JLabel labelCar = new JLabel("Car #");
        labelCar.setForeground(Color.blue);
        labelCar.setFont(largeFont);
        //labelCar.setHorizontalTextPosition(JLabel.RIGHT);
        labelCar.setHorizontalAlignment(JLabel.RIGHT);
        labelCar.setBounds(this.getPosX(2), this.getPosY(0), DIALOG_COL_WIDTH, 30);
        workingPanel.add(labelCar);


        displayResults = new DisplayTable(workingPanel);

        displayResults.setStartY(this.getPosY(1));

        displayResults.setMaxRows(6);
        displayResults.setCellPadding(6);
        displayResults.setScrollBarEnabled(false);

        String cLabel[]     = new String[10];
        int cWidth[]        = new int[10];
        boolean cUsed[]     = new boolean[10];
        int cJustify[]      = new int[10];
        cLabel[0]   =   "Heat";        cWidth[0] = 70;          cUsed[0] = true;    cJustify[0] = DisplayTable.COLUMN_JUSTIFY_CENTER;
        cLabel[1]   =   "Lane";        cWidth[1] = 70;          cUsed[1] = true;    cJustify[1] = DisplayTable.COLUMN_JUSTIFY_CENTER;
        cLabel[2]   =   "Place";       cWidth[2] = 70;          cUsed[2] = true;    cJustify[2] = DisplayTable.COLUMN_JUSTIFY_CENTER;
        cLabel[3]   =   "Time";        cWidth[3] = 90;          cUsed[3] = true;    cJustify[3] = DisplayTable.COLUMN_JUSTIFY_RIGHT;
        cLabel[4]   =   "Points";      cWidth[4] = 70;          cUsed[4] = true;    cJustify[4] = DisplayTable.COLUMN_JUSTIFY_CENTER;
        displayResults.setupColumns(5,cWidth,cLabel,cJustify,cUsed);
        displayResults.addHeader();

        displayResults.createRowLines();

        System.out.println("ResultRacerShow added panel");


        String sql;
        sql = "SELECT results.heat,results.lane,roster.carid,roster.lastname,roster.firstname,results.place,results.time,results.points" +
              " FROM roster,results WHERE results.raceid=" + raceID + " AND results.racerid=" + racerID + " AND roster.racerid=results.racerid" +
              " ORDER BY results.heat";

        ResultSet rs = database.execute(sql);
        if (rs == null) return;

        String name = "";
        String carno = "";


        try {
            int r = 0;
            while (rs.next()) {
                name = rs.getString(5) + " " + rs.getString(4);
                carno = "#" + rs.getString(3);

                displayResults.addRowData(r,0,rs.getString(1));
                displayResults.addRowData(r,1,rs.getString(2));
                displayResults.addRowData(r,2,rs.getString(6));
                displayResults.addRowData(r,3,String.format("%1$5.3f", rs.getDouble(7)));
                displayResults.addRowData(r,4,rs.getString(8));
                r++;
            }

        } catch (SQLException ex) {

        }

        labelName.setText(name);
        labelCar.setText(carno);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        super.actionPerformed(e);
    }

}
