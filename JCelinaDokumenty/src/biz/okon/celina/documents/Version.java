/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.okon.celina.documents;

/**
 * Klasa przechowuje zmienne wersji programu, które udostępnia oraz umożliwia połączenie się z serwerem aktualizaji celem sprawdzenia aktualizacji.
 * 
 * @author okongrzegorz
 */

import static biz.okon.celina.documents.ReadingXMLParams.FileLogger;

import java.nio.file.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import org.apache.log4j.Logger;

public class Version {
    //parametry wersji aplikacji wewnątrz programu
    private String localName = "JCelinaDokumenty";
    private int localMajor = 1;
    private int localMinor = 9;
    private int localRelease = 9;
    private int localBuild = 1;
    private String localType = "GA";
    private int localCompile = 20160212;
    
    //zmienne połączeniowe
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    private EventLogArea WritingToEventLogArea;
    private static Logger FileLogger = Logger.getLogger(Version.class);
    
    public String getVersion() {
        //zwraca w formacie JCelinaDokumenty v1.5.14 (rev. 20150627)
        return "" + localName + " v" + localMajor + "." + localMinor + "." + localRelease + " (rev. " + localCompile + ")";
    }
     
    public void compareVersions(JTextArea EventLog) {
        WritingToEventLogArea = new EventLogArea();
        WritingToEventLogArea.info("Próba połączenia z serwerem aktualizacji ...", EventLog);
        WritingToEventLogArea.info("\\\\***************************************************", EventLog);
        Connection2Sqlite ConnToSqlite = new Connection2Sqlite();
        conn = ConnToSqlite.connectSqlite();

        try {
            //globalne parametry wersji aplikacji pobierane z bazy sqllite
            String globalName = "";
            int globalMajor = 0;
            int globalMinor = 0;
            int globalRelease = 0;
            int globalBuild = 0;
            String globalType = "";
            int globalCompile = 0;
            String globalPath = "";
            stmt = conn.createStatement();
            rs = stmt.executeQuery( "SELECT * FROM version;" );
            
            while (rs.next()) {
                globalName = rs.getString("name");
                globalMajor = rs.getInt("major");
                globalMinor = rs.getInt("minor");
                globalRelease = rs.getInt("release");
                globalBuild = rs.getInt("build");
                globalType = rs.getString("type");
                globalCompile = rs.getInt("compile");
                globalPath = rs.getString("path");
            }

            if (localName.equals(globalName) && localMajor == globalMajor && localMinor == globalMinor && localRelease == globalRelease &&
                    localBuild == globalBuild && localType.equals(globalType) && localCompile == globalCompile) {
                WritingToEventLogArea.info("Używasz aktualnej wersji programu.", EventLog);
            }
            else {
                WritingToEventLogArea.info("Używasz nieaktualnej wersji programu.", EventLog);
                WritingToEventLogArea.info("Aktualna wersja: " + globalPath, EventLog);
                 
                // Wyświetlenie okna MessageBox o aktualizacji wersji programu
                JOptionPane.showMessageDialog(null, "Pojawiła się nowa wersja oprogramowania " + globalName + " v" + globalMajor + "." + globalMinor + "." + globalRelease + " (rev. " + globalCompile + ").\n" +
                        "Twoja wersja: " + localMajor + "." + localMinor + "." + localRelease + " (rev. " + localCompile + ").\n\n" +
                        globalPath, "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        catch (Exception e) {
            FileLogger.error(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {WritingToEventLogArea.error("Wystąpił nieoczekiwany błąd: " + e, EventLog);}
            }
        }
    }
}
