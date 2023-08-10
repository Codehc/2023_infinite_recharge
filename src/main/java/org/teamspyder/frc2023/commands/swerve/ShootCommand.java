package org.teamspyder.frc2023.commands.swerve;

import org.teamspyder.frc2023.Profile;
import org.teamspyder.frc2023.subsystems.ShooterSubsystem;

import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj2.command.CommandBase;

import org.teamspyder.spyderlib.SpyderProfile;
import org.teamspyder.spyderlib.SpyderProfile.Button;

public class ShootCommand extends CommandBase {
    private final ShooterSubsystem shooter;

    private double targetSpeed;

    public ShootCommand(ShooterSubsystem subsystem, double targetSpeed) {
        shooter = subsystem;

        this.targetSpeed = targetSpeed;

        addRequirements(subsystem);
    }


    @Override
    public void execute() {
        shooter.driveShooter((targetSpeed - shooter.getMotorRPM()) * 0.0004);
    }

    @Override
    public void end(boolean interrupted) {
        shooter.driveShooter(0);
    }
  
    @Override
    public boolean isFinished() {
        return false;
    }
}
