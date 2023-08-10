// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.teamspyder.frc2023;

import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import org.teamspyder.frc2023.Autonomous.AutoPath;
import org.teamspyder.frc2023.FieldPositions.SnapPosition;
import org.teamspyder.frc2023.commands.swerve.DriveCommand;
import org.teamspyder.frc2023.commands.swerve.MoveHoodCommand;
import org.teamspyder.frc2023.commands.swerve.ShootCommand;
import org.teamspyder.frc2023.subsystems.DrivebaseSubsystem;
import org.teamspyder.frc2023.subsystems.HoodSubsystem;
import org.teamspyder.frc2023.subsystems.ShooterSubsystem;

import java.util.HashMap;

public class RobotContainer {
    private final DrivebaseSubsystem drivebaseSubsystem;
    private final ShooterSubsystem shooterSubsystem;
    private final HoodSubsystem hoodSubsystem;

    private static final Profile profile = Profile.getProfile();

    private final SendableChooser<AutoPath> pathChooser = new SendableChooser<>();
    private final SendableChooser<SelfTest> selfTestChooser = new SendableChooser<>();

    public RobotContainer() {
        while (DriverStation.getAlliance() == Alliance.Invalid) {
            DriverStation.refreshData();
        }

        DataLogManager.logNetworkTables(true);

        SnapPosition.updatePoses(Robot.getAlliance());

        drivebaseSubsystem = new DrivebaseSubsystem();
        shooterSubsystem = new ShooterSubsystem();
        hoodSubsystem = new HoodSubsystem();

        //PathPlannerServer.startServer(5800);

        pushTuningConstants();
        configureButtonBindings();
        populateSelfTestChooser();

        generateAuto(pathChooser.getSelected());
    }

    public void configureButtonBindings() {
        drivebaseSubsystem.setDefaultCommand(new DriveCommand(drivebaseSubsystem));

        profile.ONE_BUTTON.getJoystickButton().whileTrue(new ShootCommand(shooterSubsystem, 2000));
        profile.TWO_BUTTON.getJoystickButton().whileTrue(new ShootCommand(shooterSubsystem, 3000));
        profile.THREE_BUTTON.getJoystickButton().whileTrue(new MoveHoodCommand(hoodSubsystem, 0));
    }

    public void generateAuto(AutoPath path) {
        // Map to assign path markers from PP to commands
        HashMap<String, Command> eventMap = new HashMap<>();

    }

    private Command autoCommand = null;
    public Command getAutonomousCommand() {
        if (autoCommand == null) {
            generateAuto(pathChooser.getSelected());
        }

        return autoCommand;
    }

    public Command getTestCommand() {
        return Commands.none();
    }

    public Command getOnEnableCommand() {
        return Commands.none();
    }

    public Command getOnDisableCommand() {
        return Commands.none();
    }

    public enum SelfTest {
        DRIVE("Swerve Drive");

        private final String name;

        SelfTest(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public void populateSelfTestChooser() {
        SelfTest[] selfTests = SelfTest.values();
        selfTestChooser.setDefaultOption(selfTests[0].getName(), selfTests[0]);
        for (int i = 1; i < selfTests.length; i++) {
            SelfTest selfTest = selfTests[i];
            selfTestChooser.addOption(selfTest.getName(), selfTest);
        }
    }

    public void pushTuningConstants() {
    }
}
