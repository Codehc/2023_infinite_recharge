package org.teamspyder.spyderlib.control;

// Don't use this, WPILib's works perfectly fine. I'll probably remove this at some point
public class MotionProfile {
    private final double MAX_ACCEL;
    private final double MAX_VELO;

    private double minTime;
    private double minDistance;

    private double timeAcceleration;
    private double timeCoast;
    private double timeDeceleration;

    private double error;

    private double setpoint;
    private double veloSetpoint;

    // ex: period = 0.02 for 20 ms periods
    public MotionProfile(double maxAccel, double maxVelo) {
        this.MAX_ACCEL = maxAccel;
        this.MAX_VELO = maxVelo;

        calculateMinDistance();
    }

    private void calculateMinDistance() {
        minTime = MAX_VELO / MAX_ACCEL;
        /*
        integrate accel for velo and velo for distance
        a(t) = maxAccel
        v(t) = ∫(a(t))dt = maxAccel * x + C; // C is 0 since we start at 0 velo
        s(t) = ∫(v(t))dt = maxAccel/2 * x^2 + Cx + D; // D is 0 since we start at position 0
        */
        minDistance = MAX_ACCEL / 2 * Math.pow(minTime, 2);
        minDistance *= 2; // double the distance since we have to accel and decel

        minTime *=2; // double the time since we have to accel and decel
    }

    public void calculateProfile(double current, double goal) {
        error = goal - current;
        if (Math.abs(error) < minDistance) {
            timeAcceleration = Math.sqrt(Math.abs(error) / MAX_ACCEL);
            timeCoast = timeAcceleration; // no coasting
            timeDeceleration = 2 * timeAcceleration; // decelerate for same amount of time as accelerating
        } else {
            timeAcceleration = minTime / 2;
            timeCoast = timeAcceleration + (Math.abs(error) - minDistance) / MAX_VELO;
            timeDeceleration = minTime / 2 + timeCoast;
        }

        setpoint = current;
        veloSetpoint = 0;
    }

    private double lastTime = 0;
    public double tick(double time) {
        double dt = time - lastTime;
        lastTime = time;

        if (time <= timeAcceleration) {
            veloSetpoint += Math.signum(error) * MAX_ACCEL * dt;
            setpoint += veloSetpoint * dt;
        } else if (time <= timeCoast) {
            setpoint = setpoint + Math.signum(error) * MAX_VELO * dt;
        } else if (time <= timeDeceleration) {
            veloSetpoint -= Math.signum(error) * MAX_ACCEL * dt;
            setpoint += veloSetpoint * dt;
        }

        return setpoint;
    }

    public boolean isFinished() {
        return lastTime >= timeDeceleration;
    }
}
