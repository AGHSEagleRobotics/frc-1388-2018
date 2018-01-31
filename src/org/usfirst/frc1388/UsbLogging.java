package org.usfirst.frc1388;

import java.io.*;

public class UsbLogging {
   private static PrintStream m_logStream = null;
   
   private static final String logPath = "/u/RobotLogs";
   private static final String logPrefix = "RobotLog_";
   private static final String logSuffix = "txt";
   private static final String className = Class.class.getSimpleName();

   /**
    * Open a log file, if possible, to be used by logging statements.
    * <p>
    * This method will return a reference to the opened stream, although it
    * isn't generally needed outside this class.  However, null will be returned
    * if no file was opened, which may be useful.
    * 
    * @return     A stream to the file opened, or null if no file was opened.
    */
   public static PrintStream openLog() {
      // Make sure the log directory exists
      File fLogPath = new File(logPath);
      if (! fLogPath.isDirectory()) {
         System.out.println(className + ":  Logging directory does not exist");
         m_logStream = null;
         return m_logStream;
      }
      
      // Find the file name and open it
      for (int i = 0; i <= 99999; i++) {
         // fNum represents a number of the format 00001
         // fName represents the full file path, including file name
         String fNum = String.format("%05d", i);
         String fName = logPath + "/" + logPrefix + fNum;
         if (! logSuffix.isEmpty()) {
            fName += logSuffix;
         }

         // See if the file exists
         File fLogFile = new File(fName);
         if (fLogFile.isFile()) {
            try {
               m_logStream = new PrintStream(fLogFile);
            } catch (FileNotFoundException e) {
               e.printStackTrace();
               m_logStream = null;
               return m_logStream;
            }
            break;
         }
      }
      
      if (m_logStream == null) {
         System.out.println(className + ":  Couldn't determine log file name");
      } else {
         System.out.println(className + ":  Log file: " + fLogFile);
      }
      
      return m_logStream;
   }

   /**
    * Print a string to the system console, and to a log file if one has been opened.
    * 
    * @param   str   String to be printed
    */

   public static void printLog(String str) {
      System.out.println(str);
      if (m_logStream != null) m_logStream.println(str);
   }
}
