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
public class CustomDialog extends JDialog implements ActionListener {

    protected RaceDatabase database;

    protected int buttonWidth;
    protected int buttonSpacing;

    protected boolean       errorMsgBoxShown;

    protected RaceMenuTree menuTree;

    public static final int DIALOG_COL_SPACING  =   120;
    public static final int DIALOG_COL_WIDTH    =   110;

    JButton buttonClose;

    public CustomDialog() {

        database = null;
        menuTree = null;

        errorMsgBoxShown = false;

        this.setAlwaysOnTop(false);

        buttonWidth = 80;
        buttonSpacing = 20;

        buttonClose = new JButton();
        buttonClose.setBounds(0, 0, 0, 0);
        buttonClose.setActionCommand("closedialog");
        buttonClose.addActionListener(this);
        add(buttonClose);

        addWindowFocusListener(new WindowFocusListener() {
            public void windowGainedFocus(WindowEvent e) {

            }

            public void windowLostFocus(WindowEvent e) {
                // this will invoke the action on the popup invoking button
                // and hide the visible window
                if (!errorMsgBoxShown) buttonClose.doClick();
            }
        });

    }

    public void setDatabase(RaceDatabase db) {
        database = db;
    }

    public void setMenuTree(RaceMenuTree menuTree) {
        this.menuTree = menuTree;
    }

    protected int getPosX(int col) {
        Integer posx;
        posx = 20 + col*DIALOG_COL_SPACING;
        return posx;
    }

    protected int getPosY(int row) {
        Integer posy;
        posy = 20 + row*30;
        return posy;
    }

    protected int getButtonX(int n, int nButtons) {
        int posX = 0;
        int startX = 0;
        int width = this.getWidth();

        startX = (width - nButtons * buttonWidth - (nButtons-1) * buttonSpacing) / 2;
        if (startX < 0) startX = 0;
        posX = startX + (buttonWidth + buttonSpacing) * n;

        return posX;
    }

    public void display() {

    }

    protected void showErrorMsgBox(String msg,String title) {
        errorMsgBoxShown = true;

        JOptionPane.showMessageDialog(this,msg,title,JOptionPane.ERROR_MESSAGE);

        errorMsgBoxShown = false;
    }


    public void actionPerformed(ActionEvent e) {

        if ("closedialog".equals(e.getActionCommand())) {
            setVisible(false);
        }

    }

//    public void hide() {
//        this.setVisible(false);
//    }



}
