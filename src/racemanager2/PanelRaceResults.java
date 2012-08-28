/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package racemanager2;

import RaceLibrary.RaceDatabase;
import RaceLibrary.RaceManager;
import racemanager2.dialogs.*;
import java.util.*;
import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import RaceLibrary.RaceConfig;
import RaceLibrary.DisplayTable;
import RaceLibrary.Records.RaceResults;

/**
 *
 * @author Brian
 */
public class PanelRaceResults extends BasicWorkingPanel {

    public static final boolean DEBUG   = false;

    JLabel                  label;
    JComboBox               listRaces;
    JLabel                  labelSelectMain;
    JButton                 buttonPrintAll;
    JButton                 buttonPrintRacers;
    
    int                     currentRaceID;
    RaceResults             rr;

    int                     nRaces;
    int                     comboRaceID[];
    int                     comboRaceHeats[];
    DisplayTableResults     displayResults;
    int                     resultOffset;
    int                     resultSize;

    public PanelRaceResults(Rectangle rect,RaceDatabase database) {
        super(rect);

        int width;
        int x;
        this.database = database;
        comboRaceID         = new int[100];
        comboRaceHeats      = new int[100];

        String title = "Race Results";
        addStandardTitle(title);

        width = PWIDTH*8;
//        x = (this.getWidth() - width) / 2;
        x = 20;
        JPanel panelRaces = new JPanel();
        panelRaces.setBounds(x,this.getPosY(1),width,PHEIGHT*15/10);
        panelRaces.setBackground(Color.cyan);
        panelRaces.setLayout(null);

        label = new JLabel("Select Race");
        label.setBounds(10,5,PWIDTH*2,PHEIGHT);
        label.setHorizontalAlignment(JLabel.CENTER);
        panelRaces.add(label);

        buttonPrintAll = new JButton("Print Overall");
        buttonPrintAll.setVerticalTextPosition(AbstractButton.CENTER);
        buttonPrintAll.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
        //buttonApply.setMnemonic(KeyEvent.VK_D);
        buttonPrintAll.setActionCommand("printall");
        buttonPrintAll.setBounds(this.getPosX(7),this.getPosY(1),PWIDTH*26/10,PHEIGHT);
        buttonPrintAll.addActionListener(this);
        this.add(buttonPrintAll);

        buttonPrintRacers = new JButton("Print Racers");
        buttonPrintRacers.setVerticalTextPosition(AbstractButton.CENTER);
        buttonPrintRacers.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
        //buttonApply.setMnemonic(KeyEvent.VK_D);
        buttonPrintRacers.setActionCommand("printracers");
        buttonPrintRacers.setBounds(this.getPosX(9),this.getPosY(1),PWIDTH*28/10,PHEIGHT);
        buttonPrintRacers.addActionListener(this);
        this.add(buttonPrintRacers);

        currentRaceID = 0;
        rr = null;

        String sql = "SELECT count(*) FROM races";
        ResultSet rs = database.execute(sql);
        if (rs == null) return;
        nRaces = 0;
        try {
            while (rs.next()) {
                nRaces = rs.getInt(1);
            }
        } catch (SQLException ex) {

        }

        String nameRaces[] = new String[nRaces+1];
        nameRaces[0] = "-Select Race-";
        comboRaceID[0] = 0;
        int race = 1;

        sql = "SELECT raceid,title,groupid,racetype,heats FROM races " +
              "ORDER BY groupid,racetype";
        rs = database.execute(sql);
        if (rs == null) return;
        try {
            while (rs.next()) {
                int gid = rs.getInt(3);
                comboRaceID[race]       = rs.getInt(1);
                comboRaceHeats[race]    = rs.getInt(5);
                String name = database.getRaceConfig(RaceDatabase.DB_CFGID_GROUP1_NAME + ((gid-1)*10));
                switch (rs.getInt(4)) {
                    default :
                    case RaceDatabase.DB_RACETYPE_MAIN_EVENT :
                        name = name + " : Main Event";
                        break;
                }
                nameRaces[race] = name;
                race++;
            }
        } catch (SQLException ex) {

        }

        listRaces = new JComboBox(nameRaces);
        listRaces.setBounds(PWIDTH*3,5,PWIDTH*4,PHEIGHT);
        listRaces.setSelectedIndex(0);
        listRaces.addActionListener(this);
        listRaces.setActionCommand("groupmain");
        panelRaces.add(listRaces);

        this.add(panelRaces);

        displayResults = new DisplayTableResults(this);

        displayResults.setStartY(this.getPosY(3));

        displayResults.setMaxRows(20);
        displayResults.setCellPadding(6);

        String cLabel[]     = new String[10];
        int cWidth[]        = new int[10];
        boolean cUsed[]     = new boolean[10];
        int cJustify[]      = new int[10];
        cLabel[0]   =   "Place";        cWidth[0] = 80;          cUsed[0] = true;    cJustify[0] = DisplayTable.COLUMN_JUSTIFY_RIGHT;
        cLabel[1]   =   "Name";         cWidth[1] = 240;         cUsed[1] = true;    cJustify[1] = DisplayTable.COLUMN_JUSTIFY_LEFT;
        cLabel[2]   =   "Car#";         cWidth[2] = 60;          cUsed[2] = true;    cJustify[2] = DisplayTable.COLUMN_JUSTIFY_CENTER;
        cLabel[3]   =   "Avg Time";     cWidth[3] = 80;         cUsed[3] = true;    cJustify[3] = DisplayTable.COLUMN_JUSTIFY_RIGHT;
        cLabel[4]   =   "Best Time";    cWidth[4] = 80;         cUsed[4] = true;    cJustify[4] = DisplayTable.COLUMN_JUSTIFY_RIGHT;
        displayResults.setupColumns(5,cWidth,cLabel,cJustify,cUsed);
        displayResults.addHeader();

        displayResults.createRowLines();
        displayResults.addButtons();

        resultOffset    = 0;
        resultSize      = 0;
    }

