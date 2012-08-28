/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package racemanager2;

import RaceLibrary.RaceDatabase;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author Brian
 */
public class PanelRaceSetup extends BasicWorkingPanel {

    JLabel          labelConn;
    JLabel          labelConnStatus;

    JLabel          labelTimerType;
    JComboBox       fieldTimerType;

    JTextField      fieldTitle;
    JTextField      fieldOrgName;
    JTextField      fieldComPort;
    JTextField      fieldIPAddress;
    JTextField      fieldNLanes;
    JTextField      fieldNPerLane;
    JTextField      fieldNGroups;
    JTextField      fieldMaxTime;
    JTextField      fieldLengthFeet;
    JTextField      fieldScaleFactorMPH;

    JButton         buttonApply;


    public PanelRaceSetup(Rectangle rect) {
        super(rect);

        String title = "Race Setup Information";
        addStandardTitle(title);
        int row = 2;

        fieldTitle = new JTextField("");
        addStdTextField(row++,"Race Title",fieldTitle,PWIDTH*5);

        fieldOrgName = new JTextField("");
        addStdTextField(row++,"Organization Name",fieldOrgName,PWIDTH*4);

        fieldNLanes = new JTextField("");
        addStdTextField(row++,"Lanes on Track",fieldNLanes);

        fieldNPerLane = new JTextField("");
        addStdTextField(row++,"Races per Lane",fieldNPerLane);

        fieldNGroups = new JTextField("");
        addStdTextField(row++,"# of Groups",fieldNGroups);

        fieldMaxTime = new JTextField("");
        addStdTextField(row++,"Race Max Time",fieldMaxTime);

        fieldLengthFeet = new JTextField("");
        addStdTextField(row++,"Track Length (Feet)",fieldLengthFeet);

        fieldScaleFactorMPH = new JTextField("");
        addStdTextField(row++,"MPH Scale Factor",fieldScaleFactorMPH);

        labelTimerType = new JLabel("Race Timer");
        String[] stringsTimerTypes = { "-not defined-", "FX3U - Custom", "Wizards", "-----" };
        fieldTimerType = new JComboBox(stringsTimerTypes);
        addStdLabelComboBox(row++,labelTimerType,fieldTimerType);

        fieldIPAddress = new JTextField("");
        addStdTextField(row++,"Timer IP Address",fieldIPAddress);

        buttonApply = new JButton("Apply");
        buttonApply.setBounds(getButtonRect(4));
        buttonApply.setActionCommand("apply");
        buttonApply.addActionListener(this);
        this.add(buttonApply);


    }

    public void display() {

        super.display();

        fieldTimerType.setSelectedIndex(1);

        fieldTitle.setText(database.getRaceConfig(RaceDatabase.DB_CFGID_TITLE));
        fieldOrgName.setText(database.getRaceConfig(RaceDatabase.DB_CFGID_ORG_NAME));
        fieldIPAddress.setText(database.getRaceConfig(RaceDatabase.DB_CFGID_IPADDRESS));
        fieldNLanes.setText(database.getRaceConfig(RaceDatabase.DB_CFGID_NLANES));
        fieldNPerLane.setText(database.getRaceConfig(RaceDatabase.DB_CFGID_PERLANE));
        fieldNGroups.setText(database.getRaceConfig(RaceDatabase.DB_CFGID_NGROUPS));
        fieldMaxTime.setText(database.getRaceConfig(RaceDatabase.DB_CFGID_MAXTIME));
        fieldLengthFeet.setText(database.getRaceConfig(RaceDatabase.DB_CFGID_TRACK_LENGTH));
        fieldScaleFactorMPH.setText(database.getRaceConfig(RaceDatabase.DB_CFGID_SCALE_FACTOR_MPH));

    }


    @Override
    public void actionPerformed(ActionEvent e) {

        //if (DEBUG) System.out.println(e.getActionCommand());

        if ("apply".equals(e.getActionCommand())) {
            saveSetupSetting();
        }

    }

    public void saveSetupSetting() {

        database.setRaceConfig(RaceDatabase.DB_CFGID_TITLE              ,fieldTitle.getText());
        database.setRaceConfig(RaceDatabase.DB_CFGID_ORG_NAME           ,fieldOrgName.getText());
        database.setRaceConfig(RaceDatabase.DB_CFGID_IPADDRESS          ,fieldIPAddress.getText());
        database.setRaceConfig(RaceDatabase.DB_CFGID_NLANES             ,fieldNLanes.getText());
        database.setRaceConfig(RaceDatabase.DB_CFGID_PERLANE            ,fieldNPerLane.getText());
        database.setRaceConfig(RaceDatabase.DB_CFGID_NGROUPS            ,fieldNGroups.getText());
        database.setRaceConfig(RaceDatabase.DB_CFGID_MAXTIME            ,fieldMaxTime.getText());

        Integer trackLen = 37;  // default
        try
        {
            // the String to int conversion happens here
            trackLen = Integer.parseInt(fieldLengthFeet.getText().trim());
        }
        catch (NumberFormatException nfe) {
        }

        database.setRaceConfig(RaceDatabase.DB_CFGID_TRACK_LENGTH       ,trackLen.toString());

        Integer scaleMPH = 25;  // default
        try
        {
            // the String to int conversion happens here
            scaleMPH = Integer.parseInt(fieldScaleFactorMPH.getText().trim());
        }
        catch (NumberFormatException nfe) {
        }
        
        database.setRaceConfig(RaceDatabase.DB_CFGID_SCALE_FACTOR_MPH   ,scaleMPH.toString());
        menuTree.initTree2();
        
        menuTree.setTreeStatus(RaceMenuTree.TREE_RACE_SETUP,RaceMenuTree.TREE_STATUS_COMPLETED);
    }

}
