package fr.klemek.genetics;

import java.awt.*;
import java.awt.geom.Line2D;
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

    static void shuffle(int[] ar) {
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

    static int[] indexes(int size) {
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

    private static final float LATITUTE_FACTOR = 110.574f;
    private static final float LONGITUTE_FACTOR = 111.320f;

    private static final float LATITUTE_FACTOR_APPROX = 918f / 11.93f;
    private static final float LONGITUTE_FACTOR_APPROX = 881f / 7.98f;

    private static final int EARTH_RADIUS = 6371; //km

    public static float distance(float ax, float ay, float bx, float by) {
        return distance(new float[]{bx - ax, by - ay});
    }

    private static float distance(float[] coords1, float[] coords2) {
        return distance(new float[]{coords2[0] - coords1[0], coords2[1] - coords1[1]});
    }

    public static float geoDistance(float[] coords1, float[] coords2, boolean approximate) {
        if (approximate)
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

    private static float distance(float[] coords) {
        return (float) Math.sqrt(Math.pow(coords[0], 2) + Math.pow(coords[1], 2));
    }

    public static float[] coordinatesToKm(float[] coordinates, boolean approximate) {
        float x;
        float y;
        if (approximate) {
            x = coordinates[0] * LATITUTE_FACTOR_APPROX;
            y = coordinates[1] * LONGITUTE_FACTOR_APPROX;
        } else {
            x = coordinates[0] * LATITUTE_FACTOR;
            y = (float) (coordinates[1] * LONGITUTE_FACTOR * Math.cos(Math.toRadians(coordinates[0])));
        }
        return new float[]{x, y};
    }

    public static boolean intersect(float ax, float ay, float bx, float by, float cx, float cy, float dx, float dy) {
        return (new Line2D.Double(ax, ay, bx, by)).intersectsLine(cx, cy, dx, dy);
    }

    /*
     * RANDOM
     */

    public static int randInt(int start, int stop, int excluded) {
        int out;
        do {
            out = ThreadLocalRandom.current().nextInt(start, stop);
        } while (out == excluded);
        return out;
    }

    static boolean randBoolean(float successPercentage) {
        return ThreadLocalRandom.current().nextFloat() < successPercentage;
    }

    public static short randShort(short start, short stop) {
        return (short) ThreadLocalRandom.current().nextInt(start, stop);
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

    static <T> T instantiate(Class<T> c) {
        try {
            return c.getConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new UnsupportedOperationException("No default constructor found in class " + c.getSimpleName());
        }
    }

    public static boolean distinct(int... indexes) {
        for (int i = 0; i < indexes.length - 1; i++)
            for (int j = i + 1; j < indexes.length; j++)
                if (indexes[i] == indexes[j])
                    return false;
        return true;
    }

    static boolean compare(boolean lowest, float val, float ref) {
        if (lowest)
            return val < ref;
        return val > ref;
    }
}
