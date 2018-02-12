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
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc1388.Robot;
import org.usfirst.frc1388.RobotMap;

/**
 *
 */
public class MoveElevator extends Command {

	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS

	// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS

	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
	public MoveElevator() {

		// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
		// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING

		// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING
		// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
		requires(Robot.elevator);

		// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
	}

	// Called just before this Command runs the first time
	@Override
	protected void initialize() {
	}

	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void execute() {

		double leftTrigger = Robot.oi.getOpController().getTriggerAxis(Hand.kLeft);
		System.out.println("Left Trigger:" + leftTrigger);

		double rightTrigger = Robot.oi.getOpController().getTriggerAxis(Hand.kRight);
		System.out.println("Right Trigger:" + rightTrigger);

		double elevatorSpeed;


		if ( rightTrigger > 0 && leftTrigger > 0)  { // If both triggers pressed, elevatorSpeed = 0
			
			elevatorSpeed = 0;
			//Research default value of trigger
			
		} else if ( rightTrigger > 0.1) { // Right Trigger raises elevator
			
			// 0 to 0.1 is deadzone
			elevatorSpeed = 1;
			
		} else if ( leftTrigger > 0.1) { // Left Trigger lowers elevator
			
			// 0 to 0.1 is deadzone
			elevatorSpeed = -1;
			
		} else { // If no triggers pressed, elevatorSpeed = 0
			
			elevatorSpeed = 0;
			
		}

		RobotMap.elevatorMotor.set(elevatorSpeed);    	
	}

	// Make this return true when this Command no longer needs to run execute()
	@Override
	protected boolean isFinished() {
		return false;
	}

	// Called once after isFinished returns true
	@Override
	protected void end() {
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	@Override
	protected void interrupted() {
		end();
	}
}
