package org.teamspyder.spyderlib.tuners;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// NOTE: This is a prototype, impl and use will most likely change
public class TunableDouble {
    private double lastValue;
    private String path;
    private boolean tuningMode;

    public TunableDouble(String path, double lastValue, boolean tuningMode) {
        this.lastValue = lastValue;
        this.path = path;
        this.tuningMode = tuningMode;

        SmartDashboard.putNumber(path, lastValue);
    }

    public TunableDouble(String path, double lastValue) {
        this(path, lastValue, true);
    }

    public double get() {
        if (tuningMode) {
            lastValue = SmartDashboard.getNumber(path, lastValue);
        }
        return lastValue;
    }
}
