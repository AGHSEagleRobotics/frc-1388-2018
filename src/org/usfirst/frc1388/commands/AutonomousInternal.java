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
import edu.wpi.first.wpilibj.command.WaitCommand;

import org.usfirst.frc1388.Position;
import org.usfirst.frc1388.Objective;
import org.usfirst.frc1388.Robot;
import org.usfirst.frc1388.RobotMap;
import org.usfirst.frc1388.UsbLogging;
import org.usfirst.frc1388.subsystems.*;
import org.usfirst.frc1388.subsystems.Elevator.ElevatorSetpoint;

/**
 *
 */
public class AutonomousInternal extends CommandGroup {

	private Position position;
	private Objective goal;
	private String gameData;
	private final double k_rightTurnAngle = 90;
	private final double k_leftTurnAngle = -90;
	private final double k_robotFrameLength = 28;
	private final double k_robotFrameWidth = 32;
	private final double k_robotBumper = 6;
	private final double k_robotLength = k_robotFrameLength + k_robotBumper;
	private final double k_blockSize = 13;
	private final double k_switchWall = 140;
	private final double k_autoLine = 120;
	private final double k_autoDistanceWall = k_switchWall - k_robotLength;
	private final double k_autoDistanceLine = k_autoLine - k_robotLength;
	private final double k_switchCenter = 168;
	private final double k_switchLength = 144;
	private final double k_autoDistanceSwitch = k_switchCenter - ((k_robotFrameLength + k_robotBumper)/2);
	private final double k_scaleCenter = 299.65 + (48/2); // 299.65 distance to edge of scale, 48 width of scale
	private final double k_autoDistanceScale = k_scaleCenter - ((k_robotFrameLength + k_robotBumper)/2);
	private final double k_exchangePlatformDepth = 36;
	private final double k_autoDistanceClearExchange = k_exchangePlatformDepth + 2 ; //2 is the margin of error
	private final double k_centerRightOffset = ((k_robotFrameWidth + k_robotBumper)/2) - 12;// 12 is the distance from center of field to the exchange platform
	private final double k_autoDistanceSwitchForBlock = (k_switchLength /2 ) - (k_blockSize); 
	private String switchSide;
	private String scaleSide;
	
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
		switchSide = gameData.substring(0, 1); // "L" L L 
		scaleSide = gameData.substring(1, 2); // L "L" L
		
