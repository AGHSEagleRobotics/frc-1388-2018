package org.usfirst.frc1388;

import java.io.*;
import java.util.*;

public class ReaderOfTxt {

	public ArrayList<String> readFile(String fileName) 
			throws FileNotFoundException, IOException  {
        // The name of the file to open. TODO: For testing purposes, remove later.
        //fileName = "/media/sda1/AutonScript.txt";

        ArrayList<String> fileData = new ArrayList<>();
        
        // This will reference one line at a time
        String line = null;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            System.out.println("Auton Script. fileName = '" + fileName + "'");
            System.out.println("|=========BEGIN READING FILE=========|");
            
            // Reading the file into an ArrayList line by line
            // When null is returned, it signifies the end of the file
            while((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                fileData.add(line);
            }   

            System.out.println("|==========END READING FILE==========|");
            
            // Always close files.
            bufferedReader.close();         
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
            
            // Re-throw exception so that caller can handle file errors
            throw ex;
        }
        catch(IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
            
            // Or we could just do this: 
            // ex.printStackTrace();
            
            // Re-throw exception so that caller can handle file errors
            throw ex;
        }
        
        // Finished reading file, return the data 
        return fileData;
	}
	
} // end class ReaderOfTxt