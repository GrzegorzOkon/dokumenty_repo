/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.okon.celina.documents;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import javax.swing.JTextArea;
import static biz.okon.celina.documents.ReadingXMLParams.FileLogger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author okongrzegorz
 */
public class Connection2Celina {
    private String db_ip;
    private String db_port;
    private String db_user;
    private String db_pswd;
    private String URL;   
    private Properties LoginInformation;
    private Connection ActiveConnection;
    private EventLogArea WritingToEventLogArea;
    
    Connection connectCentral(Properties ActualDatabase, JTextArea EventLog) {
        db_ip = ActualDatabase.getProperty("db_ip");
        db_port = ActualDatabase.getProperty("db_port");
        db_user = ActualDatabase.getProperty("db_user");
        db_pswd = ActualDatabase.getProperty("db_pswd");
        URL = "jdbc:sybase:Tds:" + db_ip + ":" + db_port + "*************";
        LoginInformation = new Properties();
        LoginInformation.put( "user", db_user );
        LoginInformation.put( "password", db_pswd);
        WritingToEventLogArea = new EventLogArea();
                
        try {
            WritingToEventLogArea.info("Próba połączenia z bazą danych " + db_ip + ":" + db_port + " poprzez JDBC.", EventLog);
            FileLogger.info("Próba połączenia z bazą danych " + db_ip + ":" + db_port + " poprzez JDBC.");
            ActiveConnection = DriverManager.getConnection(URL, LoginInformation);
            WritingToEventLogArea.info("Połączono z bazą danych " + db_ip + ":" + db_port + ".", EventLog);
            FileLogger.info("Połączono z bazą danych " + db_ip + ":" + db_port + ".");
        } catch (SQLException e) {
            WritingToEventLogArea.error("Wystąpił nieoczekiwany błąd. Sprawdź log aplikacji.", EventLog);
        }
        
        return ActiveConnection;
    }
    
    Connection connectLocal(Properties ActualDatabase, JTextArea EventLog) {
        db_ip = ActualDatabase.getProperty("db_ip");
        db_port = ActualDatabase.getProperty("db_port");
        db_user = ActualDatabase.getProperty("db_user");
        db_pswd = ActualDatabase.getProperty("db_pswd");
        URL = "jdbc:sybase:Tds:" + db_ip + ":" + db_port + "*********";
        LoginInformation = new Properties();
        LoginInformation.put( "user", db_user );
        LoginInformation.put( "password", db_pswd);
        WritingToEventLogArea = new EventLogArea();
                
        try {
            WritingToEventLogArea.info("Próba połączenia z bazą danych " + db_ip + ":" + db_port + " poprzez JDBC.", EventLog);
            FileLogger.info("Próba połączenia z bazą danych " + db_ip + ":" + db_port + " poprzez JDBC.");
            ActiveConnection = DriverManager.getConnection(URL, LoginInformation);
            WritingToEventLogArea.info("Połączono z bazą danych " + db_ip + ":" + db_port + ".", EventLog);
            FileLogger.info("Połączono z bazą danych " + db_ip + ":" + db_port + ".");
        } catch (SQLException e) {
            WritingToEventLogArea.error("Wystąpił nieoczekiwany błąd. Sprawdź log aplikacji.", EventLog);
        }
        
        return ActiveConnection;
    }
}
