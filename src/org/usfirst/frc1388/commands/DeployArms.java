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
import org.usfirst.frc1388.RobotMap;
import org.usfirst.frc1388.UsbLogging;
import org.usfirst.frc1388.subsystems.Elevator;
import org.usfirst.frc1388.subsystems.Elevator.ElevatorSetpoint;

/**
 *
 */
public class DeployArms extends Command {
	
	private final double k_acceptableThreshold = 3; // in inches

	private double setPoint = ElevatorSetpoint.DEPLOYARMS.getDistance();

	private final Elevator elevator = Robot.elevator;
	
	private final double k_deployArmsPwr = 0.32;
	


    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS
 
    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS
	
	

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
    public DeployArms() {

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
    	UsbLogging.printLog(">>> " + this.getClass().getSimpleName() + " started");
    }
    
   

    // Called repeatedly when this Command is scheduled to run
    
    @Override
    protected void execute() {
    	
		double delta;
		delta = elevator.distanceAboveSetPoint(setPoint);
	
		// move elevator up if elevator is below setpoint
		if( delta < 0 ) {
			elevator.setMotor(k_deployArmsPwr, true);
		}
	
		// move elevator down if elevator is above setpoint
		else if(delta > 0) {
			elevator.setMotor(k_deployArmsPwr, true);
		}
    	
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() {
    	double delta = elevator.distanceAboveSetPoint(this.setPoint);
		return ( delta >= 0 && delta < k_acceptableThreshold );
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
    	elevator.setMotor(0, false);
    	UsbLogging.printLog("<<< " + this.getClass().getSimpleName() + " ended");
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    @Override
    protected void interrupted() {
    	UsbLogging.printLog("<<< " + this.getClass().getSimpleName() + " interrupted");
    	end();
    }
}