    public void display() {
        super.display();

        displayResults.clearCellData();
        if (currentRaceID == 0) {
            buttonPrintAll.setEnabled(false);
            buttonPrintRacers.setEnabled(false);
            return;
        }

        //buttonPrintAll.setEnabled(true);
        buttonPrintRacers.setEnabled(true);

        rr = raceManager.loadResults(currentRaceID);

        int r = 0;
        int line = 0;

        while (r < rr.nRacers) {
            line = resultOffset + r;
            if (line < rr.nRacers) {

                displayResults.addRowRef(r, String.valueOf(rr.racerID[line]));

                displayResults.addRowData(r,0,String.valueOf(rr.place[line]));
                displayResults.addRowData(r,1,rr.name[line]);
                displayResults.addRowData(r,2,String.valueOf(rr.carID[line]));
                displayResults.addRowData(r,3,String.format("%1$6.3f", rr.avgTime[line]));
                displayResults.addRowData(r,4,String.format("%1$6.3f", rr.bestTime[line]));
            }
            r++;
        }

    }

    protected void selectNewRace() {
        int select = listRaces.getSelectedIndex();

        if (currentRaceID == comboRaceID[select]) return;

        currentRaceID   = comboRaceID[select];
        resultSize      = comboRaceHeats[select];
        displayResults.setScrollBarSize(resultSize);

    }

    public boolean checkMainRaceData(int ngroup) {

        String sql = "SELECT raceid FROM races WHERE groupid=" +
                     String.valueOf(ngroup) + " AND racetype=1";
        ResultSet rs = database.execute(sql);
        if (rs == null) return false;

        try {
            while (rs.next()) {
                if (rs.getInt(1) > 0) return true;
            }
        } catch (SQLException ex) {
        }
        return false;

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (DEBUG) System.out.println(e.getActionCommand());

        if ("groupmain".equals(e.getActionCommand())) {
            selectNewRace();
            display();
        }

        if ("printracers".equals(e.getActionCommand())) {
            if (currentRaceID == 0) return;
            if (false) return;
            PrintoutManager pmgr = new PrintoutManager(database,raceManager);
            //pmgr.setRaceResults(rr);
            pmgr.printRacers(currentRaceID);
        }

    }

    public void showRacer(int nShow) {
        if (nShow < 0) return;

        String sracerID = displayResults.getRowRef(nShow);
        int racerID = Integer.valueOf(sracerID);

        ResultRacerShow showRacer = new ResultRacerShow(currentRaceID,racerID,database);
        showRacer.setMenuTree(menuTree);
        showRacer.display();
        showRacer.setLocationRelativeTo(this);
        showRacer.setVisible(true);
    }

