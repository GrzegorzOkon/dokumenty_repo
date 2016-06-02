/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.okon.celina.documents;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTextArea;

/**
 *
 * @author okongrzegorz
 */

public class EventLogArea {
    
    private static String getCurrentTimeStamp() {
        SimpleDateFormat CustomizedDateFormat = new SimpleDateFormat("EEE MMM dd yyyy, HH:mm:ss.SSS");
        Date CurrentTime = new Date(); //Time zone + zzzz
        String CustomizedDate = CustomizedDateFormat.format(CurrentTime);

        return CustomizedDate;
    } 

  /** Metoda loguje w dzienniku zdarzeń wpisy z flagą error

     * Jądro metody area_log

     * @param text Opis zdarzenia

     * @param logArea pole tekstowe dla logowania zdarzeń aplikacji

     * @since 2.0

   */

  public void error(String text, JTextArea logArea){

      area_log(text, -1, logArea);

  }

  /** Metoda loguje w dzienniku zdarzeń wpisy z flagą info

     * Jądro metody area_log

     * @param text Opis zdarzenia

     * @param logArea pole tekstowe dla logowania zdarzeń aplikacji

     * @since 2.0

   */

  public void info(String text, JTextArea logArea){

      area_log(text, 0, logArea);

  }

  /** Metoda loguje w dzienniku zdarzeń wpisy z flagą trace

     * Jądro metody area_log

     * @param text Opis zdarzenia

     * @param logArea pole tekstowe dla logowania zdarzeń aplikacji

     * @since 2.0

   */

  public void trace(String text, JTextArea logArea){

      area_log(text, 1, logArea);

  }

  /** Metoda loguje w dzienniku zdarzeń wpisy z flagą warn

     * Jądro metody area_log

     * @param text Opis zdarzenia

     * @param logArea pole tekstowe dla logowania zdarzeń aplikacji

     * @since 2.0

   */

  public void warn(String text, JTextArea logArea){

      area_log(text, 2, logArea);

  }

  /** Metoda loguje w dzienniku zdarzeń wpisy z flagą debug

    * Jądro metody area_log

     * @param text Opis zdarzenia

     * @param logArea pole tekstowe dla logowania zdarzeń aplikacji

     * @since 2.0

   */

  public void debug(String text, JTextArea logArea){

      area_log(text, 3, logArea);

  }

 

  /** Metoda loguje w dzienniku zdarzeń wpisy z flagą w zależności od parametru wejściowego flaga

     * @param text Opis zdarzenia

     * @param flaga priorytet zdarzenia, -1 (error), 0 (info), 1 (trace), 2 (warn), 3 (debug).

     * @param logArea pole tekstowe dla logowania zdarzeń aplikacji

     * @since 2.0

  */

  private  void area_log(String text, int flaga, JTextArea logArea) {

               

        if (flaga == 1){

                logArea.append(getCurrentTimeStamp() + "   TRACE " + text + System.getProperty("line.separator"));

        }

        if (flaga == 0){

                logArea.append(getCurrentTimeStamp() + "   INFO  " + text + System.getProperty("line.separator"));

        }

        if (flaga == -1){

                logArea.append(getCurrentTimeStamp() + "   ERROR " + text + System.getProperty("line.separator"));

        }

        if (flaga == 2){

                logArea.append(getCurrentTimeStamp() + "   WARN  " + text + System.getProperty("line.separator"));

        }

        if (flaga == 3){

                logArea.append(getCurrentTimeStamp() + "   DEBUG " + text + System.getProperty("line.separator"));

        }

        logArea.setCaretPosition(logArea.getDocument().getLength());

                              

   }
}
