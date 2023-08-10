package org.teamspyder.frc2023.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.teamspyder.frc2023.constants.CANID;

public class DrivebaseSubsystem extends SubsystemBase {
    private final CANSparkMax rightBackMotor = new CANSparkMax(CANID.BACK_RIGHT_DRIVE, MotorType.kBrushless);
    private final CANSparkMax rightFrontMotor = new CANSparkMax(CANID.FRONT_RIGHT_DRIVE, MotorType.kBrushless);
    private final MotorControllerGroup rightMotorGroup = new MotorControllerGroup(rightBackMotor, rightFrontMotor);
    
    private final CANSparkMax leftBackMotor = new CANSparkMax(CANID.BACK_LEFT_DRIVE, MotorType.kBrushless);
    private final CANSparkMax leftFrontMotor = new CANSparkMax(CANID.FRONT_LEFT_DRIVE, MotorType.kBrushless);
    private final MotorControllerGroup leftMotorGroup = new MotorControllerGroup(leftBackMotor, leftFrontMotor);

    private final DifferentialDrive drivebase = new DifferentialDrive(leftMotorGroup, rightMotorGroup);

    public DrivebaseSubsystem() {
 
    }

    public void arcadeDrive(double xSpeed, double zRotation) {
        drivebase.arcadeDrive(xSpeed, zRotation);
    }
}
