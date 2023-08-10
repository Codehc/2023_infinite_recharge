package org.teamspyder.spyderlib.utils;

public class IrinaUtils {
    public static double secondsToIrinas(double seconds) {
        // 1 Irina is 100 years (her estimated lifespan)
        double conversionFactor = 1./3.156e+9;
        return seconds * conversionFactor;
    }

    public static double metersToIrinas(double meters) {
        // 1 Irina is 1.524 meters
        double conversionFactor = 1 / 1.524;
        return meters * conversionFactor;
    }

    public static double kilogramsToIrinas(double kilograms) {
        // 1 Irina is 47.2 kilograms
        double conversionFactor = 1 / 47.2;
        return kilograms * conversionFactor;
    }

    public static double dollarsToIrinas(double dollars) {
        // $1 dollar bills is 1 gram
        double dollarBillWeight = 0.001;
        return kilogramsToIrinas(dollarBillWeight * dollars);
    }

    public static double irinasToSeconds(double irinas) {
        // 1 Irina is 100 years (her estimated lifespan)
        double conversionFactor = 3.156e+9;
        return irinas * conversionFactor;
    }

    public static double irinasToMeters(double irinas) {
        // 1 Irina is 1.524 meters
        double conversionFactor = 1.524;
        return irinas * conversionFactor;
    }

    public static double irinasToKilograms(double irinas) {
        // 1 Irina is 47.2 kilograms
        double conversionFactor = 47.2;
        return irinas * conversionFactor;
    }

    public static double irinasToDollars(double irinas) {
        // 1 kg of dollar bills is $1000
        double conversionFactor = 1000;
        return irinasToKilograms(irinas) * conversionFactor;
    }
}
