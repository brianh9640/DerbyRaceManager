/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package racemanager2;

import RaceLibrary.RaceDatabase;
import RaceLibrary.LayoutTable;
import RaceLibrary.Comms.RaceProtocol;
import java.sql.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.Timer;

import RaceLibrary.RaceConfig;
import RaceLibrary.DisplayTable;
import RaceLibrary.RaceManager;

import racemanager2.dialogs.RaceResultsEdit;

/**
 *
 * @author Brian
 */
public class PanelRaceGroup extends BasicWorkingPanel {

    int ngroup;

    JLabel labelGroup;

    JComboBox       comboRaces;
    JButton         buttonGroup;

    JPanel          panelTimer;
    JButton         buttonCommStart;
    JButton         buttonCommStop;
    Rectangle       rectLane[];
    JLabel          labelLaneStatus[];
    JLabel          labelGateStatus;
    JLabel          labelCommStatus;
    int             commCounterLast;
    int             commLabelLastState;

    JButton         buttonHeatAccept;
    JButton         buttonHeatChange;
    JButton         buttonHeatClear;
    JButton         buttonHeatRandom;
    JButton         buttonHeatNext;
    JButton         buttonHeatPrev;
    javax.swing.ImageIcon iconNext;
    javax.swing.ImageIcon iconPrev;

    JLabel          labelRaceTimerStatus;
    JLabel          labelRaceTimer;

    JLabel          labelHeatCounter;
    JLabel          labelRacer[];
    JLabel          labelPlace[];
    JLabel          labelPoints[];
    JLabel          labelTime[];
    JLabel          labelSpeed[];

    LayoutTable     tableResults;
    int             nLanes;
    int             currentRaceID;
    int             nRaces;
    int             comboRaceID[];
    int             ngroups;

    DisplayTable    displaySchedule;

    protected       Font fontTitle  = new Font("Arial", Font.BOLD, 18);


    protected       javax.swing.ImageIcon   iconLightBlank;
    protected       javax.swing.ImageIcon   iconLightGrey;
    protected       javax.swing.ImageIcon   iconLightGreen;
    protected       javax.swing.ImageIcon   iconLightRed;
    protected       javax.swing.ImageIcon   iconLightYellow;


