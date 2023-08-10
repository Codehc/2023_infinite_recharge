// Developed by Reza from Team Spyder 1622

package org.teamspyder.spyderlib.wrappers;

import com.revrobotics.*;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.Timer;
import org.teamspyder.frc2023.Robot;
import org.teamspyder.spyderlib.SpyderRobot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/*
 * Some of this is adapted from 3005's 2022 Code
 * Original source published at https://github.com/FRC3005/Rapid-React-2022-Public/tree/d499655448ed592c85f9cfbbd78336d8841f46e2
 */

public class NEO extends CANSparkMax {
    public final RelativeEncoder encoder;
    private final SparkMaxPIDController pidController;

    private List<Function<NEO, Boolean>> mutatorChain;
    private List<NEO> followers = new ArrayList<>();

    private double targetPosition = 0;
    private double targetVelocity = 0;

    /**
     * Creates a new NEO motor
     * @param id CANID of the SparkMax the NEO is connected to.
     */
    public NEO(int id) {
        this(id, false);
    }

    /**
     * Creates a new NEO motor
     * @param id CANID of the SparkMax the NEO is connected to.
     * @param mode The idle mode of the motor. If true, the motor will brake when not powered. If false, the motor will coast when not powered.
     */
    public NEO(int id, CANSparkMax.IdleMode mode) {
        this(id, false, mode);
    }

    /**
     * Creates a new NEO motor
     * @param id CANID of the SparkMax the NEO is connected to.
     * @param reversed Whether the motor is reversed or not.
     * @param mode The idle mode of the motor. If true, the motor will brake when not powered. If false, the motor will coast when not powered.
     */
    public NEO(int id, boolean reversed, CANSparkMax.IdleMode mode) {
        super(id, CANSparkMaxLowLevel.MotorType.kBrushless);

        restoreFactoryDefaults();
        Timer.delay(0.050);

        // If a parameter set fails, this will add more time to alleviate any bus traffic
        // default is 20ms
        setCANTimeout(50);

        mutatorChain = new ArrayList<>();
        SpyderRobot.monitor(() -> SpyderRobot.neoMonitorFunction(this), this::reinitNEO);

        DataLogManager.log("Initializing NEO with ID " + getDeviceId());
        if (SpyderRobot.getBurnFlashCount() > 0) {
            DataLogManager.log("NEO with ID " + getDeviceId() + " initialized after flash has been burned");
        }

        register();

        mutate((neo) -> {
            int errors = 0;
            errors += NEO.check(neo.setIdleMode(mode));
            neo.setInverted(reversed);
            return errors == 0;
        });

        encoder = getEncoder();
        pidController = getPIDController();
    }

    /**
     * Creates a new NEO motor
     * @param id CANID of the SparkMax the NEO is connected to.
     * @param reversed Whether the motor is reversed or not.
     */
    public NEO(int id, boolean reversed) {
        this(id, reversed, CANSparkMax.IdleMode.kBrake);
    }

    /**
     * Sets the target position for the NEO.
     * @param position Position to set the NEO to in rotations.
     * @param arbitraryFeedForward Arbitrary feed forward to add to the motor output.
     */
    public synchronized void setTargetPosition(double position, double arbitraryFeedForward, int slot) {
        pidController.setReference(position, ControlType.kPosition, slot, arbitraryFeedForward, SparkMaxPIDController.ArbFFUnits.kVoltage);
        targetPosition = position;
    }

    public synchronized void setTargetPosition(double position, double arbitraryFeedForward) {
        setTargetPosition(position, arbitraryFeedForward, 0);
    }

    public synchronized void setTargetPosition(double position) {
        setTargetPosition(position, 0, 0);
    }

    /**
     * Sets the target velocity for the NEO.
     * @param velocity Velocity to set the NEO to in rotations per minute.
     */
    public synchronized void setTargetVelocity(double velocity) {
        setTargetVelocity(velocity, 0, 0);
    }

