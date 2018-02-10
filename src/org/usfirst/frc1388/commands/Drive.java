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
public class Drive extends Command {

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS
 
    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
    public Drive() {

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING

        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
        requires(Robot.driveTrain);

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
    }

    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
    	
    	// set deadband. Xbox Controller seems to need at least 0.1
    	RobotMap.driveTrainmecanumDrive.setDeadband(0.2);
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {
    	
    	// mecanum ySpeed is the X of left stick 
    	double mecanum_ySpeed = Robot.oi.getDriveController().getX(Hand.kLeft);
    	
    	// mecanum xSpeed is the inverse of Y of the left stick
    	double mecanum_xSpeed = - Robot.oi.getDriveController().getY(Hand.kLeft);
    	
    	// mecanum zRotation is the X of the right stick
    	double mecanum_zRotation = Robot.oi.getDriveController().getX(Hand.kRight);
    	
    	// mechanum gyroAngle for menanum needs to be the inverse of the gyro's Z angle 
    	double mecanum_gyroAngle = - Robot.gyro.getAngleZ();
    	
    	// console debugging
    	//System.out.println("mecanum_ySpeed (left stick X): " + mecanum_ySpeed + "\tmecanum_xSpeed (inverse left stick Y): " + mecanum_xSpeed + "\t mecanum_zRotation (right Stick X): " + mecanum_zRotation);
    	//System.out.println("mecanum_gyroAngle (inverse Gyro Z): " + mecanum_gyroAngle);
    	
    	// mecanum drive cartesian using Field orientation (gyro-based)
    	RobotMap.driveTrainmecanumDrive.driveCartesian(mecanum_ySpeed, mecanum_xSpeed, mecanum_zRotation, 0);
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
