/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.okon.celina.documents;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import javax.swing.JTextArea;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import static biz.okon.celina.documents.ReadingXMLParams.FileLogger;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Klasa ReadingXMLStatuses odpowiada za pobieranie opisów statusów dokumentów z pliku statuses.xml wyświetlanych następnie w raporcie.
 * 
 * @author okongrzegorz
 */
public class ReadingXMLStatuses {
    private EventLogArea WritingToEventLogArea;
    private HashMap<String, String> StatusesList = new HashMap<String, String>();
    private Document XMLDoc;
    
    public void parseXmlFile(){
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

	try {
            DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
            File XmlFile = new File("./settings/statuses.xml");
	    XMLDoc = dbBuilder.parse(XmlFile);
	}catch(Exception e) {
            e.printStackTrace();
	}
    }
    
    public void parseDocument(JTextArea EventLog){
	Element Root = XMLDoc.getDocumentElement();
	NodeList Children = Root.getElementsByTagName("doc_checker");
        Node Child = null;
        Element eElement = null;
	
        WritingToEventLogArea = new EventLogArea();
        WritingToEventLogArea.debug("Czytanie liczby statusów dokumentów.", EventLog);
        FileLogger.debug("Czytanie liczby statusów dokumentów.");
        
        if (Children != null && Children.getLength() > 0) {
            for (int i = 0; i < Children.getLength(); i++) {
                Child = Children.item(i);
                
                if (Child.getNodeType() == Node.ELEMENT_NODE) {
                    eElement = (Element) Child;   
                    setStatus(eElement);
                } 
            }
	}  
        
        WritingToEventLogArea.debug("Liczba statusów dokumentów: " + Children.getLength() + ".", EventLog);
        FileLogger.debug("Liczba statusów dokumentów: " + Children.getLength() + ".");
    } 
    
    public HashMap<String, String> getStatusesList() {    
        return StatusesList;
    } 
    
    private void setStatus(Element eElement) {    
        String DocumentAlias = eElement.getElementsByTagName("doc_alias").item(0).getTextContent();
        String StatusDescription = eElement.getElementsByTagName("doc_desc").item(0).getTextContent();    

        StatusDescription = correctDescription(StatusDescription);
        
        StatusesList.put(DocumentAlias, StatusDescription);
    }
    
    private String correctDescription (String StatDesc) {
        switch (StatDesc) {
            case "blad_formalny":  return "błąd formalny";
            case "blad_logiczny":  return "błąd logiczny";
            case "dla_prawnika":  return "dla prawnika";
            case "do_centrali":  return "do centrali";
            case "do_identyfikacji":  return "do identyfikacji";
            case "do_udostepnienia":  return "do udostępnienia";
            case "do_wyjasnienia":  return "do wyjaśnienia";
            case "do_zamkniecia":  return "do zamknięcia";
            case "inicjalny_dla_dokumentow_IST_przeslanych_webcelem":  return "inicjalny dla dokumentów IST przesłanych webcelem";
            case "nadany_recznie":  return "nadany ręcznie";
            case "nieprawidlowy":  return "nieprawidłowy";
            case "oczekiwanie_na_ponowna_weryfikacje_podpisu":  return "oczekiwanie ma ponowną weryfikację podpisu";
            case "oczekiwanie_na_przeslanie_do_jednostki":  return "oczekiwanie na przesłanie do jednostki";
            case "oczekiwanie_na_walidacje_formalna":  return "oczekiwanie na walidację formalną";
            case "po_awarii":  return "po awarii";
            case "po_kontroli":  return "po kontroli";
            case "oczekujacy":  return "oczekujący";
            case "po_weryfikacji":  return "po weryfikacji";
            case "potwierdzono_powiadomienie":  return "potwierdzono powiadomienie";
            case "przyjety":  return "przyjęty";
            case "udostepniony":  return "odostępniony";
            case "uniewazniony":  return "unieważniony";
            case "walidacja_formalna":  return "walidacja formalna";
            case "w_centrali":  return "w centrali";
            case "w_rejestracji":  return "w rejestracji";
            case "w_weryfikacji":  return "w weryfikacji";
            case "wystapil_blad_dla_deklaracji_intrastat":  return "wystąpił błąd dla deklaracji intrastat";
            case "zakonczono_obsluge":  return "zakończono obsługę";
            case "zamkniety":  return "zamknięty";
            case "zwroty_do_centrali":  return "zwroty do centrali";
            default: return StatDesc;
        }
    }   
}
