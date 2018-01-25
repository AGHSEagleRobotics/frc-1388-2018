package org.usfirst.frc1388;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.drive.Vector2d;
import edu.wpi.first.wpilibj.drive.RobotDriveBase.MotorType;
import edu.wpi.first.wpilibj.hal.HAL;
import edu.wpi.first.wpilibj.hal.PWMJNI;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tInstances;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tResourceType;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

public class MecanumCANTalon {
	  private static int instances = 0;

	  private WPI_TalonSRX m_frontLeftMotor;
	  private WPI_TalonSRX m_rearLeftMotor;
	  private WPI_TalonSRX m_frontRightMotor;
	  private WPI_TalonSRX m_rearRightMotor;

	  private boolean m_reported = false;
	  
	  public static final double kDefaultDeadband = 0.02;
	  public static final double kDefaultMaxOutput = 1.0;
	  
	  protected boolean m_isInverted; 

	  protected double m_deadband = kDefaultDeadband;
	  protected double m_maxOutput = kDefaultMaxOutput;

	  /**
	   * Construct a MecanumDrive.
	   *
	   * <p>If a motor needs to be inverted, do so before passing it in.
	   */
	  public MecanumCANTalon(WPI_TalonSRX frontLeftMotor, WPI_TalonSRX rearLeftMotor,
			  WPI_TalonSRX frontRightMotor, WPI_TalonSRX rearRightMotor) {
	    m_frontLeftMotor = frontLeftMotor;
	    m_rearLeftMotor = rearLeftMotor;
	    m_frontRightMotor = frontRightMotor;
	    m_rearRightMotor = rearRightMotor;
	    /*addChild(m_frontLeftMotor);
	    addChild(m_rearLeftMotor);
	    addChild(m_frontRightMotor);
	    addChild(m_rearRightMotor);*/
	    instances++;
	    //setName("MecanumDrive", instances);
	  }

	  /**
	   * Drive method for Mecanum platform.
	   *
	   * <p>Angles are measured clockwise from the positive X axis. The robot's speed is independent
	   * from its angle or rotation rate.
	   *
	   * @param ySpeed    The robot's speed along the Y axis [-1.0..1.0]. Right is positive.
	   * @param xSpeed    The robot's speed along the X axis [-1.0..1.0]. Forward is positive.
	   * @param zRotation The robot's rotation rate around the Z axis [-1.0..1.0]. Clockwise is
	   *                  positive.
	   */
	  @SuppressWarnings("ParameterName")
	  public void driveCartesian(double ySpeed, double xSpeed, double zRotation) {
	    driveCartesian(ySpeed, xSpeed, zRotation, 0.0);
	  }

	  protected double limit(double value) {
		  if(value > 1.0) {
			  return 1.0;
		  }
		  else if( value < -1.0) {
			  return -1.0;
		  }
		  return value;
	  }//limiting motor values
	  
	  protected double applyDeadband(double value, double deadband) {
		  if (Math.abs(value) > deadband) {
		      if (value > 0.0) {
		        return (value - deadband) / (1.0 - deadband);
		      } else {
		        return (value + deadband) / (1.0 - deadband);
		      }
		    } else {
		      return 0.0;
		    }//applied deadband 
	  }
	  
	  protected void normalize(double[] wheelSpeeds) {
		    double maxMagnitude = Math.abs(wheelSpeeds[0]);
		    for (int i = 1; i < wheelSpeeds.length; i++) {
		      double temp = Math.abs(wheelSpeeds[i]);
		      if (maxMagnitude < temp) {
		        maxMagnitude = temp;
		      }
		    }
		    if (maxMagnitude > 1.0) {
		      for (int i = 0; i < wheelSpeeds.length; i++) {
		        wheelSpeeds[i] = wheelSpeeds[i] / maxMagnitude;
		      }
		    }
		  }//normalizes wheels?
	 

