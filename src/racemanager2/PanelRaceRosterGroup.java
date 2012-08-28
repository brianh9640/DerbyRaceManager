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
import racemanager2.dialogs.*;

/**
 *
 * @author Brian
 */
public class PanelRaceRosterGroup extends BasicWorkingPanel {

    public static final boolean DEBUG   = true;

    Integer         ngroup;

    JButton         buttonAdd;
    JButton         buttonDelete;
    JButton         buttonEdit;

    JLabel          labelGroup;

//    JTextField      fieldGroupName[];
//    JLabel          labelGroupName[];

    DisplayTableRoster      displayRoster;
    int                     rosterOffset;
    int                     rosterSize;

    public PanelRaceRosterGroup(Rectangle rect) {
        super(rect);

        ngroup = 1;
        //addMouseListener(this);

        String title = "Edit Roster Group";
        addStandardTitle(title);

        buttonAdd = new JButton("Add");
        buttonAdd.setVerticalTextPosition(AbstractButton.CENTER);
        buttonAdd.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
        //buttonApply.setMnemonic(KeyEvent.VK_D);
        buttonAdd.setActionCommand("add");
        buttonAdd.setBounds(this.getPosX(9),this.getPosY(1),PWIDTH*2,PHEIGHT);
        buttonAdd.addActionListener(this);
        this.add(buttonAdd);

        rosterOffset = 0;
        rosterSize   = 0;
        displayRoster = new DisplayTableRoster(this);

        displayRoster.setStartY(this.getPosY(2));

        labelGroup = new JLabel("Group:",JLabel.RIGHT);
        labelGroup.setBounds(this.getWidth()-PWIDTH*4-10,1,PWIDTH*4,PHEIGHT);
        this.add(labelGroup);

        displayRoster.setMaxRows(20);
        displayRoster.setCellPadding(6);

        String cLabel[]     = new String[10];
        int cWidth[]        = new int[10];
        boolean cUsed[]     = new boolean[10];
        int cJustify[]      = new int[10];
        cLabel[0]   =   "";             cWidth[0] = 60;         cUsed[0] = false;   cJustify[0] = 0;
        cLabel[1]   =   "Name";         cWidth[1] = 220;        cUsed[1] = true;    cJustify[1] = DisplayTable.COLUMN_JUSTIFY_LEFT;
        cLabel[2]   =   "Car #";        cWidth[2] = 40;         cUsed[2] = true;    cJustify[2] = DisplayTable.COLUMN_JUSTIFY_CENTER;
        cLabel[3]   =   "Weight";       cWidth[3] = 70;         cUsed[3] = true;    cJustify[3] = DisplayTable.COLUMN_JUSTIFY_RIGHT;
        cLabel[4]   =   "Inspection";   cWidth[4] = 80;         cUsed[4] = true;    cJustify[4] = DisplayTable.COLUMN_JUSTIFY_CENTER;
        displayRoster.setupColumns(5,cWidth,cLabel,cJustify,cUsed);
        displayRoster.addHeader();

        displayRoster.createRowLines();
        displayRoster.addButtons();

    }

    public void setGroup(Integer n) {
        ngroup = n;
    }

    public void setDatabase(RaceDatabase db) {
        super.setDatabase(db);

        String sql = "SELECT count(*) " +
                     "FROM roster WHERE groupid=" + ngroup;

        rosterSize = 0;
        ResultSet rs = database.execute(sql);
        if (rs == null) return;

        try {
            while (rs.next()) {
                rosterSize = rs.getInt(1);
            }
        } catch (SQLException ex) {
            rosterSize = 0;
        }

    }

