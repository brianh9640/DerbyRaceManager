/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package racemanager2;

import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.border.LineBorder;



import RaceLibrary.RaceDatabase;
import RaceLibrary.RaceManager;
import RaceLibrary.RaceConfig;
import RaceLibrary.Comms.*;

/**
 *
 * @author Brian
 */
public class RaceMenuTree extends JPanel implements TreeSelectionListener {

    public static final boolean DEBUG   = false;

    JPanel                  panelWorking;
    JPanel                  panelApp;
    JFrame                  frameMain;

    public BasicWorkingPanel       panelDisplay;

    RaceDatabase            database;
    public RaceManager      manager;
    RaceServer              serverComm;

    public javax.swing.JTree       configTree;
    DefaultTreeModel        treeModel;
    DefaultMutableTreeNode  nodeTop;
    MenuRenderer            menuRenderer;

    Rectangle               rectDisplayPanel;

    public static final int TREE_MANAGER                    = 0;
    public static final int TREE_ABOUT                      = 1;
    public static final int TREE_RACE_SETUP                 = 10;
    public static final int TREE_ROSTER                     = 20;
    public static final int TREE_SCHEDULE                   = 30;
    public static final int TREE_RACE                       = 40;
    public static final int TREE_RESULTS                    = 80;

    public static final int TREE_STATUS_NONE                = 0;
    public static final int TREE_STATUS_COMPLETED           = 1;


    TreeItem                treeItem[];

    public RaceMenuTree() {

   	super(new FlowLayout(FlowLayout.LEFT));

        this.setBackground(java.awt.Color.WHITE);

        treeItem = new TreeItem[200];
        treeItem[TREE_MANAGER] = new TreeItem(TREE_MANAGER,"Manager");
        treeItem[TREE_RACE_SETUP] = new TreeItem(TREE_RACE_SETUP,"Race Setup");
        treeItem[TREE_ROSTER] = new TreeItem(TREE_ROSTER,"Roster");
        treeItem[TREE_ROSTER+1] = new TreeItem(TREE_ROSTER+1,"Group 1");
        treeItem[TREE_ROSTER+2] = new TreeItem(TREE_ROSTER+2,"Group 2");
        treeItem[TREE_ROSTER+3] = new TreeItem(TREE_ROSTER+3,"Group 3");
        treeItem[TREE_ROSTER+4] = new TreeItem(TREE_ROSTER+4,"Group 4");
        treeItem[TREE_ROSTER+5] = new TreeItem(TREE_ROSTER+5,"Group 5");
        treeItem[TREE_SCHEDULE] = new TreeItem(TREE_SCHEDULE,"Schedule");
        treeItem[TREE_SCHEDULE+1] = new TreeItem(TREE_SCHEDULE+1,"Group 1");
        treeItem[TREE_SCHEDULE+2] = new TreeItem(TREE_SCHEDULE+2,"Group 2");
        treeItem[TREE_SCHEDULE+3] = new TreeItem(TREE_SCHEDULE+3,"Group 3");
        treeItem[TREE_SCHEDULE+4] = new TreeItem(TREE_SCHEDULE+4,"Group 4");
        treeItem[TREE_SCHEDULE+5] = new TreeItem(TREE_SCHEDULE+5,"Group 5");
        treeItem[TREE_RACE] = new TreeItem(TREE_RACE,"Race");
//        treeItem[TREE_RACE+1] = new TreeItem(TREE_RACE+1,"Group 1");
//        treeItem[TREE_RACE+2] = new TreeItem(TREE_RACE+2,"Group 2");
//        treeItem[TREE_RACE+3] = new TreeItem(TREE_RACE+3,"Group 3");
//        treeItem[TREE_RACE+4] = new TreeItem(TREE_RACE+4,"Group 4");
//        treeItem[TREE_RACE+5] = new TreeItem(TREE_RACE+5,"Group 5");
        treeItem[TREE_RESULTS] = new TreeItem(TREE_RESULTS,"Results");
        treeItem[TREE_ABOUT] = new TreeItem(TREE_ABOUT,"About");

        database = new RaceDatabase();
        manager  = new RaceManager();
        serverComm = null;

        //OLD initTree();
        nodeTop = new DefaultMutableTreeNode(treeItem[TREE_MANAGER]);

        treeModel = new DefaultTreeModel(nodeTop);
	treeModel.addTreeModelListener(new ConfigTreeModelListener());
        configTree = new JTree(treeModel);
        configTree.setEditable(false);
        configTree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        configTree.setShowsRootHandles(true);

        //OLD configTree = new javax.swing.JTree(nodeTop);
        configTree.setToggleClickCount(1);

        this.setBorder(new javax.swing.border.LineBorder(java.awt.Color.BLACK));

        menuRenderer = new MenuRenderer();
        configTree.setCellRenderer(menuRenderer);
        configTree.setExpandsSelectedPaths(true);
        this.add(configTree);

        // TODO : Add model settings for adding and removing items from tree

        configTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        //Listen for when the selection changes.
        configTree.addTreeSelectionListener(this);

        panelDisplay = null;
        
    }

