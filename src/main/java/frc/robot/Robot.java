// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the
 * name of this class or
 * the package after creating this project, you must also update the
 * build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String driveMiddleAuto = "Default";
  private static final String scoreMiddleAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  XboxController driver_controller = new XboxController(0);
  Spark flipper = new Spark(2);

  // Encoder flipperEncoder = new Encoder(2, 3);

  /**
   * This function is run when the robot is first started up and should be used
   * for any
   * initialization code.
   */
  private DigitalOutput LEDs = new DigitalOutput(0);
  
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Drive Middle", driveMiddleAuto);
    m_chooser.addOption("Score + Drive Middle", scoreMiddleAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    LEDs.set(true);
    System.out.println("TURNED ON PORT 0, LEDS");
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items
   * like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different
   * autonomous modes using the dashboard. The sendable chooser code works with
   * the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the
   * chooser code and
   * uncomment the getString line to get the auto name from the text box below the
   * Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional comparisons to the
   * switch structure
   * below with additional strings. If using the SendableChooser make sure to add
   * them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = SmartDashboard.getString("Auto Selector", driveMiddleAuto);
    System.out.println("Auto selected: " + m_autoSelected);
    AutoStarted = System.currentTimeMillis();
  }

  /** This function is called periodically during autonomous. */
  Long AutoStarted = 0L;
  @Override
  public void autonomousPeriodic() {
    m_autoSelected = SmartDashboard.getString("Auto Selector", driveMiddleAuto);
    Long timeElapsed = System.currentTimeMillis() - AutoStarted;

    // Didnt detect changes from smart dashboard???
    // Turn on flipper for 0.7s 
    if(timeElapsed < 700) {
      flipper.set(-1 / 4.0);
    } else {
      flipper.set(0);
    }
    // Drive forward after flipper flipped
    if(timeElapsed > 1000) {
      // Drive forward for 1s after flipper
      if(timeElapsed < 1700) {
        Drive.control(0.8, 1, 1);
      }
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
  }

  /** This function is called periodically during operator control. */
  double flipperRotation = 0.0;
  double goingDownSeconds = 0.0;
  double goingUpSeconds = 0.0;

  // Periodic called every 20ms
  // 1000ms / 20ms = 50 calls per second
  @Override
  public void teleopPeriodic() {
    double x = driver_controller.getLeftX();
    double rightTrigger = driver_controller.getRightTriggerAxis();
    double leftTrigger = driver_controller.getLeftTriggerAxis();

    boolean leftBumper = driver_controller.getLeftBumper();
    boolean rightBumper = driver_controller.getRightBumper();

    if(rightBumper && goingUpSeconds != -1) {
      goingUpSeconds += 0.02;
      goingDownSeconds = 0;

      if(goingUpSeconds > 0.8) {
        goingUpSeconds = -1; // -1 meaning stop all until direction switch
        System.out.println("REACHED UP LIMIT, STOPPING");
        flipper.set(0);
      } else {
        flipper.set(-1 / 4.0);
      }
    } else if(leftBumper && goingDownSeconds != -1) {
      goingDownSeconds += 0.02;
      goingUpSeconds = 0;

      if(goingDownSeconds > 1) {
        goingDownSeconds = -1; // -1 meaning stop all until direction switch
        System.out.println("REACHED DOWN LIMIT, STOPPING");
        flipper.set(0);
      } else {
        flipper.set(1 / 8.0);
      }
    }
    
    if(!leftBumper && !rightBumper) {
      flipper.set(0);
      goingDownSeconds = 0.0;
      goingUpSeconds = 0.0;
    }

    // Just holding down a trigger with no turn
    if (x == 0) {
      if (leftTrigger > 0) {
        Drive.control(-1, -1, leftTrigger);
      } else if (rightTrigger > 0) {
        Drive.control(1, 1, rightTrigger);
      }
    }
    // Driving forward + Turning Right
    if (x > 0 && rightTrigger > 0) {
      Drive.control(1, 0.5, rightTrigger);
    }
    // Driving forward + Turning Left
    if (x < 0 && rightTrigger > 0) {
      Drive.control(0.5, 1, rightTrigger);
    }
    // Driving backward + Turning Right
    if (x > 0 && leftTrigger > 0) {
      Drive.control(-1, -0.5, leftTrigger);
    }
    // Driving backward + Turning Left
    if (x < 0 && leftTrigger > 0) {
      Drive.control(-0.5, -1, leftTrigger);
    }

    // Turning in place
    if (leftTrigger == 0 && rightTrigger == 0) {
      // x goes negative since its on axis, thus motors speed stay the same but the X
      // changes on direction
      Drive.control(1, -1, x);
    }
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {
  }

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {
  }

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {
  }

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {
  }

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {
  }
}