    /**
     * Sets the target velocity for the NEO.
     * @param velocity Velocity to set the NEO to in rotations per minute.
     * @param arbitraryFeedForward Arbitrary feed forward to add to the motor output.
     */
    public synchronized void setTargetVelocity(double velocity, double arbitraryFeedForward, int slot) {
        if (velocity == 0) {
            set(0);
        } else {
            pidController.setReference(velocity, ControlType.kVelocity, slot, arbitraryFeedForward, SparkMaxPIDController.ArbFFUnits.kVoltage);
        }
        targetVelocity = velocity;
    }

    private boolean shouldCache = false;
    private double position = 0;
    private double velo = 0;
    public void tick() {
        shouldCache = true;

        if (Robot.isReal()) {
            position = encoder.getPosition();
            velo = encoder.getVelocity();
        }
    }

    public void register() {
        SpyderRobot.registerNEO(this);
    }

    /**
     * Gets the position of the NEO in rotations.
     * @return The position of the NEO in rotations relative to the last 0 position.
     */
    public double getPosition() {
        if (shouldCache || Robot.isSimulation()) {
            return position;
        } else {
            return encoder.getPosition();
        }
    }

    /**
     * Gets the velocity of the NEO in rotations per minute.
     * @return The instantaneous velocity of the NEO in rotations per minute.
     */
    public double getVelocity() {
        if (shouldCache || Robot.isSimulation()) {
            return velo;
        } else {
            return encoder.getVelocity();
        }
    }

    public synchronized void setPosition(double position) {
        if (Robot.isReal()) {
            encoder.setPosition(position);
        }
        setPositionSim(position);
    }

    public synchronized void setPositionSim(double position) {
        if (Robot.isSimulation()) {
            this.position = position;
        }
    }

    public synchronized void setVelocitySim(double velocity) {
        if (Robot.isSimulation()) {
            this.velo = velocity;
        }
    }

    /**
     * Gets the target position of the NEO in rotations.
     * @return The target position of the NEO in rotations.
     */
    public double getTargetPosition() {
        return targetPosition;
    }

    /**
     * Gets the target velocity of the NEO in rotations per minute.
     * @return The target velocity of the NEO in rotations per minute.
     */
    public double getTargetVelocity() {
        return targetVelocity;
    }

    /**
     * Create spark max object with an initializer method. This method is called on initialization as
     * well as if the spark max resets.
     *
     * @param initialize function that returns true on success, that takes a CANSparkMax and a Boolean
     *     to indicate if the call is from initialization or after a reset.
     * @return this
     */
    public NEO withInitializer(Function<NEO, Boolean> initialize) {
        mutatorChain.add(initialize);

        DataLogManager.log("Configuring NEO ID " + getDeviceId());
        int setAttemptNumber = 0;
        boolean successful = true;
        while (!initialize.apply(this)) {
            DataLogManager.log("NEO ID " + getDeviceId() + " failed to init on attempt " + setAttemptNumber);
            setAttemptNumber++;

            if (setAttemptNumber >= 5) {
                DataLogManager.log("NEO ID " + getDeviceId() + " failed to final init");
                successful = false;
                SpyderRobot.failSettingAllConfigs();
                break;
            }
        }

        if (successful) {
            DataLogManager.log("Successfully configured NEO ID " + getDeviceId() + " in " + (setAttemptNumber + 1) + " attempts");
        }
        return this;
    }

    /**
     * Modify CANSparkMax object. Mutations using this method are re-run in order in the case of a
     * device failure that is later recovered. The same function can be called multiple times, and
     * will simply be moved to the end of the list each call.
     *
     * <p>Only adds the function to the list if it succeeds
     *
     * @param fcn a function on the underlying CANSparkMax object returning true on success. Typically
     *     used to change parameter values. Function should run quickly and return.
     * @return result of mutate function
     */
    public boolean mutate(Function<NEO, Boolean> fcn) {
        Boolean result = fcn.apply(this);

        int setAttemptNumber = 0;
        while (result == null || !result) {
            setAttemptNumber++;

            if (setAttemptNumber >= 5) {
                SpyderRobot.failSettingAllConfigs();
                DataLogManager.log("NEO " + getDeviceId() + " failed to set configs");
                break;
            }
            result = fcn.apply(this);
        }

        if (result != null && result) {
            mutatorChain.add(fcn);
        }
        return result != null && result;
    }

