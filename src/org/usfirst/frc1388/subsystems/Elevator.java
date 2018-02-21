// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc1388.subsystems;

import org.usfirst.frc1388.RobotMap;
import org.usfirst.frc1388.UsbLogging;
import org.usfirst.frc1388.commands.*;
import org.usfirst.frc1388.lib.LIDARLiteV3;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;

// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS


/**
 *
 */
public class Elevator extends Subsystem {

	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    private final DigitalInput bottomElevatorSwitch = RobotMap.elevatorbottomElevatorSwitch;
    private final Encoder elevatorEncoder = RobotMap.elevatorelevatorEncoder;
    private final WPI_VictorSPX elevatorMotor = RobotMap.elevatorelevatorMotor;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    
    private final LIDARLiteV3 elevatorLidar = RobotMap.lidarSensor;

	//enums
	private enum ElevatorSensorType{
		LIDAR, 
		ENCODER
	}

	public enum ElevatorSetpoint {
		SCALE(80), //TODO adjust values to match real
		SWITCH(40),
		DEPLOYARMS(0.25),
		BOTTOM(3),
		DONTKNOW(0.0);

		private double distance;

		private ElevatorSetpoint(double distance) {
			this.distance = distance;
		}

		public double getDistance() {
			return this.distance;
		}
	}


	/*
	 * Height variables refer to distance from lowest point of elevator travel (zero) in inches
	 */
	//TODO limits
	public final double k_maxHeight = 80; // maximum height limit
	public final double k_lowestHeight = 0; // lowest height limit
	public final double k_maxHeightMargin = 3; // margin for overshoot above max height

	//TODO lidar
	private final double k_lidarOffset = -3; // distance from bottom of lidar to lowest point of travel on elevator, should be negative
	private final double k_inPerCm = (1 / 2.54);

	//TODO elevator motor limits
	private final double k_maxPwrUp = 1.0; //max power when moving up, tested
	private final double k_finalPwrUp = 0.5; //max power when elevator is at top, not tested
	private final double k_rampDistUp = 5; //distance from top when power will scale down, not tested

	private final double k_maxPwrDwn = -1.0; //max power when moving down, tested
	private final double k_finalPwrDwn = -0.4; //max power when elevator is at bottom, not tested
	private final double k_rampDistDwn = 20; //distance from bottom when power will scale down, not tested
	
	private final double k_slopeUp = (k_maxPwrUp - k_finalPwrUp) / k_rampDistUp; // slope of the limit = y / x = power / distance
	private final double k_slopeDwn = (k_maxPwrDwn - k_finalPwrDwn) / k_rampDistDwn; // slope of the limit = y / x = power / distance

	private boolean m_initialized = false; 

	public Elevator() {
		
		// Sets elevator motor to break when not receiving input
		elevatorMotor.setNeutralMode(NeutralMode.Brake);
	}

	@Override
	public void initDefaultCommand() {
		// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

        setDefaultCommand(new MoveElevator());

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

		// Set the default command for a subsystem here.
		// setDefaultCommand(new MySpecialCommand());
	}

	@Override
	public void periodic() {
		// Put code here to be run every loop

		// ensure the elevator motor is always set to the limit in case some code 
		// or external influence did not call setMotor() which limits the power

		double currentPwr = elevatorMotor.get();
		double limitedPwr = limitMotorPwr(currentPwr, false);
		
		//elevatorMotor.set(limitedPwr); TODO Need reworking to pass in override
		
		// Check if the power limit has been violated by not calling setMotor():
		if (Math.abs(currentPwr - limitedPwr) > 0.1) {
			UsbLogging.printLog("Warning: Elevator motor power limit exceeded!");
		}

		// Zero the encoder if the elevator is at the bottom limit switch and set the elevator initialized flag
		if (atBottomLimit()) {
			// set the elevator encoder to zero so that we can use it to measure elevator height for set points 
			// and so that we can ensure that the elevator height does not exceed the limits. 
			elevatorEncoder.reset();
			m_initialized = true;
		}

	}

	/**
	 * Determine if the elevator is at the bottom limit
	 * <p>
	 * The lower limit is a hard limit, implemented as a limit switch.
	 *  
	 * @return    Elevator is at the lower limit
	 */
	public boolean atBottomLimit() {
		// Elevator switch returns true when switch is not pressed because normally open, invert
		// to represent whether or not switch is pressed
		return !bottomElevatorSwitch.get();
	}

	/**
	 * Determine if the elevator is at the top limit
	 * <p>
	 * The top limit is a soft limit, determined by elevator height.
	 *  
	 * @return    Elevator is at the upper limit
	 */
	public boolean atTopLimit() {
		boolean atLimit = true;     // default is the safest value

		double height = getHeight(); // will return negative if height not found or encoder not zeroed

		if ((height < k_maxHeight) && (height >= 0)) {
			atLimit = false;
		}
		
		return atLimit;

	}


