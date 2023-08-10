// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.teamspyder.frc2023;

import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import org.teamspyder.spyderlib.SpyderRobot;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private Command m_autonomousCommand;
  private Command m_testCommand;

  private RobotContainer m_robotContainer;

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    // Instantiate our RobotContainer.  This will perform all our button bindings, and put our
    // autonomous chooser on the dashboard.
    m_robotContainer = new RobotContainer();

    DataLogManager.start();
    DriverStation.startDataLog(DataLogManager.getLog(), true);

    lastAlliance = DriverStation.getAlliance();
    lastDisabled = DriverStation.isDisabled();

    CommandScheduler.getInstance().onCommandInitialize((Command command) -> {
      DataLogManager.log(command.getName() + " is initializing and requires: " + command.getRequirements().toString());
    });

    CommandScheduler.getInstance().onCommandInterrupt((Command command) -> {
      DataLogManager.log(command.getName() + " has been interrupted.");
    });

    CommandScheduler.getInstance().onCommandFinish((Command command) -> {
      DataLogManager.log(command.getName() + " is finishing on its own.");
    });

    addPeriodic(SpyderRobot.getNEOHealthMonitoringRunnable(), 1);

    SpyderRobot.burnSparkMaxFlashes();
  }

  private static Alliance lastAlliance = null;
  private static boolean lastDisabled = true;
  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // Runs the Scheduler.  This is responsible for polling buttons, adding newly-scheduled
    // commands, running already-scheduled commands, removing finished or interrupted commands,
    // and running subsystem periodic() methods.  This must be called from the robot's periodic
    // block in order for anything in the Command-based framework to work.
    CommandScheduler.getInstance().run();

    lastAlliance = DriverStation.getAlliance();
    lastDisabled = DriverStation.isDisabled();

    SpyderRobot.tick();
  }

  public static Alliance getAlliance() {
    return lastAlliance;
  }

  public static boolean getDisabled() {
    return lastDisabled;
  }

  private boolean wasDisabled = false;

  /** This function is called once each time the robot enters Disabled mode. */
  @Override
  public void disabledInit() {
    DataLogManager.log("DISABLED");
    wasDisabled = true;
    m_robotContainer.getOnDisableCommand().schedule();
  }

  @Override
  public void disabledPeriodic() {}

  /** This autonomous runs the autonomous command selected by your {@link RobotContainer} class. */
  @Override
  public void autonomousInit() {
    if (wasDisabled) {
      m_robotContainer.getOnEnableCommand().schedule();
      wasDisabled = false;
    }

    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
    DataLogManager.log("ENABLED AUTO");
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {
    if (wasDisabled) {
      m_robotContainer.getOnEnableCommand().schedule();
      wasDisabled = false;
    }

    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
    DataLogManager.log("ENABLED TELEOP");
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {

  }

  @Override
  public void testInit() {
    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();

    m_testCommand = m_robotContainer.getTestCommand();

    // schedule the autonomous command (example)
    if (m_testCommand != null) {
      m_testCommand.schedule();
    }
    DataLogManager.log("ENABLED SELF TEST");
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}
