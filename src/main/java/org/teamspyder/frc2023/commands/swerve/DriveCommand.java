package org.teamspyder.frc2023.commands.swerve;

import org.teamspyder.frc2023.Profile;
import org.teamspyder.frc2023.subsystems.DrivebaseSubsystem;
import org.teamspyder.spyderlib.SpyderProfile.Stick;

import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class DriveCommand extends CommandBase {
    private final DrivebaseSubsystem drivebase;

    private final Profile profile = Profile.getProfile();

    private final Stick lStick = profile.LEFT_Y;
    private final Stick rStick = profile.RIGHT_Y;
    
    public DriveCommand(DrivebaseSubsystem subsystem) {
        drivebase = subsystem;
        addRequirements(subsystem);
    }

    @Override
    public void execute() {
        drivebase.arcadeDrive(rStick.getAxisState(), lStick.getAxisState());
    }

    @Override
    public void end(boolean interrupted) {}
  
    @Override
    public boolean isFinished() {
      return false;
    }
  }