    public void display() {

        super.display();

        if (DEBUG) System.out.println("PanelRaceRosterGroup.display() called");

        int ngroups = database.getRaceConfigInt(RaceDatabase.DB_CFGID_NGROUPS);
        if (ngroups < 1 || ngroups > RaceConfig.RACECFG_MAX_GROUPS) ngroups = 1;

        String name = "Group: " + database.getRaceConfig(RaceDatabase.DB_CFGID_GROUP1_NAME + ((ngroup-1)*10));
        labelGroup.setText(name);

//        rosterOffset = 0;
//        rosterSize   = 0;
        String sql = "SELECT racerid,carid,lastname,firstname,groupid,picid,weight,pass " +
                     "FROM roster WHERE groupid=" + ngroup + " ORDER BY lastname,firstname";

        displayRoster.clearCellData();
        
        ResultSet rs = database.execute(sql);
        if (rs == null) return;

        int r = 0;
        try {
            int line = 0;
            while (rs.next()) {

                if (line >= rosterOffset && line - rosterOffset < displayRoster.getMaxRows()) {
                    displayRoster.addRowRef(r, rs.getString(1));
                
                    String tmp = rs.getString(4) + " " + rs.getString(3);
                    displayRoster.addRowData(r,1,tmp);
                    displayRoster.addRowData(r,2,rs.getString(2));
                    displayRoster.addRowData(r,3,String.format("%1$4.2f", rs.getDouble(7)));
                    int pass = Integer.parseInt(rs.getString(8));
                    if (pass == 1) tmp = "PASS";
                    else tmp = "-";
                    displayRoster.addRowData(r,4,tmp);

                    r++;
                }
                line++;
            }
        } catch (SQLException ex) {
            
        }
        displayRoster.displayButtons();
        displayRoster.setScrollBarSize(r);
    }

    public void setScroll(int value) {
        rosterOffset = value;
        display();
    }

    @Override
    public void paint(Graphics g) {
        display();

        //System.out.println("PanelRaceRosterGroup paint() called");
        //displayRoster.paint(g);

        super.paint(g);


    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (DEBUG) System.out.println(e.getActionCommand());
        if ("add".equals(e.getActionCommand())) {
            if (DEBUG) System.out.println("Create Add to Roster");

            RosterAdd addRoster = new RosterAdd(ngroup);
            addRoster.setDatabase(database);
            addRoster.setMenuTree(menuTree);
            addRoster.display();
            addRoster.setLocationRelativeTo(this);
            addRoster.setVisible(true);

        }
    }

    public void deleteRacer(int nDelete) {
        if (nDelete < 0) return;

        if (DEBUG) {
            System.out.print("Click Delete = ");
            System.out.println(nDelete);
        }

        String racerID = displayRoster.getRowRef(nDelete);
        if (racerID == null) return;
        RosterDelete deleteRoster = new RosterDelete(ngroup,racerID);
        deleteRoster.setDatabase(database);
        deleteRoster.setMenuTree(menuTree);
        deleteRoster.display();
        deleteRoster.setLocationRelativeTo(this);
        deleteRoster.setVisible(true);
    }


    public void editRacer(int nEdit) {
        if (nEdit < 0) return;

        String sracerID = displayRoster.getRowRef(nEdit);
        int racerID = Integer.valueOf(sracerID);

        RosterEdit editRoster = new RosterEdit(ngroup,racerID,database);
        editRoster.setMenuTree(menuTree);
        editRoster.display();
        editRoster.setLocationRelativeTo(this);
        editRoster.setVisible(true);
    }



//    @Override
//    public void mouseClicked(MouseEvent e)
//    {
//                                             // left mouse click
//        if (e.getButton() == MouseEvent.BUTTON1) {
//            int nEdit = displayRoster.buttonEditClick(e);
//            if (nEdit >= 0) {
//                System.out.print("Click Edit = ");
//                System.out.println(nEdit);
//
//                int racerID = Integer.parseInt(displayRoster.getRowRef(nEdit));
//            }
//            int nDelete = displayRoster.buttonDeleteClick(e);
//        }
//        //else
//        //System.out.print("Button Clicked\n");
//    }

    public class DisplayTableRoster extends DisplayTable {


