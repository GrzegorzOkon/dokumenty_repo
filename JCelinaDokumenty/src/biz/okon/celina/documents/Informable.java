/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.okon.celina.documents;

import java.lang.System;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;
import static biz.okon.celina.documents.ReadingXMLParams.FileLogger;

/**
 * Klasa ta zapisuje treść raportu, login i czas wykonania do pliku bazodanowego sqlini na serwerze zdalnym.
 * 
 * @author okongrzegorz
 */
public class Informable { 
    private static Logger FileLogger = Logger.getLogger(Informable.class); 
    private Connection conn = null;
    private String sql = "";
    private PreparedStatement pstmt = null;   
    private String UserName = System.getProperty("user.name"); 
    
    public void saveData(String Text) {
        Connection2Sqlite ConnToSqlite = new Connection2Sqlite();
        conn = ConnToSqlite.connectSqlite();

        try {
            sql = "insert into raports (date, user, raport) values (?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, getCurrentTimeStamp());
            pstmt.setString(2, UserName);
            pstmt.setString(3, Text);
            pstmt.executeUpdate();
        }
        catch (Exception e) {
            FileLogger.error(e);
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {}
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {}
            }
        }
    } 
    
    /**
     * Bieżąca data jest podana w formacie 15-07-2015, 14:17:45.
     */
    private static String getCurrentTimeStamp() {
        Date CurrentTime = new Date(); 
        SimpleDateFormat CustomizedDateFormat = new SimpleDateFormat("dd-MM-yyyy, HH:mm:ss");
        String CustomizedDate = CustomizedDateFormat.format(CurrentTime);
        return CustomizedDate;
    } 
}
