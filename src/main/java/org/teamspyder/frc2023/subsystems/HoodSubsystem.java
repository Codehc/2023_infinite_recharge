package org.teamspyder.frc2023.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class HoodSubsystem extends SubsystemBase {
    // Note: deviceId not accurate at the time of writing.
    private final CANSparkMax hoodMotor = new CANSparkMax(16, MotorType.kBrushless);
    
    private final RelativeEncoder hoodMotorEncoder = hoodMotor.getEncoder();

    public HoodSubsystem() {

    }

    public CANSparkMax getHoodMotor() {
        return hoodMotor;
    }

    public double getHoodPosition() {
        return hoodMotorEncoder.getPosition() / 6;
    }



}
