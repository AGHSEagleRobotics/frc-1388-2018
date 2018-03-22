// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc1388;

import org.usfirst.frc1388.lib.LIDARLiteV3;

// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.drive.MecanumDrive;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import com.mindsensors.CANSD540;
import edu.wpi.first.wpilibj.I2C;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    public static WPI_TalonSRX driveTrainleftFront;
    public static WPI_TalonSRX driveTrainleftRear;
    public static WPI_TalonSRX driveTrainrightFront;
    public static WPI_TalonSRX driveTrainrightRear;
    public static MecanumDrive driveTrainmecanumDrive;
    public static Encoder driveTrainleftEncoder;
    public static Encoder driveTrainrightEncoder;
    private static final double distancePerPulse = 0.0199995888;
    public static DigitalInput elevatorbottomElevatorSwitch;
    public static Encoder elevatorelevatorEncoder;
    public static WPI_VictorSPX elevatorelevatorMotor;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    public static CANSD540 leftIntakeMotor;
    public static CANSD540 rightIntakeMotor;
    
    // TODO Wire limit switches in parallel or separately?
    
    public static LIDARLiteV3 lidarSensor;

    @SuppressWarnings("deprecation")
    public static void init() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
        driveTrainleftFront = new WPI_TalonSRX(3);
        
        
        driveTrainleftRear = new WPI_TalonSRX(4);
        
        
        driveTrainrightFront = new WPI_TalonSRX(2);
        
        
        driveTrainrightRear = new WPI_TalonSRX(1);
        
        
        driveTrainmecanumDrive = new MecanumDrive(driveTrainleftFront, driveTrainleftRear,
              driveTrainrightFront, driveTrainrightRear);
        LiveWindow.addActuator("DriveTrain", "mecanumDrive", driveTrainmecanumDrive);
        driveTrainmecanumDrive.setSafetyEnabled(true);
        driveTrainmecanumDrive.setExpiration(0.1);
        driveTrainmecanumDrive.setMaxOutput(1.0);

        driveTrainleftEncoder = new Encoder(2, 3, true, EncodingType.k1X);
        LiveWindow.addSensor("DriveTrain", "leftEncoder", driveTrainleftEncoder);
        driveTrainleftEncoder.setDistancePerPulse(distancePerPulse);
        driveTrainleftEncoder.setPIDSourceType(PIDSourceType.kDisplacement);
        driveTrainrightEncoder = new Encoder(0, 1, false, EncodingType.k1X);
        LiveWindow.addSensor("DriveTrain", "rightEncoder", driveTrainrightEncoder);
        driveTrainrightEncoder.setDistancePerPulse(distancePerPulse);
        driveTrainrightEncoder.setPIDSourceType(PIDSourceType.kDisplacement);
        elevatorbottomElevatorSwitch = new DigitalInput(8);
        LiveWindow.addSensor("Elevator", "bottomElevatorSwitch", elevatorbottomElevatorSwitch);
        
        elevatorelevatorEncoder = new Encoder(5, 4, false, EncodingType.k4X);
        LiveWindow.addSensor("Elevator", "elevatorEncoder", elevatorelevatorEncoder);
        elevatorelevatorEncoder.setDistancePerPulse(0.0169270833);
        elevatorelevatorEncoder.setPIDSourceType(PIDSourceType.kDisplacement);
        elevatorelevatorMotor = new WPI_VictorSPX(12);
        
        

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
        
        leftIntakeMotor = new CANSD540(5);
        rightIntakeMotor = new CANSD540(6);
        
        /*
         * Constant in setDistancePerPulse represents movement in inches per encoder count
         * 
         * 15 Tooth Sprocket:
         * 0.079994260125 (25% increase from 12 tooth, * 1.25)
         * 
         * 12 Tooth Sprocket:
         * 0.0639954081
         * 0.0181818
         * 
         * TODO verify numbers empirically
         * 
         */
        
        lidarSensor = new LIDARLiteV3(I2C.Port.kOnboard);

        /*
         * Large deadband, but verified by driver for use
         * with Xbox Controllers
         * 
         */
        driveTrainmecanumDrive.setDeadband(0.3);
        
        driveTrainleftFront.setInverted(true);
        driveTrainleftRear.setInverted(true);
        driveTrainrightFront.setInverted(true);
        driveTrainrightRear.setInverted(true);
    }
}
