package org.teamspyder.frc2023.commands.swerve;

import org.teamspyder.frc2023.Profile;
import org.teamspyder.frc2023.subsystems.HoodSubsystem;
import org.teamspyder.frc2023.subsystems.ShooterSubsystem;

import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj2.command.CommandBase;

import org.teamspyder.spyderlib.SpyderProfile;
import org.teamspyder.spyderlib.SpyderProfile.Button;

public class MoveHoodCommand extends CommandBase {
    private final HoodSubsystem hood;
    private final CANSparkMax hoodMotor;
    private final double targetPosition;

    public MoveHoodCommand(HoodSubsystem subsystem, double targetPosition) {
        hood = subsystem;
        hoodMotor = hood.getHoodMotor();
        this.targetPosition = targetPosition;
        addRequirements(subsystem);
    }


    @Override
    public void execute() {
        hoodMotor.set((targetPosition - hood.getHoodPosition()) * 0.007);
    }

    @Override
    public void end(boolean interrupted) {
        hoodMotor.set(0);
    }
  
    @Override
    public boolean isFinished() {
        return false;
    }
}
