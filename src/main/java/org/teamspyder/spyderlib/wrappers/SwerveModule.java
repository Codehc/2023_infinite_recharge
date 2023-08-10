// Developed by Reza from Team Spyder 1622

package org.teamspyder.spyderlib.wrappers;

import com.ctre.phoenix.sensors.AbsoluteSensorRange;
import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.CANCoderStatusFrame;
import com.revrobotics.CANSparkMax.IdleMode;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Commands;
import org.teamspyder.frc2023.Robot;
import org.teamspyder.spyderlib.wrappers.NEO.StatusFrame;

public class SwerveModule {
    private static final double WHEEL_DIAMETER = Units.inchesToMeters(4);

    private static final double DRIVE_GEAR_RATIO = 6.75; // L2 modules
    private static final double AZIMUTH_GEAR_RATIO = 150.0/7.0;

    private final SwerveModuleConstants swerveModuleConstants;
    private final String name;

    private static final int DRIVE_FREE_CURRENT_LIMIT = 60;
    private static final int DRIVE_STALL_CURRENT_LIMIT = 40;
    private static final int AZIMUTH_CURRENT_LIMIT = 35;

    private final NEO azimuthMotor;
    private final NEO driveMotor;

    private CANCoder azimuthEncoder;

    private boolean initialized = false;

    /**
     * Instantiates a SwerveModule
     * @param swerveModuleConstants The constants to configure module with
     */
    public SwerveModule(SwerveModuleConstants swerveModuleConstants, String name) {
        this.name = name;
        this.azimuthMotor = new NEO(swerveModuleConstants.getAzimuthID());
        this.driveMotor = new NEO(swerveModuleConstants.getDriveID(), swerveModuleConstants.getDriveInverted());
        this.swerveModuleConstants = swerveModuleConstants;

        boolean driveMotorInitialized = this.driveMotor.mutate((neo) -> {
            int errors = 0;
            errors += NEO.check(neo.getEncoder().setPositionConversionFactor((WHEEL_DIAMETER * Math.PI) / (DRIVE_GEAR_RATIO)));
            errors += NEO.check(neo.getEncoder().setVelocityConversionFactor((WHEEL_DIAMETER * Math.PI) / (60 * DRIVE_GEAR_RATIO)));

            errors += NEO.check(neo.getEncoder().setMeasurementPeriod(swerveModuleConstants.getVeloMeasurementPeriod()));
            errors += NEO.check(neo.getEncoder().setAverageDepth(swerveModuleConstants.getVeloMeasurementDepth()));
            
            errors += NEO.check(neo.setSmartCurrentLimit(DRIVE_STALL_CURRENT_LIMIT, DRIVE_FREE_CURRENT_LIMIT));

            errors += NEO.check(neo.changeStatusFrame(StatusFrame.APPLIED_FAULTS_FOLLOWER, 65535));
            errors += NEO.check(neo.resetStatusFrame(StatusFrame.VELO_TEMP_VOLTAGE_CURRENT));
            errors += NEO.check(neo.changeStatusFrame(StatusFrame.POSITION, 65535));
            errors += NEO.check(neo.changeStatusFrame(StatusFrame.ANALOG_VOLTAGE_VELO_POS, 65535));
            errors += NEO.check(neo.changeStatusFrame(StatusFrame.ALTERNATE_VELO_POS, 65535));
            errors += NEO.check(neo.changeStatusFrame(StatusFrame.ABSOLUTE_ENCODER_POS, 65535));
            errors += NEO.check(neo.changeStatusFrame(StatusFrame.ABSOLUTE_ENCODER_VELO, 65535));

            errors += NEO.check(neo.getPID().setP(swerveModuleConstants.getDriveKP()));
            errors += NEO.check(neo.getPID().setI(swerveModuleConstants.getDriveKI()));
            errors += NEO.check(neo.getPID().setD(swerveModuleConstants.getDriveKD()));
            errors += NEO.check(neo.getPID().setFF(swerveModuleConstants.getDriveKFF()));

            return errors == 0;
        });

        boolean azimuthMotorInitialized = this.azimuthMotor.mutate((neo) -> {
            int errors = 0;
            errors += NEO.check(neo.getEncoder().setPositionConversionFactor(360.0 / AZIMUTH_GEAR_RATIO));
            errors += NEO.check(neo.getEncoder().setVelocityConversionFactor(360.0 / (AZIMUTH_GEAR_RATIO * 60)));

            errors += NEO.check(neo.getEncoder().setMeasurementPeriod(swerveModuleConstants.getVeloMeasurementPeriod()));
            errors += NEO.check(neo.getEncoder().setAverageDepth(swerveModuleConstants.getVeloMeasurementDepth()));

            errors += NEO.check(neo.setSmartCurrentLimit(AZIMUTH_CURRENT_LIMIT));

            errors += NEO.check(neo.changeStatusFrame(StatusFrame.APPLIED_FAULTS_FOLLOWER, 65535));
            errors += NEO.check(neo.changeStatusFrame(StatusFrame.VELO_TEMP_VOLTAGE_CURRENT, 65535));
            errors += NEO.check(neo.resetStatusFrame(StatusFrame.POSITION));
            errors += NEO.check(neo.changeStatusFrame(StatusFrame.ANALOG_VOLTAGE_VELO_POS, 65535));
            errors += NEO.check(neo.changeStatusFrame(StatusFrame.ALTERNATE_VELO_POS, 65535));
            errors += NEO.check(neo.changeStatusFrame(StatusFrame.ABSOLUTE_ENCODER_POS, 65535));
            errors += NEO.check(neo.changeStatusFrame(StatusFrame.ABSOLUTE_ENCODER_VELO, 65535));

            errors += NEO.check(neo.getPID().setP(swerveModuleConstants.getAzimuthKP()));
            errors += NEO.check(neo.getPID().setI(swerveModuleConstants.getAzimuthKI()));
            errors += NEO.check(neo.getPID().setD(swerveModuleConstants.getAzimuthKD()));
            errors += NEO.check(neo.getPID().setPositionPIDWrappingEnabled(true));
            errors += NEO.check(neo.getPID().setPositionPIDWrappingMinInput(-180));
            errors += NEO.check(neo.getPID().setPositionPIDWrappingMaxInput(180));

            return errors == 0;
        });

        Commands.waitSeconds(2).andThen(Commands.runOnce(() -> {
            initialized = true;
            SmartDashboard.putBoolean("Swerve/Drive Motors Initialized/" + name, driveMotorInitialized);
            SmartDashboard.putBoolean("Swerve/Azimuth Motors Initialized/" + name, azimuthMotorInitialized);
            azimuthEncoder = new CANCoder(swerveModuleConstants.getCanCoderID());
            azimuthEncoder.configAbsoluteSensorRange(AbsoluteSensorRange.Signed_PlusMinus180);
            azimuthEncoder.setStatusFramePeriod(CANCoderStatusFrame.VbatAndFaults, 255);
            seedAzimuthEncoder();
        })).ignoringDisable(true).schedule();

        // TODO: Get rid of this, this is just to test my proto code
        //SpyderRobot.tuneDouble("Swerve/Drive kP", this.swerveModuleConstants::getDriveKP, this.swerveModuleConstants::setDriveKP);
    }