        javax.swing.ImageIcon iconEdit;
        javax.swing.ImageIcon iconDelete;

        JButton buttonEdit[];
        JButton buttonDelete[];

        Rectangle       rectEdit;
        Rectangle       rectDelete;

        public DisplayTableRoster(JPanel panel) {
            super(panel);

            iconEdit    = new javax.swing.ImageIcon(getClass().getResource("images/buttonEdit.png"));
            iconDelete  = new javax.swing.ImageIcon(getClass().getResource("images/buttonDelete.png"));

            rectEdit    = new Rectangle();
            rectDelete  = new Rectangle();
            rectEdit.width      = iconEdit.getIconWidth()+2;
            rectEdit.height     = iconEdit.getIconHeight();
            rectDelete.width    = iconDelete.getIconWidth()+2;
            rectDelete.height   = iconDelete.getIconHeight();
        }

        @Override
        public void addButtons() {

            rectEdit.x          = ICON_PADDING;
            rectEdit.y          = 0;
            rectDelete.x        = iconEdit.getIconWidth() + ICON_PADDING*2;
            rectDelete.y        = 0;

            buttonEdit          = new JButton[maxRows];
            buttonDelete        = new JButton[maxRows];

            for (int r=0;r<maxRows;r++) {
                String tmp;
                buttonEdit[r]        = new JButton(iconEdit);
                tmp = "edit-" + String.valueOf(r);
                buttonEdit[r].setActionCommand(tmp);
                buttonEdit[r].addActionListener(this);
                buttonEdit[r].setBounds(rectEdit);
                buttonEdit[r].setVisible(false);
                tmp = "Edit Racer's Information";
                buttonEdit[r].setToolTipText(tmp);
                panelRow[r].add(buttonEdit[r]);

                buttonDelete[r]        = new JButton(iconDelete);
                //rectDelete.y        = colYStart + ROW_HEIGHT * (r+1);
                tmp = "del-" + String.valueOf(r);
                buttonDelete[r].setActionCommand(tmp);
                buttonDelete[r].addActionListener(this);
                buttonDelete[r].setBounds(rectDelete);
                buttonDelete[r].setVisible(false);
                tmp = "Delete this Racer";
                buttonDelete[r].setToolTipText(tmp);
                panelRow[r].add(buttonDelete[r]);
            }
        }

        public void displayButtons() {

        }

        protected void clearExtraRow(int row) {
            if (buttonEdit!=null)           buttonEdit[row].setVisible(false);
            if (buttonDelete!=null)         buttonDelete[row].setVisible(false);
        }

        public void addRowData(int r,int c,String data) {
            if (r < 0 || r >= maxRows) return;

            if (buttonEdit!=null)           buttonEdit[r].setVisible(true);
            if (buttonDelete!=null)         buttonDelete[r].setVisible(true);

            super.addRowData(r, c, data);
        }

        public void actionPerformed(ActionEvent e) {
            if (DEBUG) System.out.println("Display Roster actionPreformed()");
            if (DEBUG) System.out.println(e.getActionCommand().substring(0,4));
            if ("del-".equals(e.getActionCommand().substring(0,4))) {
                if (DEBUG) System.out.println("Delete Racer from Roster");

                int nDelete = Integer.valueOf(e.getActionCommand().substring(4));
                PanelRaceRosterGroup tmp = (PanelRaceRosterGroup) panelWorking;
                tmp.deleteRacer(nDelete);
            }

            if ("edit-".equals(e.getActionCommand().substring(0,5))) {
                int nEdit = Integer.valueOf(e.getActionCommand().substring(5));
                PanelRaceRosterGroup tmp = (PanelRaceRosterGroup) panelWorking;
                tmp.editRacer(nEdit);
            }

        }

        @Override
        protected void scrollBarNewValue(int value) {
            PanelRaceRosterGroup tmp = (PanelRaceRosterGroup) panelWorking;
            tmp.setScroll(value);
        }

    }

}
