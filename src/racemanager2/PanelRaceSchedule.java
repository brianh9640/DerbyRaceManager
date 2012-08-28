/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package racemanager2;

import RaceLibrary.RaceDatabase;
import RaceLibrary.ScheduleRace;
import java.util.*;
import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import RaceLibrary.RaceConfig;
//import racemanager2.dialogs.ScheduleGroup;
import racemanager2.dialogs.DeleteMainSchedule;

/**
 *
 * @author Brian
 */
public class PanelRaceSchedule extends BasicWorkingPanel {

    public static final boolean DEBUG   = true;

    JLabel          label;
    JComboBox       listGroups;
    JButton         buttonDeleteMain;
    JButton         buttonCreateMain;
    JLabel          labelSelectMain;
    int             groupMain;

    String          stringMainMessage[];

    JLabel          labelGroupName[];
    JButton         buttonSchedule[];

    JButton         buttonApply;

    public PanelRaceSchedule(Rectangle rect,RaceDatabase database) {
        super(rect);

        int width;
        int x;
        this.database = database;

        labelGroupName = new JLabel[RaceConfig.RACECFG_MAX_GROUPS];
        buttonSchedule = new JButton[RaceConfig.RACECFG_MAX_GROUPS];

        String title = "Setup Race Schedule";
        addStandardTitle(title);

        width = PWIDTH*11;
        x = (this.getWidth() - width) / 2;
        JPanel panelGroup = new JPanel();
        panelGroup.setBounds(x,this.getPosY(2),width,PHEIGHT*6);
        panelGroup.setBackground(Color.cyan);
        panelGroup.setLayout(null);

        label = new JLabel("Create Main Race Schedule");
        label.setBounds(2,2,panelGroup.getWidth()-4,PHEIGHT);
        label.setHorizontalAlignment(JLabel.CENTER);
        panelGroup.add(label);

        label = new JLabel("Select Group");
        label.setBounds(10,25,PWIDTH*2,PHEIGHT);
        label.setHorizontalAlignment(JLabel.CENTER);
        panelGroup.add(label);

        groupMain = 0;
        int ngroups = database.getRaceConfigInt(RaceDatabase.DB_CFGID_NGROUPS);
        if (ngroups < 1 || ngroups > RaceConfig.RACECFG_MAX_GROUPS) ngroups = 1;
        String nameGroups[] = new String[ngroups+1];
        nameGroups[0] = "---";
        for (int g=0;g<ngroups;g++) {
            nameGroups[g+1] = String.valueOf(g+1) + ": " + database.getRaceConfig(RaceDatabase.DB_CFGID_GROUP1_NAME + (g*10));
        }
        listGroups = new JComboBox(nameGroups);
        listGroups.setBounds(10,45,PWIDTH*4,PHEIGHT);
        listGroups.setSelectedIndex(0);
        listGroups.addActionListener(this);
        listGroups.setActionCommand("groupmain");
        panelGroup.add(listGroups);

        stringMainMessage = new String[3];
        stringMainMessage[0] = "<html>To create a main race schedule, select the desired group.<html>";
        stringMainMessage[1] = "<html>An existing race schedule was found.  If a new schedule needs to be created, the old schedule and results must be deleted first.<html>";
        stringMainMessage[2] = "<html>Once the roster is complete for this group, click the Create Schedule below<html>";

        labelSelectMain = new JLabel(stringMainMessage[0]);
        labelSelectMain.setBounds(20+PWIDTH*4,18,PWIDTH*65/10,PHEIGHT*4);
        labelSelectMain.setHorizontalAlignment(JLabel.CENTER);
        labelSelectMain.setVisible(true);
        panelGroup.add(labelSelectMain);

        buttonCreateMain = new JButton("Create Schedule");
        buttonCreateMain.setForeground(Color.green);
        buttonCreateMain.setVerticalTextPosition(AbstractButton.CENTER);
        buttonCreateMain.setHorizontalTextPosition(AbstractButton.CENTER);
        buttonCreateMain.setActionCommand("createmain");
        buttonCreateMain.setBounds(12+PWIDTH*6,90,PWIDTH*4,PHEIGHT*1);
        buttonCreateMain.addActionListener(this);
        buttonCreateMain.setVisible(false);
        panelGroup.add(buttonCreateMain);

        buttonDeleteMain = new JButton("Delete Previous");
        buttonDeleteMain.setForeground(Color.red);
        buttonDeleteMain.setVerticalTextPosition(AbstractButton.CENTER);
        buttonDeleteMain.setHorizontalTextPosition(AbstractButton.CENTER);
        buttonDeleteMain.setActionCommand("deletemain");
        buttonDeleteMain.setBounds(12+PWIDTH*6,90,PWIDTH*4,PHEIGHT*1);
        buttonDeleteMain.addActionListener(this);
        buttonDeleteMain.setVisible(false);
        panelGroup.add(buttonDeleteMain);

        this.add(panelGroup);

    }