    public void addFollower(NEO follower) {
        addFollower(follower, false);
    }

    public void addFollower(NEO follower, boolean invert) {
        follower.mutate(
                (neo) -> {
                    return neo.follow(this, invert) == REVLibError.kOk;
                });
        followers.add(follower);
    }

    /**
     * Reinitialize the SparkMax by running through all mutations on the object in order.
     *
     * @return true if reinitialized correctly
     */
    private boolean reinitNEO() {
        for (var fcn : mutatorChain) {
            if (!fcn.apply(this)) {
                return false;
            }
        }
        return clearFaults() == REVLibError.kOk;
    }

    /**
     * Gets the proportional gain constant for PIDFF controller.
     * @return The proportional gain constant for PIDFF controller.
     */
    public double getP() {
        return pidController.getP();
    }

    /**
     * Gets the integral gain constant for PIDFF controller.
     * @return The integral gain constant for PIDFF controller.
     */
    public double getI() {
        return pidController.getI();
    }

    /**
     * Gets the derivative gain constant for PIDFF controller.
     * @return The derivative gain constant for PIDFF controller.
     */
    public double getD() {
        return pidController.getD();
    }


    /**
     * Gets the I-Zone constant for PIDFF controller.
     * @return The I-Zone constant for PIDFF control.
     */
    public double getIZ() {
        return pidController.getIZone();
    }

    /**
     * Gets the feedforward gain constant for PIDFF controller.
     * @return The feedforward gain constant for PIDFF controller.
     */
    public double getFF() {
        return pidController.getFF();
    }

    public RelativeEncoder getEncoder() {
        return encoder;
    }

    public SparkMaxPIDController getPID() {
        return pidController;
    }

    // Documentation: https://docs.revrobotics.com/sparkmax/operating-modes/control-interfaces#periodic-status-frames
    public REVLibError changeStatusFrame(StatusFrame frame, int period) {
        REVLibError error = setPeriodicFramePeriod(frame.getFrame(), period);

        return error;
    }

    public REVLibError resetStatusFrame(StatusFrame frame) {
        return changeStatusFrame(frame, frame.getDefaultPeriod());
    }

    // Documentation: https://docs.revrobotics.com/sparkmax/operating-modes/control-interfaces#periodic-status-frames
    public enum StatusFrame {
        APPLIED_FAULTS_FOLLOWER(PeriodicFrame.kStatus0, 10),
        VELO_TEMP_VOLTAGE_CURRENT(PeriodicFrame.kStatus1, 20),
        POSITION(PeriodicFrame.kStatus2, 20),
        ANALOG_VOLTAGE_VELO_POS(PeriodicFrame.kStatus3, 50),
        ALTERNATE_VELO_POS(PeriodicFrame.kStatus4, 20),
        ABSOLUTE_ENCODER_POS(PeriodicFrame.kStatus5, 200),
        ABSOLUTE_ENCODER_VELO(PeriodicFrame.kStatus6, 200);

        private final PeriodicFrame frame;
        private final int defaultPeriod; // ms
        StatusFrame(PeriodicFrame frame, int defaultPeriod) {
            this.frame = frame;
            this.defaultPeriod = defaultPeriod;
        }

        public PeriodicFrame getFrame() {
            return frame;
        }

        public int getDefaultPeriod() {
            return defaultPeriod;
        }
    }

    public static String faultWordToString(short faults) {
        if (faults == 0) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        int faultsInt = faults;
        for (int i = 0; i < 16; i++) {
            if (((1 << i) & faultsInt) != 0) {
                builder.append(CANSparkMax.FaultID.fromId(i).toString());
                builder.append(" ");
            }
        }
        return builder.toString();
    }

    /**
     * @param error API return value
     * @return
     */
    public static int check(REVLibError error) {
        return error == REVLibError.kOk ? 0 : 1;
    }
}