    public void setPanels(JPanel panelApp,JFrame frame,JPanel panelWorking) {
        this.panelApp = panelApp;
        this.frameMain = frame;
        this.panelWorking = panelWorking;

        rectDisplayPanel        = new Rectangle();
        rectDisplayPanel.x      = 0;
        rectDisplayPanel.y      = 0;
        rectDisplayPanel.width  = panelWorking.getWidth();
        rectDisplayPanel.height = panelWorking.getHeight();

        panelDisplay = new PanelRaceMain(rectDisplayPanel);
        panelDisplay.setDatabase(database);
        panelDisplay.setMenuTree(this);
        
        panelWorking.add(panelDisplay);

        panelDisplay.display();
        //panelDisplay.repaint();

    }

    public void setTreeStatus(int item,int value) {
        treeItem[item].setStatus(value);
    }

    public void clearTree() {
        nodeTop.removeAllChildren();
        treeModel.reload();
    }

    public void initTree2() {
        DefaultMutableTreeNode category = null;
        clearTree();

        addObject(null,treeItem[TREE_RACE_SETUP],true);
        category = addObject(null,treeItem[TREE_ROSTER],true);
            initTreeGroups(category,TREE_ROSTER);

        category = addObject(null,treeItem[TREE_SCHEDULE],true);
            initTreeGroups(category,TREE_SCHEDULE);
        category = addObject(null,treeItem[TREE_RACE],true);
            //initTreeGroups(category,TREE_RACE);
        category = addObject(null,treeItem[TREE_RESULTS],true);
        addObject(null,treeItem[TREE_ABOUT],true);

        if (database.connStatus == RaceDatabase.DBCONN_ESTABLISHED) {
            if (serverComm != null) {
                serverComm.close();
            }
            serverComm = new RaceServer();
            serverComm.start();

            if (manager.getRaceID() == 0) {
                manager.setDatabase(database);
                int rID = database.getRaceConfigInt(RaceDatabase.DB_CFGID_CURRENT_RACEID);
                if (rID > 0) manager.loadRace(rID);
            }
            manager.setRaceServer(serverComm);
            if (manager.getRunning()) manager.reload();
            else manager.start();
        }

    }

