/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.okon.celina.documents;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.swing.JTextArea;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import java.util.HashMap;

/**
 *
 * @author okongrzegorz
 */
public class ReadingXMLParams {
    public EventLogArea WritingToEventLogArea;
    static Logger FileLogger = Logger.getLogger(ReadingXMLParams.class);
    private ArrayList<Properties> LoginInformationList = new ArrayList<Properties>();
    private HashMap<String, String> CodesInformationList = new HashMap<String, String>();
    Document XMLDoc;
    
    public ReadingXMLParams() {
        try {
            Properties props = new Properties();
            InputStream inStream = new FileInputStream("./settings/log4j.properties");
            props.load(inStream);
            PropertyConfigurator.configure(props);
        }
        catch (IOException io) {
        }
        finally {     
        };
    }
    
    public void parseXmlFile(){
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

	try {
            DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
            File XmlFile = new File("./settings/config.xml");
	    XMLDoc = dbBuilder.parse(XmlFile);
	}catch(Exception e) {
            e.printStackTrace();
	}
    }
    
    public void parseDocument(JTextArea EventLog){
        Properties LoginInformation = null;
        Element eElement = null;
        Node Child = null;
	Element Root = XMLDoc.getDocumentElement();
	NodeList Children = Root.getElementsByTagName("db_checker");
	
        WritingToEventLogArea = new EventLogArea();
        FileLogger.debug("Czytanie parametrów z pliku xml.");
        WritingToEventLogArea.debug("Czytanie liczby baz danych.", EventLog);
        
        if (Children != null && Children.getLength() > 0) {
            for (int i = 0; i < Children.getLength(); i++) {
                Child = Children.item(i);
                
                if (Child.getNodeType() == Node.ELEMENT_NODE) {
                    eElement = (Element) Child;   
                    getParams(eElement);
                } 
            }
	}  
        
        WritingToEventLogArea.debug("Liczba baz danych: " + Children.getLength() + ".", EventLog);
    } 
    
    private void getParams(Element eElement) {    
        String Db_alias = eElement.getElementsByTagName("db_alias").item(0).getTextContent();
        String Db_ip = eElement.getElementsByTagName("db_ip").item(0).getTextContent();
        String Db_port = eElement.getElementsByTagName("db_port").item(0).getTextContent();
        String Db_user = eElement.getElementsByTagName("db_user").item(0).getTextContent();
        String Db_pswd = eElement.getElementsByTagName("db_pswd").item(0).getTextContent();
        
        if (!Db_alias.equals("Centrala")) {
            String Codes = eElement.getElementsByTagName("codes").item(0).getTextContent();
            
            for (String SingleCode: Codes.split(",")){
                CodesInformationList.put(SingleCode, Db_alias);
            }
        }      

	Properties ChildInformations = new Properties();
        ChildInformations.put( "db_alias", Db_alias );
        ChildInformations.put( "db_ip", Db_ip );
        ChildInformations.put( "db_port", Db_port );
        ChildInformations.put( "db_user", Db_user );
        ChildInformations.put( "db_pswd", Db_pswd );
        
        LoginInformationList.add(ChildInformations);
    }
    
    public ArrayList<Properties> getParameters() {    
        return LoginInformationList;
    }
    
    public HashMap<String, String> getCodes() {    
        return CodesInformationList;
    }
}
