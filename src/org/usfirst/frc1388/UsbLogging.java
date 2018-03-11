package org.usfirst.frc1388;

import java.io.*;
import java.time.*;

public class UsbLogging {
	private static PrintStream m_logStream = null;
	private static FileOutputStream m_OutStream;
	private static LocalDate m_today = LocalDate.now();

	private static final String logPath = "/u/RobotLogs";
	private static final String logPrefix = "RobotLog_";
	private static final String logSuffix = "txt";
	private static final String className = UsbLogging.class.getSimpleName();

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
		String fName = "";
		// Make sure the log directory exists
		File fLogPath = new File(logPath);
		if (! fLogPath.isDirectory()) {
			System.out.println(className + ": Logging directory does not exist");
			m_logStream = null;
			return m_logStream;
		}

		// Find the file name and open it
		// Creating a file based on timestamp would be ideal; however, since roboRIO has no RTC,
		// there is no guarantee that a meaningful time has been set by the time we get here.
		for (int i = 0; i <= 999; i++) {
			// fNum represents a number of the format 001
			// fName represents the full file path, including file name
			String fNum = String.format("%03d", i);
			fName = logPath + "/" + logPrefix + fNum;
			if (! logSuffix.isEmpty()) {
				fName += "." + logSuffix;
			}

			// See if the file exists
			File fLogFile = new File(fName);
			if (! fLogFile.isFile()) {
				try {
					m_OutStream = new FileOutputStream(fLogFile);
					m_logStream = new PrintStream(m_OutStream);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					m_logStream = null;
					return m_logStream;
				}
				break;
			}
		}

		if (m_logStream == null) {
			System.out.println(className + ": Couldn't determine log file name");
		} else {
			System.out.println(className + ": Log file: " + fName);
			m_logStream.print(m_today + "\r\n");
		}

		return m_logStream;
	}

	/**
	 * Print a string to the system console, and to a log file if one has been opened.
	 * 
	 * @param   str   String to be printed
	 */

	public static void printLog(String str) {
		System.out.println(LocalTime.now() + "  " + str);
		// ToDo: Add timestamp to the string written to m_logStream
		if (m_logStream != null) {
			if (! m_today.equals(LocalDate.now())) {
				m_today = LocalDate.now();
				m_logStream.print(m_today + "\r\n");
			}
			m_logStream.print(LocalTime.now() + "  " + str + "\r\n");
			
			// flush the output stream, to reduce the chance of file corruption
			try {
				m_OutStream.getFD().sync();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
