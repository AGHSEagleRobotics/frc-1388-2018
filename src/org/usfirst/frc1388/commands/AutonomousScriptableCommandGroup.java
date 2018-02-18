// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc1388.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.usfirst.frc1388.ReaderOfTxt;
import org.usfirst.frc1388.subsystems.*;

/**
 *
 */
public class AutonomousScriptableCommandGroup extends CommandGroup {

	public boolean scriptExists = false;
	
	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=PARAMETERS
    public AutonomousScriptableCommandGroup() {

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=PARAMETERS
		// Add Commands here:
		// e.g. addSequential(new Command1());
		//      addSequential(new Command2());
		// these will run in order.

		// To run multiple commands at the same time,
		// use addParallel()
		// e.g. addParallel(new Command1());
		//      addSequential(new Command2());
		// Command1 and Command2 will run in parallel.

		// A command group will require all of the subsystems that each member
		// would require.
		// e.g. if Command1 requires chassis, and Command2 requires arm,
		// a CommandGroup containing them would require both the chassis and the
		// arm.
		// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=COMMAND_DECLARATIONS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=COMMAND_DECLARATIONS
		
		ArrayList<String> toScript = new ArrayList<>();
		//toScript.add("drived 100.5");
		String delims = "[ ]+";
		String[] tokens;
		String command;
		String param;
		double tempDouble;
		int tempInt;
		
		ReaderOfTxt reader = new ReaderOfTxt();

		try {
			toScript = reader.readFile("/media/sda1/AutonScript.txt");
			scriptExists = true;
			
		} catch (Exception e) {
			// readFile can throw a FileNotFoundException or an IOException if there is a problem reading the script file
			// set a flag if thats the case so that the default auton code is run since ther is no script
			scriptExists = false;
			
			// Debug output, via auto-generated catch block
			e.printStackTrace();
		} 
		

		for(String s: toScript) {
			tokens = s.split(delims);    			
			command = tokens[0].toLowerCase();
			param = tokens[1].toLowerCase();

			switch(command) {
			case "drived":
				try {
					tempDouble = Double.parseDouble(param);
				} catch (Exception e) {
					tempDouble = 0;
				}
				addSequential(new AutonomousDrive(tempDouble));
				System.out.println("drived");
				break;
			case "drivet": 
				try {
					tempInt = Integer.parseInt(param);
				} catch (Exception e) {
					tempInt = 0;
				}
				addSequential(new AutonomousDrive(tempInt));
				System.out.println("drivet");
				break;
			case "turna": 
				try {
					tempDouble = Double.parseDouble(param);
				} catch (Exception e) {
					tempDouble = 0;
				}
				addSequential(new AutonomousTurn(tempDouble));
				System.out.println("turna");
				break;
			case "turnt": 
				try {
					tempInt = Integer.parseInt(param);
				} catch (Exception e) {
					tempInt = 0;
				}
				addSequential(new AutonomousTurn(tempInt));
				System.out.println("turnt");
				break;
			case "ele": 
				addSequential(new AutonomousMoveElevator(param));
				System.out.println("ele");
				break;
			case "fork": 
				addSequential(new AutonomousMoveFork(param));
				System.out.println("fork");
				break;
			default:
				break;
			}
		}


	} 
}
