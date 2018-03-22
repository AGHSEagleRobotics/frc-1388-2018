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

/**
 *
 */
public class AutonomousDrive extends Command {

	private static double k_p = .005; // p is proportional constant for PID loop
	private final double k_powerOffset = .195; // offsets the p value in the power calculation
	private final double k_minPwrCutoff = 0.2;	// stop motors if power is below this level
	private double error;
	private double power;
	private final double k_maxPower = .3;
	private static double threshold = 1.5; // error threshold for isFinished check (based on max dist over 20ms)
	
	private int stallCount = 0;
	private final int k_stallCountThreshold = 25; // stalls per robot tic / 20 milliseconds
	private final double k_minSpeedThreshold = 2.0; //units in per sec

	private double distance = 99999;

	private double time = 99999;

	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS

	// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS

	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
	public AutonomousDrive(double distance) {

		// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
		// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING

		// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING
		// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
		requires(Robot.driveTrain);

		// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES

		this.distance = distance;
	}

	public AutonomousDrive(double timeOrDistance, boolean isTime) {

		requires(Robot.driveTrain);

		if(isTime == true) {
			this.time = timeOrDistance;
		} else {
			this.distance = timeOrDistance;
		}

	}

	// Called just before this Command runs the first time
	@Override
	protected void initialize() {
		RobotMap.driveTrainmecanumDrive.setDeadband(0.0);

		UsbLogging.printLog(">>> " + this.getClass().getSimpleName() + " started");
		UsbLogging.printLog("Distance: " + distance + "  time: " + time);

		RobotMap.driveTrainleftEncoder.reset();
		RobotMap.driveTrainrightEncoder.reset();

		setTimeout(time);
		
		stallCount = 0;
	}

	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void execute() {

		//different error values dependent on which encoder(s) work
		
		//error = distance - ((RobotMap.driveTrainleftEncoder.getDistance() + RobotMap.driveTrainrightEncoder.getDistance())/2);
		error = distance - (RobotMap.driveTrainleftEncoder.getDistance());
		//error = distance - (RobotMap.driveTrainrightEncoder.getDistance());
		
		power = k_p * error + Math.copySign(k_powerOffset, error); // need be same sign

		power = Math.min(power,  k_maxPower); 
		power = Math.max(power, -k_maxPower);

		if(Math.abs(power) <= k_minPwrCutoff) {
			power = 0;
		}

		RobotMap.driveTrainmecanumDrive.driveCartesian(0, power, 0, 0);
		
		// TODO: Should be a way to select left/right/both encoders
		if(Math.abs(RobotMap.driveTrainleftEncoder.getRate()) < k_minSpeedThreshold ) {
			stallCount ++;
		}else {
			stallCount = 0;
		}

	}
	
	

	// Make this return true when this Command no longer needs to run execute()
	@Override
	protected boolean isFinished() {
		
		if( (Math.abs(this.error) < threshold) || isTimedOut() || (stallCount > k_stallCountThreshold)) {
			return true;
		}
		return false;
	}

	// Called once after isFinished returns true
	@Override
	protected void end() {
		RobotMap.driveTrainmecanumDrive.setDeadband(0.0);
		RobotMap.driveTrainmecanumDrive.driveCartesian(0, 0, 0, 0);
		UsbLogging.printLog("Error: " + error + " StallCount: " + stallCount);
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
