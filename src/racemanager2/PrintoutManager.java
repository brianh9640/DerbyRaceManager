/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package racemanager2;


import java.sql.*;
import java.util.*;
import net.sf.jasperreports.view.JasperViewer;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.JasperReport;
import java.io.OutputStream;
import java.awt.print.*;
import java.awt.PrintJob;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import net.sf.jasperreports.engine.JRExporterParameter;
import javax.activation.URLDataSource;

import RaceLibrary.RaceDatabase;
import RaceLibrary.RaceManager;
import RaceLibrary.Records.RaceResults;

/**
 *
 * @author Brian
 */
public class PrintoutManager extends Thread {

    RaceDatabase database;
    RaceManager  raceManager;
    RaceResults  rr;

    JasperDesign jasperDesign;
    JasperReport jasperReport;
    JasperPrint jasperPrint;


    
    public PrintoutManager(RaceDatabase db,RaceManager mgr) {
        database = db;
        raceManager = mgr;
    }

    public synchronized void printOverall() {

    }

    public synchronized void printRacerSingle(int raceID,int racerID) {
        String sql;
        ResultSet rs;
    
        URLDataSource xmlFile = new URLDataSource(getClass().getResource("resources/PinewoodResults.jrxml"));

        String raceName = database.getRaceConfig(RaceDatabase.DB_CFGID_TITLE);
        String raceOrg  = database.getRaceConfig(RaceDatabase.DB_CFGID_ORG_NAME);

        try {
            jasperDesign = JRXmlLoader.load(xmlFile.getInputStream());
            jasperReport = JasperCompileManager.compileReport(jasperDesign);
        } catch(Exception ex) {
            String connectMsg = "Could not create the report " + ex.getMessage() + " " + ex.getLocalizedMessage();
            System.out.println(connectMsg);
        }

        sql = "SELECT groupid,heats FROM races WHERE raceid=" + raceID;
        rs = database.execute(sql);
        if (rs == null) return;

        int ngroup = 0;
        int heats  = 0;
        try {
            while (rs.next()) {
                ngroup = rs.getInt(1);
                heats  = rs.getInt(2);
            }
        } catch (SQLException ex) {

        }
        if (ngroup == 0) return;

        rr = raceManager.loadResults(raceID);

        int place = rr.nRacers;
        int n = 0;
        while (n < rr.nRacers && rr.racerID[n] != racerID) {
            n++;
        }
        if (n < rr.nRacers) place = rr.place[n];


        Map model = new HashMap();
        model.put("pOrgName", raceOrg);
        model.put("pRaceName", raceName);
        model.put("pRaceID", raceID);
        model.put("pRacerID", racerID);
        model.put("pHeats", heats);
        model.put("pPlace", place);
        model.put("pNRacers", rr.nRacers);

        runReport(model);
    
    }
    
    public synchronized void printRacers(int raceID) {
        String sql;
        ResultSet rs;

        URLDataSource xmlFile = new URLDataSource(getClass().getResource("resources/PinewoodResults.jrxml"));

        String raceName = database.getRaceConfig(RaceDatabase.DB_CFGID_TITLE);
        String raceOrg  = database.getRaceConfig(RaceDatabase.DB_CFGID_ORG_NAME);

        try {
            jasperDesign = JRXmlLoader.load(xmlFile.getInputStream());
            jasperReport = JasperCompileManager.compileReport(jasperDesign);
        } catch(Exception ex) {
            String connectMsg = "Could not create the report " + ex.getMessage() + " " + ex.getLocalizedMessage();
            System.out.println(connectMsg);
        }

        sql = "SELECT groupid,heats FROM races WHERE raceid=" + raceID;
        rs = database.execute(sql);
        if (rs == null) return;

        int ngroup = 0;
        int heats  = 0;
        try {
            while (rs.next()) {
                ngroup = rs.getInt(1);
                heats  = rs.getInt(2);
            }
        } catch (SQLException ex) {

        }
        if (ngroup == 0) return;

        rr = raceManager.loadResults(raceID);

        sql = "SELECT racerid FROM roster WHERE groupid=" + ngroup +
              " AND pass=1 AND carid > 0";
//        sql = "SELECT racerid FROM roster WHERE groupid=" + ngroup +
//              " AND pass=1 AND carid = 1";

        rs = database.execute(sql);
        if (rs == null) return;

        int count = 0;
        try {
            while (rs.next()) {
                int racerID = rs.getInt(1);

                int place = rr.nRacers;
                int n = 0;
                while (n < rr.nRacers && rr.racerID[n] != racerID) {
                    n++;
                }
                if (n < rr.nRacers) place = rr.place[n];


                Map model = new HashMap();
                model.put("pOrgName", raceOrg);
                model.put("pRaceName", raceName);
                model.put("pRaceID", raceID);
                model.put("pRacerID", racerID);
                model.put("pHeats", heats);
                model.put("pPlace", place);
                model.put("pNRacers", rr.nRacers);

                runReport(model);
                count++;

                //if (count > 0) return;
            }
        } catch (SQLException ex) {

        }

    }


    void runReport(Map model) {

        try{

            Connection jdbcConnection = database.dbConn;
            jasperPrint = JasperFillManager.fillReport(jasperReport, model, jdbcConnection);
            //JasperViewer.viewReport(jasperPrint);
        }catch(Exception ex) {
            String connectMsg = "Could not create the report " + ex.getMessage() + " " + ex.getLocalizedMessage();
            System.out.println(connectMsg);
        }

        PrinterJob job = PrinterJob.getPrinterJob();
        /* Create an array of PrintServices */
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        PrintService default_service = PrintServiceLookup.lookupDefaultPrintService();

        int selectedService = 0;
        /* Scan found services to see if anyone suits our needs */
        for(int i = 0; i < services.length;i++)
        {
            if(services[i].getName().toUpperCase().equals(default_service.getName().toUpperCase()))
            {
                selectedService = i;
            }
        }

        try {
            job.setPrintService(services[selectedService]);
        }
        catch(Exception ex) {
            String connectMsg = "PrintService Error --- " + ex.getMessage() + " " + ex.getLocalizedMessage();
            System.out.println(connectMsg);
        }
        PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
        printRequestAttributeSet.add(MediaSizeName.NA_LETTER);
        printRequestAttributeSet.add(new Copies(1));
        JRPrintServiceExporter exporter;
        exporter = new JRPrintServiceExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        /* We set the selected service and pass it as a paramenter */
        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE, services[selectedService]);
        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, services[selectedService].getAttributes());
        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);
        try {
            exporter.exportReport();
        }
        catch(Exception ex) {
            String connectMsg = "Print Export Error --- " + ex.getMessage() + " " + ex.getLocalizedMessage();
            System.out.println(connectMsg);
        }


    }




    @Override
    public void run() {


    }

}