    public void printRacer(int nPrint) {
        if (nPrint < 0) return;
        String sracerID = displayResults.getRowRef(nPrint);
        int racerID = Integer.valueOf(sracerID);
        PrintoutManager pmgr = new PrintoutManager(database,raceManager);
        pmgr.printRacerSingle(currentRaceID,racerID);
    }    
    
    public void setScroll(int value) {
        resultOffset = value;

        if (DEBUG) System.out.println("schedule setScroll  value=" + String.valueOf(value));

        display();
    }

    public class DisplayTableResults extends DisplayTable {

        javax.swing.ImageIcon iconShow;
        javax.swing.ImageIcon iconPrint;

        JButton buttonShow[];
        JButton buttonPrint[];

        Rectangle       rectShow;
        Rectangle       rectPrint;

        public DisplayTableResults(JPanel panel) {
            super(panel);

            iconShow    = new javax.swing.ImageIcon(getClass().getResource("images/buttonShow.png"));
            iconPrint   = new javax.swing.ImageIcon(getClass().getResource("images/buttonPrint.png"));

            rectShow    = new Rectangle();
            rectShow.width      = iconShow.getIconWidth()+2;
            rectShow.height     = iconShow.getIconHeight();

            rectPrint    = new Rectangle();
            rectPrint.width      = iconShow.getIconWidth()+2;
            rectPrint.height     = iconShow.getIconHeight();
        }

        @Override
        public void addButtons() {

            rectShow.x          = ICON_PADDING;
            rectShow.y          = 0;

            rectPrint.x          = ICON_PADDING + rectShow.x + rectShow.width;
            rectPrint.y          = 0;

            buttonShow          = new JButton[maxRows];
            buttonPrint         = new JButton[maxRows];

            for (int r=0;r<maxRows;r++) {
                String tmp;

                buttonShow[r]        = new JButton(iconShow);
                tmp = "show-" + String.valueOf(r);
                buttonShow[r].setActionCommand(tmp);
                buttonShow[r].addActionListener(this);
                buttonShow[r].setBounds(rectShow);
                buttonShow[r].setVisible(false);
                tmp = "Show Racer's Individual Races";
                buttonShow[r].setToolTipText(tmp);
                panelRow[r].add(buttonShow[r]);

                buttonPrint[r]        = new JButton(iconPrint);
                tmp = "print-" + String.valueOf(r);
                buttonPrint[r].setActionCommand(tmp);
                buttonPrint[r].addActionListener(this);
                buttonPrint[r].setBounds(rectPrint);
                buttonPrint[r].setVisible(false);
                tmp = "Print Racer's Individual Card";
                buttonPrint[r].setToolTipText(tmp);
                panelRow[r].add(buttonPrint[r]);
            }
        }

        protected void clearExtraRow(int row) {
            if (buttonShow!=null)           {
                buttonShow[row].setVisible(false);
                buttonPrint[row].setVisible(false);
            }
        }

        public void addRowData(int r,int c,String data) {
            if (r < 0 || r >= maxRows) return;

            if (buttonShow!=null)           buttonShow[r].setVisible(true);
            if (buttonShow!=null)           buttonPrint[r].setVisible(true);

            super.addRowData(r, c, data);
        }

        public void actionPerformed(ActionEvent e) {
            if (DEBUG) System.out.println("Display Roster actionPreformed()");
            if (DEBUG) System.out.println(e.getActionCommand().substring(0,4));

            if ("show-".equals(e.getActionCommand().substring(0,5))) {
                int nShow = Integer.valueOf(e.getActionCommand().substring(5));
                PanelRaceResults tmp = (PanelRaceResults) panelWorking;
                tmp.showRacer(nShow);
            }

            if ("print-".equals(e.getActionCommand().substring(0,6))) {
                int nPrint = Integer.valueOf(e.getActionCommand().substring(6));
                PanelRaceResults tmp = (PanelRaceResults) panelWorking;
                tmp.printRacer(nPrint);
            }
        }

        @Override
        protected void scrollBarNewValue(int value) {
            PanelRaceResults tmp = (PanelRaceResults) panelWorking;
            tmp.setScroll(value);
        }

    }


}
