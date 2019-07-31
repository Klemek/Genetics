package fr.klemek.genetics;

import java.awt.*;
import java.awt.geom.Line2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {

    protected Utils() {

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
        Arrays.fill(array, value);
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

    public static float distance(float ax, float ay, float bx, float by) {
        return distance(new float[]{bx - ax, by - ay});
    }

    protected static float distance(float[] coords1, float[] coords2) {
        return distance(new float[]{coords2[0] - coords1[0], coords2[1] - coords1[1]});
    }

    private static float distance(float[] coords) {
        return (float) Math.sqrt(Math.pow(coords[0], 2) + Math.pow(coords[1], 2));
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

    static Color colorFromHex(String hex) {
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

    private static String[] readFile(String resourceName) {
        try (InputStream is = Utils.class.getClassLoader().getResourceAsStream(resourceName)) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is), StandardCharsets.UTF_8))) {
                ArrayList<String> lines = new ArrayList<>();
                String line;
                while ((line = br.readLine()) != null) {
                    lines.add(line);
                }
                return lines.toArray(new String[0]);
            }
        } catch (IOException e) {
            throw new RuntimeException(resourceName + ": unable to read file: " + e.getMessage());
        }
    }

    public static String[][] readCSV(String resourceName) {
        String[] lines = Utils.readFile(resourceName);
        String[][] data = new String[lines.length][];
        for (int i = 0; i < data.length; i++)
            data[i] = lines[i].split(";");
        return data;
    }
}