    protected void initTreeGroups(DefaultMutableTreeNode pRoster,int parent) {

        if (database.connStatus != RaceDatabase.DBCONN_ESTABLISHED) return;

        int ngroups = database.getRaceConfigInt(RaceDatabase.DB_CFGID_NGROUPS);
        if (ngroups < 1 || ngroups > RaceConfig.RACECFG_MAX_GROUPS) ngroups = 1;

        for (int f=0;f<RaceConfig.RACECFG_MAX_GROUPS;f++) {
            if (f < ngroups) {
                String name = String.valueOf(f+1) + ": " + database.getRaceConfig(RaceDatabase.DB_CFGID_GROUP1_NAME + ((f)*10));
                treeItem[parent+f+1].name = name;
                addObject(pRoster,treeItem[parent+f+1],true);
            }
        }
    }

    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
                                            Object child,
                                            boolean shouldBeVisible) {
        DefaultMutableTreeNode childNode =
                new DefaultMutableTreeNode(child);

        if (parent == null) {
            parent = nodeTop;
        }

	//It is key to invoke this on the TreeModel, and NOT DefaultMutableTreeNode
        treeModel.insertNodeInto(childNode, parent,
                                 parent.getChildCount());

        //Make sure the user can see the lovely new node.
        if (shouldBeVisible) {
            configTree.scrollPathToVisible(new TreePath(childNode.getPath()));
        }
        return childNode;
    }

    public void valueChanged(TreeSelectionEvent e) {

        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           configTree.getLastSelectedPathComponent();

        if (node == null) return;

        //configTree.expandPath(configTree.getSelectionPath());
//        int row = 0;
//        while(row < configTree.getRowCount()){
//            configTree.expandRow(row);
//            row++;
//        }

        Object nodeInfo = node.getUserObject();

        TreeItem item = (TreeItem) nodeInfo;

        //item.setStatus(1);

        //node.setUserObject(item);
        
        System.out.print(item.toString());
        System.out.print(" --- ");
        System.out.print(item.getID());
        System.out.print(" --- ");
        System.out.println(item.getStatus());

        panelDisplay.removeAll();
        panelWorking.removeAll();

        switch (item.getID()) {
            case TREE_MANAGER :
                panelDisplay = new PanelRaceMain(rectDisplayPanel);
                break;
            case TREE_RACE_SETUP :
                panelDisplay = new PanelRaceSetup(rectDisplayPanel);
                break;
            case TREE_ROSTER :
                panelDisplay = new PanelRaceRoster(rectDisplayPanel);
                break;
            case TREE_ROSTER + 1:
            case TREE_ROSTER + 2:
            case TREE_ROSTER + 3:
            case TREE_ROSTER + 4:
            case TREE_ROSTER + 5:
                panelDisplay = new PanelRaceRosterGroup(rectDisplayPanel);
                PanelRaceRosterGroup tmpDisplay = (PanelRaceRosterGroup) panelDisplay;
                tmpDisplay.setGroup(item.getID() - TREE_ROSTER);
                break;
            case TREE_SCHEDULE :
                panelDisplay = new PanelRaceSchedule(rectDisplayPanel,database);
                break;
            case TREE_SCHEDULE + 1:
            case TREE_SCHEDULE + 2:
            case TREE_SCHEDULE + 3:
            case TREE_SCHEDULE + 4:
            case TREE_SCHEDULE + 5:
                panelDisplay = new PanelRaceScheduleGroup(rectDisplayPanel);
                PanelRaceScheduleGroup tmpDisplaySched = (PanelRaceScheduleGroup) panelDisplay;
                tmpDisplaySched.setGroup(item.getID() - TREE_SCHEDULE);
                break;
            case TREE_RACE:
                panelDisplay = new PanelRaceGroup(rectDisplayPanel,database);
                //PanelRaceGroup tmpDisplayRace = (PanelRaceGroup) panelDisplay;
                //tmpDisplayRace.setManager(manager);
                break;
            case TREE_RESULTS :
                panelDisplay = new PanelRaceResults(rectDisplayPanel,database);
                //PanelRaceResults tmpDisplayResults = (PanelRaceResults) panelDisplay;
                //tmpDisplayResults.setManager(manager);
                break;
            default :
                panelDisplay = new BasicWorkingPanel(rectDisplayPanel);
                break;

        }

        panelWorking.add(panelDisplay);

        panelDisplay.setDatabase(database);
        panelDisplay.setMenuTree(this);
        panelDisplay.setManager(manager);
        panelDisplay.display();

        panelWorking.repaint();

    }

    private class MenuRenderer extends DefaultTreeCellRenderer {
        javax.swing.ImageIcon iconCheckGreen;
        javax.swing.ImageIcon iconCheckRed;

        public MenuRenderer() {

            iconCheckGreen  = new javax.swing.ImageIcon(getClass().getResource("images/checkGreen.png"));
            iconCheckRed    = new javax.swing.ImageIcon(getClass().getResource("images/checkRed.png"));

        }

        @Override
        public Component getTreeCellRendererComponent(
                            JTree tree,
                            Object value,
                            boolean sel,
                            boolean expanded,
                            boolean leaf,
                            int row,
                            boolean hasFocus) {

            super.getTreeCellRendererComponent(
                            tree, value, sel,
                            expanded, leaf, row,
                            hasFocus);


            //System.out.print(value.getClass());

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            TreeItem item = (TreeItem) node.getUserObject();

            //System.out.println(node.getUserObject().getClass());
            //System.out.print(value.toString());
            //System.out.print("    ");
            //System.out.println(item.getStatus());

//            if (!expanded) {
//                tree.expandRow(row);
//            }

            if (leaf) {
                if (item.getStatus() != 0) setIcon(iconCheckGreen);
                else setIcon(iconCheckRed);

                //setToolTipText("Test of tooltip.");
            }

            return this;
        }
    }

    class ConfigTreeModelListener implements TreeModelListener {
        public void treeNodesChanged(TreeModelEvent e) {
            DefaultMutableTreeNode node;
            node = (DefaultMutableTreeNode)(e.getTreePath().getLastPathComponent());

            /*
             * If the event lists children, then the changed
             * node is the child of the node we've already
             * gotten.  Otherwise, the changed node and the
             * specified node are the same.
             */

                int index = e.getChildIndices()[0];
                node = (DefaultMutableTreeNode)(node.getChildAt(index));

            System.out.println("The user has finished editing the node.");
            System.out.println("New value: " + node.getUserObject());
        }
        public void treeNodesInserted(TreeModelEvent e) {
        }
        public void treeNodesRemoved(TreeModelEvent e) {
        }
        public void treeStructureChanged(TreeModelEvent e) {
        }
    }


    private class TreeItem {
        public String name;
        public Integer id;
        private Integer status;

        public TreeItem(Integer vID,String vName) {
            id   = vID;
            name = vName;
            status = 0;
        }

        public Integer getID() {
            return id;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer value) {
            status = value;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}

