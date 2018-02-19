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
import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc1388.Robot;
import org.usfirst.frc1388.subsystems.Elevator;
import org.usfirst.frc1388.subsystems.Elevator.ElevatorSetpoint;

/**
 *
 */


public class AutonomousMoveElevator extends Command {
	
 	private final double acceptableThreshold = 3; // in inches
 	
 	private ElevatorSetpoint currentLocation = ElevatorSetpoint.DONTKNOW;
	
	private double setPointDouble = 0;
	private ElevatorSetpoint setPointEnum = ElevatorSetpoint.DONTKNOW;
	private boolean moveToDistance = false;
	
	private final Elevator elevator = Robot.elevator;

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS
 
    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
    public AutonomousMoveElevator(double setPoint) {

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING

        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
    	this.setPointDouble = setPoint;
    	moveToDistance = true;
    }
    
    public AutonomousMoveElevator(String setPointName) {
      	moveToDistance = false;
    	
    	switch(setPointName) {
    	case "scale":
    		setPointEnum = ElevatorSetpoint.SCALE;
    		break;
    	case "switch":
    		setPointEnum = ElevatorSetpoint.SWITCH;
    		break;
    	case "bottom":
    		setPointEnum = ElevatorSetpoint.BOTTOM;
    		break;
    	default:
    		setPointEnum = ElevatorSetpoint.BOTTOM;
    		break;
    	}
    	
    	this.setPointDouble = setPointEnum.getDistance();
    }

    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
    	System.out.println("AutonomousMoveElevator init");
    }
    
    public void moveToSetpointDouble(double setpoint) {
    	double delta;
    	delta = elevator.distanceFromSetPointDouble(setpoint);
		currentLocation = ElevatorSetpoint.DONTKNOW;
    	
    	// stop the motor if elevator is within acceptable range of the setpoint
    	if( delta > 0 && delta < acceptableThreshold ) {
    		elevator.setMotor(0, false);
    	}
    	// move elevator up if elevator is below setpoint
    	else if( delta < 0 ) {
    		elevator.setMotor(1, false);
    	}
    	
    	// move elevator down if elevator is above setpoint
    	else if(delta > 0) {
    		elevator.setMotor(-1, false);
    	}
    	
    	// by default stop elevator
    	else {
    		elevator.setMotor(0, false);
    	}
    }

   public void moveToSetpointEnum(ElevatorSetpoint setpoint) {
    	double delta;
    	delta = elevator.distanceFromSetPoint(setpoint);
    	
    	// stop the motor if elevator is within acceptable range of the setpoint
    	if( delta > 0 && delta < acceptableThreshold ) {
    		elevator.setMotor(0, false);
    		currentLocation = setpoint;
    	}
    	// move elevator up if elevator is below setpoint
    	else if( delta < 0 ) {
    		elevator.setMotor(1, false);
    		currentLocation = ElevatorSetpoint.DONTKNOW;
    	}
    	
    	// move elevator down if elevator is above setpoint
    	else if(delta > 0) {
    		elevator.setMotor(-1, false);
    		currentLocation = ElevatorSetpoint.DONTKNOW;
    	}
    	
    	// by default stop elevator
    	else {
    		elevator.setMotor(0, false);
    		currentLocation = ElevatorSetpoint.DONTKNOW;
    	}
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {
    	
    	// initialize elevator - move down to bottom to reset encoder
    	if(elevator.isinitialized() == false) {
    		elevator.setMotor(-1, false) ; // TODO speed not actual, will be scaled based on power limit\
    		return;

    	// deploy the arms if not deployed 
    	} else if (elevator.armsDeployed() == false){
    		// deploy arms
    		moveToSetpointEnum(ElevatorSetpoint.DEPLOYARMS);
    		if( currentLocation == ElevatorSetpoint.DEPLOYARMS) {
    			elevator.setarmsDeployed(true);
    		}
    	
    	// move elevator to setpoint
    	} else { 
    		
    		// switch based on whether double or enum constructer was called
    		if (moveToDistance == true) { 
    			// call the setpoint based on distance
    			moveToSetpointDouble(setPointDouble);
    		} else {
    			moveToSetpointEnum(setPointEnum);
    		}
        	    		
    	}
    	


    	
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() {
        return true;
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    @Override
    protected void interrupted() {
    }
}