	  /**
	   * Drive method for Mecanum platform.
	   *
	   * <p>Angles are measured clockwise from the positive X axis. The robot's speed is independent
	   * from its angle or rotation rate.
	   *
	   * @param ySpeed    The robot's speed along the Y axis [-1.0..1.0]. Right is positive.
	   * @param xSpeed    The robot's speed along the X axis [-1.0..1.0]. Forward is positive.
	   * @param zRotation The robot's rotation rate around the Z axis [-1.0..1.0]. Clockwise is
	   *                  positive.
	   * @param gyroAngle The current angle reading from the gyro in degrees around the Z axis. Use
	   *                  this to implement field-oriented controls.
	   */
	  @SuppressWarnings("ParameterName")
	  public void driveCartesian(double ySpeed, double xSpeed, double zRotation, double gyroAngle) {
	    if (!m_reported) {
	      HAL.report(tResourceType.kResourceType_RobotDrive, 4,
	                 tInstances.kRobotDrive_MecanumCartesian);
	      m_reported = true;
	    }

	    ySpeed = limit(ySpeed);
	    ySpeed = applyDeadband(ySpeed, m_deadband);

	    xSpeed = limit(xSpeed);
	    xSpeed = applyDeadband(xSpeed, m_deadband);

	    // Compensate for gyro angle.
	    Vector2d input = new Vector2d(ySpeed, xSpeed);
	    input.rotate(-gyroAngle);

	    double[] wheelSpeeds = new double[4];
	    wheelSpeeds[MotorType.kFrontLeft.value] = input.x + input.y + zRotation;
	    wheelSpeeds[MotorType.kFrontRight.value] = input.x - input.y + zRotation;
	    wheelSpeeds[MotorType.kRearLeft.value] = -input.x + input.y + zRotation;
	    wheelSpeeds[MotorType.kRearRight.value] = -input.x - input.y + zRotation;

	    normalize(wheelSpeeds);

	    m_frontLeftMotor.set(wheelSpeeds[MotorType.kFrontLeft.value] * m_maxOutput);
	    m_frontRightMotor.set(wheelSpeeds[MotorType.kFrontRight.value] * m_maxOutput);
	    m_rearLeftMotor.set(wheelSpeeds[MotorType.kRearLeft.value] * m_maxOutput);
	    m_rearRightMotor.set(wheelSpeeds[MotorType.kRearRight.value] * m_maxOutput);

	    //m_safetyHelper.feed();  
	  }

	  /**
	   * Drive method for Mecanum platform.
	   *
	   * <p>Angles are measured counter-clockwise from straight ahead. The speed at which the robot
	   * drives (translation) is independent from its angle or rotation rate.
	   *
	   * @param magnitude The robot's speed at a given angle [-1.0..1.0]. Forward is positive.
	   * @param angle     The angle around the Z axis at which the robot drives in degrees [-180..180].
	   * @param zRotation The robot's rotation rate around the Z axis [-1.0..1.0]. Clockwise is
	   *                  positive.
	   */
	  @SuppressWarnings("ParameterName")
	  public void drivePolar(double magnitude, double angle, double zRotation) {
	    if (!m_reported) {
	      HAL.report(tResourceType.kResourceType_RobotDrive, 4, tInstances.kRobotDrive_MecanumPolar);
	      m_reported = true;
	    }

	    driveCartesian(magnitude * Math.sin(angle * (Math.PI / 180.0)),
	                   magnitude * Math.cos(angle * (Math.PI / 180.0)), zRotation, 0.0);
	  }

	  
	  public void stopMotor() {
		
	    m_frontLeftMotor.stopMotor();
	    m_frontRightMotor.stopMotor();
	    m_rearLeftMotor.stopMotor();
	    m_rearRightMotor.stopMotor();
	    //m_safetyHelper.feed();
	  }

	  /*@Override
	  public String getDescription() {
	    return "MecanumDrive";
	  }*/

	  /*@Override
	  public void initSendable(SendableBuilder builder) {
	    builder.setSmartDashboardType("MecanumDrive");
	    builder.addDoubleProperty("Front Left Motor Speed", m_frontLeftMotor::get,
	        m_frontLeftMotor::set);
	    builder.addDoubleProperty("Front Right Motor Speed", m_frontRightMotor::get,
	        m_frontRightMotor::set);
	    builder.addDoubleProperty("Rear Left Motor Speed", m_rearLeftMotor::get,
	        m_rearLeftMotor::set);
	    builder.addDoubleProperty("Rear Right Motor Speed", m_rearRightMotor::get,
	        m_rearRightMotor::set);
	  }	*/

}
