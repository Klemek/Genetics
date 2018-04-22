package fr.klemek.genetics;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class Utils {

    private Utils() {

    }

    /*
     * ARRAYS
     */

    public static void shuffle(byte[] ar) {
        // Implementing Fisher–Yates shuffle
        Random rnd = ThreadLocalRandom.current();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            byte a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

    public static void shuffle(int[] ar) {
        // Implementing Fisher–Yates shuffle
        Random rnd = ThreadLocalRandom.current();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

    public static byte[] indexes(byte size) {
        byte[] out = new byte[size];
        for (int i = 0; i < size; i++)
            out[i] = (byte) i;
        return out;
    }

    public static int[] indexes(int size) {
        int[] out = new int[size];
        for (int i = 0; i < size; i++)
            out[i] = i;
        return out;
    }

    public static void fill(byte[] array, byte value) {
        for (int i = 0; i < array.length; i++)
            array[i] = value;
    }

    public static int indexOf(byte[] array, byte value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value)
                return i;
        }
        return -1;
    }

    /*
     * COORDINATES
     */

    private final static float LATITUTE_FACTOR = 110.574f;
    private final static float LONGITUTE_FACTOR = 111.320f;

    private final static float LATITUTE_FACTOR_APROX = 918f / 11.93f;
    private final static float LONGITUTE_FACTOR_APROX = 881f / 7.98f;

    private final static int EARTH_RADIUS = 6371; //km

    public static float distance(float[] coords1, float[] coords2) {
        return distance(new float[]{coords2[0] - coords1[0], coords2[1] - coords1[1]});
    }

    public static float geoDistance(float[] coords1, float[] coords2, boolean aproximate) {
        if (aproximate)
            return distance(coordinatesToKm(coords1, true), coordinatesToKm(coords2, true));

        double phi1 = Math.toRadians(coords1[0]);
        double phi2 = Math.toRadians(coords2[0]);
        double dPhi = Math.toRadians(coords2[0] - coords1[0]);
        double dLambda = Math.toRadians(coords2[1] - coords1[1]);
        double a = Math.sin(dPhi / 2d) * Math.sin(dPhi / 2d)
                + Math.cos(phi1) * Math.cos(phi2)
                * Math.sin(dLambda / 2d) * Math.sin(dLambda / 2d);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (float) (EARTH_RADIUS * c);
    }

    public static float distance(float[] coords) {
        return (float) Math.sqrt(Math.pow(coords[0], 2) + Math.pow(coords[1], 2));
    }

    public static float[] coordinatesToKm(float[] coordinates, boolean aproximate) {
        float x;
        float y;
        if (aproximate) {
            x = coordinates[0] * LATITUTE_FACTOR_APROX;
            y = coordinates[1] * LONGITUTE_FACTOR_APROX;
        } else {
            x = coordinates[0] * LATITUTE_FACTOR;
            y = (float) (coordinates[1] * LONGITUTE_FACTOR * Math.cos(Math.toRadians(coordinates[0])));
        }
        return new float[]{x, y};
    }

    /*
     * RANDOM
     */

    public static int randInt(int min, int max, int excluded) {
        int out;
        do {
            out = ThreadLocalRandom.current().nextInt(min, max);
        } while (out == excluded);
        return out;
    }

    public static boolean randBoolean(float successPercentage) {
        return ThreadLocalRandom.current().nextFloat() < successPercentage;
    }

    /*
     * COLOR
     */

    public static Color colorFromHex(String hex) {
        return new Color(
                Integer.valueOf(hex.substring(1, 3), 16),
                Integer.valueOf(hex.substring(3, 5), 16),
                Integer.valueOf(hex.substring(5, 7), 16));
    }

    /*
     * OTHER
     */

    public static <T> T instantiate(Class<T> c) {
        try {
            return c.getConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new NotImplementedException();
        }
    }
}
