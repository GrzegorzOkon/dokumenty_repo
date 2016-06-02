/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.okon.celina.documents;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author okongrzegorz
 */
public class Connection2Sqlite {   
    private Connection ActiveConnection = null;    

    Connection connectSqlite() {
        try {
            Class.forName("org.sqlite.JDBC");
            ActiveConnection = DriverManager.getConnection("jdbc:sqlite://*************");
        } catch (Exception e) {
            System.err.println( e );
        } 
        return ActiveConnection;
    }
}