    public void display() {

        super.display();

        int ngroups = database.getRaceConfigInt((int) RaceDatabase.DB_CFGID_NGROUPS);

        if (ngroups < 1 || ngroups > RaceConfig.RACECFG_MAX_GROUPS) ngroups = 1;

        groupMain = listGroups.getSelectedIndex();
        if (groupMain == 0) {
            labelSelectMain.setText(stringMainMessage[0]);
            buttonCreateMain.setVisible(false);
            buttonDeleteMain.setVisible(false);
        }
        else {
            if (checkMainRaceData(groupMain)) {
                labelSelectMain.setText(stringMainMessage[1]);
                buttonCreateMain.setVisible(false);
                buttonDeleteMain.setVisible(true);
            }
            else {
                labelSelectMain.setText(stringMainMessage[2]);
                buttonCreateMain.setVisible(true);
                buttonDeleteMain.setVisible(false);
            }
        }

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
            display();
        }

        if ("createmain".equals(e.getActionCommand())) {
            createMainSchedule(groupMain);
            display();
            menuTree.configTree.setSelectionPath(menuTree.configTree.getPathForRow(6));

        }

        if ("deletemain".equals(e.getActionCommand())) {
            deleteMainSchedule(groupMain);
            display();
        }

    }

    public void deleteMainSchedule(int group) {

        DeleteMainSchedule deleteSchedule = new DeleteMainSchedule(group);
        deleteSchedule.setDatabase(database);
        deleteSchedule.setMenuTree(menuTree);
        deleteSchedule.setManager(raceManager);
        deleteSchedule.display();
        deleteSchedule.setLocationRelativeTo(this);
        deleteSchedule.setVisible(true);

    }


    protected void createMainSchedule(int ngroup) {

        int nLanes = database.getRaceConfigInt(RaceDatabase.DB_CFGID_NLANES);
        int nRacers = 0;
        int raceID = 0;

        String sql = "SELECT count(*) FROM roster WHERE groupid=" + String.valueOf(ngroup) +
                     " AND pass=1 AND carid > 0";

        ResultSet rs = database.execute(sql);
        if (rs == null) return;

        try {
            while (rs.next()) {
                nRacers = rs.getInt(1);
            }
        } catch (SQLException ex) {

        }

        if (nLanes < 1 || nRacers < 1) return;


        sql = "SELECT max(raceid) FROM races";
        rs = database.execute(sql);
        if (rs == null) return;

        try {
            while (rs.next()) {
                raceID = rs.getInt(1) + 1;
            }
        } catch (SQLException ex) {
            raceID = 1;
        }
        if (raceID == 0) raceID = 1;

        sql = "INSERT INTO races (raceid,groupid,racetype,title,heats,heat)" +
              " VALUES (" +
              String.valueOf(raceID) + "," +
              String.valueOf(ngroup) + "," +
              String.valueOf(RaceDatabase.DB_RACETYPE_MAIN_EVENT) + "," +   // racetype = 1 : main race for this group
              "' '" + "," +
              String.valueOf(nRacers) + "," +
              "1)";
        database.execute(sql);

        ScheduleRace sched = new ScheduleRace();
        sched.define(nLanes, nRacers);
        sched.generate();
        //sched.print();

        int racerID[] = new int[nRacers+2];

        sql = "SELECT racerid FROM roster WHERE groupid=" + String.valueOf(ngroup) +
              " AND pass=1 AND carid > 0";
        rs = database.execute(sql);
        if (rs == null) return;
        try {
            int r = 0;
            while (rs.next()) {
                racerID[r] = rs.getInt(1);
                r++;
            }
        } catch (SQLException ex) {

        }

        for (int heat=0;heat<nRacers;heat++) {
            for (int lane=0;lane<nLanes;lane++) {
                int racer = racerID[sched.getRacerPos(heat, lane)-1];

                sql = "INSERT INTO results (raceid,heat,lane,racerid,time,place,points,speed,invalidtime) " +
                      "VALUES (" +
                      String.valueOf(raceID) + "," +
                      String.valueOf(heat+1) + "," +
                      String.valueOf(lane+1) + "," +
                      String.valueOf(racer) + "," +
                      "0.0," +
                      "0," +
                      "0," +
                      "0," +
                      "0" +
                      ")";
                database.execute(sql);
            }
        }

//        database.setRaceConfig(RaceDatabase.DB_CFGID_GROUPn_SCHEDULED      + ngroup*10, "1");
//        database.setRaceConfig(RaceDatabase.DB_CFGID_GROUPn_NHEATS         + ngroup*10, String.valueOf(nRacers));
//        database.setRaceConfig(RaceDatabase.DB_CFGID_GROUPn_HEAT           + ngroup*10, "1");
//        database.setRaceConfig(RaceDatabase.DB_CFGID_GROUPn_RACE_FINISHED  + ngroup*10, "0");

    }

}
