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

// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.mindsensors.CANSD540;

import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.drive.MecanumDrive;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

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

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    public static CANSD540 leftForkMotor;
    public static CANSD540 rightForkMotor;

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

        driveTrainleftEncoder = new Encoder(3, 4, false, EncodingType.k4X);
        LiveWindow.addSensor("DriveTrain", "leftEncoder", driveTrainleftEncoder);
        driveTrainleftEncoder.setDistancePerPulse(0.0639954081);
        driveTrainleftEncoder.setPIDSourceType(PIDSourceType.kRate);
        driveTrainrightEncoder = new Encoder(1, 2, false, EncodingType.k4X);
        LiveWindow.addSensor("DriveTrain", "rightEncoder", driveTrainrightEncoder);
        driveTrainrightEncoder.setDistancePerPulse(0.0639954081);
        driveTrainrightEncoder.setPIDSourceType(PIDSourceType.kRate);

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
        
        leftForkMotor = new CANSD540(5);
        rightForkMotor = new CANSD540(6);
        
        driveTrainmecanumDrive.setDeadband(0.3);
        
        driveTrainleftFront.setInverted(true);
        driveTrainleftRear.setInverted(true);
        driveTrainrightFront.setInverted(true);
        driveTrainrightRear.setInverted(true);
    }
}
