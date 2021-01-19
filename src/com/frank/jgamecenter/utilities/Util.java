package com.frank.jgamecenter.utilities;

import java.util.concurrent.ThreadLocalRandom;

public class Util {
    public static double getRandomNumberBetween(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    public static int getRandomIntBetween(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    public static boolean probableEventOcurred(int percentProbability) {
        int number = getRandomIntBetween(1, 100 + 1);
        return percentProbability >= number;
    }

    public static boolean probableEventOcurred(double percentProbability) {
        double number = ThreadLocalRandom.current().nextDouble(0, 100);
        return percentProbability >= number;
    }
}