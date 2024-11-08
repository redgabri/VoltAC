package com.volt.voltac.utils.anticheat.click;

import lombok.experimental.UtilityClass;

import java.util.Deque;

@UtilityClass
public class ClickUtils {

    public static double getVariance(Deque<Long> clickTimeDifferences) {
        // Calculate time differences between consecutive timestamps
        long previousTime = -1;
        double sumDifferences = 0.0;
        double maxDifference = 0.0;
        double minDifference = Double.MAX_VALUE;

        for (long clickTime : clickTimeDifferences) {
            if (previousTime != -1) {
                long diff = clickTime - previousTime;
                sumDifferences += diff;
                maxDifference = Math.max(maxDifference, diff);
                minDifference = Math.min(minDifference, diff);
            }
            previousTime = clickTime;
        }

        // Calculate the variance in click time differences
        double mean = sumDifferences / (clickTimeDifferences.size() - 1);
        double variance = 0.0;
        for (long clickTime : clickTimeDifferences) {
            if (previousTime != -1) {
                long diff = clickTime - previousTime;
                variance += Math.pow(diff - mean, 2);
            }
            previousTime = clickTime;
        }
        return variance /= (clickTimeDifferences.size() - 1);
    }
}
