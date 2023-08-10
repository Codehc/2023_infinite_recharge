// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.teamspyder.spyderlib;

import com.revrobotics.CANSparkMax.FaultID;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.teamspyder.spyderlib.tuners.DoubleTuner;
import org.teamspyder.spyderlib.tuners.Tuner;
import org.teamspyder.spyderlib.wrappers.NEO;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

/*
 * Some of this is adapted from 3005's 2022 Code
 * Original source published at https://github.com/FRC3005/Rapid-React-2022-Public/tree/d499655448ed592c85f9cfbbd78336d8841f46e2
 */

public class SpyderRobot {
  private static List<Tuner> tuners = new ArrayList<>();

  private static HashMap<NEO, Short> neos = new HashMap<>();

  private static HashSet<MonitoredElement> monitoredElements = new HashSet<MonitoredElement>();

  private static int burnFlashCount = 0;

  private static boolean allConfigsSuccessful = true;

  public static boolean registerNEO(NEO neo) {
    if (neos.containsKey(neo)) {
      return false;
    }
    neos.put(neo, (short) 0);
    return true;
  }

  public static void registerTuner(Tuner tuner) {
    tuners.add(tuner);
  }

  private static Alliance lastAlliance = null;

  public static void tuneDouble(String path, Supplier<Double> doubleSupplier, Consumer<Double> doubleConsumer) {
    new DoubleTuner(path, doubleSupplier, doubleConsumer);
  }

  public static void tick() {
    lastAlliance = DriverStation.getAlliance();

    for (Map.Entry<NEO, Short> neoShortEntry : neos.entrySet()) {
      neoShortEntry.getKey().tick();
    }
    tuners.forEach(Tuner::tick);

    for (MonitoredElement el : monitoredElements) {
      if (el.m_monitor.getAsBoolean()) {
        // TODO: Log error occured
        if (el.m_errorCnt < el.m_retries) {
          el.m_hasError = el.m_reinit.getAsBoolean(); // This is where reinit gets called
          el.m_errorCnt++;
        } else {
          el.m_hasError = true;
        }
      } else {
        el.m_errorCnt--;
      }
    }

    SmartDashboard.putBoolean("Robot/All Configs Successful", allConfigsSuccessful);
  }


  public static MonitoredElement monitor(
          BooleanSupplier monitorFunction, BooleanSupplier reinitFunction) {
    MonitoredElement el = new MonitoredElement(monitorFunction, reinitFunction);
    monitoredElements.add(el);
    return el;
  }

  public static Runnable getNEOHealthMonitoringRunnable() {
    return () -> {
      neos.forEach(
        (neo, prevFault) -> {
          short faults = neo.getStickyFaults();
          if (faults != prevFault) {
            DataLogManager.log("NEO " + neo.getDeviceId() + " faulting: " + NEO.faultWordToString(neo.getFaults()));
          }
          neos.put(neo, faults);
        }
      );
    };
  }

  /**
   * Monitor the Spark Max to check for reset. This is used by the health monitor to automatically
   * re-initialize the spark max in case of reboot.
   */
  public static boolean neoMonitorFunction(NEO neo) {
    return neo.getStickyFault(FaultID.kHasReset);
  }

  public static void failSettingAllConfigs() {
    allConfigsSuccessful = false;
  }

  /**
   * Run burnFlash() for all controllers initialized. burnFlash() stops comms w/ device for 200ms or more.
   * Might include calls from before method was called or calls from after. Too risky so we do this and burn everything in sync
   * to avoid accidentally stopping messages we send from getting to the device.
   */
  public static void burnSparkMaxFlashes() {
    DataLogManager.log("Burning Flash Count: " + ++burnFlashCount);
    Timer.delay(0.25);
    for (Map.Entry<NEO, Short> neoShortEntry : neos.entrySet()) {
      neoShortEntry.getKey().burnFlash();
      Timer.delay(0.005);
    }
    Timer.delay(0.25);
  }

  public static int getBurnFlashCount() {
    return burnFlashCount;
  }

  public static Alliance getAlliance() {
    return lastAlliance;
  }
}