    public PanelRaceGroup(Rectangle rect,RaceDatabase database) {
        super(rect);

        int width;
        int x;

        this.database = database;
        comboRaceID = new int[100];

        JLabel label;
        ngroup = 1;
        nRaces = 0;
        currentRaceID = 0;

        String title = "Race Control / Status";
        addStandardTitle(title);

        nLanes = database.getRaceConfigInt(RaceDatabase.DB_CFGID_NLANES);

        labelGroup = new JLabel("Group:",JLabel.RIGHT);
        labelGroup.setBounds(this.getWidth()-PWIDTH*4-10,1,PWIDTH*4,PHEIGHT);
        this.add(labelGroup);

        ngroups = database.getRaceConfigInt(RaceDatabase.DB_CFGID_NGROUPS);

        createRacePanel();

        createResultsPanel();

        createRaceTimerPanel();

        createDisplayPanel();

        createSchedulePanel();

        Timer timer = new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                display();
            }
        });
        timer.start();

    }

    protected void createRacePanel() {

        JPanel panelRace = new JPanel();
        panelRace.setBounds(10,this.getPosY(1),PWIDTH*8,PHEIGHT*25/10);
        panelRace.setBackground(Color.cyan);
        panelRace.setLayout(null);

        JLabel label = new JLabel("Current Race");
        label.setBounds(0,2,panelRace.getWidth(),15);
        label.setHorizontalAlignment(JLabel.CENTER);
        panelRace.add(label);

//        int ngroups = database.getRaceConfigInt(RaceDatabase.DB_CFGID_NGROUPS);
//        if (ngroups < 1 || ngroups > RaceConfig.RACECFG_MAX_GROUPS) ngroups = 1;
//
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
                comboRaceID[race] = rs.getInt(1);
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
        
        comboRaces = new JComboBox(nameRaces);
        comboRaces.setBounds(5,20,PWIDTH*5,PHEIGHT);
        panelRace.add(comboRaces);

        currentRaceID = database.getRaceConfigInt(RaceDatabase.DB_CFGID_CURRENT_RACEID);
        int index = 0;
        if (currentRaceID >= 0) {
            while (index < nRaces && currentRaceID != comboRaceID[index]) {
                index++;
            }
        }
        comboRaces.setSelectedIndex(index);

        buttonGroup = new JButton("Select");
        buttonGroup.setVerticalTextPosition(AbstractButton.CENTER);
        buttonGroup.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
        //buttonApply.setMnemonic(KeyEvent.VK_D);
        buttonGroup.setActionCommand("select");
        buttonGroup.setBounds(12+PWIDTH*5,20,PWIDTH*2,PHEIGHT);
        buttonGroup.addActionListener(this);
        panelRace.add(buttonGroup);

        this.add(panelRace);

    }

    protected void createRaceTimerPanel() {
        JLabel label;

        commCounterLast = 0;
        commLabelLastState = 0;

        iconLightBlank      = new javax.swing.ImageIcon(getClass().getResource("images/lightBlank.png"));
        iconLightRed        = new javax.swing.ImageIcon(getClass().getResource("images/lightRed.png"));
        iconLightGreen      = new javax.swing.ImageIcon(getClass().getResource("images/lightGreen.png"));
        iconLightYellow     = new javax.swing.ImageIcon(getClass().getResource("images/lightYellow.png"));
        iconLightGrey       = new javax.swing.ImageIcon(getClass().getResource("images/lightGrey.png"));

        panelTimer = new JPanel();
        int width = PWIDTH*7-40;
        panelTimer.setBounds(this.getPosX(6)+20,this.getPosY(1),width,PHEIGHT*35/10);
        panelTimer.setBackground(Color.cyan);
        panelTimer.setLayout(null);

        label = new JLabel("Race Timer Comms");
        label.setBounds(0,2,panelTimer.getWidth(),15);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setFont(fontTitle);
        panelTimer.add(label);

        labelRaceTimerStatus = new JLabel("--status--");
        labelRaceTimerStatus.setBounds(30,23,PWIDTH*3,PHEIGHT);
        labelRaceTimerStatus.setHorizontalAlignment(JLabel.LEFT);
        panelTimer.add(labelRaceTimerStatus);

        Rectangle rGate = new Rectangle();
        rGate.width = iconLightBlank.getIconWidth();
        rGate.height = iconLightBlank.getIconHeight();
        rGate.x = 2;
        rGate.y = 20;
        labelGateStatus = new JLabel();
        labelGateStatus.setIcon(iconLightBlank);
        labelGateStatus.setBounds(rGate);
        panelTimer.add(labelGateStatus);

        Rectangle rComm = new Rectangle();
        rComm.width = iconLightBlank.getIconWidth();
        rComm.height = iconLightBlank.getIconHeight();
        rComm.x = width - 2 - rComm.width;
        rComm.y = 2;
        labelCommStatus = new JLabel();
        labelCommStatus.setIcon(iconLightBlank);
        labelCommStatus.setBounds(rComm);
        panelTimer.add(labelCommStatus);


        labelRaceTimer = new JLabel("Timer -.--- sec");
        labelRaceTimer.setBounds(5,45,PWIDTH*3,PHEIGHT);
        labelRaceTimer.setHorizontalAlignment(JLabel.LEFT);
        panelTimer.add(labelRaceTimer);

        labelLaneStatus     = new JLabel[nLanes];
        rectLane            = new Rectangle[nLanes];
        for (int n=0;n<nLanes;n++) {
            rectLane[n] = new Rectangle();
            rectLane[n].width = iconLightBlank.getIconWidth();
            rectLane[n].height = iconLightBlank.getIconHeight();
            rectLane[n].x = 5+PWIDTH*3 + (rectLane[n].width + 5)*n;
            rectLane[n].y = 45;
            labelLaneStatus[n] = new JLabel();
            labelLaneStatus[n].setIcon(iconLightBlank);
            labelLaneStatus[n].setBounds(rectLane[n]);

            String txt = String.valueOf(n+1);
            label = new JLabel(txt);
            Rectangle rlane = new Rectangle(rectLane[n]);
            rlane.y = 25;
            label.setBounds(rlane);
            label.setHorizontalAlignment(JLabel.CENTER);
            panelTimer.add(label);

            panelTimer.add(labelLaneStatus[n]);
        }

        this.add(panelTimer);

    }

    protected void createResultsPanel() {

        JLabel label;

        int width = PWIDTH*10;
        int x = (this.getWidth() - width) / 2;
        x = 10;

        JPanel panelResults = new JPanel();
        panelResults.setBounds(x,this.getPosY(4),width,PHEIGHT*(7+nLanes));
        panelResults.setBackground(Color.LIGHT_GRAY);
        panelResults.setLayout(null);

        label = new JLabel("Race Results");
        label.setFont(fontTitle);
        label.setBounds(0,2,panelResults.getWidth(),18);
        label.setHorizontalAlignment(JLabel.CENTER);
        panelResults.add(label);

        labelHeatCounter = new JLabel("Heat - of -");
        labelHeatCounter.setBounds(50,2,100,18);
        panelResults.add(labelHeatCounter);

        iconNext    = new javax.swing.ImageIcon(getClass().getResource("images/buttonNext.png"));
        iconPrev    = new javax.swing.ImageIcon(getClass().getResource("images/buttonPrev.png"));

        buttonHeatNext = new JButton(iconNext);
        buttonHeatNext.setVerticalTextPosition(AbstractButton.CENTER);
        buttonHeatNext.setActionCommand("heatnext");
        buttonHeatNext.setBounds(27,2,22,20);
        //buttonHeatNext.setBackground(Color.green);
        buttonHeatNext.addActionListener(this);
        panelResults.add(buttonHeatNext);

        buttonHeatPrev = new JButton(iconPrev);
        buttonHeatPrev.setVerticalTextPosition(AbstractButton.CENTER);
        buttonHeatPrev.setActionCommand("heatprev");
        buttonHeatPrev.setBounds(4,2,22,20);
        //buttonHeatPrev.setBackground(Color.green);
        buttonHeatPrev.addActionListener(this);
        panelResults.add(buttonHeatPrev);


        tableResults = new LayoutTable();
        tableResults.setStartY(30);

        int nColumns        = 6;
        String cLabel[]     = new String[10];
        int cWidth[]        = new int[10];
        boolean cUsed[]     = new boolean[10];
        int cJustify[]      = new int[10];
        cLabel[0]   =   "Lane";        cWidth[0] = 40;         cUsed[0] = true;    cJustify[0] = DisplayTable.COLUMN_JUSTIFY_CENTER;
        cLabel[1]   =   "Racer";       cWidth[1] = 140;        cUsed[1] = true;    cJustify[1] = DisplayTable.COLUMN_JUSTIFY_CENTER;
        cLabel[2]   =   "Place";       cWidth[2] = 50;         cUsed[2] = true;    cJustify[2] = DisplayTable.COLUMN_JUSTIFY_LEFT;
        cLabel[3]   =   "Points";      cWidth[3] = 50;         cUsed[3] = true;    cJustify[3] = DisplayTable.COLUMN_JUSTIFY_LEFT;
        cLabel[4]   =   "Time";        cWidth[4] = 50;         cUsed[4] = true;    cJustify[4] = DisplayTable.COLUMN_JUSTIFY_LEFT;
        cLabel[5]   =   "Speed";       cWidth[5] = 50;         cUsed[5] = true;    cJustify[5] = DisplayTable.COLUMN_JUSTIFY_LEFT;

        tableResults.setupColumns(nColumns,cWidth, cLabel, cJustify, cUsed);

        JLabel labelLane = new JLabel();
        labelRacer = new JLabel[nLanes];
        labelPlace = new JLabel[nLanes];
        labelPoints = new JLabel[nLanes];
        labelTime = new JLabel[nLanes];
        labelSpeed = new JLabel[nLanes];

        for (int c=0;c<nColumns;c++) {
            label = tableResults.getTitleLabel(c);      panelResults.add(label);
        }
        for (int r=0;r<nLanes;r++) {
            labelLane = new JLabel(String.valueOf(r+1));
            labelLane.setHorizontalAlignment(JLabel.CENTER);
            labelLane.setBounds(tableResults.getFieldRect(r+1,0));
            panelResults.add(labelLane);

            labelRacer[r] = new JLabel();
            labelRacer[r].setBounds(tableResults.getFieldRect(r+1,1));
            labelRacer[r].setHorizontalAlignment(JLabel.LEFT);
            panelResults.add(labelRacer[r]);

            labelPlace[r] = new JLabel();
            labelPlace[r].setBounds(tableResults.getFieldRect(r+1,2));
            labelPlace[r].setHorizontalAlignment(JLabel.CENTER);
            panelResults.add(labelPlace[r]);

            labelPoints[r] = new JLabel("-");
            labelPoints[r].setBounds(tableResults.getFieldRect(r+1,3));
            labelPoints[r].setHorizontalAlignment(JLabel.CENTER);
            panelResults.add(labelPoints[r]);

            labelTime[r] = new JLabel();
            labelTime[r].setBounds(tableResults.getFieldRect(r+1,4));
            labelTime[r].setHorizontalAlignment(JLabel.RIGHT);
            panelResults.add(labelTime[r]);

            labelSpeed[r] = new JLabel();
            labelSpeed[r].setBounds(tableResults.getFieldRect(r+1,5));
            labelSpeed[r].setHorizontalAlignment(JLabel.RIGHT);
            panelResults.add(labelSpeed[r]);
         }

        buttonHeatAccept = new JButton("Accept/Next");
        buttonHeatAccept.setVerticalTextPosition(AbstractButton.CENTER);
        buttonHeatAccept.setActionCommand("heataccept");
        buttonHeatAccept.setBounds(10,30+PHEIGHT*(nLanes+2),PWIDTH*3,PHEIGHT);
        buttonHeatAccept.setBackground(Color.green);
        buttonHeatAccept.addActionListener(this);
        panelResults.add(buttonHeatAccept);

        buttonHeatChange = new JButton("Edit Results");
        buttonHeatChange.setVerticalTextPosition(AbstractButton.CENTER);
        buttonHeatChange.setActionCommand("heatchange");
        buttonHeatChange.setBounds(panelResults.getWidth()-PWIDTH*3-15,30+PHEIGHT*(nLanes+2),PWIDTH*3,PHEIGHT);
        buttonHeatChange.setBackground(Color.magenta);
        buttonHeatChange.addActionListener(this);
        panelResults.add(buttonHeatChange);

        width = PWIDTH*24/10;
        buttonHeatClear = new JButton("Clear");
        buttonHeatClear.setVerticalTextPosition(AbstractButton.CENTER);
        buttonHeatClear.setActionCommand("heatclear");
        buttonHeatClear.setBounds((panelResults.getWidth()-width)/2,30+PHEIGHT*(nLanes+2),width,PHEIGHT);
        buttonHeatClear.setBackground(Color.ORANGE);
        buttonHeatClear.addActionListener(this);
        panelResults.add(buttonHeatClear);

        buttonHeatRandom = new JButton("Random");
        buttonHeatRandom.setVerticalTextPosition(AbstractButton.CENTER);
        buttonHeatRandom.setActionCommand("heatrandom");
        buttonHeatRandom.setBounds((panelResults.getWidth()-width)/2,35+PHEIGHT*(nLanes+3),width,PHEIGHT);
        buttonHeatRandom.setBackground(Color.red);
        buttonHeatRandom.addActionListener(this);
        panelResults.add(buttonHeatRandom);

        this.add(panelResults);

    }

    protected void createDisplayPanel() {
        JLabel label;

        int width = PWIDTH*5;
        int x = (this.getWidth() - width) - 20;

        JPanel panelDisplay = new JPanel();
        panelDisplay.setBounds(x,this.getPosY(4),width,PHEIGHT*9);
        panelDisplay.setBackground(Color.blue);
        panelDisplay.setLayout(null);

        label = new JLabel("Display Controls");
        label.setFont(fontTitle);
        label.setBounds(0,2,panelDisplay.getWidth(),18);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setForeground(Color.white);
        panelDisplay.add(label);

        int ht = PHEIGHT * 11 / 10;
        width = PWIDTH*45/10;
        x = (panelDisplay.getWidth() - width)/2;
        Color bColor = new Color(170,170,255);
        JButton button;

        button = new JButton("Show Title");
        button.setVerticalTextPosition(AbstractButton.CENTER);
        button.setActionCommand("displaytitle");
        button.setBounds(x,ht*1,width,PHEIGHT);
        button.setBackground(bColor);
        button.addActionListener(this);
        panelDisplay.add(button);

        button = new JButton("Show Roster");
        button.setVerticalTextPosition(AbstractButton.CENTER);
        button.setActionCommand("displayroster");
        button.setBounds(x,ht*2,width,PHEIGHT);
        button.setBackground(bColor);
        button.addActionListener(this);
        panelDisplay.add(button);

        button = new JButton("Show Schedule");
        button.setVerticalTextPosition(AbstractButton.CENTER);
        button.setActionCommand("displayschedule");
        button.setBounds(x,ht*3,width,PHEIGHT);
        button.setBackground(bColor);
        button.addActionListener(this);
        panelDisplay.add(button);

        button = new JButton("Show Current Race");
        button.setVerticalTextPosition(AbstractButton.CENTER);
        button.setActionCommand("displayrace");
        button.setBounds(x,ht*4,width,PHEIGHT);
        button.setBackground(bColor);
        button.addActionListener(this);
        panelDisplay.add(button);

//        button = new JButton("Show Top 3 Results");
//        button.setVerticalTextPosition(AbstractButton.CENTER);
//        button.setActionCommand("displayresultstop");
//        button.setBounds(x,ht*5,width,PHEIGHT);
//        button.setBackground(bColor);
//        button.addActionListener(this);
//        panelDisplay.add(button);

        int widthPlace = width / 3;

        button = new JButton("3rd");
        button.setVerticalTextPosition(AbstractButton.CENTER);
        button.setActionCommand("display3rdPlace");
        button.setBounds(x,ht*5,widthPlace,PHEIGHT);
        button.setBackground(bColor);
        button.addActionListener(this);
        panelDisplay.add(button);

        button = new JButton("2nd");
        button.setVerticalTextPosition(AbstractButton.CENTER);
        button.setActionCommand("display2ndPlace");
        button.setBounds(x+widthPlace,ht*5,widthPlace,PHEIGHT);
        button.setBackground(bColor);
        button.addActionListener(this);
        panelDisplay.add(button);

        button = new JButton("1st");
        button.setVerticalTextPosition(AbstractButton.CENTER);
        button.setActionCommand("display1stPlace");
        button.setBounds(x+widthPlace*2,ht*5,widthPlace,PHEIGHT);
        button.setBackground(bColor);
        button.addActionListener(this);
        panelDisplay.add(button);

        button = new JButton("Display Results");
        button.setVerticalTextPosition(AbstractButton.CENTER);
        button.setActionCommand("displayresults");
        button.setBounds(x,ht*6,width,PHEIGHT);
        button.setBackground(bColor);
        button.addActionListener(this);
        panelDisplay.add(button);

        button = new JButton("Refresh");
        button.setVerticalTextPosition(AbstractButton.CENTER);
        button.setActionCommand("displayrefresh");
        button.setBounds(x,ht*7,width,PHEIGHT);
        button.setBackground(bColor);
        button.addActionListener(this);
        panelDisplay.add(button);

        this.add(panelDisplay);

    }

    protected void createSchedulePanel() {

        int width = this.getWidth()-10;
        int x = (this.getWidth() - width) / 2;

        JPanel panelSchedule = new JPanel();
        panelSchedule.setBounds(x,this.getPosY(7+5),width,PHEIGHT*(5+3));
        panelSchedule.setBackground(Color.lightGray);
        panelSchedule.setLayout(null);

        displaySchedule = new DisplayTable(panelSchedule);

        displaySchedule.setStartY(10);

        displaySchedule.setMaxRows(5);
        displaySchedule.setCellPadding(6);
        displaySchedule.setScrollBarEnabled(false);

        String cLabel[]     = new String[10];
        int cWidth[]        = new int[10];
        boolean cUsed[]     = new boolean[10];
        int cJustify[]      = new int[10];
        cLabel[0]   =   "Heat";         cWidth[0] = 40;          cUsed[0] = true;    cJustify[0] = DisplayTable.COLUMN_JUSTIFY_CENTER;
        cLabel[1]   =   "Status";       cWidth[1] = 60;          cUsed[1] = true;    cJustify[1] = DisplayTable.COLUMN_JUSTIFY_CENTER;
        cLabel[2]   =   "Lane #1";      cWidth[2] = 120;         cUsed[2] = true;    cJustify[2] = DisplayTable.COLUMN_JUSTIFY_LEFT;
        cLabel[3]   =   "Lane #2";      cWidth[3] = 120;         cUsed[3] = true;    cJustify[3] = DisplayTable.COLUMN_JUSTIFY_LEFT;
        cLabel[4]   =   "Lane #3";      cWidth[4] = 120;         cUsed[4] = true;    cJustify[4] = DisplayTable.COLUMN_JUSTIFY_LEFT;
        cLabel[5]   =   "Lane #4";      cWidth[5] = 120;         cUsed[5] = true;    cJustify[5] = DisplayTable.COLUMN_JUSTIFY_LEFT;
        displaySchedule.setupColumns(6,cWidth,cLabel,cJustify,cUsed);
        displaySchedule.addHeader();

        displaySchedule.createRowLines();

        this.add(panelSchedule);

    }

