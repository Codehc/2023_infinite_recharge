package org.teamspyder.frc2023.subsystems;

import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.teamspyder.frc2023.constants.CANID;

public class ShooterSubsystem extends SubsystemBase {
    private final CANSparkMax mainShooterMotor = new CANSparkMax(CANID.MAIN_SHOOTER, MotorType.kBrushless);
    private final CANSparkMax secondShooterMotor = new CANSparkMax(CANID.SECOND_SHOOTER, MotorType.kBrushless);

    private final RelativeEncoder mainShooterEncoder = mainShooterMotor.getEncoder();;


    public ShooterSubsystem() {
        secondShooterMotor.follow(mainShooterMotor, true);
    }

    public void driveShooter(double speed) {
        mainShooterMotor.set(speed);
    }

    public double getMotorRPM() {
        return mainShooterEncoder.getVelocity();
    }
}
