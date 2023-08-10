package org.teamspyder.spyderlib.tuners;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.teamspyder.spyderlib.SpyderRobot;

import java.util.function.Consumer;
import java.util.function.Supplier;

// NOTE: This is a prototype, impl and use will most likely change
public class DoubleTuner implements Tuner {
    private final String path;
    private final Supplier<Double> doubleSupplier;
    private final Consumer<Double> doubleConsumer;

    public DoubleTuner(String path, Supplier<Double> doubleSupplier, Consumer<Double> doubleConsumer) {
        this.path = "Tuners/" + path;
        this.doubleSupplier = doubleSupplier;
        this.doubleConsumer = doubleConsumer;

        SmartDashboard.putNumber(this.path, doubleSupplier.get());

        SpyderRobot.registerTuner(this);
    }

    @Override
    public void tick() {
        double lastValue = doubleSupplier.get();
        double tunedValue = SmartDashboard.getNumber(path, lastValue);
        if (tunedValue != lastValue) {
            doubleConsumer.accept(tunedValue);
        }
    }
}
