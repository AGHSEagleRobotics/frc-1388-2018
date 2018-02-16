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
import org.usfirst.frc1388.commands.*;
import edu.wpi.first.wpilibj.command.Subsystem;
// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS

// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS


/**
 *
 */
public class Elevator extends Subsystem {

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    
    public final DigitalInput bottomLimit = RobotMap.elevatorBottomLimit;

    // Enums
    private enum ElevatorSensorType {
       LIDAR,
       ENCODER
    }

    // CONSTANTS
    
    public final double topLimitHeight = 47;       // inches (THIS VALUE IS JUST A PLACEHOLDER)
    public final double bottomLimitHeight = 10;    // inches (THIS VALUE IS JUST A PLACEHOLDER)
    
    // LiDAR
    private final double lidarOffset = 3;           // distance in inches from the base of the LiDAR to the floor
    private final double inchPerCm = (1 / 2.54);    // inches per centimeter
    
    // Elevator motor power limits
    private final double motorUpMaxPwr = 0.4;      // max power limit when moving up
    private final double motorUpFinalPwr = 0.15;   // power limit when elevator is at the top limit
    private final double motorUpRampDist = 12;     // Distance from top limit at which the motor power will start to ramp down

    private final double motorDnMaxPwr = -0.2;     // max power limit when moving down
    private final double motorDnFinalPwr = -0.1;   // power limit when elevator is at the bottom limit
    private final double motorDnRampDist = 20;     // Distance from bottom limit at which the motor power will start to ramp down
    
    
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
        
        // Check elevator motor power limit
        // Note:  This will not strictly override a command which is actively setting the power outside the limit,
        // as the command and this code will be alternately setting different power values.
        double currentPwr = elevatorMotor.get();
        double limitedPwr = limitMotorPwr(currentPwr);
        elevatorMotor.set(limitedPwr);
        
        // Check if the power limit has been violated:
        if (fabs(currentPwr - limitedPwr) > 0.1) {
           UsbLogging.printLog("Warning: Elevator motor power limit exceeded!");
        }

        // Zero the encoder if the elevator is at the bottom limit switch
        if (atBottomLimit()) {
           encoder.reset();
        }
    }

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    
    /**
     * Determine if the elevator is at the bottom limit
     * <p>
     * The lower limit is a hard limit, implemented as a limit switch.
     *  
     * @return    Elevator is at the lower limit
     */
    public bool atBottomLimit() {
       return bottomLimit.get();
    }
    
    /**
     * Determine if the elevator is at the top limit
     * <p>
     * The top limit is a soft limit, determined by elevator height.
     *  
     * @return    Elevator is at the upper limit
     */
    public bool atTopLimit() {
        bool atLimit = true;     // default is the safest value
        
        double height = getHeight();
        
        if ((height < topLimitHeight) && (height >= 0)) {
           atLimit = false;
           
        return atLimit;
    }
     

    /**
     * Return the elevator height
     * <p>
     * If the height cannot be determined, a negative value will be returned
     *  
     * @return    Height in inches, or negative if height cannot be determined
     */
    public double getHeight() {
       // Which sensor to use?
       final ElevatorSensorType heightSensorMethod = ENCODER;
       
       double height = -1;     // default indicates an error
       
       switch (heightSensorMethod) {
          case LIDAR:
            // get the height from the LIDAR
            height = getLidarMeasurement();
            
            if ((height < 0) || (height > (topLimitHeight + 10)) {
               // something is wrong
               height = -1;
            }
            break;
            
          case ENCODER:
            height = encoder.getDistance();
            break;
            
          default:
            // unknown sensor type
            height = -1;
            break;
       }
       
       return height;
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
     * Desired motor power is limited based on max allowable power, physical limits, and proximity to physical limits.
     *  
     * @param     pwr   Desired motor power
     * @return          Limited motor power
     */
    public double limitMotorPwr(double pwr) {
       double height = getHeight();
       
       // top/bottom limits (limit switches or soft limits)
       if (atTopLimit()    && (pwr > 0)) pwr = 0;                          // Do not go up
       if (atBottomLimit() && (pwr < 0)) pwr = 0;                          // Do not go down
       
       // max power limit
       if (pwr > motorUpMaxPwr) pwr = motorUpMaxPwr;                       // Never exceed the max Up power
       if (pwr < motorDnMaxPwr) pwr = motorDnMaxPwr;                       // Never exceed the max Down power
       
       // Reduce the power as the elevator approaches top/bottom limits
       if (height > 0) {
         // Height of the elevator is known; apply ramped limits based on height
         double upLimit, dnLimit, distance, slope;
          
         // limit the power when approaching the top limit
         distance = topLimitHeight - height;                               // distance from the limit
         slope = (motorUpMaxPwr - motorUpFinalPwr) / motorUpRampDist;      // slope of the limit = y / x
         upLimit = motorUpFinalPwr + (distance * slope);
         if (pwr > upLimit) pwr = upLimit;

         // limit the power when approaching the bottom limit
         distance = height - bottomLimitHeight;                            // distance from the limit
         slope = (motorDnMaxPwr - motorDnFinalPwr) / motorDnRampDist;      // slope of the limit = y / x
         dnLimit = motorDnFinalPwr + (distance * slope);
         if (pwr < dnLimit) pwr = dnLimit;
       }
       else {
         // height is unknown; apply "final" limits
         if (pwr > motorUpFinalPwr) pwr = motorUpFinalPwr;
         if (pwr < motorDnFinalPwr) pwr = motorDnFinalPwr;
       }
       
       return pwr;
    }
}
