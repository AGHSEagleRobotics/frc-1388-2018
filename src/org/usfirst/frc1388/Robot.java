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

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc1388.commands.*;
import org.usfirst.frc1388.subsystems.*;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.RobotController;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in 
 * the project.
 */
public class Robot extends TimedRobot {

    Command autonomousCommand; // always set to AutonomousLauncher
    
    private SendableChooser<String> autonomousChooser = new SendableChooser<>();
    
    private SendableChooser<Objective> objectiveChooser = new SendableChooser<>();
    private SendableChooser<Position> positionChooser = new SendableChooser<>();    

    public static DriverStation driverStation;
    public static String gameData;
    
	public static Objective autonObjective;
	public static Position fieldPosition;
	public static String autonToRun;
    
	// TODO: Review these static fields after AutonomousDB branch is merged into master

    public static OI oi;
    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    public static DriveTrain driveTrain;
    public static Climber climber;
    public static Elevator elevator;
    public static Intake intake;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS

	public static ADIS16448_IMU gyro; // Should this be moved to RobotMap?
	
	private boolean lastUserButton = false;
	private boolean gyroReset = false;


    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    @Override
    public void robotInit() {
        
        // Open log file
        UsbLogging.openLog();
        
		RobotMap.init();

        // print software version
        UsbLogging.printLog("Git version: " + BuildInfo.GIT_VERSION + " (branch: " + BuildInfo.GIT_BRANCH + BuildInfo.GIT_STATUS + ")");
        UsbLogging.printLog("Built: " + BuildInfo.BUILD_DATE + "  " + BuildInfo.BUILD_TIME);

        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
        driveTrain = new DriveTrain();
        climber = new Climber();
        elevator = new Elevator();
        intake = new Intake();

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
        // OI must be constructed after subsystems. If the OI creates Commands
        //(which it very likely will), subsystems are not guaranteed to be
        // constructed yet. Thus, their requires() statements may grab null
        // pointers. Bad news. Don't move it.
        oi = new OI();

        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=AUTONOMOUS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=AUTONOMOUSS

        gyro = new ADIS16448_IMU();
        
		// reset the gyro rate to zero
        gyro.calibrate();

		// camera, TODO add check for Camera to see if camera is on robot, otherwise do not stop capture 
		CameraServer.getInstance().startAutomaticCapture("Front Camera", 0);
		
		//elevator encoder
		//SmartDashboard.getData(elevator.getHeight());
		
		autonomousChooser.addObject("SCRIPT", "script");
		autonomousChooser.addDefault("INTERNAL", "internal");
		
		objectiveChooser.addObject("SCALE", Objective.SCALE);
		objectiveChooser.addObject("SWITCH", Objective.SWITCH);
		objectiveChooser.addDefault("LINE", Objective.LINE); //TODO test if addDefault() functions similar to addObject()
		
		positionChooser.addObject("LEFT", Position.LEFT);
		positionChooser.addDefault("CENTER", Position.CENTER);
		positionChooser.addObject("RIGHT", Position.RIGHT); //TODO test addDefault()
		
		SmartDashboard.putData("Autonomous Select", autonomousChooser);
		SmartDashboard.putData("Objective", objectiveChooser);
		SmartDashboard.putData("Position", positionChooser);
		
		SmartDashboard.setPersistent("Autonomous Select");
		SmartDashboard.setPersistent("Objective");
		SmartDashboard.setPersistent("Position");

    }
    /**
     * This function is called when the disabled button is hit.
     * You can use it to reset subsystems before shutting down.
     */
    @Override
    public void disabledInit(){

		UsbLogging.printLog("########  Robot disabled");
		
		RobotMap.lidarSensor.stopMeasuring();

    }

    /**
     * This function is called periodically in any robot mode.
     */
    @Override
    public void robotPeriodic() {
    	
    	if(RobotController.getUserButton()) {
    		if(lastUserButton == false) {
        		gyro.reset();
        		gyroReset = true;
        		UsbLogging.printLog("USER button pushed, gyro reset"); 
    		}
    	}
    	
    	lastUserButton = RobotController.getUserButton();
    }
    
    @Override
    public void disabledPeriodic() {
    	Scheduler.getInstance().run();
    }

    @Override
    public void autonomousInit() {

		UsbLogging.printLog("########  Autonomous enabled");

		gyro.reset();
		
		/*
		if(gyroReset == false) {
			gyro.reset();
			UsbLogging.printLog("USER button not pushed after startup, gyro reset automatically");
		}
		*/
		
		RobotMap.lidarSensor.startMeasuring();

    	// reset the gyro to zero at the start of Auto. We do not want to do this at the start of Tele because we won't know what position
    	// the robot will be at the end of Auto

    	autonObjective = objectiveChooser.getSelected(); // what you want to go for, Scale, Switch, Line
    	fieldPosition = positionChooser.getSelected(); // L C R
    	
    	gameData = DriverStation.getInstance().getGameSpecificMessage(); // 
    	
        autonToRun = autonomousChooser.getSelected(); // selector between script or hard code 
        autonomousCommand = new AutonomousLauncher(autonToRun);
    	
    	if (driverStation == null)
    		driverStation = DriverStation.getInstance();

    	// Get match info from FMS
    	if (driverStation.isFMSAttached()) {
    		String fmsInfo = "FMS info:";
    		fmsInfo += "  " + driverStation.getEventName();
    		fmsInfo += " " + driverStation.getMatchType();
    		fmsInfo += " match " + driverStation.getMatchNumber();
    		if (driverStation.getReplayNumber() > 0) {
    			fmsInfo += " replay " + driverStation.getReplayNumber();
    		}
    		fmsInfo += ";  " + driverStation.getAlliance() + " alliance";
    		fmsInfo += ",  Driver Station " + driverStation.getLocation();
    		UsbLogging.printLog(fmsInfo);
    	} else {
    		UsbLogging.printLog("FMS not connected");
    	}

    	// get autonomous parameters from DriverStation and SmartDashboard
    	
        
        if (autonomousCommand != null) autonomousCommand.start();
    }

    /**
     * This function is called periodically during autonomous
     */
    @Override
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
    }

    @Override
    public void teleopInit() {

		UsbLogging.printLog("########  Teleop enabled");
		
		RobotMap.lidarSensor.startMeasuring();

        // This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to
        // continue until interrupted by another command, remove
        // this line or comment it out.
        if (autonomousCommand != null) autonomousCommand.cancel();

    }

    /**
     * This function is called periodically during operator control
     */
    @Override
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
    }

}
