/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package racemanager2;

import RaceLibrary.RaceDatabase;
import java.sql.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import RaceLibrary.RaceConfig;
import RaceLibrary.DisplayTable;

/**
 *
 * @author Brian
 */
public class PanelRaceScheduleGroup extends BasicWorkingPanel {

    public static final boolean DEBUG   = true;

    Integer                 ngroup;
    DisplayTableSchedule    displaySchedule;
    int                     scheduleOffset;
    int                     scheduleSize;

    JLabel                  labelGroup;

    public PanelRaceScheduleGroup(Rectangle rect) {
        super(rect);

        String title = "View Race Schedule";
        addStandardTitle(title);

        scheduleOffset = 0;
        scheduleSize   = 0;

        displaySchedule = new DisplayTableSchedule(this);

        displaySchedule.setStartY(this.getPosY(2));

        labelGroup = new JLabel("Group:",JLabel.RIGHT);
        labelGroup.setBounds(this.getWidth()-PWIDTH*4-10,1,PWIDTH*4,PHEIGHT);
        this.add(labelGroup);

        displaySchedule.setMaxRows(20);
        displaySchedule.setCellPadding(6);

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

    }

    public void setGroup(Integer n) {
        ngroup = n;
        if (DEBUG) System.out.println("PanelRaceScheduleGroup  ngroup=" + String.valueOf(n));
    }

    @Override
    public void display() {

        super.display();

        if (DEBUG) System.out.println("PanelRaceScheduleGroup.display() called");

        int ngroups = database.getRaceConfigInt(RaceDatabase.DB_CFGID_NGROUPS);
        if (ngroups < 1 || ngroups > RaceConfig.RACECFG_MAX_GROUPS) ngroups = 1;

        String name = "Group: " + database.getRaceConfig(RaceDatabase.DB_CFGID_GROUP1_NAME + ((ngroup-1)*10));
        labelGroup.setText(name);

        String sql = "SELECT raceid FROM races " +
              "WHERE groupid=" + String.valueOf(ngroup) + " AND racetype=1";
        ResultSet rs = database.execute(sql);
        int raceid = 0;
        if (rs == null) return;
        try {
            while (rs.next()) {
                raceid = rs.getInt(1);
            }
        } catch (SQLException ex) {

        }

        sql = "SELECT results.heat,results.lane,results.racerid,roster.carid,roster.lastname,roster.firstname " +
                     "FROM results,roster WHERE results.raceid=" + String.valueOf(raceid) + " AND results.racerid=roster.racerid " +
                     "ORDER BY results.heat,results.lane";

        displaySchedule.clearCellData();

        if (DEBUG) System.out.println(sql);
        rs = database.execute(sql);
        if (rs == null) return;

        scheduleSize   = 0;

        try {
            int r = 0;
            while (rs.next()) {
                //displayRoster.addRowRef(r, rs.getString(1));
                String tmp;

                int line = rs.getInt(1) - 1;
                if (line > scheduleSize) scheduleSize = line;
                if (line >= scheduleOffset && line - scheduleOffset < displaySchedule.getMaxRows()) {
                    r = rs.getInt(1) - 1 - scheduleOffset;
                    displaySchedule.addRowData(r,0,rs.getString(1));
                    displaySchedule.addRowData(r,1,"-");
                    int lane = rs.getInt(2);

//                    if (DEBUG) {
//                        System.out.print(r);
//                        System.out.print(":");
//                        System.out.println(lane);
//                    }
                    int carID = rs.getInt(4);
                    tmp = String.format("%1$2d", carID) + ": " + rs.getString(6) + " " + rs.getString(5);
                    displaySchedule.addRowData(r,1+lane,tmp);
                }
            }
        } catch (SQLException ex) {

        }
        displaySchedule.setScrollBarSize(scheduleSize);

    }

    public void setScroll(int value) {
        scheduleOffset = value;

        if (DEBUG) System.out.println("schedule setScroll  value=" + String.valueOf(value));

        display();
    }

    @Override
    public void paint(Graphics g) {
        display();

        super.paint(g);


    }

    public class DisplayTableSchedule extends DisplayTable {

        public DisplayTableSchedule(JPanel panel) {
            super(panel);
        }

        @Override
        protected void scrollBarNewValue(int value) {
            PanelRaceScheduleGroup tmp = (PanelRaceScheduleGroup) panelWorking;
            tmp.setScroll(value);
        }

    }

}
