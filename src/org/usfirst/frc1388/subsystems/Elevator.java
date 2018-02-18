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

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.mindsensors.CANSD540;

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

	//enums
	private enum ElevatorSensorType{
		LIDAR, 
		ENCODER
	}
	
	public enum ElevatorSetpoint {
		SCALE(1.0), //TODO adjust values to match real
		SWITCH(0.5),
		DEPLOYARMS(0.25),
		BOTTOM(0.0),
		DONTKNOW(0.0);
		
		private double distance;
		
		private ElevatorSetpoint(double distance) {
			this.distance = distance;
		}
		
		public double getDistance() {
			return this.distance;
		}
	}


	//limits
	public final double k_maxHeight = 50; //inches, not tested
	public final double k_lowestHeight = 10; //inches, not tested
	public final double k_maxHeightMargin = 5; // margin for overshoot above max height

	//lidar
	private final double k_lidarOffset = 3; //inches, distance from base of lidar to floor, not tested
	private final double k_inPerCm = (1 / 2.54); //inches per centimeter, not sure what the number is...

	//elevator motor limits
	private final double k_maxPwrUp = .5; //max power when moving up, not tested
	private final double k_finalPwrUp = 0.15; //max power when elevator is at top, not tested
	private final double k_rampDistUp = 12; //distance from top when power will scale down, not tested

	private final double k_maxPwrDwn = -.2; //max power when moving down, not tested
	private final double k_finalPwrDwn = -0.1; //max power when elevator is at bottom, not tested
	private final double k_rampDistDwn = 20; //distance from bottom when power will scale down, not tested
	
	private boolean m_initialized = false;
	private boolean m_armsDeployed = false;


	public Elevator() {
		m_initialized = false;
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
		double limitedPwr = limitMotorPwr(currentPwr);
		elevatorMotor.set(limitedPwr);
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

	// Put methods for controlling this subsystem
	// here. Call these from Commands.

	// Put methods for controlling this subsystem
	// here. Call these from Commands.

	/**
	 * Determine if the elevator is at the bottom limit
	 * <p>
	 * The lower limit is a hard limit, implemented as a limit switch.
	 *  
	 * @return    Elevator is at the lower limit
	 */
	public boolean atBottomLimit() {
		return bottomElevatorSwitch.get();
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

		double height = getHeight();

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
			// get the height from the LIDAR
			//height = (getLidarMeasurement() * inPerCm) + lidarOffset;
			height = -1; // for now, indicate error

			// maxHeight plus offest = height relative to floor
			if (height < 0 || height > (k_maxHeight + k_maxHeightMargin)) {
				// something is wrong
				height = -1;
			}
			
			if (m_initialized == false) {
				height = -1;
				// print out if encoder not initialized
			}
			
			break;

		case ENCODER:
			height = elevatorEncoder.getDistance() + k_lowestHeight;

			// maxHeight plus offest = height relative to floor
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
	
	public double distanceFromSetPoint( ElevatorSetpoint setpoint) {
		double delta;
		delta = getHeight() - setpoint.getDistance();
		
		return delta;
	}

	public double distanceFromSetPointDouble(double setpoint) {
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
	public double setMotor(double pwr) {
		double limitedPwr = limitMotorPwr(pwr);

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
	public double limitMotorPwr(double pwr) {
		double pwrRequested = pwr; 
		
		// returns height relative to floor
		double height = getHeight();

		// top/bottom limits (limit switches or soft limits)
		if (atTopLimit()    && (pwr > 0)) pwr = 0;                          // Do not go up
		if (atBottomLimit() && (pwr < 0)) pwr = 0;                          // Do not go down

		// max power limit
		if (pwr > k_maxPwrUp) pwr = k_maxPwrUp;                         // Never exceed the max Up power
		if (pwr < k_maxPwrDwn) pwr = k_maxPwrDwn;                       // Never exceed the max Down power

		// Reduce the power as the elevator approaches top/bottom limits
		if (height > 0) {
			// Height of the elevator is known; apply ramped limits based on height
			double upLimit, dnLimit, distance, slope;

			// limit the power when approaching the top limit
			distance = k_maxHeight - height;                               // distance from the limit
			slope = (k_maxPwrUp - k_finalPwrUp) / k_rampDistUp;      // slope of the limit = y / x
			upLimit = k_finalPwrUp + (distance * slope);
			if (pwr > upLimit) pwr = upLimit;

			// limit the power when approaching the bottom limit
			distance = height - k_lowestHeight;                            // distance from the limit
			slope = (k_maxPwrDwn - k_finalPwrDwn) / k_rampDistDwn;      // slope of the limit = y / x
			dnLimit = k_finalPwrDwn + (distance * slope);
			if (pwr < dnLimit) pwr = dnLimit;
		}
		else {
			// height is unknown; apply "final" limits
			if (pwr > k_finalPwrUp) pwr = k_finalPwrUp;
			if (pwr < k_finalPwrDwn) pwr = k_finalPwrDwn;
		}

		// Log if the power limit has been limited:
		if (Math.abs(pwrRequested - pwr) > 0.1) {
			UsbLogging.printLog("Limited Power from "+ pwrRequested +" to "+ pwr);
		}

		return pwr;
	}
	
	/**
	 * @return m_initialized
	 */
	public boolean isinitialized() {
		return m_initialized;
	}

	/**
	 * @return void
	 */
	public void setarmsDeployed(boolean b) {
		m_armsDeployed = b;
	}

	/**
	 * @return m_armsdeployed
	 */
	public boolean armsDeployed() {
		return m_armsDeployed;
	}

}