//    public void setGroup(Integer n) {
//        ngroup = n;
//    }
//
    public void setManager(RaceManager mgr) {
        raceManager = mgr;
        raceManager.loadCurrentHeat();

        displaySchedule();
    }

    public void display() {
        String txt;
        RaceManager.LaneInfo laneInfo;

        super.display();

        if (DEBUG) System.out.println("PanelRaceGroup.display() called");
      
        if (ngroups < 1 || ngroups > RaceConfig.RACECFG_MAX_GROUPS) ngroups = 1;

        String name = "RaceID: " + raceManager.getRaceID();
        labelGroup.setText(name);

        if (!raceManager.heatsCompleted()) {
            txt = "Heat " + String.valueOf(raceManager.getCurrentHeat()) + " of " + String.valueOf(raceManager.getHeats());
            buttonHeatNext.setEnabled(true);
            buttonHeatAccept.setEnabled(true);
            buttonHeatChange.setEnabled(true);
        }
        else {
            txt = "Completed";
            buttonHeatNext.setEnabled(false);
            buttonHeatAccept.setEnabled(false);
            buttonHeatChange.setEnabled(false);
        }
        labelHeatCounter.setText(txt);

        switch (raceManager.getRaceStage()) {
            default :
            case RaceManager.STAGE_NOT_READY :
                labelRaceTimerStatus.setText("Timer Not Ready");
                labelGateStatus.setIcon(iconLightGrey);
                break;
            case RaceManager.STAGE_READY :
                labelRaceTimerStatus.setText("Timer Ready");
                labelGateStatus.setIcon(iconLightGreen);
                break;
            case RaceManager.STAGE_RACING :
            case RaceManager.STAGE_LANES_FINISHED :
                labelRaceTimerStatus.setText("--RACING--");
                labelGateStatus.setIcon(iconLightRed);
                break;
        }
        labelRaceTimer.setText(String.format("Timer %1$6.3f sec", raceManager.getRaceCurrentTimer()));

        for (int n=0;n<nLanes;n++) {
            if (raceManager.getRaceLaneFinished(n+1)) labelLaneStatus[n].setIcon(iconLightGreen);
            else labelLaneStatus[n].setIcon(iconLightGrey);
        }

        if (commCounterLast != raceManager.getCounter()) {
            commCounterLast = raceManager.getCounter();
            if (commLabelLastState == 0) {
                labelCommStatus.setIcon(iconLightGrey);
                commLabelLastState = 1;
            }
            else {
                labelCommStatus.setIcon(iconLightYellow);
                commLabelLastState = 0;
            }
        }

        //if (raceManager.laneInfoChanged()) {
            for (int n=0;n<nLanes;n++) {
                laneInfo = raceManager.getHeatLane(n+1);
                if (laneInfo == null) continue;
                String tmp = String.format("%1$2d", laneInfo.carID) + ": " + laneInfo.racerName;
                labelRacer[n].setText(tmp);
                labelPlace[n].setText(String.valueOf(laneInfo.place));
                labelPoints[n].setText(String.valueOf(laneInfo.points));
                labelTime[n].setText(String.format("%1$5.3f",laneInfo.time));
                labelSpeed[n].setText(String.format("%1$5.1f",laneInfo.speed));
            }
        //}

        //displaySchedule();

        //paintTimer();
    }

    protected void displaySchedule() {
        String      sql;
        ResultSet   rs;
        int         offset = 0;

        sql = "SELECT results.heat,results.lane,results.racerid,roster.carid,roster.lastname,roster.firstname " +
                     "FROM results,roster WHERE results.raceid=" + String.valueOf(raceManager.getRaceID()) + " AND results.racerid=roster.racerid " +
                     "AND results.heat >=" + String.valueOf(raceManager.getCurrentHeat()-1) + " " +
                     "ORDER BY results.heat,results.lane";

        displaySchedule.clearCellData();

        if (DEBUG) System.out.println(sql);
        rs = database.execute(sql);
        if (rs == null) return;

        try {
            int r = 0;

            while (rs.next()) {
                String tmp;
                if (offset == 0) offset = rs.getInt(1);
                r = rs.getInt(1) - offset;
                displaySchedule.addRowData(r,0,rs.getString(1));
                displaySchedule.addRowData(r,1,"-");
                int lane = rs.getInt(2);

                if (DEBUG) {
                    System.out.print(r);
                    System.out.print(":");
                    System.out.println(lane);
                }
                int carID = rs.getInt(4);
                tmp = String.format("%1$2d", carID) + ": " + rs.getString(6) + " " + rs.getString(5);
                displaySchedule.addRowData(r,1+lane,tmp);
            }
        } catch (SQLException ex) {

        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (DEBUG) System.out.println(e.getActionCommand());

        if ("select".equals(e.getActionCommand())) {
            selectNewRace();
            display();
            displaySchedule();
            return;
        }

        if ("commstart".equals(e.getActionCommand())) {
            raceManager.start();
            display();
            displaySchedule();
            return;
        }

        if ("commstop".equals(e.getActionCommand())) {
            raceManager.stopManager();
            display();
            displaySchedule();
            return;
        }

        if ("heataccept".equals(e.getActionCommand())) {
            raceManager.nextHeat();
            display();
            displaySchedule();
            return;
        }

        if ("heatchange".equals(e.getActionCommand())) {

            RaceResultsEdit editResults = new RaceResultsEdit(raceManager);
            editResults.setDatabase(database);
            editResults.setMenuTree(menuTree);
            editResults.display();
            editResults.setLocationRelativeTo(this);
            editResults.setVisible(true);


//            for (int n=0;n<nLanes;n++) {
//                RaceManager.LaneInfo laneInfo = raceManager.getHeatLane(n+1);
//                if (fieldPlace[n].getText().isEmpty()) laneInfo.place = 0;
//                else    laneInfo.place  = Double.valueOf(fieldPlace[n].getText()).intValue();
//                if (fieldTime[n].getText().isEmpty()) laneInfo.time = 0.0;
//                else laneInfo.time   = Double.valueOf(fieldTime[n].getText());
//                raceManager.updateHeatLane(n+1,laneInfo);
//            }
//
//            int valid = raceManager.verifyCurrentHeat();
//            if (valid < 0) {
//                JOptionPane.showMessageDialog(this.menuTree.frameMain,
//                    "Invalid Race Results Entered",
//                    "ERROR",JOptionPane.ERROR_MESSAGE);
//
//                return;
//            }
//
//            raceManager.saveCurrentHeat();
//
//            JOptionPane.showMessageDialog(this.menuTree.frameMain,
//                "Race Results Updated",
//                "Information",JOptionPane.INFORMATION_MESSAGE);
//
//            raceManager.getCurrentHeat();

            display();
            displaySchedule();
            return;
        }

        if ("heatclear".equals(e.getActionCommand())) {

//            JOptionPane.showMessageDialog(this.menuTree.frameMain,
//                "Race Results Updated",
//                "Information",JOptionPane.INFORMATION_MESSAGE);

            int answer = JOptionPane.showConfirmDialog(
                    this.menuTree.frameMain,
                    "Clear this heat results?",
                    "Clear Heat",
                    JOptionPane.YES_NO_OPTION);

            if (answer == JOptionPane.NO_OPTION) return;

            for (int n=0;n<nLanes;n++) {
                RaceManager.LaneInfo laneInfo = raceManager.getHeatLane(n+1);
                laneInfo.place = 0;
                laneInfo.time = 0.0;
                laneInfo.points = 0;
                raceManager.updateHeatLane(n+1,laneInfo);
            }

            raceManager.saveCurrentHeat();

            raceManager.getCurrentHeat();

            display();
            return;
        }

        if ("heatrandom".equals(e.getActionCommand())) {
            raceManager.randomHeat();
            display();
            return;
        }


        if ("heatnext".equals(e.getActionCommand())) {
            raceManager.nextHeat();
            display();
            displaySchedule();
            return;
        }
        if ("heatprev".equals(e.getActionCommand())) {
            raceManager.prevHeat();
            display();
            displaySchedule();
            return;
        }
        if ("displaytitle".equals(e.getActionCommand())) {
            raceManager.sendDisplayCmd(RaceProtocol.PAGE_CHG_TITLE);
            return;
        }
        if ("displayroster".equals(e.getActionCommand())) {
            raceManager.sendDisplayCmd(RaceProtocol.PAGE_CHG_ROSTER);
            return;
        }
        if ("displayschedule".equals(e.getActionCommand())) {
            raceManager.sendDisplayCmd(RaceProtocol.PAGE_CHG_SCHEDULE);
            return;
        }
        if ("displayrace".equals(e.getActionCommand())) {
            raceManager.sendDisplayCmd(RaceProtocol.PAGE_CHG_RACE);
            return;
        }
        if ("display1stPlace".equals(e.getActionCommand())) {
            raceManager.sendDisplayCmd(RaceProtocol.PAGE_CHG_1ST_PLACE);
            return;
        }
        if ("display2ndPlace".equals(e.getActionCommand())) {
            raceManager.sendDisplayCmd(RaceProtocol.PAGE_CHG_2ND_PLACE);
            return;
        }
        if ("display3rdPlace".equals(e.getActionCommand())) {
            raceManager.sendDisplayCmd(RaceProtocol.PAGE_CHG_3RD_PLACE);
            return;
        }
        if ("displayresults".equals(e.getActionCommand())) {
            raceManager.sendDisplayCmd(RaceProtocol.PAGE_CHG_RESULTS_ALL);
            return;
        }
        if ("displayrefresh".equals(e.getActionCommand())) {
            raceManager.refreshDisplays();
            return;
        }

    }

    protected void selectNewRace() {
        int select = comboRaces.getSelectedIndex();

        if (currentRaceID == comboRaceID[select]) return;

        currentRaceID = comboRaceID[select];

        raceManager.loadRace(currentRaceID);
        // TODO - send commands out on new race
    }

}