    /**
     * Sets the swerve modules target state.
     * @param state Target state for the swerve module
     */
    public void setState(SwerveModuleState state) {
        state = SwerveModuleState.optimize(state, Rotation2d.fromDegrees(getAzimuth()));

        double targetSpeed = state.speedMetersPerSecond;
        double targetAngle = state.angle.getDegrees();

        SmartDashboard.putBoolean("Swerve/Initialized Modules/" + name, initialized);
        SmartDashboard.putNumber("Swerve/Target Module Velos/" + name, targetSpeed);
        SmartDashboard.putNumber("Swerve/Target Module Angles/" + name, targetAngle);
        SmartDashboard.putNumber("Swerve/Actual Module Velos/" + name, driveMotor.getVelocity());
        SmartDashboard.putNumber("Swerve/Actual Module Angles/" + name, azimuthMotor.getPosition());
        SmartDashboard.putNumber("Swerve/Module Drive Voltages/" + name, driveMotor.getAppliedOutput() * driveMotor.getBusVoltage());
        SmartDashboard.putNumber("Swerve/Module Azimuth Voltages/" + name, azimuthMotor.getAppliedOutput() * azimuthMotor.getBusVoltage());
        SmartDashboard.putNumber("Swerve/Module Drive Current Draw/" + name, driveMotor.getOutputCurrent());
        SmartDashboard.putNumber("Swerve/Module Azimuth Current Draw/" + name, azimuthMotor.getOutputCurrent());

        if (!initialized) return;

        if (targetSpeed != driveMotor.getTargetVelocity()) {
            driveMotor.setTargetVelocity(targetSpeed);
        }

        if (targetAngle != azimuthMotor.getTargetPosition()) {
            azimuthMotor.setTargetPosition(targetAngle);
        }
    }

    /**
     * Turns off the swerve module. Turns off motors
     */
    public void turnOffModule() {
        driveMotor.setVoltage(0);
        azimuthMotor.setVoltage(0);
    }

    public void limpModule() {
        driveMotor.setIdleMode(IdleMode.kCoast);
        azimuthMotor.setIdleMode(IdleMode.kCoast);
    }

    public void unlimpModule() {
        driveMotor.setIdleMode(IdleMode.kBrake);
        azimuthMotor.setIdleMode(IdleMode.kBrake);
    }

    /**
     * Gets the current drive speed of the module
     * @return The current drive speed of the module in meters per second
     */
    public double getSpeed() {
        return driveMotor.getVelocity();
    }

    /**
     * Gets the current azimuth of the module using the integrated NEO encoder
     * @return The current azimuth of the module
     */
    public double getAzimuth() {
        return azimuthMotor.getPosition();
    }

    /**
     * Gets the current actual state of the module
     * @return The current actual state of the module
     */
    public SwerveModuleState getState() {
        return new SwerveModuleState(getSpeed(), Rotation2d.fromDegrees(getAzimuth()));
    }

    public SwerveModulePosition getPosition() {
        return new SwerveModulePosition(driveMotor.getPosition(), Rotation2d.fromDegrees(getAzimuth()));
    }

    public void seedAzimuthEncoder() {
        if (Robot.isReal()) {
            azimuthMotor.setPosition(azimuthEncoder.getAbsolutePosition());
        } else {
            azimuthMotor.setPosition(0);
        }
    }
}