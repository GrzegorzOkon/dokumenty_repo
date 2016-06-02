/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.okon.celina.documents;

import java.util.ArrayList;
import java.util.Properties;
import javax.swing.JRadioButton;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import static biz.okon.celina.documents.ReadingXMLParams.FileLogger;

/*
 * ffffff
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

public class BusinessLogic {
    private EventLogArea WritingToEventLogArea;
    private Informable WritingToSqlite;
    
    void checkDocument (int flag, JTabbedPane TabbedPanel, JRadioButton ActNumberButton, JRadioButton IdDokButton, JRadioButton SymDokButton, JTextArea Numbers, JTextArea Report, JTextArea EventLog, JProgressBar jProgressBar1, ArrayList<Properties> XMLProperties, HashMap<String, String> XMLCodes, HashMap<String, String> XMLStatuses) {
        if (flag == 1) {
            //Wyszukiwanie dokumentow po nr_akt
            if (ActNumberButton.isSelected() == true) {
                WritingToEventLogArea = new EventLogArea();
                WritingToSqlite = new Informable();
                ResultSet rs1 = null;
                ResultSet rs2 = null;
                String sql = "";
                String x = "";
                PreparedStatement pstmt = null;
                int rowcount1 = 0;
                int rowcount2 = 0;
                
                //Pobranie ilości wierszy i wyświetlenie paska postępu
                String[] linie = Numbers.getText().split("\n");
                jProgressBar1.setValue(0);
                jProgressBar1.setStringPainted(true);  //pokazuje znaki
                jProgressBar1.setMinimum(0);
                jProgressBar1.setMaximum(linie.length);
                jProgressBar1.setVisible(true);
                
                Properties SearchedProperty = getProperties("Centrala", XMLProperties);
                Connection2Celina ConnectionToCentral = new Connection2Celina();
                Connection ActiveConnection = ConnectionToCentral.connectCentral(SearchedProperty, EventLog);
                
                try {
                    for (int i = 0; i < linie.length; i++) {
                        rs1 = null;
                        sql = "select * from cntr_valid_dok where nr_akt = ?";
                        x = linie[i];
                        pstmt = null;
                        rowcount1 = 0;
                        rowcount2 = 0;
                    
                        //Zmienne dla wartości przechowywanych w tabeli cntr_valid _dok
                        String id_dok = "";
                        String nr_akt = ""; 
                        String status_przetw = "";
                        String rodzaj_dokumentu = "";
                        String xml_error = "";
                        
                        //Zmienne dla wartości przechowywanych w tabeli dokumenty
                        String id_dok2 = "";
                        String sym_dok = "";
                        String status = "";
                        
                        // Wykonanie SQL
                        WritingToEventLogArea.debug("Wyszukiwanie dokumentu po numerze własnym " + x + ".", EventLog);
                        FileLogger.info("Wyszukiwanie dokumentu po numerze własnym " + x + ".");
                        pstmt = ActiveConnection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        pstmt.setString(1, x);
                        WritingToEventLogArea.debug("Wykonywanie " + sql.substring(0, 44).concat(x) + ".", EventLog);
                        FileLogger.debug("Wykonywanie " + sql.substring(0, 44).concat(x) + ".");
                        rs1 = pstmt.executeQuery();
                        
                        // Sprawdzenie ilości zwróconych wierszy
                        if (rs1.last()) { //idziemy na ostani wiersz
                            rowcount1 = rs1.getRow(); //patrzymy, który to wiersz
                            rs1.beforeFirst(); //przesuwamy nie rs.first() bo ja użyjesz rs.next() to stracisz pierwszy element
                        }
                        WritingToEventLogArea.info("Zwrócona liczba wierszy: " + rowcount1 + ".", EventLog);  
                        FileLogger.debug("Zwrócona liczba wierszy: " + rowcount1 + ".");
                        
                        if (rowcount1 == 0) {
                            Report.append("\nDokumentu " + x + " brak w bazie centralnej.\n");
                            FileLogger.debug("Dokumentu " + x +" brak w bazie centralnej.");
                            WritingToSqlite.saveData("Dokumentu " + x + " brak w bazie centralnej.");
                        }    
                        else {
                            // Wyciąganie danych ze zbioru rezultatów tabeli cntr_valid_dok
                            while (rs1.next()) {
                                id_dok = rs1.getString("id_dok");
                                nr_akt = rs1.getString("nr_akt");
                                status_przetw = rs1.getString("status_przetw");
                                rodzaj_dokumentu = rs1.getString("id_rodz_dok");
                                xml_error = rs1.getString("xml_error");
                                         
                                if (XMLStatuses.containsKey(rodzaj_dokumentu + "-" + status_przetw + "-C")) {
                                    Report.append("\nDokument " + nr_akt + " znajduje się w tabeli cntr_valid_dok bazy centralnej. "
                                        + "Ma nadany numer systemowy " + id_dok + ". "
                                        + "Status dokumentu: " + status_przetw + " - " + XMLStatuses.get(rodzaj_dokumentu + "-" + status_przetw + "-C") + ".\n" + checkErrors(xml_error));
                                    FileLogger.debug("RAPORT: Dokument " + nr_akt + " znajduje się w tabeli cntr_valid_dok bazy centralnej. "
                                        + "Ma nadany numer systemowy " + id_dok + ". "
                                        + "Status dokumentu: " + status_przetw + " - " + XMLStatuses.get(rodzaj_dokumentu + "-" + status_przetw + "-C") + ".\n" + checkErrors(xml_error));
                                    WritingToSqlite.saveData("RAPORT: Dokument " + nr_akt + " znajduje się w tabeli cntr_valid_dok bazy centralnej. "
                                        + "Ma nadany numer systemowy " + id_dok + " ."
                                        + "Status dokumentu: " + status_przetw + " - " + XMLStatuses.get(rodzaj_dokumentu + "-" + status_przetw + "-C") + ".\n" + checkErrors(xml_error));
                                }
                                else {
                                    Report.append("\nDokument " + nr_akt + " znajduje się w tabeli cntr_valid_dok w bazie centralnej. "
                                        + "Ma nadany numer systemowy " + id_dok + ". "
                                        + "Status dokumentu: " + status_przetw + ".\n" + checkErrors(xml_error));
                                    FileLogger.debug("RAPORT: Dokument " + nr_akt + " znajduje się w tabeli cntr_valid_dok bazy centralnej. "
                                        + "Ma nadany numer systemowy " + id_dok + ". "
                                        + "Status dokumentu: " + status_przetw + ".\n" + checkErrors(xml_error));
                                    WritingToSqlite.saveData("RAPORT: Dokument " + nr_akt + " znajduje się w tabeli cntr_valid_dok bazy centralnej. "
                                        + "Ma nadany numer systemowy " + id_dok + " ."
                                        + "Status dokumentu: " + status_przetw + ".\n" + checkErrors(xml_error));
                                }
                                 
                                sql = "select * from dokumenty where id_dok = ?";
                                // Wykonanie SQL
                                WritingToEventLogArea.debug("Wyszukiwanie dokumentu po numerze systemowym " + id_dok + ".", EventLog);
                                FileLogger.info("Wyszukiwanie dokumentu po numerze systemowym " + id_dok + ".");
                                pstmt = ActiveConnection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                                pstmt.setString(1, id_dok);
                                WritingToEventLogArea.debug("Wykonywanie " + sql.substring(0, 39).concat(id_dok) + ".", EventLog);
                                FileLogger.debug("Wykonywanie " + sql.substring(0, 39).concat(id_dok) + ".");
                                rs2 = pstmt.executeQuery();
                            
                                // Sprawdzenie ilości zwróconych wierszy
                                if (rs2.last()) { //idziemy na ostani wiersz
                                    rowcount2 = rs2.getRow(); //patrzymy, który to wiersz
                                    rs2.beforeFirst(); //przesuwamy nie rs.first() bo ja użyjesz rs.next() to stracisz pierwszy element
                                }
                                WritingToEventLogArea.info("Zwrócona liczba wierszy: " + rowcount2 + ".", EventLog);  
                                FileLogger.debug("Zwrócona liczba wierszy: " + rowcount2 + ".");
                            
                                if (rowcount2 == 0) {
                                    Report.append("\nDokumentu " + id_dok + " brak w tabeli dokumenty bazy centralnej.\n");
                                    FileLogger.debug("Dokumentu " + id_dok + " brak w tabeli dokumenty bazy centralnej.");
                                    WritingToSqlite.saveData("Dokumentu " + id_dok + " brak w tabeli dokumenty bazy centralnej.");
                                }
                                else {
                                    // Wyciąganie danych z tabeli dokumenty
                                    while (rs2.next()) {
                                        // Wybieranie według nazw kolumn
                                        id_dok2 =rs2.getString("id_dok");
                                        sym_dok = rs2.getString("sym_dok");
                                        status = rs2.getString("status");
                                        rodzaj_dokumentu = rs2.getString("id_rodz_dok");

                                        if (XMLStatuses.containsKey(rodzaj_dokumentu + "-" + status + "-D")) {
                                            Report.append("\nDokument " + id_dok + " znajduje się w tabeli dokumenty bazy centralnej. "
                                                + "Ma nadany numer ewidencyjny " + sym_dok + ". "
                                                + "Status dokumentu: " + status + " - " + XMLStatuses.get(rodzaj_dokumentu + "-" + status + "-D") + ".\n");
                                            FileLogger.debug("RAPORT: Dokument " + id_dok + " znajduje się w tabeli dokumenty bazy centralnej. "
                                                + "Ma nadany numer ewidencyjny " + sym_dok + ". "
                                                + "Status dokumentu: " + status + " - " + XMLStatuses.get(rodzaj_dokumentu + "-" + status + "-D") + ".");
                                            WritingToSqlite.saveData("RAPORT: Dokument " + id_dok + " znajduje się w tabeli dokumenty bazy centralnej. "
                                                + "Ma nadany numer ewidencyjny " + sym_dok + ". "
                                                + "Status dokumentu: " + status + " - " + XMLStatuses.get(rodzaj_dokumentu + "-" + status + "-D") + ".");
                                        }
                                        else {
                                            Report.append("\nDokument " + id_dok + " znajduje się w tabeli dokumenty bazy centralnej. "
                                                + "Ma nadany numer ewidencyjny " + sym_dok + ". "
                                                + "Status dokumentu: " + status + ".\n");
                                            FileLogger.debug("RAPORT: Dokument " + id_dok + " znajduje się w tabeli dokumenty bazy centralnej. "
                                                + "Ma nadany numer ewidencyjny " + sym_dok + ". "
                                                + "Status dokumentu: " + status + ".");
                                            WritingToSqlite.saveData("RAPORT: Dokument " + id_dok + " znajduje się w tabeli dokumenty bazy centralnej. "
                                                + "Ma nadany numer ewidencyjny " + sym_dok + ". "
                                                + "Status dokumentu: " + status + ".");
                                        }
                                    }  
                                }
                            }
                        }  
                        
                        //Aktualizuje procentową wartość ilości wyszukanych dokumentów na pasku postępu
                        jProgressBar1.setValue((100*(i+1)/linie.length)); 
                    }
                    jProgressBar1.setVisible(false);
                    TabbedPanel.setSelectedIndex(1);
                } catch (SQLException e) {
                    //e.printStackTrace();
                    WritingToEventLogArea.error("Wystąpił nieoczekiwany błąd.", EventLog);
                } finally {
                    if (rs1 != null) {
                        try {
                            rs1.close();
                        } catch (SQLException e) {WritingToEventLogArea.error("Wystąpił nieoczekiwany błąd.", EventLog);}
                    }
                    if (rs2 != null) {
                        try {
                            rs2.close();
                        } catch (SQLException e) {WritingToEventLogArea.error("Wystąpił nieoczekiwany błąd.", EventLog);}
                    }
                    if (pstmt != null) {
                        try {
                            pstmt.close();
                            WritingToEventLogArea.info("Rozłączono z bazą danych.", EventLog);
                            FileLogger.debug("Rozłączono z bazą danych.");
                        } catch (SQLException e) {WritingToEventLogArea.error("Wystąpił nieoczekiwany błąd.", EventLog);}
                    }
                    if (ActiveConnection != null) {
                        try {
                            ActiveConnection.close();
                        } catch (SQLException e) {WritingToEventLogArea.error("Wystąpił nieoczekiwany błąd.", EventLog);}
                    }
                }
            }     
            
            //Wyszukiwanie dokumentow po id_dok
            if (IdDokButton.isSelected() == true) {
                WritingToEventLogArea = new EventLogArea();
                WritingToSqlite = new Informable();
                ResultSet rs1 = null;
                ResultSet rs2 = null;
                String sql = "";
                String x = "";
                PreparedStatement pstmt = null;
                int rowcount1 = 0;
                int rowcount2 = 0;
                
                //Pobranie ilości wierszy i wyświetlenie paska postępu
                String[] linie = Numbers.getText().split("\n"); 
                jProgressBar1.setValue(0);
                jProgressBar1.setStringPainted(true);  //pokazuje znaki
                jProgressBar1.setMinimum(0);
                jProgressBar1.setMaximum(linie.length);
                jProgressBar1.setVisible(true);
                
                Properties SearchedProperty = getProperties("Centrala", XMLProperties);
                Connection2Celina ConnectionToCentral = new Connection2Celina();
                Connection ActiveConnection = ConnectionToCentral.connectCentral(SearchedProperty, EventLog);
                
                try {
                    for (int i = 0; i < linie.length; i++) {
                        rs1 = null;
                        sql = "select * from dokumenty where id_dok = ?";
                        x = linie[i];
                        pstmt = null;
                        rowcount1 = 0;
                        rowcount2 = 0;
                        
                        //Zmienne dla wartości przechowywanych w tabeli dokumenty
                        String id_dok = "";
                        String sym_dok = "";
                        String status = "";
                        
                         //Zmienne dla wartości przechowywanych w tabeli cntr_valid _dok
                        String id_dok2 = "";
                        String nr_akt = "";
                        String data_przeslania = "";
                        String rodzaj_dokumentu = "";
                        String status_przetw = "";
                        String xml_error = "";
                        
                        // Wykonanie SQL
                        WritingToEventLogArea.debug("Wyszukiwanie dokumentu po numerze systemowym " + x + ".", EventLog);
                        FileLogger.info("Wyszukiwanie dokumentu po numerze systemowym " + x + ".");
                        pstmt = ActiveConnection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        pstmt.setString(1, x);
                        WritingToEventLogArea.debug("Wykonywanie " + sql.substring(0, 39).concat(x) + ".", EventLog);
                        FileLogger.debug("Wykonywanie " + sql.substring(0, 39).concat(x) + ".");
                        rs1 = pstmt.executeQuery();
                        
                        // Sprawdzenie ilości zwróconych wierszy
                        if (rs1.last()) { //idziemy na ostani wiersz
                            rowcount1 = rs1.getRow(); //patrzymy, który to wiersz
                            rs1.beforeFirst(); //przesuwamy nie rs.first() bo ja użyjesz rs.next() to stracisz pierwszy element
                        }
                        WritingToEventLogArea.info("Zwrócona liczba wierszy: " + rowcount1 + ".", EventLog);  
                        FileLogger.debug("Zwrócona liczba wierszy: " + rowcount1 + ".");
                        
                        if (rowcount1 == 0) {                           
                            sql = "select * from cntr_valid_dok where id_dok = ?";
                            // Wykonanie SQL
                            WritingToEventLogArea.debug("Wyszukiwanie dokumentu po numerze systemowym " + x + ".", EventLog);
                            FileLogger.info("Wyszukiwanie dokumentu po numerze systemowym " + x + ".");
                            pstmt = ActiveConnection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                            pstmt.setString(1, x);
                            WritingToEventLogArea.debug("Wykonywanie " + sql.substring(0, 44).concat(x) + ".", EventLog);
                            FileLogger.debug("Wykonywanie " + sql.substring(0, 44).concat(x) + ".");
                            rs2 = pstmt.executeQuery();
                            
                            // Sprawdzenie ilości zwróconych wierszy
                            if (rs2.last()) { //idziemy na ostani wiersz
                                rowcount2 = rs2.getRow(); //patrzymy, który to wiersz
                                rs2.beforeFirst(); //przesuwamy nie rs.first() bo ja użyjesz rs.next() to stracisz pierwszy element
                            }
                            WritingToEventLogArea.info("Zwrócona liczba wierszy: " + rowcount2 + ".", EventLog);  
                            FileLogger.debug("Zwrócona liczba wierszy: " + rowcount2 + ".");
                            
                            if (rowcount2 == 0) {
                                Report.append("\nDokumentu " + x + " brak w bazie centralnej.\n");
                                FileLogger.debug("Dokumentu " + x + " brak w bazie centralnej.");
                                WritingToSqlite.saveData("Dokumentu " + x + " brak w bazie centralnej.");
                            }
                            else {
                                while (rs2.next()) {
                                    // Wybieranie danych z tabeli cntr_valid_dok
                                    id_dok2 = rs2.getString("id_dok");
                                    nr_akt = rs2.getString("nr_akt");
                                    data_przeslania = rs2.getString("data_przeslania");
                                    status_przetw = rs2.getString("status_przetw");
                                    rodzaj_dokumentu = rs2.getString("id_rodz_dok");
                                    xml_error = rs2.getString("xml_error");
                                    
                                    if (XMLStatuses.containsKey(rodzaj_dokumentu + "-" + status_przetw + "-C")) {
                                        Report.append("\nDokument " + id_dok2 + " wysłany " + data_przeslania + " znajduje się w bazie centralnej. "
                                            + "Ma przypisany numer własny " + nr_akt + ". "
                                            + "Status dokumentu: " + status_przetw + " - " + XMLStatuses.get(rodzaj_dokumentu + "-" + status_przetw + "-C") + ".\n" + checkErrors(xml_error));
                                        FileLogger.debug("RAPORT: Dokument " + id_dok2 + " wysłany " + data_przeslania + " znajduje się w bazie centralnej. "
                                            + "Ma przypisany numer własny " + nr_akt + ". "
                                            + "Status dokumentu: " + status_przetw + " - " + XMLStatuses.get(rodzaj_dokumentu + "-" + status_przetw + "-C") + ".\n" + checkErrors(xml_error));
                                        WritingToSqlite.saveData("RAPORT: Dokument " + id_dok2 + " wysłany " + data_przeslania + " znajduje się w bazie centralnej. "
                                            + "Ma przypisany numer własny " + nr_akt + ". "
                                            + "Status dokumentu: " + status_przetw + " - " + XMLStatuses.get(rodzaj_dokumentu + "-" + status_przetw + "-C") + ".\n" + checkErrors(xml_error));
                                    }
                                    else {
                                        Report.append("\nDokument " + id_dok2 + " wysłany " + data_przeslania + " znajduje się w bazie centralnej. "
                                            + "Ma przypisany numer własny " + nr_akt + ". "
                                            + "Status dokumentu: " + status_przetw + ".\n" + checkErrors(xml_error));
                                        FileLogger.debug("RAPORT: Dokument " + id_dok2 + " wysłany " + data_przeslania + " znajduje się w bazie centralnej. "
                                            + "Ma przypisany numer własny " + nr_akt + ". "
                                            + "Status dokumentu: " + status_przetw + ".\n" + checkErrors(xml_error));
                                        WritingToSqlite.saveData("RAPORT: Dokument " + id_dok2 + " wysłany " + data_przeslania + " znajduje się w bazie centralnej. "
                                            + "Ma przypisany numer własny " + nr_akt + ". "
                                            + "Status dokumentu: " + status_przetw + ".\n" + checkErrors(xml_error));
                                    }
                                }
                            }
                        }
                        else {
                            // Wyciąganie danych ze zbioru rezultatów z tabeli dokumenty
                            while (rs1.next()) {
                                // Wybieranie według nazw kolumn
                                id_dok = rs1.getString("id_dok");
                                sym_dok = rs1.getString("sym_dok");
                                status = rs1.getString("status");
                                rodzaj_dokumentu = rs1.getString("id_rodz_dok");
                                
                                if (XMLStatuses.containsKey(rodzaj_dokumentu + "-" + status + "-D")) {
                                    Report.append("\nDokument " + id_dok + " znajduje się w bazie centralnej. "
                                        + "Ma nadany numer ewidencyjny " + sym_dok + ". "
                                        + "Status dokumentu: " + status + " - " + XMLStatuses.get(rodzaj_dokumentu + "-" + status + "-D") + ".\n");
                                    FileLogger.debug("RAPORT: Dokument " + id_dok + " znajduje się w bazie centralnej. "
                                        + "Ma nadany numer ewidencyjny " + sym_dok + ". "
                                        + "Status dokumentu: " + status + " - " + XMLStatuses.get(rodzaj_dokumentu + "-" + status + "-D") + ".");
                                    WritingToSqlite.saveData("RAPORT: Dokument " + id_dok + " znajduje się w bazie centralnej. "
                                        + "Ma nadany numer ewidencyjny " + sym_dok + ". "
                                        + "Status dokumentu: " + status + " - " + XMLStatuses.get(rodzaj_dokumentu + "-" + status + "-D") + ".");
                                }
                                else {
                                    Report.append("\nDokument " + id_dok + " znajduje się w bazie centralnej. "
                                        + "Ma nadany numer ewidencyjny " + sym_dok + ". "
                                        + "Status dokumentu: " + status + ".\n");
                                    FileLogger.debug("RAPORT: Dokument " + id_dok + " znajduje się w bazie centralnej. "
                                        + "Ma nadany numer ewidencyjny " + sym_dok + ". "
                                        + "Status dokumentu: " + status + ".");
                                    WritingToSqlite.saveData("RAPORT: Dokument " + id_dok + " znajduje się w bazie centralnej. "
                                        + "Ma nadany numer ewidencyjny " + sym_dok + ". "
                                        + "Status dokumentu: " + status + ".");
                                }
                            }   
                        }  
                        //Aktualizuje procentową wartość ilości wyszukanych dokumentów na pasku postępu
                        jProgressBar1.setValue((100*(i+1)/linie.length)); 
                    }
                    jProgressBar1.setVisible(false);
                    TabbedPanel.setSelectedIndex(1);
                } catch (SQLException e) {
                    //e.printStackTrace();
                    WritingToEventLogArea.error("Wystąpił nieoczekiwany błąd.", EventLog);
                } finally {
                    if (rs1 != null) {
                        try {
                            rs1.close();
                        } catch (SQLException e) {WritingToEventLogArea.error("Wystąpił nieoczekiwany błąd.", EventLog);}
                    }
                    if (rs2 != null) {
                        try {
                            rs2.close();
                        } catch (SQLException e) {WritingToEventLogArea.error("Wystąpił nieoczekiwany błąd.", EventLog);}
                    }
                    if (pstmt != null) {
                        try {
                            pstmt.close();
                            WritingToEventLogArea.info("Rozłączono z bazą danych.", EventLog);
                            FileLogger.debug("Rozłączono z bazą danych.");
                        } catch (SQLException e) {WritingToEventLogArea.error("Wystąpił nieoczekiwany błąd.", EventLog);}
                    }
                    if (ActiveConnection != null) {
                        try {
                            ActiveConnection.close();
                        } catch (SQLException e) {WritingToEventLogArea.error("Wystąpił nieoczekiwany błąd.", EventLog);}
                    }
                }
            }   
         
            //Wyszukiwanie dokumentow po sym_dok
            if (SymDokButton.isSelected() == true) {
                WritingToEventLogArea = new EventLogArea();
                WritingToSqlite = new Informable();
                ResultSet rs = null;
                String sql = "";
                String x = "";
                PreparedStatement pstmt = null;
                int rowcount = 0;
                
                //Zmienne do przechowywania wartości przechowywanych w bazie danych
                String id_dok = "";
                String id_jedn = "";
                String sym_dok = "";
                String dat_wystaw_dok = "";
                String status = "";
                String semafor = "";
                String rodzaj_dokumentu = "";
                
                //Pobranie ilości wierszy i wyświetlenie paska postępu
                String[] linie = Numbers.getText().split("\n"); 
                jProgressBar1.setValue(0);
                jProgressBar1.setStringPainted(true);  //pokazuje znaki
                jProgressBar1.setMinimum(0);
                jProgressBar1.setMaximum(linie.length);
                jProgressBar1.setVisible(true);
                
                Properties SearchedProperty = getProperties("Centrala", XMLProperties);
                Connection2Celina ConnectionToCentral = new Connection2Celina();
                Connection ActiveConnection = ConnectionToCentral.connectCentral(SearchedProperty, EventLog);
                
                try {
                    for (int i = 0; i < linie.length; i++) {
                        rs = null;
                        sql = "select * from dokumenty where sym_dok = ?";
                        x = linie[i];
                        pstmt = null;
                        rowcount = 0;
                    
                        //Zapis wartości przechowywanych w bazie danych
                        id_dok = "";
                        id_jedn = "";
                        sym_dok = "";
                        dat_wystaw_dok = "";
                        status = "";
                        semafor = "";
                        rodzaj_dokumentu = "";
                        
                        // Wykonanie SQL
                        WritingToEventLogArea.debug("Wyszukiwanie dokumentu po numerze ewidencyjnym " + x + ".", EventLog);
                        FileLogger.info("Wyszukiwanie dokumentu po numerze ewidencyjnym " + x + ".");
                        pstmt = ActiveConnection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        pstmt.setString(1, x);
                        WritingToEventLogArea.debug("Wykonywanie " + sql.substring(0, 40).concat(x) + ".", EventLog);
                        FileLogger.debug("Wykonywanie " + sql.substring(0, 40).concat(x) + ".");
                        rs = pstmt.executeQuery();
                        
                        // Sprawdzenie ilości zwróconych wierszy
                        if (rs.last()) { //idziemy na ostani wiersz
                            rowcount = rs.getRow(); //patrzymy, który to wiersz
                            rs.beforeFirst(); //przesuwamy nie rs.first() bo ja użyjesz rs.next() to stracisz pierwszy element
                        }
                        WritingToEventLogArea.info("Zwrócona liczba wierszy: " + rowcount + ".", EventLog);  
                        FileLogger.debug("Zwrócona liczba wierszy: " + rowcount + ".");
                        
                        if (rowcount == 0) {
                            Report.append("\nDokumentu " + x +" brak w tabeli dokumenty na bazie centralnej.\n");
                            FileLogger.debug("Dokumentu " + x +" brak w tabeli dokumenty na bazie centralnej.");
                            WritingToSqlite.saveData("Dokumentu " + x +" brak w tabeli dokumenty na bazie centralnej.");
                        }
                        else {
                            // Wyciąganie danych ze zbioru rezultatów z tabeli dokumenty
                            while (rs.next()) {
                                // Wybieranie według nazw kolumn
                                id_dok = rs.getString("id_dok");
                                sym_dok = rs.getString("sym_dok");
                                status = rs.getString("status");
                                rodzaj_dokumentu = rs.getString("id_rodz_dok");
                                
                                if (XMLStatuses.containsKey(rodzaj_dokumentu + "-" + status + "-D")) {
                                    Report.append("\nDokument " + sym_dok + " znajduje się w bazie centralnej. "
                                        + "Ma nadany numer systemowy " + id_dok + ". "
                                        + "Status dokumentu: " + status + " - " + XMLStatuses.get(rodzaj_dokumentu + "-" + status + "-D") + ".\n");
                                    FileLogger.debug("RAPORT: Dokument " + sym_dok + " znajduje się w bazie centralnej. " 
                                        + "Ma nadany numer systemowy " + id_dok + ". "
                                        + "Status dokumentu: " + status + " - " + XMLStatuses.get(rodzaj_dokumentu + "-" + status + "-D") + "."); 
                                    WritingToSqlite.saveData("RAPORT: Dokument " + sym_dok + " znajduje się w bazie centralnej. " 
                                        + "Ma nadany numer systemowy " + id_dok + ". "
                                        + "Status dokumentu: " + status + " - " + XMLStatuses.get(rodzaj_dokumentu + "-" + status + "-D") + "."); 
                                }
                                else {
                                    Report.append("\nDokument " + sym_dok + " znajduje się w bazie centralnej. "
                                        + "Ma nadany numer systemowy " + id_dok + ". "
                                        + "Status dokumentu: " + status + ".\n");
                                    FileLogger.debug("RAPORT: Dokument " + sym_dok + " znajduje się w bazie centralnej. "
                                        + "Ma nadany numer systemowy " + id_dok + ". "
                                        + "Status dokumentu: " + status + ".");
                                    WritingToSqlite.saveData("RAPORT: Dokument " + sym_dok + " znajduje się w bazie centralnej. "
                                        + "Ma nadany numer systemowy " + id_dok + ". "
                                        + "Status dokumentu: " + status + ".");
                                }
                            }   
                        }  
                        //Aktualizuje procentową wartość ilości wyszukanych dokumentów na pasku postępu
                        jProgressBar1.setValue((100*(i+1)/linie.length)); 
                    }
                    jProgressBar1.setVisible(false);
                    TabbedPanel.setSelectedIndex(1);
                } catch (SQLException e) {
                    //e.printStackTrace();
                    WritingToEventLogArea.error("Wystąpił nieoczekiwany błąd.", EventLog);
                } finally {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException e) {WritingToEventLogArea.error("Wystąpił nieoczekiwany błąd.", EventLog);}
                    }
                    if (pstmt != null) {
                        try {
                            pstmt.close();
                            WritingToEventLogArea.info("Rozłączono z bazą danych.", EventLog);
                            FileLogger.debug("Rozłączono z bazą danych.");
                        } catch (SQLException e) {WritingToEventLogArea.error("Wystąpił nieoczekiwany błąd.", EventLog);}
                    }
                    if (ActiveConnection != null) {
                        try {
                            ActiveConnection.close();
                        } catch (SQLException e) {WritingToEventLogArea.error("Wystąpił nieoczekiwany błąd.", EventLog);}
                    }
                }
            }
        }
        
        if (flag == 2) {
            if (ActNumberButton.isSelected() == true) {
                Report.append("\nNależy podać inny numer niż numer własny dla izb lokalnych.\n");
            }     
            
            //Wyszukiwanie dokumentow po id_dok na bazie lokalnej
            if (IdDokButton.isSelected() == true) {
                WritingToEventLogArea = new EventLogArea();
                WritingToSqlite = new Informable();
                ResultSet rs1 = null;
                ResultSet rs2 = null;
                ResultSet rs3 = null;
                String sql = "";
                String x = "";
                PreparedStatement pstmt = null;
                int rowcount1 = 0;
                int rowcount2 = 0;
                int rowcount3 = 0;
                
                //Pobranie ilości wierszy i wyświetlenie paska postępu
                String[] linie = Numbers.getText().split("\n"); 
                jProgressBar1.setValue(0);
                jProgressBar1.setStringPainted(true);  //pokazuje znaki
                jProgressBar1.setMinimum(0);
                jProgressBar1.setMaximum(linie.length);
                jProgressBar1.setVisible(true);
                
                Properties SearchedProperty = getProperties("Centrala", XMLProperties);
                Connection2Celina ConnectionToCentral = new Connection2Celina();
                Connection ActiveConnection = ConnectionToCentral.connectCentral(SearchedProperty, EventLog);
                
                try {
                    for (int i = 0; i < linie.length; i++) {
                        rs1 = null;
                        sql = "select * from dokumenty where id_dok = ?";
                        x = linie[i];
                        pstmt = null;
                        rowcount1 = 0;
                    
                        //Zapis wartości przechowywanych w bazie danych
                        String id_dok = "";
                        String id_jedn = "";
                        String sym_dok = "";
                        String dat_wystaw_dok = "";
                        String status = "";
                        String semafor = "";
                        String jednostka = "";
                        
                        // Wykonanie SQL
                        WritingToEventLogArea.debug("Wyszukiwanie dokumentu po numerze systemowym " + x + ".", EventLog);
                        FileLogger.info("Wyszukiwanie dokumentu po numerze systemowym " + x + ".");
                        pstmt = ActiveConnection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        pstmt.setString(1, x);
                        WritingToEventLogArea.debug("Wykonywanie " + sql.substring(0, 39).concat(x) + ".", EventLog);
                        FileLogger.debug("Wykonywanie " + sql.substring(0, 39).concat(x) + ".");
                        rs1 = pstmt.executeQuery();
                        
                        // Sprawdzenie ilości zwróconych wierszy
                        if (rs1.last()) { //idziemy na ostani wiersz
                            rowcount1 = rs1.getRow(); //patrzymy, który to wiersz
                            rs1.beforeFirst(); //przesuwamy nie rs.first() bo ja użyjesz rs.next() to stracisz pierwszy element
                        }
                        WritingToEventLogArea.info("Zwrócona liczba wierszy: " + rowcount1 + ".", EventLog);  
                        FileLogger.debug("Zwrócona liczba wierszy: " + rowcount1 + ".");
                        
                        //Szukanie dokumentu w tabeli cntr_valid_dok, skoro nie ma w tabeli dokumenty
                        if (rowcount1 == 0) {
                            //Report.append("\nDokumentu " + x +" brak w tabeli dokumenty na bazie centralnej.\n");
                            //FileLogger.debug("Dokumentu " + x +" brak w tabeli dokumenty na bazie centralnej.");
                            
                            sql = "select * from cntr_valid_dok where id_dok = ?";
                            // Wykonanie SQL
                            WritingToEventLogArea.debug("Wyszukiwanie dokumentu po numerze systemowym " + x + ".", EventLog);
                            FileLogger.info("Wyszukiwanie dokumentu po numerze systemowym " + x + ".");
                            pstmt = ActiveConnection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                            pstmt.setString(1, x);
                            WritingToEventLogArea.debug("Wykonywanie " + sql.substring(0, 43).concat(x) + ".", EventLog);
                            FileLogger.debug("Wykonywanie " + sql.substring(0, 43).concat(x) + ".");
                            rs2 = pstmt.executeQuery();
                            
                            // Sprawdzenie ilości zwróconych wierszy
                            if (rs2.last()) { //idziemy na ostani wiersz
                                rowcount2 = rs2.getRow(); //patrzymy, który to wiersz
                                rs2.beforeFirst(); //przesuwamy nie rs.first() bo ja użyjesz rs.next() to stracisz pierwszy element
                            }
                            WritingToEventLogArea.info("Zwrócona liczba wierszy: " + rowcount2 + ".", EventLog);  
                            FileLogger.debug("Zwrócona liczba wierszy: " + rowcount2 + ".");
                            
                            if (rowcount2 == 0) {
                                Report.append("\nDokumentu " + x + " brak w tabelach dokumenty oraz cntr_valid_dok na bazie centralnej. Nie można pobrać kodu oddziału.\n");
                                FileLogger.debug("Dokumentu " + x + " brak w tabelach dokumenty oraz cntr_valid_dok na bazie centralnej. Nie można pobrać kodu oddziału.");
                                WritingToSqlite.saveData("Dokumentu " + x + " brak w tabelach dokumenty oraz cntr_valid_dok na bazie centralnej. Nie można pobrać kodu oddziału.");
                            }
                            else {
                                // Wyciąganie danych ze zbioru rezultatów
                                while (rs2.next()) {
                                    jednostka = rs2.getString("jedn_przezn");
                                }   
                            }
                        }
                        else {
                            // Wyciąganie danych ze zbioru rezultatów
                            while (rs1.next()) {
                                jednostka = rs1.getString("id_jedn");
                            }   
                        }  
                        
                        //Szukanie na bazie lokalnej po przekazaniu numeru oddziału
                        if (jednostka != "") {
                            String SearchedDatabase = XMLCodes.get(jednostka);
                            SearchedProperty = getProperties(SearchedDatabase, XMLProperties);
                            Connection2Celina ConnectionToLocal = new Connection2Celina();
                            ActiveConnection = ConnectionToLocal.connectLocal(SearchedProperty, EventLog);
                        
                            rs3 = null;
                            sql = "select * from dokumenty where id_dok = ?";
                            x = linie[i];
                            pstmt = null;
                            rowcount3 = 0;
                        
                            // Wykonanie SQL
                            WritingToEventLogArea.debug("Wyszukiwanie dokumentu po numerze systemowym " + x + ".", EventLog);
                            FileLogger.info("Wyszukiwanie dokumentu po numerze systemowym " + x + ".");
                            pstmt = ActiveConnection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                            pstmt.setString(1, x);
                            WritingToEventLogArea.debug("Wykonywanie " + sql.substring(0, 39).concat(x) + ".", EventLog);
                            FileLogger.debug("Wykonywanie " + sql.substring(0, 39).concat(x) + ".");
                            rs3 = pstmt.executeQuery();
                        
                            // Sprawdzenie ilości zwróconych wierszy
                            if (rs3.last()) { //idziemy na ostani wiersz
                                rowcount3 = rs3.getRow(); //patrzymy, który to wiersz
                                rs3.beforeFirst(); //przesuwamy nie rs.first() bo ja użyjesz rs.next() to stracisz pierwszy element
                            }
                            WritingToEventLogArea.info("Zwrócona liczba wierszy: " + rowcount3 + ".", EventLog);  
                            FileLogger.debug("Zwrócona liczba wierszy: " + rowcount3 + ".");
                        
                            if (rowcount3 == 0) {
                                Report.append("\nDokumentu " + x + " brak w bazie lokalnej oddziału " + jednostka + " (" + SearchedDatabase + ").\n");
                                FileLogger.debug("Dokumentu " + x + " brak w bazie lokalnej oddziału " + jednostka + " (" + SearchedDatabase + ").");
                                WritingToSqlite.saveData("Dokumentu " + x + " brak w bazie lokalnej oddziału " + jednostka + " (" + SearchedDatabase + ").");
                            }
                            else {
                                // Wyciąganie danych ze zbioru rezultatów
                                while (rs3.next()) {
                                    // Wybieranie według nazw kolumn
                                    id_dok =rs3.getString("id_dok");
                                    id_jedn = rs3.getString("id_jedn");
                                    sym_dok = rs3.getString("sym_dok");
                                    dat_wystaw_dok = rs3.getString("dat_wystaw_dok");
                                    status = rs3.getString("status");
                                    semafor = rs3.getString("semafor");
                                    Report.append("\nDokument " + sym_dok + " znajduje się w tabeli dokumenty na bazie lokalnej oddziału " + id_jedn + " ("
                                        + SearchedDatabase + "). Ma nadany numer systemowy " + id_dok + ". Status dokumentu: " + status + ". Semafor: " + semafor + ". Data"
                                        + " wystawienia: " + dat_wystaw_dok + "\n");
                                    FileLogger.debug("Dokument " + sym_dok + " znajduje się w tabeli dokumenty na bazie lokalnej oddziału " + id_jedn + " ("
                                        + SearchedDatabase + "). Ma nadany numer systemowy " + id_dok + ". Status dokumentu: " + status + ". Semafor: " + semafor + ". Data"
                                        + " wystawienia: " + dat_wystaw_dok);
                                    WritingToSqlite.saveData("Dokument " + sym_dok + " znajduje się w tabeli dokumenty na bazie lokalnej oddziału " + id_jedn + " ("
                                        + SearchedDatabase + "). Ma nadany numer systemowy " + id_dok + ". Status dokumentu: " + status + ". Semafor: " + semafor + ". Data"
                                        + " wystawienia: " + dat_wystaw_dok);
                                }   
                            } 
                        }
                        //Aktualizuje procentową wartość ilości wyszukanych dokumentów na pasku postępu
                        jProgressBar1.setValue((100*(i+1)/linie.length));
                    }
                    jProgressBar1.setVisible(false);
                    TabbedPanel.setSelectedIndex(1);
                } catch (SQLException e) {
                    //e.printStackTrace();
                    WritingToEventLogArea.error("Wystąpił nieoczekiwany błąd.", EventLog);
                } finally {
                    if (rs1 != null) {
                        try {
                            rs1.close();
                        } catch (SQLException e) {WritingToEventLogArea.error("Wystąpił nieoczekiwany błąd.", EventLog);}
                    }
                    if (rs2 != null) {
                        try {
                            rs2.close();
                        } catch (SQLException e) {WritingToEventLogArea.error("Wystąpił nieoczekiwany błąd.", EventLog);}
                    }
                    if (rs3 != null) {
                        try {
                            rs3.close();
                        } catch (SQLException e) {WritingToEventLogArea.error("Wystąpił nieoczekiwany błąd.", EventLog);}
                    }
                    if (pstmt != null) {
                        try {
                            pstmt.close();
                            WritingToEventLogArea.info("Rozłączono z bazą danych.", EventLog);
                            FileLogger.debug("Rozłączono z bazą danych.");
                        } catch (SQLException e) {WritingToEventLogArea.error("Wystąpił nieoczekiwany błąd.", EventLog);}
                    }
                    if (ActiveConnection != null) {
                        try {
                            ActiveConnection.close();
                        } catch (SQLException e) {WritingToEventLogArea.error("Wystąpił nieoczekiwany błąd.", EventLog);}
                    }
                }
            }    
         
            if (SymDokButton.isSelected() == true) {
                WritingToEventLogArea = new EventLogArea();
                WritingToSqlite = new Informable();
                ResultSet rs1 = null;
                ResultSet rs2 = null;
                String sql = "";
                String x = "";
                PreparedStatement pstmt = null;
                int rowcount1 = 0;
                int rowcount2 = 0;
                
                //Pobranie ilości wierszy i wyświetlenie paska postępu
                String[] linie = Numbers.getText().split("\n"); 
                jProgressBar1.setValue(0);
                jProgressBar1.setStringPainted(true);  //pokazuje znaki
                jProgressBar1.setMinimum(0);
                jProgressBar1.setMaximum(linie.length);
                jProgressBar1.setVisible(true);
                
                Properties SearchedProperty = getProperties("Centrala", XMLProperties);
                Connection2Celina ConnectionToCentral = new Connection2Celina();
                Connection ActiveConnection = ConnectionToCentral.connectCentral(SearchedProperty, EventLog);
                
                try {
                    for (int i = 0; i < linie.length; i++) {
                        rs1 = null;
                        sql = "select * from dokumenty where sym_dok = ?";
                        x = linie[i];
                        pstmt = null;
                        rowcount1 = 0;
                    
                        //Zapis wartości przechowywanych w bazie danych
                        String id_dok = "";
                        String id_jedn = "";
                        String sym_dok = "";
                        String dat_wystaw_dok = "";
                        String status = "";
                        String semafor = "";
                        String jednostka = "";
                        
                        // Wykonanie SQL
                        WritingToEventLogArea.debug("Wyszukiwanie dokumentu po numerze systemowym " + x + ".", EventLog);
                        FileLogger.info("Wyszukiwanie dokumentu po numerze systemowym " + x + ".");
                        pstmt = ActiveConnection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        pstmt.setString(1, x);
                        WritingToEventLogArea.debug("Wykonywanie " + sql.substring(0, 40).concat(x) + ".", EventLog);
                        FileLogger.debug("Wykonywanie " + sql.substring(0, 40).concat(x) + ".");
                        rs1 = pstmt.executeQuery();
                        
                        // Sprawdzenie ilości zwróconych wierszy
                        if (rs1.last()) { //idziemy na ostani wiersz
                            rowcount1 = rs1.getRow(); //patrzymy, który to wiersz
                            rs1.beforeFirst(); //przesuwamy nie rs.first() bo ja użyjesz rs.next() to stracisz pierwszy element
                        }
                        WritingToEventLogArea.info("Zwrócona liczba wierszy: " + rowcount1 + ".", EventLog);  
                        FileLogger.debug("Zwrócona liczba wierszy: " + rowcount1 + ".");
                        
                        //Szukanie dokumentu w tabeli cntr_valid_dok, skoro nie ma w tabeli dokumenty
                        if (rowcount1 == 0) {
                            Report.append("\nDokumentu " + x +" brak w tabeli dokumenty na bazie centralnej.\n");
                            FileLogger.debug("Dokumentu " + x +" brak w tabeli dokumenty na bazie centralnej.");
                            WritingToSqlite.saveData("Dokumentu " + x +" brak w tabeli dokumenty na bazie centralnej.");
                        }
                        else {
                            // Wyciąganie danych ze zbioru rezultatów
                            while (rs1.next()) {
                                jednostka = rs1.getString("id_jedn");
                            }   
                        }  
                        
                        //Szukanie na bazie lokalnej po przekazaniu numeru oddziału
                        if (jednostka != "") {
                            String SearchedDatabase = XMLCodes.get(jednostka);
                            SearchedProperty = getProperties(SearchedDatabase, XMLProperties);
                            Connection2Celina ConnectionToLocal = new Connection2Celina();
                            ActiveConnection = ConnectionToLocal.connectLocal(SearchedProperty, EventLog);
                        
                            rs2 = null;
                            sql = "select * from dokumenty where sym_dok = ?";
                            x = linie[i];
                            pstmt = null;
                            rowcount2 = 0;
                        
                            // Wykonanie SQL
                            WritingToEventLogArea.debug("Wyszukiwanie dokumentu po numerze systemowym " + x + ".", EventLog);
                            FileLogger.info("Wyszukiwanie dokumentu po numerze systemowym " + x + ".");
                            pstmt = ActiveConnection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                            pstmt.setString(1, x);
                            WritingToEventLogArea.debug("Wykonywanie " + sql.substring(0, 40).concat(x) + ".", EventLog);
                            FileLogger.debug("Wykonywanie " + sql.substring(0, 40).concat(x) + ".");
                            rs2 = pstmt.executeQuery();
                        
                            // Sprawdzenie ilości zwróconych wierszy
                            if (rs2.last()) { //idziemy na ostani wiersz
                                rowcount2 = rs2.getRow(); //patrzymy, który to wiersz
                                rs2.beforeFirst(); //przesuwamy nie rs.first() bo ja użyjesz rs.next() to stracisz pierwszy element
                            }
                            WritingToEventLogArea.info("Zwrócona liczba wierszy: " + rowcount2 + ".", EventLog);  
                            FileLogger.debug("Zwrócona liczba wierszy: " + rowcount2 + ".");
                        
                            if (rowcount2 == 0) {
                                Report.append("\nDokumentu " + x + " brak w bazie lokalnej oddziału " + jednostka + " (" + SearchedDatabase + ").\n");
                                FileLogger.debug("Dokumentu " + x + " brak w bazie lokalnej oddziału " + jednostka + " (" + SearchedDatabase + ").");
                                WritingToSqlite.saveData("Dokumentu " + x + " brak w bazie lokalnej oddziału " + jednostka + " (" + SearchedDatabase + ").");
                            }
                            else {
                                // Wyciąganie danych ze zbioru rezultatów
                                while (rs2.next()) {
                                    // Wybieranie według nazw kolumn
                                    id_dok = rs2.getString("id_dok");
                                    id_jedn = rs2.getString("id_jedn");
                                    sym_dok = rs2.getString("sym_dok");
                                    dat_wystaw_dok = rs2.getString("dat_wystaw_dok");
                                    status = rs2.getString("status");
                                    semafor = rs2.getString("semafor");
                                    Report.append("\nDokument " + sym_dok + " znajduje się w tabeli dokumenty na bazie lokalnej oddziału " + id_jedn + " ("
                                        + SearchedDatabase + "). Ma nadany numer systemowy " + id_dok + ". Status dokumentu: " + status + ". Semafor: " + semafor + ". Data"
                                        + " wystawienia: " + dat_wystaw_dok + "\n");
                                    FileLogger.debug("Dokument " + sym_dok + " znajduje się w tabeli dokumenty na bazie lokalnej oddziału " + id_jedn + " ("
                                        + SearchedDatabase + "). Ma nadany numer systemowy " + id_dok + ". Status dokumentu: " + status + ". Semafor: " + semafor + ". Data"
                                        + " wystawienia: " + dat_wystaw_dok);
                                    WritingToSqlite.saveData("Dokument " + sym_dok + " znajduje się w tabeli dokumenty na bazie lokalnej oddziału " + id_jedn + " ("
                                        + SearchedDatabase + "). Ma nadany numer systemowy " + id_dok + ". Status dokumentu: " + status + ". Semafor: " + semafor + ". Data"
                                        + " wystawienia: " + dat_wystaw_dok);
                                }   
                            } 
                        }
                        //Aktualizuje procentową wartość ilości wyszukanych dokumentów na pasku postępu
                        jProgressBar1.setValue((100*(i+1)/linie.length));
                    }
                    jProgressBar1.setVisible(false);
                    TabbedPanel.setSelectedIndex(1);
                } catch (SQLException e) {
                    //e.printStackTrace();
                    WritingToEventLogArea.error("Wystąpił nieoczekiwany błąd.", EventLog);
                } finally {
                    if (rs1 != null) {
                        try {
                            rs1.close();
                        } catch (SQLException e) {WritingToEventLogArea.error("Wystąpił nieoczekiwany błąd.", EventLog);}
                    }
                    if (rs2 != null) {
                        try {
                            rs2.close();
                        } catch (SQLException e) {WritingToEventLogArea.error("Wystąpił nieoczekiwany błąd.", EventLog);}
                    }
                    if (pstmt != null) {
                        try {
                            pstmt.close();
                            WritingToEventLogArea.info("Rozłączono z bazą danych.", EventLog);
                            FileLogger.debug("Rozłączono z bazą danych.");
                        } catch (SQLException e) {WritingToEventLogArea.error("Wystąpił nieoczekiwany błąd.", EventLog);}
                    }
                    if (ActiveConnection != null) {
                        try {
                            ActiveConnection.close();
                        } catch (SQLException e) {WritingToEventLogArea.error("Wystąpił nieoczekiwany błąd.", EventLog);}
                    }
                }
            }   
        }         
    } 
    
    public Properties getProperties(String DatabaseAlias, ArrayList<Properties> XMLProperties) {
        Properties Result = null;
        
        if (XMLProperties.size() != 0) {
            for (int i = 0; i < XMLProperties.size(); i++) {
                if (XMLProperties.get(i).getProperty("db_alias").equals(DatabaseAlias)) {
                   Result = XMLProperties.get(i);
                }
            }
        }
         
	return Result;
    }
    
    public String checkErrors(String XML){
        String Result = "";
        String[] rows = XML.split(">");
        
        for (int x=0; x<rows.length; x++) {
            if (rows[x].indexOf("Err", 1) != -1) {
                if (rows[x].indexOf("Waga=\"0\"", 1) != -1) {
                    String temp = rows[x].substring(rows[x].indexOf("Tekst=", 1)+7).replace("\n", ". ");
                    temp = temp.substring(0, temp.indexOf("\"", 1));
                    Result = Result + temp + ".\n";
                }
                if (rows[x].indexOf("Waga=\"1\"", 1) != -1) {
                    String temp = rows[x].substring(rows[x].indexOf("Tekst=", 1)+7).replace("\n", ". ");
                    temp = temp.substring(0, temp.indexOf("\"", 1));
                    Result = Result + temp + ".\n";
                }
                if (rows[x].indexOf("Waga=\"2\"", 1) != -1) {
                    String temp = rows[x].substring(rows[x].indexOf("Tekst=", 1)+7).replace("\n", ". ");
                    temp = temp.substring(0, temp.indexOf("\"", 1));
                    Result = Result + temp + ".\n";
                }
                if (rows[x].indexOf("Waga=\"3\"", 1) != -1) {
                    String temp = rows[x].substring(rows[x].indexOf("Tekst=", 1)+7).replace("\n", ". ");
                    temp = temp.substring(0, temp.indexOf("\"", 1));
                    Result = Result + temp + ".\n";
                }
                if (rows[x].indexOf("Waga=\"4\"", 1) != -1) {
                    String temp = rows[x].substring(rows[x].indexOf("Tekst=", 1)+7).replace("\n", ". ");
                    temp = temp.substring(0, temp.indexOf("\"", 1));
                    Result = Result + temp + ".\n";
                }
                if (rows[x].indexOf("Waga=\"5\"", 1) != -1) {
                    String temp = rows[x].substring(rows[x].indexOf("Tekst=", 1)+7).replace("\n", ". ");
                    temp = temp.substring(0, temp.indexOf("\"", 1));
                    Result = Result + temp + ".\n";
                }
            }    
        }
        
        return Result;
    }
}