		switch (goal) {
		case SCALE:
			runScale(position, scaleSide);
			break;

		case SWITCH:
			runSwitch(position, switchSide);
			break;
		default:
			runLine();
		}
	}

	public void runSwitch(Position position, String switchSide) {
		UsbLogging.printLog("Auto: runSwitch:  position=" + position + "  switchSide=" + switchSide);

		// Deploy arms
		addSequential( new DeployArms() );
		
		if( position.equals(Position.CENTER) ) {
			
			double w = k_autoDistanceClearExchange + 10;
			double xL = k_autoDistanceSwitchForBlock + k_centerRightOffset;
			double xR = k_autoDistanceSwitchForBlock - k_centerRightOffset;
			double y = k_autoDistanceWall - w;
			double z = -20;
			// switch Dims 12ft 9.5in wide, 4ft 8in deep, 1ft 6 3/4 in tall
			// scale Dims
			
			// Drive forward w
			addSequential( new AutonomousDrive(w));
			
			// Turn Same
			if(switchSide.equals("L")) addSequential( new AutonomousTurnTo(k_leftTurnAngle));
			else addSequential( new AutonomousTurnTo(k_rightTurnAngle));
			
			// Drive Forward x
			if(switchSide.equals("L")) addSequential( new AutonomousDrive(xL));
			else addSequential( new AutonomousDrive(xR));
			
			// Turn to 0
			addSequential( new AutonomousTurnTo(0));
			
			// P Elevator to switch
			addParallel( new AutonomousMoveElevator(ElevatorSetpoint.SWITCH));
			
			// P Drive Forward y
			addSequential( new AutonomousDrive(y));
			
			// Drop Box
			addSequential( new AutonomousRunIntake("out"));
			
			// Drive BackWards z
			addSequential( new AutonomousDrive(z));
			
			// Lower Elevator
			addSequential( new AutonomousMoveElevator(ElevatorSetpoint.BOTTOM));
			
		}//end if
		else { // position == R or L 

			double x = k_autoDistanceSwitch; 
			double y = 10.5; //distance to switch wall
			double z = -20; // distance to backup
			
			// Drive forward x
			addSequential( new AutonomousDrive(x));
			
			// Turn opposite
			if(switchSide.equals("L")) addSequential( new AutonomousTurnTo(k_rightTurnAngle));
			else addSequential( new AutonomousTurnTo(k_leftTurnAngle));
			
			// P Elevator to switch
			addParallel( new AutonomousMoveElevator(ElevatorSetpoint.SWITCH));
			
			// P Drive forward y
			addSequential( new AutonomousDrive(y));
			
			// drop box
			addSequential( new AutonomousRunIntake("out"));
			
			// Drive backwards z
			addSequential( new AutonomousDrive(z));
			
			// Lower Elevator
			addSequential( new AutonomousMoveElevator(ElevatorSetpoint.BOTTOM));
		}
	}

	public void runScale(Position position, String scaleSide) {
		UsbLogging.printLog("Auto: runScale:  position=" + position + "  scaleSide=" + scaleSide);
		double x = k_autoDistanceScale; 
		double y = -19;//how far to go back from under the scale not to hit it
		double y2 = 14; // 3 is to get the arms over the scale edge
		double z = -20;//to clear the scale after auton
		
		// Deploy arms
		addSequential( new DeployArms() );
		
		// Drive forward x
		addSequential( new AutonomousDrive(x));
		
		// Turn opposite
		if(scaleSide.equals("L")) addSequential( new AutonomousTurnTo(k_rightTurnAngle));
		else addSequential( new AutonomousTurnTo(k_leftTurnAngle));
		
		// P Drive backwards y
		addSequential( new AutonomousDrive(y));
		
		// P Elevator to scale
		addSequential( new AutonomousMoveElevator(ElevatorSetpoint.SCALE));
		
		// Drive Forward
		addSequential( new AutonomousDrive(y2)); 
		
		// drop box
		addSequential( new AutonomousRunIntake("out"));
		
		// Drive backwards z
		addSequential( new AutonomousDrive(z));
		
		// Lower Elevator
		addSequential( new AutonomousMoveElevator(ElevatorSetpoint.BOTTOM));
	}

	public void runLine() {
		UsbLogging.printLog("Auto: runLine");
		
		if(position == Position.CENTER) {
			runSwitch(position, switchSide);
		} else {
			//addSequential( new AutonomousDrive(k_autoDistanceWall));
			addSequential( new AutonomousDrive(50));
		}
			
//		addSequential( new AutonomousDrive(k_autoDistanceLine));
//		addSequential( new AutonomousTurnTo(180));
//		addSequential( new AutonomousDrive(k_autoDistanceLine-k_robotFrame));
//		addSequential( new AutonomousTurnTo(0));
//		addSequential( new AutonomousDrive(k_autoDistanceLine-k_robotFrame));
//		addSequential( new AutonomousTurnTo(-180));
//		addSequential( new AutonomousDrive(k_autoDistanceLine-k_robotFrame));
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
			UsbLogging.printLog( "Positon Left is equal to Left side" );
			return true;
		} 
		else if (position.equals(Position.RIGHT) && gameData.equals("R")) {
			UsbLogging.printLog( "Positon Right is equal to right side" );
			return true;
		}
		UsbLogging.printLog( "not equal RR or LL" );
		return false;
	}

	private void setGoal( Objective priority) {

		if( this.gameData.length() != 3 || this.position == null || priority == Objective.LINE ) {
			this.goal = Objective.LINE;
			UsbLogging.printLog( "Code not entered correctly" );
			return;
		}
	
		if( this.position == position.CENTER ) {
			this.goal = Objective.SWITCH;
			UsbLogging.printLog( "Positon is equal to Center objective Switch" );
			return;
		}
		
		String switchSide = gameData.substring(0, 1);
		String scaleSide = gameData.substring(1, 2);

		if( priority.equals(Objective.SWITCH) ) {
			UsbLogging.printLog( "Priority == Switch" );
			
			if( compare(this.position, switchSide) ) {
				this.goal = Objective.SWITCH;
				UsbLogging.printLog( "Objective now Switch" );
				return;
			}
			else if( compare(this.position, scaleSide) ) {
				this.goal = Objective.SCALE;
				UsbLogging.printLog( "Objective now Scale" );
				return;
			}
		}
		
		if( priority.equals(Objective.SCALE) ) {
			UsbLogging.printLog( "Priority == Scale" );

			if( compare(this.position, scaleSide) ) {
				this.goal = Objective.SCALE;
				UsbLogging.printLog( "Objective now Scale" );
				return;
			}
			else if( compare(this.position, switchSide) ) {
				UsbLogging.printLog( "Goal: " + goal );
				this.goal = Objective.SWITCH;
				UsbLogging.printLog( "Goal: " + goal );
				UsbLogging.printLog( "Objective now Switch" );
				return;
			} 
		}
		
		this.goal = Objective.LINE;
	}
}


