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

import org.usfirst.frc1388.Position;
import org.usfirst.frc1388.Objective;
import org.usfirst.frc1388.Robot;
import org.usfirst.frc1388.RobotMap;
import org.usfirst.frc1388.subsystems.*;

/**
 *
 */
public class AutonomousInternal extends CommandGroup {

	private Position position;
	private Objective goal;
	private String gameData;
	private final double k_robotFrame = 28;
	private final double k_robotBumber = 6;
	private final double k_robotLength = k_robotFrame + k_robotBumber;
	private final double k_switchWall = 140;
	private final double k_autoLine = 120;
	private final double k_autoDistanceWall = k_switchWall - k_robotLength;
	private final double k_autoDistanceLine = k_autoLine - k_robotLength;

	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=PARAMETERS
	public AutonomousInternal() {

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

		// END AUTOGENERATEE, SOURCE=ROBOTBUILDER ID=COMMAND_DECLARATIONS

		gameData = Robot.gameData; // 3-char string from FMS / Driver Station / ex. "LRL"
		position = Robot.fieldPosition; // Enum equal to value set in positionSelector SendableChooser, set in autonInit
		
		Objective priority = Robot.autonObjective; // Enum equal to value set in autonSelector SendableChooser, set in autonInit
		setGoal(priority);

		//AutonShake command(s)? add here if necessary
		
		switch (goal) {
		case SCALE:
			String scaleSide = gameData.substring(1, 2);
			runScale(position, scaleSide);
			break;

		case SWITCH:
			String switchSide = gameData.substring(0, 1);
			runSwitch(position, switchSide);
			break;

		default:
			runLine();
		}
	}

	public void runSwitch(Position position, String switchSide) {

		if( position.equals(Position.CENTER) ) {
			if(switchSide == "R") {
				System.out.println("CENTER POSITION, SWITCH, SW RIGHT");
				//drive forward
				//turn right
				//drive forward
				//turn left
				//drive forward
				//raise elevator
				//drop cube
			}
			else {
				System.out.println("CENTER POSITION, SWITCH, SW LEFT");
				//drive forward
				//turn left
				//drive forward
				//turn right
				//drive forward
				//drop cube
			}
		}
		//drive forward
		//turn opposite
		//drive forward
		//raise elevator
		//drop cube
		
	}

	public void runScale(Position position, String scaleSide) {
		//drive forward
		//turn opposite
		//drive forward
		//raise elevator
		//drop cube
		
	}

	public void runLine() {
		addSequential( new AutonomousDrive(k_autoDistanceWall));
	}

	/**
	 * 
	 * @param position Position to compare with gameData item
	 * @param gameData gameData substring to compare with Position
	 * 
	 * @return True if position and gameData are on the same side, false if otherwise
	 */
	private boolean compare(Position position, String gameData) {
		if(position.equals(Position.LEFT) && gameData.equals("L")) {
			return true;
		} 
		else if (position.equals(Position.RIGHT) && gameData.equals("R")) {
			return true;
		}
		
		return false;
	}

	private void setGoal( Objective priority) {

		if( this.gameData.length() != 3 || this.position == null || priority == Objective.LINE ) {
			this.goal = Objective.LINE;
			return;
		}
	
		if( this.position == position.CENTER ) {
			this.goal = Objective.SWITCH;
			return;
		}
		
		String switchSide = gameData.substring(0, 1);
		String scaleSide = gameData.substring(1, 2);

		if( priority.equals(Objective.SWITCH) ) {
			
			if( compare(this.position, switchSide) ) {
				this.goal = Objective.SWITCH;
				return;
			}
			else if( compare(this.position, scaleSide) ) {
				this.goal = Objective.SCALE;
				return;
			}
		}
		
		if( priority.equals(Objective.SCALE) ) {

			if( compare(this.position, scaleSide) ) {
				this.goal = Objective.SCALE;
				return;
			}
			else if( compare(this.position, switchSide) ) {
				this.goal = Objective.SWITCH;
				return;
			} 
		}
		
		this.goal = Objective.LINE;
	}
}


