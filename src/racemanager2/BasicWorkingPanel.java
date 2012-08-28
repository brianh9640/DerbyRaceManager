/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package racemanager2;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import RaceLibrary.RaceDatabase;
import RaceLibrary.RaceManager;

/**
 *
 * @author Brian
 */
public class BasicWorkingPanel extends JPanel implements ActionListener, MouseListener {

    public static final boolean DEBUG   = false;

    public static final int PWIDTH              = 40;
    public static final int PHEIGHT             = 20;
    public static final int PCOL_WIDTH          = 50;
    public static final int PROW_HEIGHT         = 30;

    //JPanel panelWorking;

    JLabel labelRaceName;

    RaceDatabase database;
    RaceMenuTree menuTree;
    RaceManager  raceManager;


    public BasicWorkingPanel(Rectangle rect) {
        super();
        setBounds(rect);        // 0,0,650,555
        setBorder(new javax.swing.border.LineBorder(java.awt.Color.BLACK));
        setLayout(null);

        menuTree = null;
        raceManager = null;

        labelRaceName = new JLabel("Race Database Name:",JLabel.LEFT);
        labelRaceName.setBounds(5,1,PWIDTH*8,PHEIGHT);
        this.add(labelRaceName);

    }

    public void setMenuTree(RaceMenuTree menuTree) {
        this.menuTree = menuTree;
    }

    public void setManager(RaceManager mgr) {
        raceManager = mgr;
    }

    public void setDatabase(RaceDatabase db) {
        database = db;
    }

    public void display() {

        String race = "Race Database Name: " + database.getName();
        labelRaceName.setText(race);

    }

    public void actionPerformed(ActionEvent e) {

    }

    protected void addStandardTitle(String title) {
        Rectangle rectTitle = new Rectangle(10,20,this.getWidth()-20,30);
        Border border = BorderFactory.createLineBorder(Color.blue,3);
        addLabelBorderSize(title,border,rectTitle,20);
    }

    protected void addLabelBorderSize(String text,javax.swing.border.Border border,Rectangle rect,int size) {
        Font font1 = new Font("Arial", Font.BOLD,size);

        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setBorder(border);
        label.setBounds(rect.x,rect.y,rect.width,rect.height);
        label.setFont(font1);
        this.add(label);
    }

    protected void addLabelBorder(String text,javax.swing.border.Border border,Rectangle rect) {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setBorder(border);
        label.setBounds(rect.x,rect.y,rect.width,rect.height);
        this.add(label);
    }
    protected void addStdTextField(int row,String text,JTextField field) {
        addStdTextField(row,text,field,PWIDTH*3);
    }

    protected void addStdTextField(int row,String text,JTextField field,int width) {
        JLabel label = new JLabel(text);
        addStdLabelTextField(row,label,field,width);
    }

    protected void addStdLabelTextField(int row,JLabel label,JTextField field) {
        addStdLabelTextField(row,label,field,PWIDTH*3);
    }

    protected void addStdLabelTextField(int row,JLabel label,JTextField field,int width) {
        label.setBounds(getPosX(1), getPosY(row), PWIDTH*3, PHEIGHT);
        this.add(label);

        field.setHorizontalAlignment(JTextField.LEFT);
        field.setBounds(getPosX(4), getPosY(row), width, PHEIGHT);
        this.add(field);

    }

    protected void addStdLabelComboBox(int row,JLabel label,JComboBox field) {
        label.setBounds(getPosX(1), getPosY(row), PWIDTH*3, PHEIGHT);
        this.add(label);

        field.setBounds(getPosX(4), getPosY(row), PWIDTH*3, PHEIGHT);
        this.add(field);

    }

    protected int getPosX(int col) {
        Integer posx;
        posx = 30 + col*PCOL_WIDTH;
        return posx;
    }

    protected int getPosY(int row) {
        Integer posy;
        posy = 30 + row*PROW_HEIGHT;
        return posy;
    }


    protected Rectangle getButtonRect(int col) {
        Rectangle rect = new Rectangle();

        rect.x          = getPosX(col);
        rect.y          = this.getHeight() - PHEIGHT * 3;
        rect.width      = PWIDTH*3;
        rect.height     = PHEIGHT;
        
        return rect;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (DEBUG) System.out.println("BasicWorkingPanel paint() called");
    }

    public void mouseClicked(MouseEvent arg0) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mousePressed(MouseEvent arg0) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseReleased(MouseEvent arg0) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseEntered(MouseEvent arg0) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseExited(MouseEvent arg0) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

}