	/**
	 * Return the elevator height
	 * <p>
	 * If the height cannot be determined, a negative value will be returned
	 *  
	 * @return    Height in inches relative to ground, or negative if height cannot be determined
	 */
	public double getHeight() {
		// Which sensor to use?
		final ElevatorSensorType heightSensorMethod = ElevatorSensorType.ENCODER;

		double height = -1;     // default indicates an error

		switch (heightSensorMethod) {
		case LIDAR:
			// height plus offset = height relative to floor
			height = (elevatorLidar.getDistance() * k_inPerCm) + k_lidarOffset;

			if (height < 0 || height > (k_maxHeight + k_maxHeightMargin)) {
				// something is wrong
				height = -1;
			}

			break;

		case ENCODER:
			
			// height is encoder distance + distance of bottom of elevator to floor
			height = elevatorEncoder.getDistance() + k_lowestHeight;
//			double lidarHeight = (elevatorLidar.getDistance() * k_inPerCm) + k_lidarOffset;
			
			final double acceptableErrorMargin = 1.0;
			
//			if(Math.abs(lidarHeight - height) > acceptableErrorMargin)
//				System.out.println("LIDAR and Encoder not in agreement within specified margin of error");

			// maxHeight plus offset = height relative to floor
			if (height < 0 || height > (k_maxHeight + k_maxHeightMargin)) {
				// something is wrong
				height = -1;
			}

			if (m_initialized == false) {
				height = -1;
				// print out if encoder not initialized
			}

			break;

		default:
			// unknown sensor type
			height = -1;
			break;
		}

		return height;
	}

	public double distanceAboveSetPoint(ElevatorSetpoint setpoint) {
		return distanceAboveSetPoint(setpoint.getDistance());
	}

	public double distanceAboveSetPoint(double setpoint) {
		double delta;
		delta = getHeight() - setpoint;

		return delta;
	}


	/**
	 * Set the elevator motor power, after enforcing power limits
	 * <p>
	 * To ensure that the elevator is not overdriven, this method should be used to set the motor power,
	 * and the motor's power should NOT be set directly using the motor's set() method.
	 *  
	 * @param     pwr   Desired motor power
	 * @return          Actual motor power that was set
	 */
	public double setMotor(double pwr, boolean override) {
		double limitedPwr = limitMotorPwr(pwr, override);

		elevatorMotor.set(limitedPwr);

		return limitedPwr;
	}


	/**
	 * Check elevator motor power limits
	 * <p>
	 * Desired motor power is to be limited based on max allowable power, physical limits, and proximity to physical limits.
	 * Convert desired power value to limited power value based on constraints
	 *  
	 * @param     pwr   Desired motor power value
	 * @return          Limited motor power value
	 */
	public double limitMotorPwr(double pwr, boolean override) {
		double pwrRequested = pwr; 

		double height = getHeight();

		// max power limit
		if (pwr > k_maxPwrUp) pwr = k_maxPwrUp;                         // Never exceed the max Up power
		if (pwr < k_maxPwrDwn) pwr = k_maxPwrDwn;                       // Never exceed the max Down power

		if(override == false ) {

			// top/bottom limits (limit switches or soft limits)
			if (atTopLimit()    && (pwr > 0)) pwr = 0;                          // Do not go up
			if (atBottomLimit() && (pwr < 0)) pwr = 0;                          // Do not go down

			// Reduce the power as the elevator approaches top/bottom limits
			if (isHeightValid()) {
				// Height of the elevator is known; apply ramped limits based on height
				double upLimit, dnLimit, distance;

				// limit the power when approaching the top limit
				distance = k_maxHeight - height;                               // distance from the limit
				upLimit = k_finalPwrUp + (distance * k_slopeUp);				   // calculating upLimit, could much greater than maxPwrUp
				pwr = Math.min(pwr, upLimit);								   // use appropriate value within range
				
				// limit the power when approaching the bottom limit
				distance = height - k_lowestHeight;                            // distance from the limit
				dnLimit = k_finalPwrDwn + (distance * k_slopeDwn);				   // calculating dnLimit, could much less than maxPwrDwn
				pwr = Math.max(pwr, dnLimit);								   // use appropriate value within range
			}
			else {
				// height is unknown; apply "final" limits
				if (pwr > k_finalPwrUp) pwr = k_finalPwrUp;
				if (pwr < k_finalPwrDwn) pwr = k_finalPwrDwn;
			}

		}

		return pwr;

	}
	
	public boolean isHeightValid() {
		return (getHeight() > 0);
	}

	/**
	 * @return m_initialized
	 */
	public boolean isinitialized() {
		return m_initialized;
	}

}

